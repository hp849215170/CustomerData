package m.hp.customerdata.activity;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import m.hp.customerdata.R;
import m.hp.customerdata.adapter.MessageBeanListAdapter;
import m.hp.customerdata.entity.MessageBean;
import m.hp.customerdata.model.UserDataViewModel;
import m.hp.customerdata.utils.MyCompareUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    //一组通知消息
    private static final String GROUP_KEY_USER_NOTIFICE = "m.hp.customerdata";
    private static final int REQUEST_OK = 300;
    private static final int RESULT_SET_OK = 1000;
    private static final int MODIFY_REQUEST = 400;
    //是新加数据还是更新数据
    private static final String IS_ADD = "IS_ADD";
    private static final String CHANNEL_ID = "800";
    //根据条件查询到的userBean数据
    private final String MESSAGE_BEAN = "MESSAGE_BEAN";
    private MessageBeanListAdapter msgAdapter;
    private RecyclerView rv_msg;
    private UserDataViewModel mUserDataViewModel;
    private static final String SAVE_DATA = "SAVE_DATA";
    private HashMap<String, String> hashMap = new HashMap<String, String>();
    private final String USER_NAME = "USER_NAME";
    private static final String USER_BEAN = "USER_BEAN";
    private FloatingActionButton fab_add;
    private FloatingActionButton fab_search;
    private MaterialSearchBar mSearchBar;
    //投保人标题
    private TextView tv_userName;
    //到期时间标题
    private TextView tv_lastDate;
    //点击投保人标题次数
    private int count_userName;
    //点击到期时间标题次数
    private int count_lastDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        //到期时间标题
        tv_lastDate = findViewById(R.id.lastDate);
        tv_lastDate.setOnClickListener(this);
        //投保人标题
        tv_userName = findViewById(R.id.userName);
        tv_userName.setOnClickListener(this);
        //获取ViewModel
        ViewModelProvider.AndroidViewModelFactory androidViewModelFactory = new ViewModelProvider.AndroidViewModelFactory(getApplication());
        mUserDataViewModel = new ViewModelProvider(this, androidViewModelFactory).get(UserDataViewModel.class);
        //初始化RecycleView控件
        rv_msg = findViewById(R.id.rv_msg);
        //创建RecycleView的适配器
        //msgAdapter = new CustomerMsgAdapter(this, mList);
        msgAdapter = new MessageBeanListAdapter(new MessageBeanListAdapter.MessageBeanDiff(), this);
        rv_msg.setAdapter(msgAdapter);
        //设置RecycleView的布局
        rv_msg.setLayoutManager(new LinearLayoutManager(this));
        //初始化FloatingActionButton控件
        fab_add = findViewById(R.id.fab_add);
        fab_search = findViewById(R.id.fab_search);
        //监听FloatingActionButton点击
        fab_search.setOnClickListener(this);
        fab_add.setOnClickListener(this);
        //MaterialSearchBar搜索框
        mSearchBar = findViewById(R.id.searchBar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
        //查询当天是否有可续保的客户
        notificeCanBuy();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mUserDataViewModel.getAllUserData().observe(this, messageBeans -> {
            msgAdapter.submitList(messageBeans);
        });
        msgAdapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_search:
                //搜索功能
                showSearchBar();
                break;
            case R.id.fab_add:
                //添加数据功能
                starAddUserActivity();
                break;
            case R.id.userName:
                //按名字排序功能
                sortByName();
                break;
            case R.id.lastDate:
                //按时间排序功能
                sortByDate();
                break;
        }
    }

    /**
     * 按名字搜索相关数据
     */
    private void showSearchBar() {
        mSearchBar.setVisibility(View.VISIBLE);
        //获取搜索框输入的搜索信息
        String text = mSearchBar.getText();
        if (!TextUtils.isEmpty(text)) {
            //开始查询
            MessageBean userBean = mUserDataViewModel.getDataByUserName(text);
            //提示用户找到查询结果
            if (userBean == null) {
                //没有查到
                Toast.makeText(MainActivity.this, "未查询到相关信息", Toast.LENGTH_LONG).show();
            } else {
                //查到
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle("提示").setMessage("找到[" + userBean.getUserName() + "]相关结果，是否查看详细信息？").setPositiveButton("确定", (dialog, which) -> {
                    //跳转到详细信息页面
                    Intent intent = new Intent(MainActivity.this, DetailedActivity.class);
                    intent.putExtra(MESSAGE_BEAN, userBean);
                    startActivity(intent);
                    Toast.makeText(MainActivity.this, "查询完毕", Toast.LENGTH_LONG).show();
                }).setNegativeButton("取消", null).show();
            }

        } else {
            Toast.makeText(MainActivity.this, "请在搜索框输入投保人名字", Toast.LENGTH_LONG).show();
        }

    }

    /**
     * 启动添加数据Activity
     */
    private void starAddUserActivity() {
        Intent intent = new Intent(this, AddUserActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(SAVE_DATA, "SAVE_DATA");
        bundle.putBoolean(IS_ADD, true);
        intent.putExtras(bundle);
        startActivityForResult(intent, REQUEST_OK);
    }

    /**
     * 按名字排序
     */
    private void sortByName() {
        count_userName++;
        mUserDataViewModel.getAllUserData().observe(this, messageBeans -> {
            if (count_userName % 2 == 0) {
                //降序排序
                Collections.sort(messageBeans, new MyCompareUtil(MyCompareUtil.SORT_DES, MyCompareUtil.COMPARE_NAME));
            } else {
                //升序排序
                Collections.sort(messageBeans, new MyCompareUtil(MyCompareUtil.SORT_ASC, MyCompareUtil.COMPARE_NAME));
            }
            msgAdapter.submitList(messageBeans);
        });
        msgAdapter.notifyDataSetChanged();
    }

    /**
     * 按时间排序
     */
    private void sortByDate() {
        count_lastDate++;
        mUserDataViewModel.getAllUserData().observe(this, messageBeans -> {
            if (count_lastDate % 2 == 0) {
                //降序排序
                Collections.sort(messageBeans, new MyCompareUtil(MyCompareUtil.SORT_DES, MyCompareUtil.COMPARE_DATE));
            } else {
                //升序排序
                Collections.sort(messageBeans, new MyCompareUtil(MyCompareUtil.SORT_ASC, MyCompareUtil.COMPARE_DATE));
            }
            msgAdapter.submitList(messageBeans);
        });
        msgAdapter.notifyDataSetChanged();
    }


    /**
     * 向数据库插入数据
     */
    private void insertUserData() {
        MessageBean bean = getMessageBean();
        //插入数据
        mUserDataViewModel.insert(bean);
        msgAdapter.notifyDataSetChanged();
    }

    /**
     * 更新数据
     */
    private void updateUserData() {
        MessageBean bean = getMessageBean();
        int i = mUserDataViewModel.updateData(bean);
        //Log.e("updateData==result===", i + "");
        msgAdapter.notifyDataSetChanged();
    }

    /**
     * 获取保存之后的bean实体数据
     *
     * @return
     */
    private MessageBean getMessageBean() {
        String[] keys = {"车牌号：", "投保人：", "终保时间：", "承保时间：", "车架号：", "手机号：", "商业险费用：",
                "交强险费用：", "驾乘险费用：", "商业险费率：", "交强险费率：", "驾乘险费率：", "返现：", "客户来源：", "备注："};

        MessageBean bean = new MessageBean();

        bean.setCarNumber(hashMap.get(keys[0]));
        bean.setUserName(hashMap.get(keys[1]));
        bean.setLastDate(hashMap.get(keys[2]));
        // Log.e("hashMap.get(keys[2])", hashMap.get(keys[2]));
        bean.setBuyTime(hashMap.get(keys[3]));
        bean.setCarSerialNumber(hashMap.get(keys[4]));
        bean.setPhone(hashMap.get(keys[5]));
        if (TextUtils.isEmpty(hashMap.get(keys[6]))) {
            bean.setSyPrice(0.0);
        } else {
            bean.setSyPrice(Double.parseDouble(hashMap.get(keys[6])));
        }
        if (TextUtils.isEmpty(hashMap.get(keys[7]))) {
            bean.setJqPrice(0.0);
        } else {
            bean.setJqPrice(Double.parseDouble(hashMap.get(keys[7])));
        }
        if (TextUtils.isEmpty(hashMap.get(keys[8]))) {
            bean.setJcPrice(0.0);
        } else {
            bean.setJcPrice(Double.parseDouble(hashMap.get(keys[8])));
        }
        if (TextUtils.isEmpty(hashMap.get(keys[9]))) {
            bean.setJcPrice(0.0);
        } else {
            bean.setSyRebate(Double.parseDouble(hashMap.get(keys[9])));
        }
        if (TextUtils.isEmpty(hashMap.get(keys[10]))) {
            bean.setJqRebate(0.0);
        } else {
            bean.setJqRebate(Double.parseDouble(hashMap.get(keys[10])));
        }
        if (TextUtils.isEmpty(hashMap.get(keys[11]))) {
            bean.setJqRebate(0.0);
        } else {
            bean.setJcRebate(Double.parseDouble(hashMap.get(keys[11])));
        }
        if (TextUtils.isEmpty(hashMap.get(keys[12]))) {
            bean.setCashBack(0.0);
        } else {
            bean.setCashBack(Double.parseDouble(hashMap.get(keys[12])));
        }
        bean.setType(hashMap.get(keys[13]));
        bean.setRemarks(hashMap.get(keys[14]));
        bean.setId(Integer.valueOf(hashMap.get("id")));
        return bean;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == REQUEST_OK && resultCode == RESULT_SET_OK) {
            //获取需要添加的数据
            hashMap = (HashMap<String, String>) data.getSerializableExtra(SAVE_DATA);
            insertUserData();
        } else if (requestCode == MODIFY_REQUEST && resultCode == RESULT_SET_OK) {
            //获取需要修改的数据
            hashMap = (HashMap<String, String>) data.getSerializableExtra(SAVE_DATA);
            updateUserData();//更新数据
        }
        // Log.d(TAG, "onActivityResult====hashMap====" + hashMap);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case 1://修改按钮
                Intent intent = new Intent(this, AddUserActivity.class);
                MessageBean bean = item.getIntent().getParcelableExtra(USER_BEAN);
                Bundle bundle = new Bundle();
                bundle.putBoolean(IS_ADD, false);
                bundle.putParcelable(USER_BEAN, bean);
                intent.putExtras(bundle);
                startActivityForResult(intent, MODIFY_REQUEST);
                break;
            case 2://删除按钮
                String userName = item.getIntent().getStringExtra(USER_NAME);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle("警告").setMessage("数据不可恢复，确定要删除吗？").setPositiveButton("确定", (dialog, which) -> {
                    mUserDataViewModel.delByName(userName);
                    Toast.makeText(MainActivity.this, "删除成功", Toast.LENGTH_LONG).show();
                }).setNegativeButton("取消", null).show();
                break;
        }
        return true;
    }

    /**
     * 提醒已有客户到续保时间
     */
    private void notificeCanBuy() {

        //获取当前的日期
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        //今天的日期几号
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        //29天即可续保几号
        calendar.set(Calendar.DAY_OF_MONTH, day + 29);
        //29天后的日期
        int dayAfter29 = calendar.get(Calendar.DATE);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        String canBuyDate = year + "/" + month + "/" + dayAfter29;

        //开始查数据
        List<MessageBean> lastDateUsers = mUserDataViewModel.getLastDateUsers(canBuyDate);
        showCanBuyNotification(lastDateUsers);
    }

    /**
     * 创建续保通知消息
     */
    private void showCanBuyNotification(List<MessageBean> lastDateUsers) {
        if (lastDateUsers.size() == 0) {
            return;
        }
        Notification notification;
        int notificeid = 1;//通知消息id
        int SUMMARY_ID = 0;//通知消息组id
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        int size = lastDateUsers.size();
        for (int i = 0; i < size; i++) {
            //点击通知消息跳转到详细信息页面
            Intent intent = new Intent(this, DetailedActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            //传递数据
            intent.putExtra(MESSAGE_BEAN, lastDateUsers.get(i));
            //i表示传递数据id，PendingIntent.FLAG_UPDATE_CURRENT表示更新当前的数据
            PendingIntent pendingIntent = PendingIntent.getActivity(this, i, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            //创建多个通知
            notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic__notifications)
                    .setContentTitle("我的承保清单")
                    .setContentText("客户" + lastDateUsers.get(i).getUserName() + "可以续保了，请及时跟进！")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setGroup(GROUP_KEY_USER_NOTIFICE)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build();
            notificationManagerCompat.notify(notificeid + i, notification);

        }
        //创建消息组
        Notification summaryNotification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentText("没有更多消息了")
                .setSmallIcon(R.drawable.ic__notifications)
                .setGroup(GROUP_KEY_USER_NOTIFICE)
                .setGroupSummary(true)
                .build();

        notificationManagerCompat.notify(SUMMARY_ID, summaryNotification);
    }

    @Override
    public void onBackPressed() {
        //关闭搜索框
        if (mSearchBar.isShown()) {
            mSearchBar.setVisibility(View.INVISIBLE);
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        count_lastDate = 0;
        count_userName = 0;
        if (hashMap != null) {
            hashMap = null;
        }
        if (mUserDataViewModel != null) {
            mUserDataViewModel = null;
        }
        if (msgAdapter != null) {
            msgAdapter = null;
        }
    }
}
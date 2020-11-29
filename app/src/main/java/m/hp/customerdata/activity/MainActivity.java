package m.hp.customerdata.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import m.hp.customerdata.R;
import m.hp.customerdata.adapter.MessageBeanListAdapter;
import m.hp.customerdata.databinding.ActivityMainBinding;
import m.hp.customerdata.entity.UserJsonRoot;
import m.hp.customerdata.entity.UsersDataBean;
import m.hp.customerdata.http.ConnectMyServer;
import m.hp.customerdata.model.UserDataViewModel;
import m.hp.customerdata.poiexcel.ExportUsersDateExcel;
import m.hp.customerdata.poiexcel.ReadExcelByPOI;
import m.hp.customerdata.utils.MyCompareUtil;
import m.hp.customerdata.utils.RealPathFromUriUtils;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    //一组通知消息
    private static final String GROUP_KEY_USER_NOTIFICE = "m.hp.customerdata";
    private static final int REQUEST_OK = 300;
    //选择保存路径完成结果码
    private static final int RESULT_SAVE_PATH = 600;
    private static final int RESULT_SET_OK = 1000;
    private static final int MODIFY_REQUEST = 400;
    //是新加数据还是更新数据
    private static final String IS_ADD = "IS_ADD";
    private static final String CHANNEL_ID = "800";
    //已选择的保存路径
    private static final String SAVE_PATH = "SAVE_PATH";
    //权限请求码
    private static final int PERMISSION_REQUEST = 100;
    //打开文件管理器请求码
    private static final int OPEN_FILE_REQUEST = 200;
    //打开选择保存目录
    private static final int REQUEST_SHOW_DIRS = 500;
    private MessageBeanListAdapter msgAdapter;
    private UserDataViewModel mUserDataViewModel;
    private static final String SAVE_DATA = "SAVE_DATA";
    private HashMap<String, String> hashMap = new HashMap<>();
    private static final String USER_BEAN = "USER_BEAN";
    //点击投保人标题次数
    private int count_userName;
    //点击到期时间标题次数
    private int count_lastDate;
    //当前数据库数据集合
    private List<UsersDataBean> usersDataBeanList;
    public static MainActivity instance;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setCustomActionBar();
        initView();
        //检查是否获取到需要的权限
        requestPermissions();
        instance = this;
    }

    /**
     * 自定义ActionBar
     */
    private void setCustomActionBar() {
        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        @SuppressLint("InflateParams") View actionBarView = LayoutInflater.from(this).inflate(R.layout.customacitonbar_layout, null);
        TextView tvTitle = actionBarView.findViewById(R.id.actionBarTile);
        ImageView ivBack = actionBarView.findViewById(R.id.ivBack);
        ivBack.setVisibility(View.GONE);
        tvTitle.setText("承保清单列表");
        ActionBar supportActionBar = getSupportActionBar();
        assert supportActionBar != null;
        supportActionBar.setCustomView(actionBarView, layoutParams);
        supportActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        supportActionBar.setDisplayShowHomeEnabled(true);
        supportActionBar.setDisplayShowTitleEnabled(false);
        Toolbar parent = (Toolbar) actionBarView.getParent();
        //去两边空白
        parent.setPadding(0, 0, 0, 0);
        parent.setContentInsetsAbsolute(0, 0);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        //全选标题
        binding.titles.checkAll.setOnClickListener(v -> {
            if (binding.titles.checkAll.getText().toString().equals("全选")) {
                msgAdapter.checkAll(true);
                binding.titles.checkAll.setText("不选");
            } else {
                msgAdapter.checkAll(false);
                binding.titles.checkAll.setText("全选");
            }
        });
        //到期时间标题
        binding.titles.lastDate.setOnClickListener(v -> {
            //按时间排序功能
            sortByDate();
        });
        //投保人标题
        binding.titles.userName.setOnClickListener(v -> {
            //按名字排序功能
            sortByName();
        });
        //获取ViewModel
        ViewModelProvider.AndroidViewModelFactory androidViewModelFactory = new ViewModelProvider.AndroidViewModelFactory(getApplication());
        mUserDataViewModel = new ViewModelProvider(this, androidViewModelFactory).get(UserDataViewModel.class);
        //创建RecycleView的适配器
        msgAdapter = new MessageBeanListAdapter(new MessageBeanListAdapter.MessageBeanDiff(), this);
        binding.rvMsg.setAdapter(msgAdapter);
        binding.rvMsg.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        //设置RecycleView的布局
        binding.rvMsg.setLayoutManager(new LinearLayoutManager(this));
        //监听FloatingActionButton点击
        binding.fabSearch.setOnClickListener(v -> {
            //搜索功能
            showSearchBar();
        });
        binding.fabAdd.setOnClickListener(v -> {
            //添加数据功能
            starAddUserActivity();
        });
        binding.fabDelete.setOnClickListener(v -> {
            //删除数据功能
            deleteUserByFab();
        });
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
            //传递当前查询到的数据给usersDataBeanList做数据重复校验
            usersDataBeanList = messageBeans;
            msgAdapter.submitList(messageBeans);
        });
        msgAdapter.notifyDataSetChanged();
    }

    /**
     * 通过名字删除
     */
    private void deleteUserByFab() {

        new AlertDialog.Builder(this)
                .setTitle("警告")
                .setMessage("数据不可恢复，确定要删除吗？")
                .setPositiveButton("确定", (dialog, which) -> {
                    List<UsersDataBean> checkedUsers = msgAdapter.getCheckedUsers();
                    for (int i = 0; i < checkedUsers.size(); i++) {
                        mUserDataViewModel.delByName(checkedUsers.get(i).getUserName());
                    }
                    Toast.makeText(MainActivity.this, "删除成功", Toast.LENGTH_LONG).show();
                    setCheckOptViewGone();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    /**
     * 按名字搜索相关数据
     */
    private void showSearchBar() {
        //跳转到搜索页面
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
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
                messageBeans.sort(new MyCompareUtil(MyCompareUtil.SORT_DES, MyCompareUtil.COMPARE_NAME));
            } else {
                //升序排序
                messageBeans.sort(new MyCompareUtil(MyCompareUtil.SORT_ASC, MyCompareUtil.COMPARE_NAME));
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
                messageBeans.sort(new MyCompareUtil(MyCompareUtil.SORT_DES, MyCompareUtil.COMPARE_DATE));
            } else {
                //升序排序
                messageBeans.sort(new MyCompareUtil(MyCompareUtil.SORT_ASC, MyCompareUtil.COMPARE_DATE));
            }
            msgAdapter.submitList(messageBeans);
        });
        msgAdapter.notifyDataSetChanged();
    }


    /**
     * 向数据库插入数据
     */
    private void insertUserData(boolean isAdd) {
        UsersDataBean bean = getMessageBean(isAdd);
        //插入数据
        mUserDataViewModel.insert(bean);
        msgAdapter.notifyDataSetChanged();
        new android.app.AlertDialog.Builder(this)
                .setTitle("")
                .setPositiveButton("确定", (dialog, which) -> {

                })
                .setMessage("添加成功")
                .show();
    }

    public boolean isTheSame(String userName) {
        List<UsersDataBean> usersDataBeanList = this.usersDataBeanList;
        for (int i = 0; i < usersDataBeanList.size(); i++) {
            if (usersDataBeanList.get(i).getUserName().equals(userName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 更新数据
     */
    private void updateUserData(boolean isAdd) {
        UsersDataBean bean = getMessageBean(isAdd);

        mUserDataViewModel.updateData(bean);
        new android.app.AlertDialog.Builder(this)
                .setTitle("")
                .setPositiveButton("确定", (dialog, which) -> {

                })
                .setMessage("保存成功")
                .show();
        //Log.e("updateData==result===", i + "");
        msgAdapter.notifyDataSetChanged();
    }

    /**
     * 获取保存之后的bean实体数据
     *
     * @return 返回是添加数据，还是更新数据返回的数据
     */
    private UsersDataBean getMessageBean(boolean isAdd) {
        String[] keys = {"车牌号", "投保人", "终保时间", "承保时间", "车架号", "手机号", "商业险费用",
                "交强险费用", "驾乘险费用", "商业险费率", "交强险费率", "驾乘险费率", "返现", "客户来源", "备注"};

        UsersDataBean bean = new UsersDataBean();

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
            bean.setSyPrice(Double.parseDouble(Objects.requireNonNull(hashMap.get(keys[6]))));
        }
        if (TextUtils.isEmpty(hashMap.get(keys[7]))) {
            bean.setJqPrice(0.0);
        } else {
            bean.setJqPrice(Double.parseDouble(Objects.requireNonNull(hashMap.get(keys[7]))));
        }
        if (TextUtils.isEmpty(hashMap.get(keys[8]))) {
            bean.setJcPrice(0.0);
        } else {
            bean.setJcPrice(Double.parseDouble(Objects.requireNonNull(hashMap.get(keys[8]))));
        }
        if (TextUtils.isEmpty(hashMap.get(keys[9]))) {
            bean.setJcPrice(0.0);
        } else {
            bean.setSyRebate(Double.parseDouble(Objects.requireNonNull(hashMap.get(keys[9]))));
        }
        if (TextUtils.isEmpty(hashMap.get(keys[10]))) {
            bean.setJqRebate(0.0);
        } else {
            bean.setJqRebate(Double.parseDouble(Objects.requireNonNull(hashMap.get(keys[10]))));
        }
        if (TextUtils.isEmpty(hashMap.get(keys[11]))) {
            bean.setJqRebate(0.0);
        } else {
            bean.setJcRebate(Double.parseDouble(Objects.requireNonNull(hashMap.get(keys[11]))));
        }
        if (TextUtils.isEmpty(hashMap.get(keys[12]))) {
            bean.setCashBack(0.0);
        } else {
            bean.setCashBack(Double.parseDouble(Objects.requireNonNull(hashMap.get(keys[12]))));
        }
        bean.setType(hashMap.get(keys[13]));
        bean.setRemarks(hashMap.get(keys[14]));
        if (!isAdd) {
            bean.setId(Integer.parseInt(Objects.requireNonNull(hashMap.get("id"))));
        }
        return bean;
    }

    /**
     * 启动Activity之后返回的结果处理
     *
     * @param requestCode 请求跳转的结果码
     * @param resultCode  跳转activity之后返回的结果码
     * @param data        activity传递的intent数据
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == REQUEST_OK && resultCode == RESULT_SET_OK) {
            //获取需要添加的数据
            assert data != null;
            hashMap = (HashMap<String, String>) data.getSerializableExtra(SAVE_DATA);
            boolean isAdd = data.getBooleanExtra(IS_ADD, true);
            insertUserData(isAdd);
        } else if (requestCode == MODIFY_REQUEST && resultCode == RESULT_SET_OK) {
            //获取需要修改的数据
            assert data != null;
            hashMap = (HashMap<String, String>) data.getSerializableExtra(SAVE_DATA);
            boolean isAdd = data.getBooleanExtra(IS_ADD, true);
            updateUserData(isAdd);//更新数据
        } else if (requestCode == OPEN_FILE_REQUEST) {
            //打开文件管理器选择Excel
            if (data == null) {
                return;
            }
            Uri excelUri = data.getData();
            String path = RealPathFromUriUtils.getRealPathFromUri(this, excelUri);
            ;
            Log.e(TAG, "realPath==" + path);
            addUserFromExcel(path);
        } else if (requestCode == REQUEST_SHOW_DIRS && resultCode == RESULT_SAVE_PATH) {
            //导出Excel表格
            assert data != null;//断言，data不能为空
            String saveDir = data.getStringExtra(SAVE_PATH);
            mUserDataViewModel.getAllUserData().observe(this, messageBeans -> new ExportUsersDateExcel(messageBeans, (ok, outputPath) -> {
                if (ok) {
                    Looper.prepare();//加上避免报错：Can't toast on a thread that has not called Looper.prepare()
                    new AlertDialog.Builder(this)
                            .setTitle("导出成功").setMessage("已保存至：" + outputPath)
                            .setPositiveButton("确定", null)
                            .show();
                    Looper.loop();
                } else {
                    Looper.prepare();
                    Toast.makeText(MainActivity.this, "导出失败", Toast.LENGTH_LONG).show();
                    Looper.loop();
                }
            }, saveDir));
        }

    }

    /**
     * 上下文菜单点击监听回调
     *
     * @param item 菜单控件
     * @return 自己处理上下文菜单
     */
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        String USER_NAME = "USER_NAME";
        switch (item.getItemId()) {
            case 1://修改菜单
                Intent intent = new Intent(this, AddUserActivity.class);
                UsersDataBean bean = (UsersDataBean) item.getIntent().getSerializableExtra(USER_BEAN);
                Bundle bundle = new Bundle();
                bundle.putBoolean(IS_ADD, false);
                bundle.putSerializable(USER_BEAN, bean);
                intent.putExtras(bundle);
                startActivityForResult(intent, MODIFY_REQUEST);
                break;
            case 2://删除菜单
                String userName = item.getIntent().getStringExtra(USER_NAME);
                deleteUserByName(userName);
                break;
            case 3://编辑菜单
                binding.titles.checkAll.setVisibility(View.VISIBLE);
                msgAdapter.setCheckBoxIsVisible(true);
                binding.fabDelete.setVisibility(View.VISIBLE);
                msgAdapter.notifyDataSetChanged();
                break;
        }
        return true;
    }

    /**
     * 通过名字删除
     *
     * @param userName 投保人姓名
     */
    private void deleteUserByName(String userName) {
        new AlertDialog.Builder(this)
                .setTitle("警告")
                .setMessage("数据不可恢复，确定要删除吗？")
                .setPositiveButton("确定", (dialog, which) -> {
                    mUserDataViewModel.delByName(userName);
                    Toast.makeText(MainActivity.this, "删除成功", Toast.LENGTH_LONG).show();
                })
                .setNegativeButton("取消", null)
                .show();
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
        List<UsersDataBean> lastDateUsers = mUserDataViewModel.getLastDateUsers(canBuyDate);
        showCanBuyNotification(lastDateUsers);
    }

    /**
     * 创建续保通知消息
     */
    private void showCanBuyNotification(List<UsersDataBean> lastDateUsers) {
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
            //根据条件查询到的userBean数据
            String MESSAGE_BEAN = "MESSAGE_BEAN";
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
        if (binding != null) {
            binding = null;
        }
    }

    /**
     * 申请权限
     */
    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST);
        }
    }

    /**
     * 权限申请处理结果
     *
     * @param requestCode  申请权限的请求码
     * @param permissions  申请的权限集合
     * @param grantResults 申请权限的结果集合
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST) {//判断用户是否同意了全部权限
            int length = permissions.length;
            //假设全部同意
            if (length == grantResults.length) {
                for (int i = 0; i < length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        break;
                    }
                }
            }
        }
    }

    /**
     * 从Excel文件添加用户数据
     */
    private void addUserFromExcel(String filePath) {


        new ReadExcelByPOI(filePath, usersDataBeanList -> {
            //导入成功数据的条数
            int successCount = 0;
            //总共数据数量
            int total = usersDataBeanList.size();
            //重复判断
            for (int i = 0; i < total; i++) {
                //判断当前导入的Excel数据的投保人是否重复
                boolean theSame = isTheSame(usersDataBeanList.get(i).getUserName());
                if (!theSame) {//不重复
                    //添加用户数据到数据库
                    mUserDataViewModel.insert(usersDataBeanList.get(i));
                    successCount++;
                }
            }
            //解决Can't create handler inside thread Thread[Thread-3,5,main] that has not called Looper.prepare()
            Looper.prepare();
            //提示导入Excel结果
            new AlertDialog.Builder(this)
                    .setTitle("导入完成").setMessage("成功导入" + successCount + "条信息，" + (total - successCount) + "条信息重复未导入！")
                    .setPositiveButton("确定", null)
                    .show();
            Looper.loop();
        });
        msgAdapter.notifyDataSetChanged();
    }

    /**
     * 创建右上角菜单按钮
     *
     * @param menu 创建的Toolbar菜单
     * @return 成功创建菜单让上下文处理
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 右上角菜单点击监听
     *
     * @param item 菜单item
     * @return 菜单被点击了
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            //从服务器下载数据
            case R.id.downloadServer:
                downloadServer();
                break;
            //上传数据到服务器
            case R.id.uploadServer:
                uploadServer();
                break;
            //导入Excel数据
            case R.id.addFromExcel:
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("*/*");
                String[] mimeTypes = {"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "application/vnd.ms-excel"};
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, OPEN_FILE_REQUEST);
                break;
            //导出Excel数据
            case R.id.exportExcel:
                Intent intentToShowDir = new Intent(this, ShowDirectoryActivity.class);
                startActivityForResult(intentToShowDir, REQUEST_SHOW_DIRS);
                break;
        }
        return true;
    }

    /**
     * 从服务器下载数据
     */
    private void downloadServer() {

        new Thread(() -> {
            ConnectMyServer connectMyServer = new ConnectMyServer();
            //发送json请求数据给服务器
            UserJsonRoot userJsonRoot = new UserJsonRoot();
            userJsonRoot.setOption("findAll");
            userJsonRoot.setUserList(null);
            String jsonString = JSONArray.toJSONString(userJsonRoot);
            //获取服务器返回的json数据
            String getData = connectMyServer.postData(jsonString);

            Log.d("getData", "服务器返回的数据----" + getData);
            if (TextUtils.isEmpty(getData)) {
                return;
            }
            //解析json数据
            JSONObject jsonObject = JSON.parseObject(getData);
            JSONArray jsonArray = jsonObject.getJSONArray("userList");

            boolean thSame = false;
            List<UsersDataBean> userList = new ArrayList<>();
            userList.clear();
            for (int i = 0; i < jsonArray.size(); i++) {
                String buyTime = jsonArray.getJSONObject(i).getString("buyTime");
                String carNumber = jsonArray.getJSONObject(i).getString("carNumber");
                String carSerialNumber = jsonArray.getJSONObject(i).getString("carSerialNumber");
                String lastDate = jsonArray.getJSONObject(i).getString("lastDate");
                String type = jsonArray.getJSONObject(i).getString("type");
                String userName = jsonArray.getJSONObject(i).getString("userName");
                String phone = jsonArray.getJSONObject(i).getString("phone");
                String remark = jsonArray.getJSONObject(i).getString("remarks");
                Double cashBack = jsonArray.getJSONObject(i).getDouble("cashBack");
                Double jcPrice = jsonArray.getJSONObject(i).getDouble("jcPrice");
                Double jcRebate = jsonArray.getJSONObject(i).getDouble("jcRebate");
                Double jqPrice = jsonArray.getJSONObject(i).getDouble("jqPrice");
                Double jqRebate = jsonArray.getJSONObject(i).getDouble("jqRebate");
                Double syPrice = jsonArray.getJSONObject(i).getDouble("syPrice");
                Double syRebate = jsonArray.getJSONObject(i).getDouble("syRebate");

                for (int j = 0; j < usersDataBeanList.size(); j++) {
                    if (carSerialNumber.equals(usersDataBeanList.get(j).getCarSerialNumber())) {
                        thSame = true;
                        break;
                    } else {
                        thSame = false;
                    }
                }
                if (!thSame) {
                    UsersDataBean user = new UsersDataBean(carNumber, userName, lastDate, buyTime, carSerialNumber, phone, syPrice, jqPrice, jcPrice, syRebate, jqRebate, jcRebate, cashBack, type, remark);
                    userList.add(user);
                }

            }
            int count = 0;
            for (int i = 0; i < userList.size(); i++) {
                mUserDataViewModel.insert(userList.get(i));
                count++;
            }
            Looper.prepare();
            if (count == 0) {
                new AlertDialog.Builder(this).setMessage("没有发现新数据").setPositiveButton("确定", null).show();

            } else {
                new AlertDialog.Builder(this).setMessage("更新了" + count + "条数据").setPositiveButton("确定", null).show();
            }
            Looper.loop();
            runOnUiThread(() -> msgAdapter.notifyDataSetChanged());

        }).start();
    }

    /**
     * 上传数据到服务器
     */
    private void uploadServer() {
        new Thread(() -> {
            ConnectMyServer connectMyServer = new ConnectMyServer();
            UserJsonRoot userJsonRoot = new UserJsonRoot();
            userJsonRoot.setOption("insert");
            userJsonRoot.setUserList(usersDataBeanList);
            String jsonString = JSONArray.toJSONString(userJsonRoot);
            String getData = connectMyServer.postData(jsonString);

            Log.d("getData", "服务器返回的数据----" + getData);
            // Log.d("jsonString", "jsonString----" + jsonString);
            Looper.prepare();
            new AlertDialog.Builder(this).setMessage(getData).setPositiveButton("确定", null).show();
            Looper.loop();
        }).start();
    }

    @Override
    public void onBackPressed() {
        if (binding.titles.checkAll.getVisibility() == View.VISIBLE) {
            //按返回之后选择操作恢复初始状态
            setCheckOptViewGone();
            return;
        }
        super.onBackPressed();
    }

    /**
     * 设置编辑操作view不可见
     */
    private void setCheckOptViewGone() {
        binding.titles.checkAll.setVisibility(View.GONE);
        binding.fabDelete.setVisibility(View.GONE);
        msgAdapter.setCheckBoxIsVisible(false);
        msgAdapter.checkAll(false);
        binding.titles.checkAll.setText("全选");
        msgAdapter.notifyDataSetChanged();
    }
}
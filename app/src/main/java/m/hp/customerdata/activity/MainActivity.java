package m.hp.customerdata.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;
import java.util.List;

import m.hp.customerdata.R;
import m.hp.customerdata.adapter.MessageBeanListAdapter;
import m.hp.customerdata.entity.MessageBean;
import m.hp.customerdata.model.UserDataViewModel;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_OK = 300;
    private static final int RESULT_SET_OK = 1000;
    private static final String USER_DATA = "USER_DATA";
    private static final int MODIFY_REQUEST = 400;
    //是新加数据还是更新数据
    private static final String IS_ADD = "IS_ADD";
    /* //更新以及要传递的数据Bundle
     private static final String MODIFY_BUNDLE = "MODIFY_BUNDLE";*/
    /*//新增以及要接收保存的数据标记
    private static final String ADD_BUNDLE = "ADD_BUNDLE";*/
    private MessageBeanListAdapter msgAdapter;
    private RecyclerView rv_msg;
    private UserDataViewModel mUserDataViewModel;
    private static final String SAVE_DATA = "SAVE_DATA";
    private HashMap<String, String> hashMap = new HashMap<String, String>();
    private final String USER_NAME = "USER_NAME";
    private static final String USER_BEAN = "USER_BEAN";

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
        FloatingActionButton fabt = findViewById(R.id.fab_add);
        //监听FloatingActionButton点击
        fabt.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mUserDataViewModel.getAllUserData().observe(this, messageBeans -> {
            msgAdapter.submitList(messageBeans);
           /*
            Log.d(TAG, "observe===" + messageBeans.size()+"|count==="+OBSERVE_COUNT);
            for (int i = 0; i < messageBeans.size(); i++) {
                MessageBean getBean = new MessageBean();
                //投保人
                getBean.setUserName(messageBeans.get(i).getUserName());
                //序号
                getBean.setSerialNumber(messageBeans.get(i).getSerialNumber());
                //车牌
                getBean.setCarNumber(messageBeans.get(i).getCarNumber());
                //到期时间
                getBean.setLastDate(messageBeans.get(i).getLastDate());
                //承保时间
                getBean.setBuyTime(messageBeans.get(i).getBuyTime());
                //车架号
                getBean.setCarSerialNumber(messageBeans.get(i).getCarSerialNumber());
                //手机号
                getBean.setPhone(messageBeans.get(i).getPhone());
                //商业险保费
                getBean.setSyPrice(messageBeans.get(i).getSyPrice());
                //交强险保费
                getBean.setJqPrice(messageBeans.get(i).getJqPrice());
                //驾乘险保费
                getBean.setJcPrice(messageBeans.get(i).getJcPrice());
                //商业险费率
                getBean.setSyRebate(messageBeans.get(i).getSyRebate());
                //交强险费率
                getBean.setJqRebate(messageBeans.get(i).getJqRebate());
                //驾乘险费率
                getBean.setJcRebate(messageBeans.get(i).getJcRebate());
                //返现
                getBean.setCashBack(messageBeans.get(i).getCashBack());
                //客户来源
                getBean.setType(messageBeans.get(i).getType());
                //备注
                getBean.setRemarks(messageBeans.get(i).getRemarks());
                mList.add(getBean);
            }*/
        });
        msgAdapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, AddUserActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(SAVE_DATA, "SAVE_DATA");
        bundle.putBoolean(IS_ADD, true);
        intent.putExtras(bundle);
        startActivityForResult(intent, REQUEST_OK);
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
        mUserDataViewModel.updaterByName(bean);
//        mUserDataViewModel.getAllUserData().observe(this, new Observer<List<MessageBean>>() {
//            @Override
//            public void onChanged(List<MessageBean> beans) {
//                msgAdapter.submitList(beans);
//                msgAdapter.notifyDataSetChanged();
//            }
//        });

    }


    /**
     * 获取保存之后的bean实体数据
     *
     * @return
     */
    private MessageBean getMessageBean() {
        String[] keys = {"序号：", "车牌号：", "投保人：", "终保时间：", "承保时间：", "车架号：", "手机号：", "商业险费用：",
                "交强险费用：", "驾乘险费用：", "商业险费率：", "交强险费率：", "驾乘险费率：", "返现：", "客户来源：", "备注："};

        MessageBean bean = new MessageBean();
        if (TextUtils.isEmpty(hashMap.get(keys[0]))) {
            bean.setSerialNumber(0);
        } else {
            bean.setSerialNumber(Integer.parseInt(hashMap.get(keys[0])));
        }
        bean.setCarNumber(hashMap.get(keys[1]));
        bean.setUserName(hashMap.get(keys[2]));
        bean.setLastDate(hashMap.get(keys[3]));
        bean.setBuyTime(hashMap.get(keys[4]));
        bean.setCarSerialNumber(hashMap.get(keys[5]));
        bean.setPhone(hashMap.get(keys[6]));
        if (TextUtils.isEmpty(hashMap.get(keys[7]))) {
            bean.setSyPrice(0.0);
        } else {
            bean.setSyPrice(Double.parseDouble(hashMap.get(keys[7])));
        }
        if (TextUtils.isEmpty(hashMap.get(keys[8]))) {
            bean.setJqPrice(0.0);
        } else {
            bean.setJqPrice(Double.parseDouble(hashMap.get(keys[8])));
        }
        if (TextUtils.isEmpty(hashMap.get(keys[9]))) {
            bean.setJcPrice(0.0);
        } else {
            bean.setJcPrice(Double.parseDouble(hashMap.get(keys[9])));
        }
        if (TextUtils.isEmpty(hashMap.get(keys[10]))) {
            bean.setJcPrice(0.0);
        } else {
            bean.setSyRebate(Double.parseDouble(hashMap.get(keys[10])));
        }
        if (TextUtils.isEmpty(hashMap.get(keys[11]))) {
            bean.setJqRebate(0.0);
        } else {
            bean.setJqRebate(Double.parseDouble(hashMap.get(keys[11])));
        }
        if (TextUtils.isEmpty(hashMap.get(keys[12]))) {
            bean.setJqRebate(0.0);
        } else {
            bean.setJcRebate(Double.parseDouble(hashMap.get(keys[12])));
        }
        if (TextUtils.isEmpty(hashMap.get(keys[13]))) {
            bean.setCashBack(0.0);
        } else {
            bean.setCashBack(Double.parseDouble(hashMap.get(keys[13])));
        }
        bean.setType(hashMap.get(keys[14]));
        bean.setRemarks(hashMap.get(keys[15]));
        return bean;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "RESULT_OK====" + hashMap + "|data====" + data);
        if (requestCode == REQUEST_OK && resultCode == RESULT_SET_OK) {
            hashMap = (HashMap<String, String>) data.getSerializableExtra(SAVE_DATA);
            insertUserData();
        } else if (requestCode == MODIFY_REQUEST && resultCode == RESULT_SET_OK) {
            hashMap = (HashMap<String, String>) data.getSerializableExtra(SAVE_DATA);
            Log.e(TAG, "get");
            updateUserData();//更新数据
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                Log.d(TAG, "item====1");
                Intent intent = new Intent(this, AddUserActivity.class);
                MessageBean bean = item.getIntent().getParcelableExtra(USER_BEAN);
                Bundle bundle = new Bundle();
                bundle.putBoolean(IS_ADD, false);
                bundle.putParcelable(USER_BEAN, bean);
                intent.putExtras(bundle);
                startActivityForResult(intent, MODIFY_REQUEST);
                break;
            case 2:
                String userName = item.getIntent().getStringExtra(USER_NAME);
                Log.d(TAG, "item====2|" + userName);
                new AlertDialog.Builder(this).setTitle("警告").setMessage("数据不可恢复，确定要删除吗？").setPositiveButton("确定", (dialog, which) -> {
                    mUserDataViewModel.delByName(userName);
                    Toast.makeText(MainActivity.this, "删除成功", Toast.LENGTH_LONG).show();
                }).setNegativeButton("取消", null).show();
                break;
        }
        return true;
    }
}
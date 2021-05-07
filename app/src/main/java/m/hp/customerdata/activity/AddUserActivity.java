package m.hp.customerdata.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import m.hp.customerdata.R;
import m.hp.customerdata.adapter.AddUserAdapter;
import m.hp.customerdata.databinding.ActivityAddUserBinding;
import m.hp.customerdata.entity.DetailedMsgBean;
import m.hp.customerdata.entity.UsersDataBean;
import m.hp.customerdata.myevents.SendCarSerialNumber;
import m.hp.customerdata.myevents.SendIsSame;
import m.hp.customerdata.utils.Constant;
import m.hp.customerdata.utils.DateFormatUtil;

/**
 * @author huangping
 */
public class AddUserActivity extends AppCompatActivity {

    private static final String SAVE_DATA = "SAVE_DATA";
    private static final int RESULT_SET_OK = 1000;
    /**
     * 添加还是更新数据的标识
     */
    private static final String IS_ADD = "IS_ADD";
    private AddUserAdapter adapter;
    private List<DetailedMsgBean> mList;
    /**
     * 是新加数据还是更新数据
     */
    public boolean isAdd;
    /**
     * MainActivity传递过来的数据
     */
    private static final String USER_BEAN = "USER_BEAN";
    private Intent intent;
    private UsersDataBean intentBean = null;
    private ActivityAddUserBinding binding;
    private boolean isSame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    /**
     * 自定义ActionBar
     */
    private void setCustomActionBar(String title) {
        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        @SuppressLint("InflateParams") View actionBarView = LayoutInflater.from(this).inflate(R.layout.customacitonbar_layout, null);
        TextView tvTitle = actionBarView.findViewById(R.id.actionBarTile);
        //返回操作
        ImageView ivBack = actionBarView.findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> finish());
        tvTitle.setText(title);
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

    private void initView() {
        binding.saveData.setOnClickListener(v -> {
            if (!saveData()) {
                return;
            }
            finish();
        });
        mList = new ArrayList<>();
        adapter = new AddUserAdapter(this, mList);
        binding.rvAdd.setAdapter(adapter);
        binding.rvAdd.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        //获取传进来的intent
        intent = getIntent();
        //判断是新增数据还是修改数据
        isAdd = intent.getBooleanExtra(IS_ADD, true);
        if (isAdd) {
            setCustomActionBar("添加用户数据");
        } else {
            setCustomActionBar("修改用户数据");
        }
        initData();
    }

    private void initData() {

        String[] columnValue;
        if (isAdd) {
            columnValue = new String[]{"请输入车牌号", "请输入投保人", "请输入终保时间", "请输入承保时间", "请输入车架号", "请输入手机号", "请输入商业险费用",
                    "请输入交强险费用", "请输入驾乘险费用", "请输入商业险费率", "请输入交强险费率", "请输入驾乘险费率", "请输入返现", "请输入客户来源", "请输入备注"};
        } else {
            //修改信息标识
            adapter.isAdd(false);
            //更新上下文菜单按钮跳转过来传递的bean
            intentBean = (UsersDataBean) intent.getSerializableExtra(USER_BEAN);
            columnValue = new String[]{String.valueOf(intentBean.getCarNumber()), intentBean.getUserName(), intentBean.getLastDate(), intentBean.getBuyTime(),
                    intentBean.getCarSerialNumber(), intentBean.getPhone(), String.valueOf(intentBean.getSyPrice()), String.valueOf(intentBean.getJqPrice()), String.valueOf(intentBean.getJcPrice()),
                    String.valueOf(intentBean.getSyRebate()), String.valueOf(intentBean.getJqRebate()), String.valueOf(intentBean.getJcRebate()),
                    String.valueOf(intentBean.getCashBack()), intentBean.getType(), intentBean.getRemarks()};
        }
        if (mList != null) {
            mList.clear();
        }
        String[] columnName = {"车牌号", "投保人", "终保时间", "承保时间", "车架号", "手机号", "商业险费用",
                "交强险费用", "驾乘险费用", "商业险费率", "交强险费率", "驾乘险费率", "返现", "客户来源", "备注"};
        DetailedMsgBean bean;
        for (int i = 0; i < columnName.length; i++) {

            bean = new DetailedMsgBean();
            bean.setDetailedTitle(columnName[i]);
            bean.setDetailedMessage(columnValue[i]);
            mList.add(bean);
        }
        adapter.notifyDataSetChanged();
    }


    /**
     * 利用好其预编译功能，可以有效加快正则匹配速度
     */
    Pattern p = Pattern.compile("^[A-Z0-9]");


    /**
     * 保存新添加的数据
     *
     * @return 是否保存成功
     */
    private boolean saveData() {
        HashMap<String, String> hashMap = adapter.getHashMap();
        EventBus.getDefault().post(new SendCarSerialNumber(hashMap.get(Constant.CAR_SERIAL_NUMBER)));
        if (TextUtils.isEmpty(hashMap.get(Constant.CAR_NUMBER))) {
            showToast("车牌不能为空");
            return false;
        }
        if (TextUtils.isEmpty(hashMap.get(Constant.USER_NAME))) {
            showToast("投保人不能为空");
            return false;
        }
        if (!TextUtils.isEmpty(hashMap.get(Constant.CAR_SERIAL_NUMBER)) && Objects.requireNonNull(hashMap.get(Constant.CAR_SERIAL_NUMBER)).length() != Constant.CAR_SERIAL_NUMBER_LENGTH) {
            showToast("车架号为17位");
            return false;
        } else if (!TextUtils.isEmpty(hashMap.get(Constant.CAR_SERIAL_NUMBER)) && Objects.requireNonNull(hashMap.get(Constant.CAR_SERIAL_NUMBER)).length() == Constant.CAR_SERIAL_NUMBER_LENGTH) {
            //判断车架号是否已大写字母开头并且是字母和数字组合
            Matcher m = p.matcher(Objects.requireNonNull(hashMap.get(Constant.CAR_SERIAL_NUMBER)));
            boolean b = m.find();
            if (!b) {
                showToast("车架号首位是大写字母或数字");
                return false;
            }
        }
        if (!TextUtils.isEmpty(hashMap.get(Constant.PHONE_NUMBER))) {
            if (Objects.requireNonNull(hashMap.get(Constant.PHONE_NUMBER)).length() != Constant.PHONE_NUMBER_LENGTH) {
                showToast("请输入十一位数字有效手机号");
                return false;
            } else if (!Objects.requireNonNull(hashMap.get(Constant.PHONE_NUMBER)).startsWith(Constant.PHONE_NUMBER_START)) {
                showToast("请输有效手机号");
                return false;
            }
        }
        try {
            String buyTime = hashMap.get(Constant.BUY_TIME);
            String lastDate = hashMap.get(Constant.LAST_DATE);
            if (TextUtils.isEmpty(buyTime)) {
                showToast("承保时间未输入，将默认为当前日期");
                buyTime = DateFormatUtil.getCurrentDate();
            }
            if (TextUtils.isEmpty(lastDate)) {
                showToast("终保时间未输入，将默认为当前日期");
                lastDate = DateFormatUtil.getCurrentDate();
            }
            String formatBuyTime = DateFormatUtil.getFormatDate(buyTime, "yyyy/M/d");
            String formatLastDate = DateFormatUtil.getFormatDate(lastDate, "yyyy/M/d");
            hashMap.put(Constant.BUY_TIME, formatBuyTime);
            hashMap.put(Constant.LAST_DATE, formatLastDate);
        } catch (ParseException e) {
            e.printStackTrace();
            showToast("日期格式错误");
            return false;
        }
        if (!isAdd) {
            hashMap.put("id", String.valueOf(intentBean.getId()));
        }
        if (isAdd && isSame) {
            new android.app.AlertDialog.Builder(this)
                    .setTitle("")
                    .setPositiveButton("确定", (dialog, which) -> {
                    })
                    .setMessage(hashMap.get("投保人") + "已存在，未添加！")
                    .show();
            return false;
        }
        intent.putExtra(SAVE_DATA, hashMap);
        intent.putExtra(IS_ADD, isAdd);
        intent.putExtras(intent);
        setResult(RESULT_SET_OK, intent);
        return true;
    }


    /**
     * 判断车架号是否相同
     */
    @Subscribe
    public void isTheSame(SendIsSame isSame) {
        this.isSame = isSame.isSame();
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(AddUserActivity.this)) {
            EventBus.getDefault().register(AddUserActivity.this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(AddUserActivity.this)) {
            EventBus.getDefault().unregister(AddUserActivity.this);
        }
    }
}
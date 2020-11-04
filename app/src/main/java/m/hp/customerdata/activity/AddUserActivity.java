package m.hp.customerdata.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import m.hp.customerdata.R;
import m.hp.customerdata.adapter.AddUserAdapter;
import m.hp.customerdata.entity.DetailedMsgBean;
import m.hp.customerdata.entity.MessageBean;

public class AddUserActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String SAVE_DATA = "SAVE_DATA";
    private static final String TAG = "AddUserActivity";
    private static final int RESULT_SET_OK = 1000;
    private static final String IS_ADD = "IS_ADD";
    private RecyclerView rv_add_users;
    private AddUserAdapter adapter;
    private List<DetailedMsgBean> mList;
    private Button bt_save;
    private HashMap<String, String> hashMap = new HashMap<>();
    //是新加数据还是更新数据
    public boolean isAdd;
    //MainActivity传递过来的数据
    private static final String USER_BEAN = "USER_BEAN";
    private Intent intent;
    public static AddUserActivity instance;
    private MessageBean intent_bean = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        initView();
    }

    private void initView() {
        rv_add_users = findViewById(R.id.rv_add);
        bt_save = findViewById(R.id.save_data);
        bt_save.setOnClickListener(this);

        mList = new ArrayList<>();
        adapter = new AddUserAdapter(this, mList);
        rv_add_users.setAdapter(adapter);
        rv_add_users.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        //获取传进来的intent
        intent = getIntent();
        //判断是新增数据还是修改数据
        isAdd = intent.getBooleanExtra(IS_ADD, true);
        instance = this;
        initData();
    }

    private void initData() {

        String[] column_value;
        if (isAdd) {
            column_value = new String[]{"请输入车牌号", "请输入投保人", "请输入终保时间", "请输入承保时间", "请输入车架号", "请输入手机号", "请输入商业险费用",
                    "请输入交强险费用", "请输入驾乘险费用", "请输入商业险费率", "请输入交强险费率", "请输入驾乘险费率", "请输入返现", "请输入客户来源", "请输入备注"};
        } else {
            //更新按钮跳转过来传递的bean
            intent_bean = intent.getParcelableExtra(USER_BEAN);
            column_value = new String[]{String.valueOf(intent_bean.getCarNumber()), intent_bean.getUserName(), intent_bean.getLastDate(), intent_bean.getBuyTime(),
                    intent_bean.getCarSerialNumber(), intent_bean.getPhone(), String.valueOf(intent_bean.getSyPrice()), String.valueOf(intent_bean.getJqPrice()), String.valueOf(intent_bean.getJcPrice()),
                    String.valueOf(intent_bean.getSyRebate()), String.valueOf(intent_bean.getJqRebate()), String.valueOf(intent_bean.getJcRebate()),
                    String.valueOf(intent_bean.getCashBack()), intent_bean.getType(), intent_bean.getRemarks()};
        }
        if (mList != null) {
            mList.clear();
        }
        String[] column_name = {"车牌号：", "投保人：", "终保时间：", "承保时间：", "车架号：", "手机号：", "商业险费用：",
                "交强险费用：", "驾乘险费用：", "商业险费率：", "交强险费率：", "驾乘险费率：", "返现：", "客户来源：", "备注："};
        DetailedMsgBean bean;
        for (int i = 0; i < column_name.length; i++) {

            bean = new DetailedMsgBean();
            bean.setDetailedTitle(column_name[i]);
            bean.setDetailedMessage(column_value[i]);
            mList.add(bean);
        }
        if (!isAdd) {
            //添加主键id给更新数据操作
            bean = new DetailedMsgBean();
            bean.setDetailedTitle("id");
            bean.setDetailedMessage(String.valueOf(intent_bean.getId()));
            mList.add(bean);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {

        if (saveData()) return;
        finish();
    }


    /**
     * 保存新添加的数据
     *
     * @return
     */
    private boolean saveData() {
        hashMap = AddUserAdapter.instance.getHashMap();

        if (TextUtils.isEmpty(hashMap.get("车牌号："))) {
            showToast("车牌不能为空");
            return true;
        }
        if (TextUtils.isEmpty(hashMap.get("投保人："))) {
            showToast("投保人不能为空");
            return true;
        }


        if (TextUtils.isEmpty(hashMap.get("车架号："))) {
            showToast("车架号不能为空");
            return true;
        } else if (hashMap.get("车架号：").length() != 17) {
            showToast("车架号为17位");
            return true;
        } else {
            //判断车架号是否已大写字母开头并且是字母和数字组合
            Pattern p = Pattern.compile("^[A-Z][0-9]");
            Matcher m = p.matcher(hashMap.get("车架号："));
            boolean b = m.find();
            if (!b) {
                showToast("车架号应当由大写字母开头");
                return true;
            }
        }
        if (!TextUtils.isEmpty(hashMap.get("手机号："))) {
            if (hashMap.get("手机号：").length() != 11) {
                showToast("请输入十一位数字有效手机号");
                return true;
            } else if (!hashMap.get("手机号：").startsWith("1")) {
                showToast("请输有效手机号");
                return true;
            }
        }
        if (!isAdd) {
            hashMap.put("id", String.valueOf(intent_bean.getId()));
        }
        intent.putExtra(SAVE_DATA, hashMap);
        //Log.d(TAG, "重新绑定数据====" + intent);
        setResult(RESULT_SET_OK, intent);
        return false;
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}
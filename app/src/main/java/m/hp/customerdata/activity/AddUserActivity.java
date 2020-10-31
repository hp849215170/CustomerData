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

import m.hp.customerdata.R;
import m.hp.customerdata.adapter.AddUserAdapter;
import m.hp.customerdata.entity.DetailedMsgBean;

public class AddUserActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String SAVE_DATA = "SAVE_DATA";
    private static final String TAG = "AddUserActivity";
    private static final int RESULT_SET_OK = 1000;
    private RecyclerView rv_add_users;
    private AddUserAdapter adapter;
    private List<DetailedMsgBean> mList;
    private Button bt_save;
    private HashMap<String, String> hashMap = new HashMap<>();

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
        initData();
    }

    private void initData() {
        if (mList != null) {
            mList.clear();
        }
        String[] column_name = {"序号：", "车牌号：", "投保人：", "终保时间：", "承保时间：", "车架号：", "手机号：", "商业险费用：",
                "交强险费用：", "驾乘险费用：", "商业险费率：", "交强险费率：", "驾乘险费率：", "返现：", "客户来源：", "备注："};
        String[] column_value = {"请输入序号", "请输入车牌号", "请输入投保人", "请输入终保时间", "请输入承保时间", "请输入车架号", "请输入手机号", "请输入商业险费用",
                "请输入交强险费用", "请输入驾乘险费用", "请输入商业险费率", "请输入交强险费率", "请输入驾乘险费率", "请输入返现", "请输入客户来源", "请输入备注"};
        for (int i = 0; i < column_name.length; i++) {
            DetailedMsgBean bean = new DetailedMsgBean();
            bean.setDetailedTitle(column_name[i]);
            bean.setDetailedMessage(column_value[i]);
            mList.add(bean);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        Intent intent = getIntent();
        hashMap = AddUserAdapter.instance.getHashMap();
        if (TextUtils.isEmpty(hashMap.get("投保人："))) {
            showToast("投保人不能为空");
            return;
        }
        intent.putExtra(SAVE_DATA, hashMap);
//        Log.d(TAG, "重新绑定数据====" + intent);
        setResult(RESULT_SET_OK, intent);
        finish();
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}
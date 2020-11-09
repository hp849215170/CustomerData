package m.hp.customerdata.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import m.hp.customerdata.R;
import m.hp.customerdata.adapter.DetailedMsgAdapter;
import m.hp.customerdata.entity.DetailedMsgBean;
import m.hp.customerdata.entity.UsersDataBean;

public class DetailedActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView rv_detail;
    private DetailedMsgAdapter adapter;
    private List<DetailedMsgBean> mList;
    private final String MESSAGE_BEAN = "MESSAGE_BEAN";
    private String tag = getClass().getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);
        setCustomActionBar();
        initView();
    }

    /**
     * 自定义ActionBar
     */
    private void setCustomActionBar() {
        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        View actionBarView = LayoutInflater.from(this).inflate(R.layout.customacitonbar_layout, null);
        TextView tvTitle = actionBarView.findViewById(R.id.actionBarTile);
        //返回操作
        ImageView ivBack = actionBarView.findViewById(R.id.ivBack);
        ivBack.setOnClickListener(this);
        tvTitle.setText("详细信息");
        ActionBar supportActionBar = getSupportActionBar();
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
        rv_detail = findViewById(R.id.rv_detailed_msg);
        mList = new ArrayList<>();
        adapter = new DetailedMsgAdapter(this, mList);
        rv_detail.setAdapter(adapter);
        rv_detail.setLayoutManager(new LinearLayoutManager(this));
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

        mList.clear();
        //拿到MainActivity通过Intent传来的数据
        Intent intent = getIntent();
        UsersDataBean usersDataBean = intent.getParcelableExtra(MESSAGE_BEAN);
        //把数据加载到当前Activity
        DetailedMsgBean detailedMsgBean;
        // DetailedMsgBean serialNumber = new DetailedMsgBean();
        String[] titles = {"序号", "车牌号", "投保人", "终保时间", "承保时间", "车架号", "手机号", "商业险费用",
                "交强险费用", "驾乘险费用", "商业险费率", "交强险费率", "驾乘险费率", "返现", "客户来源", "备注"};
        String[] messages = {String.valueOf(usersDataBean.getId()), usersDataBean.getCarNumber(), usersDataBean.getUserName(),
                usersDataBean.getLastDate(), usersDataBean.getBuyTime(), usersDataBean.getCarSerialNumber(), usersDataBean.getPhone(),
                String.valueOf(usersDataBean.getSyPrice()), String.valueOf(usersDataBean.getJqPrice()), String.valueOf(usersDataBean.getJcPrice()),
                String.valueOf(usersDataBean.getSyRebate()), String.valueOf(usersDataBean.getJqRebate()), String.valueOf(usersDataBean.getJcRebate()),
                String.valueOf(usersDataBean.getCashBack()), usersDataBean.getType(), usersDataBean.getRemarks()};

        for (int i = 0; i <= 15; i++) {
            detailedMsgBean = new DetailedMsgBean();
            detailedMsgBean.setDetailedTitle(titles[i]);
            detailedMsgBean.setDetailedMessage(messages[i]);
            mList.add(detailedMsgBean);
        }

    }

    @Override
    public void onClick(View v) {
        finish();
    }
}
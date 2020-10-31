package m.hp.customerdata.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import m.hp.customerdata.R;
import m.hp.customerdata.adapter.DetailedMsgAdapter;
import m.hp.customerdata.entity.DetailedMsgBean;
import m.hp.customerdata.entity.MessageBean;

public class DetailedActivity extends AppCompatActivity {

    private RecyclerView rv_detail;
    private DetailedMsgAdapter adapter;
    private List<DetailedMsgBean> mList;
    private final String MESSAGE_BEAN = "MESSAGE_BEAN";
    private String tag = getClass().getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);

        initView();
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
        MessageBean messageBean = intent.getParcelableExtra(MESSAGE_BEAN);
        //把数据加载到当前Activity
        DetailedMsgBean detailedMsgBean;
        // DetailedMsgBean serialNumber = new DetailedMsgBean();
        String[] titles = {"序号", "车牌号", "投保人", "终保时间", "承保时间", "车架号", "手机号", "商业险费用",
                "交强险费用", "驾乘险费用", "商业险费率", "交强险费率", "驾乘险费率", "返现", "客户来源", "备注"};
        String[] messages = {String.valueOf(messageBean.getSerialNumber()), messageBean.getCarNumber(), messageBean.getUserName(),
                messageBean.getLastDate(), messageBean.getBuyTime(), messageBean.getCarSerialNumber(), messageBean.getPhone(),
                String.valueOf(messageBean.getSyPrice()), String.valueOf(messageBean.getJqPrice()), String.valueOf(messageBean.getJcPrice()),
                String.valueOf(messageBean.getSyRebate()), String.valueOf(messageBean.getJqRebate()), String.valueOf(messageBean.getJcRebate()),
                String.valueOf(messageBean.getCashBack()), messageBean.getType(), messageBean.getRemarks()};

        for (int i = 0; i <= 15; i++) {
            detailedMsgBean = new DetailedMsgBean();
            detailedMsgBean.setDetailedTitle(titles[i]);
            detailedMsgBean.setDetailedMessage(messages[i]);
            mList.add(detailedMsgBean);
        }

    }
}
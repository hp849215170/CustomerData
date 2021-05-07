package m.hp.customerdata.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import m.hp.customerdata.R;
import m.hp.customerdata.adapter.ShowDirectoryAdapter;
import m.hp.customerdata.databinding.ActivityShowDirectoryBinding;
import m.hp.customerdata.entity.DirectoryBean;
import m.hp.customerdata.utils.Constant;
import m.hp.customerdata.utils.MyFileUtils;

/**
 * @author huangping
 */
public class ShowDirectoryActivity extends AppCompatActivity {
    /**
     * SD卡根目录
     */
    private static final String EXT_STORAGE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath();
    /**
     * 已选择的保存路径
     */
    private static final String SAVE_PATH = "SAVE_PATH";
    /**
     * 选择保存路径完成结果码
     */
    private static final int RESULT_SAVE_PATH = 600;
    /**
     * RecycleView适配器
     */
    private ShowDirectoryAdapter showDirectoryAdapter;
    /**
     * 文件夹信息实体
     */
    private List<DirectoryBean> directoryBeanList;
    /**
     * 当前选择的目录
     */
    private String currentDirFromAdapter;
    ActivityShowDirectoryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowDirectoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setCustomActionBar();
        initView();
    }

    /**
     * 自定义ActionBar
     */
    private void setCustomActionBar() {
        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        @SuppressLint("InflateParams") View actionBarView = LayoutInflater.from(this).inflate(R.layout.customacitonbar_layout, null);
        TextView tvTitle = actionBarView.findViewById(R.id.actionBarTile);
        ImageView ivBack = actionBarView.findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> finish());
        tvTitle.setText("选择要保存的目录");
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
     * 初始化View控件
     */
    private void initView() {
        //选择当前目录
        binding.checkOK.setOnClickListener(v -> getSavePath());
        //返回上一级目录按钮
        binding.previousDir.setOnClickListener(v -> previousDir());
        //实例化directoryBeanList
        directoryBeanList = new ArrayList<>();
        //初始化适配器
        showDirectoryAdapter = new ShowDirectoryAdapter(this, directoryBeanList, setCurrentDirHandler);
        //加载适配器
        binding.rvShowDir.setAdapter(showDirectoryAdapter);
        //设置RecycleView布局加载方式
        binding.rvShowDir.setLayoutManager(new LinearLayoutManager(this));
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
        getCurrentDir(EXT_STORAGE_DIR);
    }

    /**
     * 列出所有的目录
     *
     * @param path 当前选择的路径
     */
    public void getCurrentDir(String path) {
        if (directoryBeanList != null) {
            directoryBeanList.clear();
        }
        //实例化MyFileUtils
        MyFileUtils myFileUtils = new MyFileUtils();
        //赋值结果给directoryBeanList
        List<DirectoryBean> list = myFileUtils.getDirs(path);
        //显示当前所在目录
        binding.currentDir.setText(path);
        directoryBeanList.addAll(list);
        //刷新适配器
        showDirectoryAdapter.notifyDataSetChanged();
        currentDirFromAdapter = path;
    }

    /**
     * 获取要保存的目录
     */
    private void getSavePath() {
        //获取当前保存的路径
        String savePath = binding.currentDir.getText().toString();
        //返回给MainActivity
        Intent intent = getIntent();
        intent.putExtra(SAVE_PATH, savePath);
        setResult(RESULT_SAVE_PATH, intent);
        finish();
    }

    /**
     * 返回上一级目录
     */
    private void previousDir() {
        String previousPath = currentDirFromAdapter.substring(0, currentDirFromAdapter.lastIndexOf("/"));
        //根目录是/storage/emulated/0
        if (Constant.START_DIR.equals(previousPath)) {
            return;
        }
        getCurrentDir(previousPath);
    }

    /**
     * 接收ShowDirectoryAdapter发送来的路径数据
     */
    private final Handler setCurrentDirHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            getCurrentDir(msg.getData().getString("DIR"));
        }
    };
}
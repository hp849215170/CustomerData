package m.hp.customerdata.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import m.hp.customerdata.R;
import m.hp.customerdata.adapter.ShowDirectoryAdapter;
import m.hp.customerdata.entity.DirectoryBean;
import m.hp.customerdata.utils.MyFileUtils;

public class ShowDirectoryActivity extends AppCompatActivity implements View.OnClickListener {

    //SD卡根目录
    private static final String EXT_STORAGE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath();
    //已选择的保存路径
    private static final String SAVE_PATH = "SAVE_PATH";
    //选择保存路径完成结果码
    private static final int RESULT_SAVE_PATH = 600;
    //RecycleView适配器
    private ShowDirectoryAdapter showDirectoryAdapter;
    //文件夹信息实体
    private List<DirectoryBean> directoryBeanList;
    //RecycleView
    private RecyclerView dirRecyclerView;
    //当前文件夹路径
    private TextView currentDir;
    //返回上一个目录
    private FloatingActionButton floatingActionButton;
    //选择当前目录
    private FloatingActionButton fabChecked;
    //单例
    public static ShowDirectoryActivity instance;
    //当前选择的目录
    private String currentDirFromAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_directory);
        setCustomActionBar();
        instance = this;
        initView();
    }

    /**
     * 自定义ActionBar
     */
    private void setCustomActionBar() {
        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        View actionBarView = LayoutInflater.from(this).inflate(R.layout.customacitonbar_layout, null);
        TextView tvTitle = actionBarView.findViewById(R.id.actionBarTile);
        ImageView ivBack = actionBarView.findViewById(R.id.ivBack);
        ivBack.setVisibility(View.GONE);
        tvTitle.setText("选择要保存的目录");
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
     * 初始化View控件
     */
    private void initView() {
        //选择当前目录
        fabChecked = findViewById(R.id.checkOK);
        fabChecked.setOnClickListener(this);
        //返回上一级目录按钮
        floatingActionButton = findViewById(R.id.previousDir);
        floatingActionButton.setOnClickListener(this);
        currentDir = findViewById(R.id.currentDir);
        //实例化directoryBeanList
        directoryBeanList = new ArrayList<>();
        //RecycleView R.id
        dirRecyclerView = findViewById(R.id.rvShowDir);
        //初始化适配器
        showDirectoryAdapter = new ShowDirectoryAdapter(this, directoryBeanList);
        //加载适配器
        dirRecyclerView.setAdapter(showDirectoryAdapter);
        //设置RecycleView布局加载方式
        dirRecyclerView.setLayoutManager(new LinearLayoutManager(this));
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
     * @param path
     */
    public void getCurrentDir(String path) {
        directoryBeanList.clear();
        //实例化MyFileUtils
        MyFileUtils myFileUtils = new MyFileUtils();
        //赋值结果给directoryBeanList
        List<DirectoryBean> list = myFileUtils.getDirs(path);
        //显示当前所在目录
        currentDir.setText(path);
        for (int i = 0; i < list.size(); i++) {
            directoryBeanList.add(list.get(i));
        }
        //刷新适配器
        showDirectoryAdapter.notifyDataSetChanged();
        currentDirFromAdapter = path;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.previousDir:
                previousDir();
                break;
            case R.id.checkOK:
                getSavePath();
                break;
        }
    }

    /**
     * 获取要保存的目录
     */
    private void getSavePath() {
        //获取当前保存的路径
        String savePath = currentDir.getText().toString();
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
        if (previousPath.equals("/storage/emulated")) {
            return;
        }
        getCurrentDir(previousPath);
    }
}
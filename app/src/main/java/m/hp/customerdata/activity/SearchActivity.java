package m.hp.customerdata.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.mancj.materialsearchbar.MaterialSearchBar;

import m.hp.customerdata.R;
import m.hp.customerdata.adapter.SearchRVAdapter;
import m.hp.customerdata.databinding.ActivitySearchBinding;
import m.hp.customerdata.entity.UsersDataBean;
import m.hp.customerdata.model.UserDataViewModel;

public class SearchActivity extends AppCompatActivity {

    private SearchRVAdapter searchRVAdapter;
    private ActivitySearchBinding binding;
    private static final String USER_BEAN = "USER_BEAN";
    //是新加数据还是更新数据
    private static final String IS_ADD = "IS_ADD";
    private static final int MODIFY_REQUEST = 400;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setCustomActionBar();
        initView();
    }

    private void initView() {
        binding.fabSearch.setOnClickListener(v -> searchUserData(false));
        searchRVAdapter = new SearchRVAdapter(new SearchRVAdapter.MessageBeanDiff(), this);
        binding.rvSearch.setAdapter(searchRVAdapter);
        binding.rvSearch.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        binding.rvSearch.setLayoutManager(new LinearLayoutManager(this));
        //键盘搜索键点击监听事件
        binding.searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                searchUserData(false);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });
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
        tvTitle.setText("查询客户");
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
     * 查找用户信息
     *
     * @param afterOperate 是否编辑了查询出来的信息
     */
    private void searchUserData(boolean afterOperate) {
        ViewModelProvider.AndroidViewModelFactory androidViewModelFactory = new ViewModelProvider.AndroidViewModelFactory(getApplication());
        UserDataViewModel mUserDataViewModel = new ViewModelProvider(this, androidViewModelFactory).get(UserDataViewModel.class);
        String inputText = binding.searchBar.getText();
        mUserDataViewModel.getDataByUserName(inputText)
                .observe(this, usersDataBeanList -> {
                    if (usersDataBeanList.size() == 0 && !afterOperate) {
                        new AlertDialog.Builder(this)
                                .setTitle("搜索结果").setMessage("未找到相关数据")
                                .setPositiveButton("确定", null)
                                .show();
                    }
                    searchRVAdapter.submitList(usersDataBeanList);
                    searchRVAdapter.notifyDataSetChanged();
                });

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
                    //获取ViewModel
                    ViewModelProvider.AndroidViewModelFactory androidViewModelFactory = new ViewModelProvider.AndroidViewModelFactory(getApplication());
                    UserDataViewModel userDataViewModel = new ViewModelProvider(this, androidViewModelFactory).get(UserDataViewModel.class);
                    userDataViewModel.delByName(userName);
                    Toast.makeText(SearchActivity.this, "删除成功", Toast.LENGTH_LONG).show();
                    searchUserData(true);
                })
                .setNegativeButton("取消", null)
                .show();
    }
}
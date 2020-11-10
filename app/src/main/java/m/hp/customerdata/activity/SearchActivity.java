package m.hp.customerdata.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.List;

import m.hp.customerdata.R;
import m.hp.customerdata.adapter.SearchRVAdapter;
import m.hp.customerdata.entity.UsersDataBean;
import m.hp.customerdata.model.UserDataViewModel;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener, MaterialSearchBar.OnSearchActionListener {

    private RecyclerView rvSearch;
    private SearchRVAdapter searchRVAdapter;
    private List<UsersDataBean> usersDataBeanList;
    private FloatingActionButton fabSearch;
    private MaterialSearchBar mSearchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setCustomActionBar();

        initView();
    }

    private void initView() {
        mSearchBar = findViewById(R.id.searchBar);
        mSearchBar.setOnSearchActionListener(this);
        fabSearch = findViewById(R.id.fab_search);
        fabSearch.setOnClickListener(this);
        rvSearch = findViewById(R.id.rvSearch);
        searchRVAdapter = new SearchRVAdapter(new SearchRVAdapter.MessageBeanDiff(), this);
        rvSearch.setAdapter(searchRVAdapter);
        rvSearch.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * 自定义ActionBar
     */
    private void setCustomActionBar() {
        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        View actionBarView = LayoutInflater.from(this).inflate(R.layout.customacitonbar_layout, null);
        TextView tvTitle = actionBarView.findViewById(R.id.actionBarTile);
        ImageView ivBack = actionBarView.findViewById(R.id.ivBack);
        ivBack.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_search:
                searchUserData();
                break;
            case R.id.ivBack:
                finish();
                break;
        }
    }

    private void searchUserData() {
        ViewModelProvider.AndroidViewModelFactory androidViewModelFactory = new ViewModelProvider.AndroidViewModelFactory(getApplication());
        UserDataViewModel mUserDataViewModel = new ViewModelProvider(this, androidViewModelFactory).get(UserDataViewModel.class);
        String inputText = mSearchBar.getText();
        List<UsersDataBean> usersDataBeanList = mUserDataViewModel.getDataByUserName(inputText);
        if (usersDataBeanList.size() == 0) {
            new AlertDialog.Builder(this)
                    .setTitle("搜索结果").setMessage("未找到相关数据")
                    .setPositiveButton("确定", null)
                    .show();
        }
        searchRVAdapter.submitList(usersDataBeanList);
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {

    }

    @Override
    public void onSearchConfirmed(CharSequence text) {
        searchUserData();
    }

    @Override
    public void onButtonClicked(int buttonCode) {

    }
}
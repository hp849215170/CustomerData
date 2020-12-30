package m.hp.customerdata.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import m.hp.customerdata.activity.AddUserActivity;
import m.hp.customerdata.activity.SearchActivity;
import m.hp.customerdata.adapter.MessageBeanListAdapter;
import m.hp.customerdata.databinding.MainfragmentLayoutBinding;
import m.hp.customerdata.entity.UsersDataBean;
import m.hp.customerdata.model.UserDataViewModel;
import m.hp.customerdata.myevents.SendAllDataList;
import m.hp.customerdata.myevents.SendCarSerialNumber;
import m.hp.customerdata.myevents.SendIsSame;
import m.hp.customerdata.utils.MyCompareUtil;

public class MainFragment extends Fragment {

    private static final int REQUEST_OK = 300;
    //选择保存路径完成结果码
    private static final int RESULT_SET_OK = 1000;
    private static final int MODIFY_REQUEST = 400;
    //是新加数据还是更新数据
    private static final String IS_ADD = "IS_ADD";
    //已选择的保存路径
    private static final String SAVE_PATH = "SAVE_PATH";
    private MessageBeanListAdapter msgAdapter;
    private UserDataViewModel mUserDataViewModel;
    private static final String SAVE_DATA = "SAVE_DATA";
    private HashMap<String, String> hashMap = new HashMap<>();
    private static final String USER_BEAN = "USER_BEAN";
    //点击投保人标题次数
    private int count_userName;
    //点击到期时间标题次数
    private int count_lastDate;
    //当前数据库数据集合
    private List<UsersDataBean> usersDataBeanList;
    private MainfragmentLayoutBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = MainfragmentLayoutBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        initView();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        getActivity().getOnBackPressedDispatcher().addCallback(backPressed);
    }

    /**
     * 初始化控件
     */
    private void initView() {

        //全选标题
        binding.titles.checkAll.setOnClickListener(v -> {
            if (binding.titles.checkAll.getText().toString().equals("全选")) {
                msgAdapter.checkAll(true);
                binding.titles.checkAll.setText("不选");
            } else {
                msgAdapter.checkAll(false);
                binding.titles.checkAll.setText("全选");
            }
        });
        //到期时间标题
        binding.titles.lastDate.setOnClickListener(v -> {
            //按时间排序功能
            sortByDate();
        });
        //投保人标题
        binding.titles.userName.setOnClickListener(v -> {
            //按名字排序功能
            sortByName();
        });
        //获取ViewModel
        ViewModelProvider.AndroidViewModelFactory androidViewModelFactory = new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication());
        mUserDataViewModel = new ViewModelProvider(getActivity(), androidViewModelFactory).get(UserDataViewModel.class);
        //创建RecycleView的适配器
        msgAdapter = new MessageBeanListAdapter(new MessageBeanListAdapter.MessageBeanDiff(), getContext());
        binding.rvMsg.setAdapter(msgAdapter);
        binding.rvMsg.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        //设置RecycleView的布局
        binding.rvMsg.setLayoutManager(new LinearLayoutManager(getContext()));
        //隐藏和显示fab按钮
        binding.rvMsg.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY - oldScrollY > 0) {
                binding.fabSearch.hide();
                binding.fabAdd.hide();
            }
            if (scrollY - oldScrollY < 0) {
                binding.fabSearch.show();
                binding.fabAdd.show();
            }
        });
        //监听FloatingActionButton点击
        binding.fabSearch.setOnClickListener(v -> {
            //搜索功能
            showSearchBar();
        });
        binding.fabAdd.setOnClickListener(v -> {
            //添加数据功能
            starAddUserActivity();
        });
        binding.fabDelete.setOnClickListener(v -> {
            //删除数据功能
            deleteUserByFab();
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    /**
     * 向数据库插入数据
     */
    private void insertUserData(boolean isAdd) {
        UsersDataBean bean = getMessageBean(isAdd);
        //插入数据
        mUserDataViewModel.insert(bean);
        msgAdapter.notifyDataSetChanged();
        new android.app.AlertDialog.Builder(getContext())
                .setTitle("")
                .setPositiveButton("确定", (dialog, which) -> {

                })
                .setMessage("添加成功")
                .show();
    }

    /**
     * 启动Activity之后返回的结果处理
     *
     * @param requestCode 请求跳转的结果码
     * @param resultCode  跳转activity之后返回的结果码
     * @param data        activity传递的intent数据
     */
    @SuppressWarnings("unchecked")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == REQUEST_OK && resultCode == RESULT_SET_OK) {
            //获取需要添加的数据
            assert data != null;
            hashMap = (HashMap<String, String>) data.getSerializableExtra(SAVE_DATA);
            boolean isAdd = data.getBooleanExtra(IS_ADD, true);
            insertUserData(isAdd);
        } else if (requestCode == MODIFY_REQUEST && resultCode == RESULT_SET_OK) {
            //获取需要修改的数据
            assert data != null;
            hashMap = (HashMap<String, String>) data.getSerializableExtra(SAVE_DATA);
            boolean isAdd = data.getBooleanExtra(IS_ADD, true);
            updateUserData(isAdd);//更新数据
        }

    }


    /**
     * 返回监听
     */
    private OnBackPressedCallback backPressed = new OnBackPressedCallback(false) {
        @Override
        public void handleOnBackPressed() {
            setCheckOptViewGone();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (binding != null) {
            binding = null;
        }
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mUserDataViewModel.getAllUserData().observe(this, messageBeans -> {
            //传递当前查询到的数据给usersDataBeanList做数据重复校验
            usersDataBeanList = messageBeans;
            msgAdapter.submitList(messageBeans);
            EventBus.getDefault().post(new SendAllDataList(messageBeans));
        });
        msgAdapter.notifyDataSetChanged();
    }

    /**
     * 通过名字删除
     */
    private void deleteUserByFab() {

        new AlertDialog.Builder(getContext())
                .setTitle("警告")
                .setMessage("数据不可恢复，确定要删除吗？")
                .setPositiveButton("确定", (dialog, which) -> {
                    List<UsersDataBean> checkedUsers = msgAdapter.getCheckedUsers();
                    for (int i = 0; i < checkedUsers.size(); i++) {
                        mUserDataViewModel.delByName(checkedUsers.get(i).getUserName());
                    }
                    Toast.makeText(getContext(), "删除成功", Toast.LENGTH_LONG).show();
                    setCheckOptViewGone();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    /**
     * 按名字搜索相关数据
     */
    private void showSearchBar() {
        //跳转到搜索页面
        Intent intent = new Intent(getContext(), SearchActivity.class);
        startActivity(intent);
    }

    /**
     * 启动添加数据Activity
     */
    private void starAddUserActivity() {
        Intent intent = new Intent(getContext(), AddUserActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(SAVE_DATA, "SAVE_DATA");
        bundle.putBoolean(IS_ADD, true);
        intent.putExtras(bundle);
        startActivityForResult(intent, REQUEST_OK);
    }

    /**
     * 按名字排序
     */
    private void sortByName() {
        count_userName++;
        mUserDataViewModel.getAllUserData().observe(this, messageBeans -> {
            if (count_userName % 2 == 0) {
                //降序排序
                messageBeans.sort(new MyCompareUtil(MyCompareUtil.SORT_DES, MyCompareUtil.COMPARE_NAME));
            } else {
                //升序排序
                messageBeans.sort(new MyCompareUtil(MyCompareUtil.SORT_ASC, MyCompareUtil.COMPARE_NAME));
            }
            msgAdapter.submitList(messageBeans);
        });
        msgAdapter.notifyDataSetChanged();
    }

    /**
     * 按时间排序
     */
    private void sortByDate() {
        count_lastDate++;
        mUserDataViewModel.getAllUserData().observe(this, messageBeans -> {
            if (count_lastDate % 2 == 0) {
                //降序排序
                messageBeans.sort(new MyCompareUtil(MyCompareUtil.SORT_DES, MyCompareUtil.COMPARE_DATE));
            } else {
                //升序排序
                messageBeans.sort(new MyCompareUtil(MyCompareUtil.SORT_ASC, MyCompareUtil.COMPARE_DATE));
            }
            msgAdapter.submitList(messageBeans);
        });
        msgAdapter.notifyDataSetChanged();
    }


    /**
     * 获取新添加的车架号
     */
    @Subscribe
    public void getSerialCarNumber(SendCarSerialNumber csNumber) {
        boolean theSame = isTheSame(csNumber.getSerialCarNumber());
        EventBus.getDefault().post(new SendIsSame(theSame));
    }

    public boolean isTheSame(String carSerialNumber) {

        List<UsersDataBean> usersDataBeanList = this.usersDataBeanList;
        for (int i = 0; i < usersDataBeanList.size(); i++) {
            if (usersDataBeanList.get(i).getCarSerialNumber().equals(carSerialNumber)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 更新数据
     */
    private void updateUserData(boolean isAdd) {
        UsersDataBean bean = getMessageBean(isAdd);

        mUserDataViewModel.updateData(bean);
        new android.app.AlertDialog.Builder(getContext())
                .setTitle("")
                .setPositiveButton("确定", (dialog, which) -> {

                })
                .setMessage("保存成功")
                .show();
        //Log.e("updateData==result===", i + "");
        msgAdapter.notifyDataSetChanged();
    }

    /**
     * 获取保存之后的bean实体数据
     *
     * @return 返回是添加数据，还是更新数据返回的数据
     */
    private UsersDataBean getMessageBean(boolean isAdd) {
        String[] keys = {"车牌号", "投保人", "终保时间", "承保时间", "车架号", "手机号", "商业险费用",
                "交强险费用", "驾乘险费用", "商业险费率", "交强险费率", "驾乘险费率", "返现", "客户来源", "备注"};

        UsersDataBean bean = new UsersDataBean();

        bean.setCarNumber(hashMap.get(keys[0]));
        bean.setUserName(hashMap.get(keys[1]));
        bean.setLastDate(hashMap.get(keys[2]));
        // Log.e("hashMap.get(keys[2])", hashMap.get(keys[2]));
        bean.setBuyTime(hashMap.get(keys[3]));
        bean.setCarSerialNumber(hashMap.get(keys[4]));
        bean.setPhone(hashMap.get(keys[5]));
        if (TextUtils.isEmpty(hashMap.get(keys[6]))) {
            bean.setSyPrice(0.0);
        } else {
            bean.setSyPrice(Double.parseDouble(Objects.requireNonNull(hashMap.get(keys[6]))));
        }
        if (TextUtils.isEmpty(hashMap.get(keys[7]))) {
            bean.setJqPrice(0.0);
        } else {
            bean.setJqPrice(Double.parseDouble(Objects.requireNonNull(hashMap.get(keys[7]))));
        }
        if (TextUtils.isEmpty(hashMap.get(keys[8]))) {
            bean.setJcPrice(0.0);
        } else {
            bean.setJcPrice(Double.parseDouble(Objects.requireNonNull(hashMap.get(keys[8]))));
        }
        if (TextUtils.isEmpty(hashMap.get(keys[9]))) {
            bean.setJcPrice(0.0);
        } else {
            bean.setSyRebate(Double.parseDouble(Objects.requireNonNull(hashMap.get(keys[9]))));
        }
        if (TextUtils.isEmpty(hashMap.get(keys[10]))) {
            bean.setJqRebate(0.0);
        } else {
            bean.setJqRebate(Double.parseDouble(Objects.requireNonNull(hashMap.get(keys[10]))));
        }
        if (TextUtils.isEmpty(hashMap.get(keys[11]))) {
            bean.setJqRebate(0.0);
        } else {
            bean.setJcRebate(Double.parseDouble(Objects.requireNonNull(hashMap.get(keys[11]))));
        }
        if (TextUtils.isEmpty(hashMap.get(keys[12]))) {
            bean.setCashBack(0.0);
        } else {
            bean.setCashBack(Double.parseDouble(Objects.requireNonNull(hashMap.get(keys[12]))));
        }
        bean.setType(hashMap.get(keys[13]));
        bean.setRemarks(hashMap.get(keys[14]));
        if (!isAdd) {
            bean.setId(Integer.parseInt(Objects.requireNonNull(hashMap.get("id"))));
        }
        return bean;
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
                Intent intent = new Intent(getContext(), AddUserActivity.class);
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
            case 3://编辑菜单
                binding.titles.checkAll.setVisibility(View.VISIBLE);
                msgAdapter.setCheckBoxIsVisible(true);
                //监听返回操作
                backPressed.setEnabled(true);
                binding.fabDelete.setVisibility(View.VISIBLE);
                msgAdapter.notifyDataSetChanged();
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
        new AlertDialog.Builder(getActivity())
                .setTitle("警告")
                .setMessage("数据不可恢复，确定要删除吗？")
                .setPositiveButton("确定", (dialog, which) -> {
                    mUserDataViewModel.delByName(userName);
                    Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_LONG).show();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    /**
     * 设置编辑操作view不可见
     */
    private void setCheckOptViewGone() {
        binding.titles.checkAll.setVisibility(View.GONE);
        binding.fabDelete.setVisibility(View.GONE);
        msgAdapter.setCheckBoxIsVisible(false);
        msgAdapter.checkAll(false);
        backPressed.setEnabled(false); //释放返回操作
        binding.titles.checkAll.setText("全选");
        msgAdapter.notifyDataSetChanged();
    }


}

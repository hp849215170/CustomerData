package m.hp.customerdata.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tencent.bugly.beta.Beta;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import m.hp.customerdata.R;
import m.hp.customerdata.activity.ShowDirectoryActivity;
import m.hp.customerdata.databinding.MysfragmentLayoutBinding;
import m.hp.customerdata.databinding.SwitchServerLayoutBinding;
import m.hp.customerdata.entity.UserJsonRoot;
import m.hp.customerdata.entity.UsersDataBean;
import m.hp.customerdata.http.ConnectMyServer;
import m.hp.customerdata.model.UserDataViewModel;
import m.hp.customerdata.poiexcel.ExportUsersDateExcel;
import m.hp.customerdata.poiexcel.ReadExcelByPOI;
import m.hp.customerdata.utils.Constant;
import m.hp.customerdata.utils.MySharedPreferenceUtils;
import m.hp.customerdata.utils.RealPathFromUriUtils;

import static android.content.Context.MODE_PRIVATE;

/**
 * @author huangping
 */
public class MysFragment extends Fragment implements View.OnClickListener {
    /**
     * 打开文件管理器请求码
     */
    private static final int OPEN_FILE_REQUEST = 200;
    private MysfragmentLayoutBinding binding;
    /**
     * 查找上次选择的服务器
     */
    private String url;
    /**
     * 选择服务器对话框
     */
    private AlertDialog alertDialog;
    private MySharedPreferenceUtils spUtils;
    private UserDataViewModel mUserDataViewModel;
    private List<UsersDataBean> allUsers;
    /**
     * 打开选择保存目录
     */
    private static final int REQUEST_SHOW_DIRS = 500;
    /**
     * 选择保存路径完成结果码
     */
    private static final int RESULT_SAVE_PATH = 600;
    /**
     * 已选择的保存路径
     */
    private static final String SAVE_PATH = "SAVE_PATH";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = MysfragmentLayoutBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        spUtils = new MySharedPreferenceUtils(Objects.requireNonNull(getContext()), "server_url", MODE_PRIVATE);
        url = spUtils.getspstring("url", Constant.NATIVE_SERVER_URL);
        initView();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        //获取ViewModel
        ViewModelProvider.AndroidViewModelFactory androidViewModelFactory = new ViewModelProvider.AndroidViewModelFactory(Objects.requireNonNull(getActivity()).getApplication());
        mUserDataViewModel = new ViewModelProvider(this, androidViewModelFactory).get(UserDataViewModel.class);
        mUserDataViewModel.getAllUserData().observe(this, usersDataBeanList -> allUsers = usersDataBeanList);
    }

    private void initView() {
        showCurrentServer();
        getAppVersion();
        binding.switchServer.setOnClickListener(this);
        binding.downloadServer.setOnClickListener(this);
        binding.uploadServer.setOnClickListener(this);
        binding.addFromExcel.setOnClickListener(this);
        binding.exportExcel.setOnClickListener(this);
        binding.updateApp.setOnClickListener(this);

    }

    @SuppressLint("SetTextI18n")
    private void getAppVersion() {
        PackageManager packageManager = Objects.requireNonNull(getContext()).getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getContext().getPackageName(), 0);
            binding.appVersion.setText(getString(R.string.version) + " " + packageInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void showCurrentServer() {
        switch (url) {
            case Constant.NATIVE_SERVER_URL:
                binding.currentServer.setText("当前服务器：局域网服务器");
                break;
            case Constant.TENCENT_LIGHT_SERVER_URL:
                binding.currentServer.setText("当前服务器：腾讯服务器");
                break;
            case Constant.PHDDNS_SERVER_URL:
                binding.currentServer.setText("当前服务器：花生壳服务器");
                break;
            default:
                break;
        }
    }

    /**
     * 切换服务器
     */
    private void switchServer() {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.switch_server_layout, null, false);
        //切换服务器布局
        SwitchServerLayoutBinding switchServerBinding;
        switchServerBinding = SwitchServerLayoutBinding.bind(view);

        switch (url) {
            case Constant.NATIVE_SERVER_URL:
                switchServerBinding.nativeServer.setChecked(true);
                break;
            case Constant.TENCENT_LIGHT_SERVER_URL:
                switchServerBinding.tencentServer.setChecked(true);
                break;
            case Constant.PHDDNS_SERVER_URL:
                switchServerBinding.phddnsServer.setChecked(true);
                break;
            default:
                break;
        }

        alertDialog = new AlertDialog.Builder(Objects.requireNonNull(getContext()))
                .setCancelable(false)
                .setView(view)
                .show();
        switchServerBinding.radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.nativeServer) {
                url = Constant.NATIVE_SERVER_URL;
            } else if (checkedId == R.id.tencentServer) {
                url = Constant.TENCENT_LIGHT_SERVER_URL;
            } else if (checkedId == R.id.phddnsServer) {
                url = Constant.PHDDNS_SERVER_URL;
            }
        });
        //确定按钮
        switchServerBinding.okBt.setOnClickListener(this);
        //取消按钮
        switchServerBinding.cancelBt.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ok_bt) {
            spUtils.savespstring("url", url);
            alertDialog.dismiss();
            showCurrentServer();
        } else if (v.getId() == R.id.cancel_bt) {
            url = spUtils.getspstring("url", Constant.NATIVE_SERVER_URL);
            alertDialog.dismiss();
        } else if (v.getId() == R.id.switchServer) {
            switchServer();
        } else if (v.getId() == R.id.downloadServer) {
            downloadServer();
        } else if (v.getId() == R.id.uploadServer) {
            uploadServer();
        } else if (v.getId() == R.id.addFromExcel) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("*/*");
            String[] mimeTypes = {"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "application/vnd.ms-excel"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, OPEN_FILE_REQUEST);
        } else if (v.getId() == R.id.exportExcel) {
            Intent intentToShowDir = new Intent(getContext(), ShowDirectoryActivity.class);
            startActivityForResult(intentToShowDir, REQUEST_SHOW_DIRS);
        } else if (v.getId() == R.id.updateApp) {
            Beta.checkAppUpgrade();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OPEN_FILE_REQUEST) {
            //打开文件管理器选择Excel
            if (data == null) {
                return;
            }
            Uri excelUri = data.getData();
            String path = RealPathFromUriUtils.getRealPathFromUri(getContext(), excelUri);
            addUserFromExcel(path);
        } else if (requestCode == REQUEST_SHOW_DIRS && resultCode == RESULT_SAVE_PATH) {
            //导出Excel表格
            assert data != null;
            String saveDir = data.getStringExtra(SAVE_PATH);
            mUserDataViewModel.getAllUserData().observe(this, messageBeans -> new ExportUsersDateExcel(messageBeans, (ok, outputPath) -> {
                if (ok) {
                    Looper.prepare();//加上避免报错：Can't toast on a thread that has not called Looper.prepare()
                    new AlertDialog.Builder(Objects.requireNonNull(getContext()))
                            .setTitle("导出成功").setMessage("已保存至：" + outputPath)
                            .setPositiveButton("确定", null)
                            .show();
                } else {
                    Looper.prepare();
                    Toast.makeText(getContext(), "导出失败", Toast.LENGTH_LONG).show();
                }
                Looper.loop();
            }, saveDir));
        }
    }

    /**
     * 从Excel文件添加用户数据
     */
    private void addUserFromExcel(String filePath) {


        new ReadExcelByPOI(filePath, usersDataBeanList -> {
            //导入成功数据的条数
            int successCount = 0;
            //总共不重复数据数量
            List<UsersDataBean> noRepeatList = noRepeatList(usersDataBeanList);
            int total = noRepeatList.size();
            //重复判断
            for (int i = 0; i < total; i++) {
                //判断当前导入的Excel数据的投保人是否重复
                boolean theSame = isTheSame(noRepeatList.get(i).getCarSerialNumber());
                if (!theSame) {//不重复

                    //添加用户数据到数据库
                    mUserDataViewModel.insert(noRepeatList.get(i));
                    successCount++;
                }
            }
            //解决Can't create handler inside thread Thread[Thread-3,5,main] that has not called Looper.prepare()
            Looper.prepare();
            //提示导入Excel结果
            new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                    .setTitle("导入完成").setMessage("成功导入" + successCount + "条信息，" + (total - successCount) + "条信息重复未导入！")
                    .setPositiveButton("确定", null)
                    .show();
            Looper.loop();
        });
    }

    public boolean isTheSame(String carSerialNumber) {
        for (int i = 0; i < allUsers.size(); i++) {
            if (allUsers.get(i).getCarSerialNumber().equals(carSerialNumber)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 上传数据到服务器
     */
    private void uploadServer() {

        ThreadPoolExecutor executor = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<>(), r -> new Thread(r, "update"));

        executor.execute(new MyUploadDataTask());
/*
        new Thread(() -> {
            ConnectMyServer connectMyServer = new ConnectMyServer();
            connectMyServer.setUrl(url);
            UserJsonRoot userJsonRoot = new UserJsonRoot();
            userJsonRoot.setOption("insert");
            userJsonRoot.setUserList(allUsers);
            String jsonString = JSONArray.toJSONString(userJsonRoot);
            String getData = connectMyServer.postData(jsonString);
            if (TextUtils.isEmpty(getData)) {
                getData = "上传失败，请检查网络是否连接";
            }

            Log.d("getData", "服务器返回的数据----" + getData);
            // Log.d("jsonString", "jsonString----" + jsonString);
            Looper.prepare();
            new AlertDialog.Builder(getContext()).setMessage(getData).setPositiveButton("确定", null).show();
            Looper.loop();
        }).start();*/
    }


    /**
     * 从服务器下载数据
     */
    private void downloadServer() {
        String url = spUtils.getspstring("url", Constant.NATIVE_SERVER_URL);
        Log.d("url", "当前服务器是" + url);

        ThreadPoolExecutor executor = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<>(), r -> new Thread(r, "downloaddata"));

        executor.execute(new MyDownloadDataTask());
    }

    /**
     * list集合去重复数据
     *
     * @param list 要去重复的集合
     * @return 去重复之后的集合
     */
    public List<UsersDataBean> noRepeatList(List<UsersDataBean> list) {
        HashSet<UsersDataBean> hashSet = new HashSet<>(list);
        list.clear();
        list.addAll(hashSet);
        Log.d("noRepeatList", "noRepeatList is " + list.size());
        return list;
    }

    /**
     * 下载数据任务
     */
    class MyDownloadDataTask implements Runnable {
        String url = spUtils.getspstring("url", Constant.NATIVE_SERVER_URL);

        @Override
        public void run() {
            ConnectMyServer connectMyServer = new ConnectMyServer();
            connectMyServer.setUrl(url);
            //发送json请求数据给服务器
            UserJsonRoot userJsonRoot = new UserJsonRoot();
            userJsonRoot.setOption("findAll");
            userJsonRoot.setUserList(null);
            String jsonString = JSONArray.toJSONString(userJsonRoot);
            //获取服务器返回的json数据
            String getData = connectMyServer.postData(jsonString);

            Log.d("getData", "服务器返回的数据----" + getData);
            if (TextUtils.isEmpty(getData)) {
                Looper.prepare();
                Toast.makeText(getContext(), "未查询到信息，请检查网络是否连接", Toast.LENGTH_LONG).show();
                Looper.loop();
                return;
            }
            if (getData.startsWith(Constant.HTML_START)) {
                return;
            }
            //解析json数据
            JSONObject jsonObject = JSON.parseObject(getData);
            JSONArray jsonArray = jsonObject.getJSONArray("userList");

            boolean thSame = false;
            List<UsersDataBean> userList = new ArrayList<>();
            userList.clear();
            for (int i = 0; i < jsonArray.size(); i++) {
                int uid = jsonArray.getJSONObject(i).getInteger("id");
                String buyTime = jsonArray.getJSONObject(i).getString("buyTime");
                String carNumber = jsonArray.getJSONObject(i).getString("carNumber");
                String carSerialNumber = jsonArray.getJSONObject(i).getString("carSerialNumber");
                String lastDate = jsonArray.getJSONObject(i).getString("lastDate");
                String type = jsonArray.getJSONObject(i).getString("type");
                String userName = jsonArray.getJSONObject(i).getString("userName");
                String phone = jsonArray.getJSONObject(i).getString("phone");
                String remark = jsonArray.getJSONObject(i).getString("remarks");
                Double cashBack = jsonArray.getJSONObject(i).getDouble("cashBack");
                Double jcPrice = jsonArray.getJSONObject(i).getDouble("jcPrice");
                Double jcRebate = jsonArray.getJSONObject(i).getDouble("jcRebate");
                Double jqPrice = jsonArray.getJSONObject(i).getDouble("jqPrice");
                Double jqRebate = jsonArray.getJSONObject(i).getDouble("jqRebate");
                Double syPrice = jsonArray.getJSONObject(i).getDouble("syPrice");
                Double syRebate = jsonArray.getJSONObject(i).getDouble("syRebate");
                if (allUsers != null) {
                    for (int j = 0; j < allUsers.size(); j++) {
                        if (carSerialNumber.equals(allUsers.get(j).getCarSerialNumber())) {
                            thSame = true;
                            break;
                        } else {
                            thSame = false;
                        }
                    }
                }
                if (!thSame) {
                    UsersDataBean user = new UsersDataBean(uid, carNumber, userName, lastDate, buyTime, carSerialNumber, phone, syPrice, jqPrice, jcPrice, syRebate, jqRebate, jcRebate, cashBack, type, remark);
                    userList.add(user);
                }

            }
            //去重复List数据
            List<UsersDataBean> noRepeatList = noRepeatList(userList);

            //记录更新条数
            int count = 0;
            for (int i = 0; i < noRepeatList.size(); i++) {
                mUserDataViewModel.insert(noRepeatList.get(i));
                count++;
            }
            Looper.prepare();
            if (count == 0) {
                new AlertDialog.Builder(Objects.requireNonNull(getContext())).setMessage("没有发现新数据").setPositiveButton("确定", null).show();

            } else {
                new AlertDialog.Builder(Objects.requireNonNull(getContext())).setMessage("更新了" + count + "条数据").setPositiveButton("确定", null).show();
            }
            Looper.loop();
        }
    }

    /**
     * 上传数据任务
     */
    class MyUploadDataTask implements Runnable {
        String url = spUtils.getspstring("url", Constant.NATIVE_SERVER_URL);

        @Override
        public void run() {
            ConnectMyServer connectMyServer = new ConnectMyServer();
            connectMyServer.setUrl(url);
            UserJsonRoot userJsonRoot = new UserJsonRoot();
            userJsonRoot.setOption("insert");
            userJsonRoot.setUserList(allUsers);
            String jsonString = JSONArray.toJSONString(userJsonRoot);
            String getData = connectMyServer.postData(jsonString);
            if (TextUtils.isEmpty(getData)) {
                getData = "上传失败，请检查网络是否连接";
            }

            Log.d("getData", "服务器返回的数据----" + getData);
            Looper.prepare();
            new AlertDialog.Builder(Objects.requireNonNull(getContext())).setMessage(getData).setPositiveButton("确定", null).show();
            Looper.loop();
        }
    }
}



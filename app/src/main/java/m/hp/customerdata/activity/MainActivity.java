package m.hp.customerdata.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import m.hp.customerdata.R;
import m.hp.customerdata.adapter.MainPageAdapter;
import m.hp.customerdata.databinding.ActivityMainBinding;
import m.hp.customerdata.entity.UsersDataBean;
import m.hp.customerdata.fragment.CenterFragment;
import m.hp.customerdata.fragment.MainFragment;
import m.hp.customerdata.fragment.MysFragment;
import m.hp.customerdata.model.UserDataViewModel;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, RadioGroup.OnCheckedChangeListener {
    //一组通知消息
    private static final String GROUP_KEY_USER_NOTIFICE = "m.hp.customerdata";
    //是新加数据还是更新数据
    private static final String CHANNEL_ID = "0x111";
    //权限请求码
    private static final int PERMISSION_REQUEST = 100;
    private UserDataViewModel mUserDataViewModel;
    private ActivityMainBinding binding;
    //标题栏父控件
    private Toolbar parent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setCustomActionBar();
        initView();
        //检查是否获取到需要的权限
        requestPermissions();
        //查询当天是否有可续保的客户
        notificeCanBuy();
    }


    /**
     * 自定义ActionBar
     */
    private void setCustomActionBar() {

        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        @SuppressLint("InflateParams") View actionBarView = LayoutInflater.from(MainActivity.this).inflate(R.layout.customacitonbar_layout, null);
        TextView tvTitle = actionBarView.findViewById(R.id.actionBarTile);
        ImageView ivBack = actionBarView.findViewById(R.id.ivBack);
        ivBack.setVisibility(View.GONE);
        tvTitle.setText("承保清单");
        ActionBar supportActionBar = getSupportActionBar();
        assert supportActionBar != null;
        supportActionBar.setCustomView(actionBarView, layoutParams);
        supportActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        supportActionBar.setDisplayShowHomeEnabled(true);
        supportActionBar.setDisplayShowTitleEnabled(false);

        parent = (Toolbar) actionBarView.getParent();
        //去两边空白
        parent.setPadding(0, 0, 0, 0);
        parent.setContentInsetsAbsolute(0, 0);
    }

    /**
     * 初始化控件
     */
    private void initView() {

        MainFragment mainFragment = new MainFragment();
        CenterFragment centerFragment = new CenterFragment();
        MysFragment mysFragment = new MysFragment();
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(mainFragment);
        fragments.add(centerFragment);
        fragments.add(mysFragment);
        FragmentManager fragmentManager = getSupportFragmentManager();
        PagerAdapter pgAdapter = new MainPageAdapter(fragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, fragments);
        binding.mainVP.setAdapter(pgAdapter);
        binding.mainVP.addOnPageChangeListener(this);
        binding.radioGroup.setOnCheckedChangeListener(this);
        binding.allDataPage.setChecked(true);
        binding.allDataPage.setTextColor(Color.WHITE);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.allDataPage) {
            binding.allDataPage.setTextColor(Color.WHITE);
            binding.monthDay.setTextColor(Color.BLACK);
            binding.mys.setTextColor(Color.BLACK);
            binding.mainVP.setCurrentItem(0, true);
        } else if (checkedId == R.id.monthDay) {
            binding.allDataPage.setTextColor(Color.BLACK);
            binding.monthDay.setTextColor(Color.WHITE);
            binding.mys.setTextColor(Color.BLACK);
            binding.mainVP.setCurrentItem(1, true);
        } else if (checkedId == R.id.mys) {
            binding.allDataPage.setTextColor(Color.BLACK);
            binding.monthDay.setTextColor(Color.BLACK);
            binding.mys.setTextColor(Color.WHITE);
            binding.mainVP.setCurrentItem(2, true);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == 0) {
            binding.allDataPage.setChecked(true);
        } else if (position == 1) {
            binding.monthDay.setChecked(true);
        } else if (position == 2) {
            binding.mys.setChecked(true);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    /**
     * 提醒已有客户到续保时间
     */
    private void notificeCanBuy() {

        //获取当前的日期
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        //今天的日期几号
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        //30天内即可续保几号
        calendar.set(Calendar.DAY_OF_MONTH, day + 29);
        //30天内后的日期
        int dayAfter29 = calendar.get(Calendar.DATE);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        String canBuyDate = year + "/" + month + "/" + dayAfter29;
        Log.d("MainActivity", "canBuyDate is " + canBuyDate);
        //开始查数据
        //获取ViewModel
        ViewModelProvider.AndroidViewModelFactory androidViewModelFactory = new ViewModelProvider.AndroidViewModelFactory(getApplication());
        mUserDataViewModel = new ViewModelProvider(MainActivity.this, androidViewModelFactory).get(UserDataViewModel.class);
        mUserDataViewModel.getLastDateUsers(canBuyDate).observe(this, usersDataBeanList -> {
            showCanBuyNotification(usersDataBeanList);
        });

    }

    /**
     * 创建续保通知消息
     */
    private void showCanBuyNotification(List<UsersDataBean> lastDateUsers) {
        if (lastDateUsers.size() == 0) {
            return;
        }


        Notification notification;
        int notificeid = 1;//通知消息id
        int SUMMARY_ID = 0;//通知消息组id
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);
        //创建自定义渠道通知
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "my_channel", NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableLights(true);//桌面icon右上角展示小红点
        channel.setLightColor(Color.GREEN);//小红点颜色
        channel.setShowBadge(true);//长按桌面图标时显示此渠道的通知
        notificationManagerCompat.createNotificationChannel(channel);
        int size = lastDateUsers.size();
        for (int i = 0; i < size; i++) {
            //点击通知消息跳转到详细信息页面
            Intent intent = new Intent(MainActivity.this, DetailedActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //传递数据
            //根据条件查询到的userBean数据
            String MESSAGE_BEAN = "MESSAGE_BEAN";
            intent.putExtra(MESSAGE_BEAN, lastDateUsers.get(i));
            //i表示传递数据id，PendingIntent.FLAG_UPDATE_CURRENT表示更新当前的数据
            PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, i, intent, PendingIntent.FLAG_IMMUTABLE);
            //创建多个通知
            notification = new NotificationCompat.Builder(MainActivity.this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic__notifications)
                    .setContentTitle("我的承保清单")
                    .setContentText("客户" + lastDateUsers.get(i).getUserName() + "可以续保了，请及时跟进！")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setGroup(GROUP_KEY_USER_NOTIFICE)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build();
            notificationManagerCompat.notify(notificeid + i, notification);

        }

        //创建消息组
        Notification summaryNotification = new NotificationCompat.Builder(MainActivity.this, CHANNEL_ID)
                .setContentText("没有更多消息了")
                .setSmallIcon(R.drawable.ic__notifications)
                .setGroup(GROUP_KEY_USER_NOTIFICE)
                .setGroupSummary(true)
                .build();
        notificationManagerCompat.notify(SUMMARY_ID, summaryNotification);
    }


    /**
     * 申请权限
     */
    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST);
        }
    }

    /**
     * 权限申请处理结果
     *
     * @param requestCode  申请权限的请求码
     * @param permissions  申请的权限集合
     * @param grantResults 申请权限的结果集合
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST) {//判断用户是否同意了全部权限
            int length = permissions.length;
            //假设全部同意
            if (length == grantResults.length) {
                for (int i = 0; i < length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        break;
                    }
                }
            }
        }
    }
}
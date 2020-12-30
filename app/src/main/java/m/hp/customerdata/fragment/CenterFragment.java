package m.hp.customerdata.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import m.hp.customerdata.R;
import m.hp.customerdata.adapter.GetUsersByMonthAdapter;
import m.hp.customerdata.databinding.CenterfragmentLayoutBinding;
import m.hp.customerdata.entity.UsersDataBean;
import m.hp.customerdata.model.UserDataViewModel;
import m.hp.customerdata.myevents.SendAllDataList;
import m.hp.customerdata.view.MySectorView;

public class CenterFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private CenterfragmentLayoutBinding binding;
    private ArrayAdapter<String> monthAdapter;
    private GetUsersByMonthAdapter msgAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = CenterfragmentLayoutBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        String[] moth = {"1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"};

        monthAdapter = new ArrayAdapter<>(getContext(), R.layout.myspinner_layout, R.id.showMoth, moth);
        binding.selectMonth.setAdapter(monthAdapter);
        binding.selectMonth.setOnItemSelectedListener(this);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        ViewModelProvider.AndroidViewModelFactory androidViewModelFactory = ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication());
        UserDataViewModel userDataViewModel = new ViewModelProvider(this, androidViewModelFactory).get(UserDataViewModel.class);
        userDataViewModel.getAllUserData().observe(this, usersDataBeanList -> {
            Log.d("usersDataBeanList", "usersDataBeanList is " + usersDataBeanList.size());
            getAllDataList(usersDataBeanList);
        });
    }

    @Subscribe
    public void getUserList(SendAllDataList userList) {
        // getAllDataList(userList);
    }


    private void getAllDataList(List<UsersDataBean> userList) {

        Log.d("getUserList", "userListUserList is " + userList.size());
        if (userList == null || userList.size() == 0) {
            return;
        }
        double syPrice = 0;
        double jqPrice = 0;
        double jcPrice = 0;
        for (int i = 0; i < userList.size(); i++) {
            syPrice += userList.get(i).getSyPrice();
            jqPrice += userList.get(i).getJqPrice();
            jcPrice += userList.get(i).getJcPrice();
        }
        double total = syPrice + jqPrice + jcPrice;

        DecimalFormat decimalFormat = new DecimalFormat("#.00");

        binding.sectorView.setSectorRadius(200);
        binding.sectorView.setTitle("数据统计");
        //扇形颜色
        int[] colors = {Color.BLUE, Color.GREEN, Color.RED};
        //项目区域描述
        String[] describes = {"商业险: " + decimalFormat.format(syPrice), "交强险: " + decimalFormat.format(jqPrice), "驾乘险: " + decimalFormat.format(jcPrice), "总保费: " + decimalFormat.format(total)};
        //扇形起始角度
        int[] startAngles = {0, (int) (syPrice / total * 360), (int) (syPrice / total * 360) + (int) (jqPrice / total * 360)};
        //扇形面积角度
        int[] sweepAngles = {(int) (syPrice / total * 360), (int) (jqPrice / total * 360), 360 - ((int) (syPrice / total * 360) + (int) (jqPrice / total * 360))};
        List<MySectorView.SectorParams> sectorList = new ArrayList<>();
        MySectorView.SectorParams sectorParams = null;
        //
        List<Integer> colorList = new ArrayList<>();
        for (int color : colors) {
            colorList.add(color);
        }
        //项目区域描述
        List<String> dscList = new ArrayList<>();
        for (String describe : describes) {
            dscList.add(describe);
        }
        //扇形起始角度
        List<Integer> startAngleList = new ArrayList<>();
        for (int startAngle : startAngles) {
            startAngleList.add(startAngle);
        }
        //扇形面积角度
        List<Integer> sweepAngleList = new ArrayList<>();
        for (int sweepAngle : sweepAngles) {
            sweepAngleList.add(sweepAngle);
        }

        for (int i = 0; i < colorList.size(); i++) {
            sectorParams = new MySectorView.SectorParams();
            sectorParams.setColor(colorList.get(i));
            sectorParams.setStartAngle(startAngleList.get(i));
            sectorParams.setSweepAngle(sweepAngleList.get(i));
            sectorParams.setText(dscList.get(i));
            sectorList.add(sectorParams);
            Log.d("sectorParams", "getStartAngle is " + sectorParams.getStartAngle());
            Log.d("sectorParams", "getSweepAngle is " + sectorParams.getSweepAngle());
            Log.d("sectorParams", "getText is " + sectorParams.getText());
            Log.d("sectorParams", "getColor is " + sectorParams.getColor());
        }
        sectorParams = new MySectorView.SectorParams();
        sectorParams.setText(describes[3]);
        sectorParams.setColor(Color.GRAY);
        sectorList.add(sectorParams);
        binding.sectorView.addSectorParams(sectorList);
        Log.d("sectorParams", "总保费 is " + total);
        binding.sectorView.invalidate();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        msgAdapter = new GetUsersByMonthAdapter(new GetUsersByMonthAdapter.MessageBeanDiff(), getContext());
        String item = monthAdapter.getItem(position);
        binding.rvMsg.setAdapter(msgAdapter);
        binding.rvMsg.setLayoutManager(new LinearLayoutManager(getContext()));
        //Log.d("item", "select month is " + item.substring(0,item.lastIndexOf("月")));
        getUsersByMonth(item.substring(0, item.lastIndexOf("月")));
    }

    /**
     * 显示月数据
     *
     * @param month 月份
     */
    private void getUsersByMonth(String month) {
        ViewModelProvider.AndroidViewModelFactory androidViewModelFactory = ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication());
        UserDataViewModel userDataViewModel = new ViewModelProvider(this, androidViewModelFactory).get(UserDataViewModel.class);
        userDataViewModel.getUsersByMonth(month).observe(this, usersDataBeanList -> {
            msgAdapter.submitList(usersDataBeanList);
            Log.d("usersDataBeanList", "usersDataBeanList is " + usersDataBeanList.size());
        });
        msgAdapter.notifyDataSetChanged();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

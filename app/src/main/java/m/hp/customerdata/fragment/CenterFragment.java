package m.hp.customerdata.fragment;

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

import m.hp.customerdata.R;
import m.hp.customerdata.adapter.GetUsersByMonthAdapter;
import m.hp.customerdata.databinding.CenterfragmentLayoutBinding;
import m.hp.customerdata.model.UserDataViewModel;

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
        String[] moth = {"1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"};

        monthAdapter = new ArrayAdapter<>(getContext(), R.layout.myspinner_layout, R.id.showMoth, moth);
        binding.selectMonth.setAdapter(monthAdapter);
        binding.selectMonth.setOnItemSelectedListener(this);
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
}

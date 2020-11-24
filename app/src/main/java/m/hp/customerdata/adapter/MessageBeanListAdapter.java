package m.hp.customerdata.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import m.hp.customerdata.R;
import m.hp.customerdata.activity.DetailedActivity;
import m.hp.customerdata.entity.UsersDataBean;
import m.hp.customerdata.view.MyCheckBox;

public class MessageBeanListAdapter extends ListAdapter<UsersDataBean, MessageBeanListAdapter.MessageBeanViewHolder> {

    private final Context mContext;
    private final LayoutInflater mInflater;
    private static final String USER_BEAN = "USER_BEAN";
    private final HashMap<UsersDataBean, Boolean> checkMap;
    private static List<UsersDataBean> usersDataBeanList;
    private boolean isVisible;

    public void setCheckBoxIsVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    public MessageBeanListAdapter(@NonNull DiffUtil.ItemCallback<UsersDataBean> diffCallback, Context mContext) {
        super(diffCallback);
        mInflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
        checkMap = new HashMap<>();
        usersDataBeanList = new ArrayList<>();
    }

    @NonNull
    @Override
    public MessageBeanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MessageBeanListAdapter.MessageBeanViewHolder(mInflater.inflate(R.layout.rv_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MessageBeanViewHolder holder, int position) {
        //获取当前显示的bean实体
        UsersDataBean usersDataBean = getItem(position);
        int id = position + 1;
        String carNumber = usersDataBean.getCarNumber();
        String userName = usersDataBean.getUserName();
        String lastDate = usersDataBean.getLastDate();
        holder.tv_serial_number.setText(String.valueOf(id));
        holder.tv_car_number.setText(carNumber);
        holder.tv_user_name.setText(userName);
        holder.tv_last_date.setText(lastDate);
        //显示和隐藏
        if (isVisible) {
            holder.myCheckBox.setVisibility(View.VISIBLE);
        } else {
            holder.myCheckBox.setVisibility(View.GONE);
        }
        //检测已勾选的item
        if (checkMap.containsKey(usersDataBean)) {
            holder.myCheckBox.setChecked(checkMap.get(usersDataBean));
        } else {
            holder.myCheckBox.setChecked(false);
        }
    }

    class MessageBeanViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener, CompoundButton.OnCheckedChangeListener {


        private final TextView tv_serial_number;
        private final TextView tv_user_name;
        private final TextView tv_car_number;
        private final TextView tv_last_date;
        private final MyCheckBox myCheckBox;

        public MessageBeanViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_serial_number = itemView.findViewById(R.id.serial_number);
            tv_user_name = itemView.findViewById(R.id.user_name);
            tv_car_number = itemView.findViewById(R.id.car_number);
            tv_last_date = itemView.findViewById(R.id.last_date);
            myCheckBox = itemView.findViewById(R.id.checkUser);
            myCheckBox.setOnCheckedChangeListener(this);
            //点击跳转信息详情
            itemView.setOnClickListener(this);
            //长按弹出操作菜单
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            //跳转到详细信息界面
            UsersDataBean usersDataBean = getItem(getAdapterPosition());
            Intent intent = new Intent(mContext, DetailedActivity.class);
            String MESSAGE_BEAN = "MESSAGE_BEAN";
            intent.putExtra(MESSAGE_BEAN, usersDataBean);
            mContext.startActivity(intent);
        }


        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            int position = getAdapterPosition();

            String userName = getItem(position).getUserName();
            Intent intent = new Intent();
            String USER_NAME = "USER_NAME";
            intent.putExtra(USER_NAME, userName);

            UsersDataBean bean = getItem(position);
            Intent intentBean = new Intent();
            intentBean.putExtra(USER_BEAN, bean);
            menu.add(0, 3, 0, "编辑");
            menu.add(0, 1, 0, "修改").setIntent(intentBean);
            menu.add(0, 2, 0, "删除").setIntent(intent);
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            UsersDataBean usersDataBean = getItem(getAdapterPosition());

            if (isChecked) {
                checkMap.put(usersDataBean, true);
                usersDataBeanList.add(usersDataBean);
            } else {
                usersDataBeanList.remove(usersDataBean);
                checkMap.remove(usersDataBean);
            }
        }
    }

    /**
     * 是否全选
     *
     * @param checkedAll 是否全选
     */
    public void checkAll(boolean checkedAll) {
        List<UsersDataBean> currentList = getCurrentList();
        if (checkedAll) {
            for (int i = 0; i < currentList.size(); i++) {
                checkMap.put(currentList.get(i), true);
            }
        } else {
            for (int i = 0; i < currentList.size(); i++) {
                checkMap.put(currentList.get(i), false);
            }
        }
        notifyDataSetChanged();
    }


    public static class MessageBeanDiff extends DiffUtil.ItemCallback<UsersDataBean> {

        @Override
        public boolean areItemsTheSame(@NonNull UsersDataBean oldItem, @NonNull UsersDataBean newItem) {
            usersDataBeanList.clear();
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull UsersDataBean oldItem, @NonNull UsersDataBean newItem) {
            usersDataBeanList.clear();
            return oldItem.getUserName().equals(newItem.getUserName());
        }
    }

    /**
     * 获取被勾选的客户数据
     *
     * @return 被勾选的数据
     */
    public List<UsersDataBean> getCheckedUsers() {
        return usersDataBeanList;
    }
}

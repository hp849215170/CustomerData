package m.hp.customerdata.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import m.hp.customerdata.R;
import m.hp.customerdata.activity.DetailedActivity;
import m.hp.customerdata.entity.UsersDataBean;

public class SearchRVAdapter extends ListAdapter<UsersDataBean, SearchRVAdapter.MessageBeanViewHolder> {

    private Context mContext;
    private LayoutInflater mInflater;
    private String tag = getClass().getName();
    private final String MESSAGE_BEAN = "MESSAGE_BEAN";
    private final String USER_NAME = "USER_NAME";
    private static final String USER_BEAN = "USER_BEAN";

    public SearchRVAdapter(@NonNull DiffUtil.ItemCallback<UsersDataBean> diffCallback, Context mContext) {
        super(diffCallback);
        mInflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MessageBeanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchRVAdapter.MessageBeanViewHolder(mInflater.inflate(R.layout.rv_search_item, parent, false));
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
    }

    class MessageBeanViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {


        private TextView tv_serial_number;
        private TextView tv_user_name;
        private TextView tv_car_number;
        private TextView tv_last_date;

        public MessageBeanViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_serial_number = itemView.findViewById(R.id.serial_number);
            tv_user_name = itemView.findViewById(R.id.user_name);
            tv_car_number = itemView.findViewById(R.id.car_number);
            tv_last_date = itemView.findViewById(R.id.last_date);
            //点击跳转信息详情
            itemView.setOnClickListener(this);
            //长按弹出操作菜单
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            //跳转到详细信息界面
            UsersDataBean usersDataBean = getItem(getAdapterPosition());
            Log.d(tag, "messageBean==" + usersDataBean.getUserName());
            Intent intent = new Intent(mContext, DetailedActivity.class);
            intent.putExtra(MESSAGE_BEAN, usersDataBean);
            mContext.startActivity(intent);

        }


        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            int position = getAdapterPosition();

            String userName = getItem(position).getUserName();
            Intent intent = new Intent();
            intent.putExtra(USER_NAME, userName);

            UsersDataBean bean = getItem(position);
            Intent intentBean = new Intent();
            intentBean.putExtra(USER_BEAN, bean);
            menu.add(0, 1, 0, "修改").setIntent(intentBean);
            menu.add(0, 2, 0, "删除").setIntent(intent);
        }
    }

    public static class MessageBeanDiff extends DiffUtil.ItemCallback<UsersDataBean> {

        @Override
        public boolean areItemsTheSame(@NonNull UsersDataBean oldItem, @NonNull UsersDataBean newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull UsersDataBean oldItem, @NonNull UsersDataBean newItem) {
            return oldItem.getUserName().equals(newItem.getUserName());
        }
    }
}

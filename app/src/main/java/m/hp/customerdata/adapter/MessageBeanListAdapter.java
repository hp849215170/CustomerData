package m.hp.customerdata.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
import m.hp.customerdata.entity.MessageBean;

public class MessageBeanListAdapter extends ListAdapter<MessageBean, MessageBeanListAdapter.MessageBeanViewHolder> {

    private Context mContext;
    private LayoutInflater mInflater;
    private String tag = getClass().getName();
    private final String MESSAGE_BEAN = "MESSAGE_BEAN";

    public MessageBeanListAdapter(@NonNull DiffUtil.ItemCallback<MessageBean> diffCallback, Context mContext) {
        super(diffCallback);
        mInflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MessageBeanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MessageBeanListAdapter.MessageBeanViewHolder(mInflater.inflate(R.layout.rv_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MessageBeanViewHolder holder, int position) {

        MessageBean messageBean = getItem(position);
        int serialNumber = messageBean.getSerialNumber();
        String carNumber = messageBean.getCarNumber();
        String userName = messageBean.getUserName();
        String lastDate = messageBean.getLastDate();
        holder.tv_serial_number.setText(String.valueOf(serialNumber));
        holder.tv_car_number.setText(carNumber);
        holder.tv_user_name.setText(userName);
        holder.tv_last_date.setText(lastDate);
    }

    class MessageBeanViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            MessageBean messageBean = getItem(getAdapterPosition());
            Log.d(tag, "messageBean==" + messageBean.getUserName());

            Intent intent = new Intent(mContext, DetailedActivity.class);
            intent.putExtra(MESSAGE_BEAN, messageBean);
            mContext.startActivity(intent);

        }
    }


    public static class MessageBeanDiff extends DiffUtil.ItemCallback<MessageBean> {

        @Override
        public boolean areItemsTheSame(@NonNull MessageBean oldItem, @NonNull MessageBean newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull MessageBean oldItem, @NonNull MessageBean newItem) {
            return oldItem.getUserName().equals(newItem.getUserName());
        }
    }
}

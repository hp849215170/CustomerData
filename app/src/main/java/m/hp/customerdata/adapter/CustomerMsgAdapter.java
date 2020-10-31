/*
package m.hp.customerdata.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import m.hp.customerdata.R;
import m.hp.customerdata.activity.DetailedActivity;
import m.hp.customerdata.entity.MessageBean;

public class CustomerMsgAdapter extends RecyclerView.Adapter<CustomerMsgAdapter.MyViewHolder> {

    private Context mContext;
    private List<MessageBean> mList;
    private LayoutInflater mInflater;
    private String tag = getClass().getName();
    private final String MESSAGE_BEAN = "MESSAGE_BEAN";

    public CustomerMsgAdapter(Context mContext, List<MessageBean> mList) {
        this.mContext = mContext;
        this.mList = mList;
        mInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(mInflater.inflate(R.layout.rv_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        int serialNumber = mList.get(position).getSerialNumber();
        String carNumber = mList.get(position).getCarNumber();
        String userName = mList.get(position).getUserName();
        String lastDate = mList.get(position).getLastDate();
        holder.tv_serial_number.setText(String.valueOf(serialNumber));
        holder.tv_car_number.setText(carNumber);
        holder.tv_user_name.setText(userName);
        holder.tv_last_date.setText(lastDate);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tv_serial_number;
        private TextView tv_user_name;
        private TextView tv_car_number;
        private TextView tv_last_date;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_serial_number = itemView.findViewById(R.id.serial_number);
            tv_user_name = itemView.findViewById(R.id.user_name);
            tv_car_number = itemView.findViewById(R.id.car_number);
            tv_last_date = itemView.findViewById(R.id.last_date);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            MessageBean bean = mList.get(getAdapterPosition());
            Intent intent = new Intent(mContext, DetailedActivity.class);
            intent.putExtra(MESSAGE_BEAN, bean);
            mContext.startActivity(intent);
        }
    }
}
*/

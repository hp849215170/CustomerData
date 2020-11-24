package m.hp.customerdata.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import m.hp.customerdata.R;
import m.hp.customerdata.entity.DetailedMsgBean;

public class DetailedMsgAdapter extends RecyclerView.Adapter<DetailedMsgAdapter.MyViewHolder> {

    private final List<DetailedMsgBean> mList;
    private final LayoutInflater mInflater;

    public DetailedMsgAdapter(Context mContext, List<DetailedMsgBean> mList) {
        this.mList = mList;
        this.mInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(mInflater.inflate(R.layout.rv_detaileditem_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String detailedTitle = mList.get(position).getDetailedTitle();
        String detailedMessage = mList.get(position).getDetailedMessage();
        holder.tv_detailedTitle.setText(detailedTitle);
        holder.tv_detailedMessage.setText(detailedMessage);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView tv_detailedTitle;
        private final TextView tv_detailedMessage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_detailedTitle = itemView.findViewById(R.id.detailed_title);
            tv_detailedMessage = itemView.findViewById(R.id.detailed_message);
        }
    }
}

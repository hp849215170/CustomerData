package m.hp.customerdata.adapter;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import m.hp.customerdata.R;
import m.hp.customerdata.entity.DirectoryBean;

/**
 * @author huangping
 */
public class ShowDirectoryAdapter extends RecyclerView.Adapter<ShowDirectoryAdapter.MyViewHolder> {

    private final List<DirectoryBean> mList;
    private final LayoutInflater mInflater;
    private final Handler currentDirHandler;

    public ShowDirectoryAdapter(Context mContext, List<DirectoryBean> mList, Handler setCurrentDirHandler) {
        this.mList = mList;
        mInflater = LayoutInflater.from(mContext);
        currentDirHandler = setCurrentDirHandler;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(mInflater.inflate(R.layout.dir_items_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        String dirName = mList.get(position).getDirName();
        String lastModifyTime = mList.get(position).getLastModifyTime();
        holder.tvDirName.setText(dirName);
        holder.tvLastModifyName.setText(lastModifyTime);
        holder.ivDirImg.setImageResource(R.drawable.ic_folder);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView tvDirName;
        private final TextView tvLastModifyName;

        private final ImageView ivDirImg;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDirName = itemView.findViewById(R.id.dirName);
            tvLastModifyName = itemView.findViewById(R.id.lastModifyTime);
            ivDirImg = itemView.findViewById(R.id.dirImg);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            String currentDir = mList.get(getAdapterPosition()).getCurrentDir();
            //handler把当前路径发送给ShowDirectoryActivity去显示
            Message message = Message.obtain();
            Bundle bundle = new Bundle();
            bundle.putString("DIR", currentDir);
            message.setData(bundle);
            currentDirHandler.sendMessage(message);
        }
    }
}

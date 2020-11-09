package m.hp.customerdata.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import m.hp.customerdata.R;
import m.hp.customerdata.activity.ShowDirectoryActivity;
import m.hp.customerdata.entity.DirectoryBean;

public class ShowDirectoryAdapter extends RecyclerView.Adapter<ShowDirectoryAdapter.MyViewHolder> {

    private Context mContext;
    private List<DirectoryBean> mList;
    private LayoutInflater mInflater;
    private String tag = getClass().getName();

    public ShowDirectoryAdapter(Context mContext, List<DirectoryBean> mList) {
        this.mContext = mContext;
        this.mList = mList;
        mInflater = LayoutInflater.from(mContext);
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
        /* int folderImg = mList.get(position).getFolderImgId();*/
        holder.tvDirName.setText(dirName);
        holder.tvLastModifyName.setText(lastModifyTime);
        holder.ivDirImg.setImageResource(R.drawable.ic_folder);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvDirName;
        private TextView tvLastModifyName;

        private ImageView ivDirImg;

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
            Log.d("currentDir", currentDir);
            ShowDirectoryActivity.instance.getCurrentDir(currentDir);
        }
    }
}

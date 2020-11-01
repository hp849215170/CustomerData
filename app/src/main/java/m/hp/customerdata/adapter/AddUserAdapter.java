package m.hp.customerdata.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;

import m.hp.customerdata.R;
import m.hp.customerdata.activity.AddUserActivity;
import m.hp.customerdata.entity.DetailedMsgBean;

public class AddUserAdapter extends RecyclerView.Adapter<AddUserAdapter.MyViewHolder> {

    private Context mContext;
    private List<DetailedMsgBean> mList;
    private LayoutInflater mInflater;
    private static final String TAG = "AddUserAdapter";
    private HashMap<String, String> hashMap = new HashMap<>();//存放item数据
    public static AddUserAdapter instance;

    public AddUserAdapter(Context mContext, List<DetailedMsgBean> mList) {
        this.mContext = mContext;
        this.mList = mList;
        mInflater = LayoutInflater.from(mContext);
        instance = this;
    }

    /**
     * 用于传递输入的数据
     *
     * @return
     */
    public HashMap<String, String> getHashMap() {
        return hashMap;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(mInflater.inflate(R.layout.rv_add_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tv_column_name.setText(mList.get(position).getDetailedTitle());

        holder.et_column_value.setHint(mList.get(position).getDetailedMessage());

        //解决EditText数据错乱问题
        //1、先移除ViewHolder里Item条目EditText的监听TextWatcher
        if (holder.et_column_value.getTag() != null && holder.et_column_value.getTag() instanceof TextWatcher) {
            //判断当前EditText的标记是否是我们标记过的，如果不是就移除原来的监听
            holder.et_column_value.removeTextChangedListener((TextWatcher) holder.et_column_value.getTag());
        }
        //2、新建TextWatcher监听
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //光标移动到文字最后面
                holder.et_column_value.setSelection(s.toString().length());
            }

            @Override
            public void afterTextChanged(Editable s) {
                //3、保存当前数据的数据和item的位置Position
                addTextToList(position, s.toString());
            }
        };

        //设置输入数据类型
        setInputType(holder, mList.get(position).getDetailedTitle());
        //4、把保存了位置的数据显示在对应的位置上
        holder.et_column_value.setText(hashMap.get(holder.tv_column_name.getText().toString()));
        //5、给EditText添加新建的TextWatcher监听
        holder.et_column_value.addTextChangedListener(textWatcher);
        //6、标记EditText
        holder.et_column_value.setTag(textWatcher);
        //更新数据时显示要改的数据
        if (!AddUserActivity.instance.isAdd) {
            holder.et_column_value.setText(mList.get(position).getDetailedMessage());
        }
    }


    private void addTextToList(int position, String s) {

        String title = mList.get(position).getDetailedTitle();
        Log.d(TAG, "title==" + title);
        hashMap.put(title, s);
        //用于返回数据给其他地方用，与解决错乱没有关系
        getHashMap();
    }

    private void setInputType(MyViewHolder holder, String title) {
        if (title.equals("投保人：")) {
            holder.et_column_value.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        }
        if (title.equals("车牌：")) {
            holder.et_column_value.setInputType(InputType.TYPE_CLASS_TEXT);
        }
        if (title.equals("终保时间：")) {
            holder.et_column_value.setInputType(InputType.TYPE_DATETIME_VARIATION_DATE);
        }
        if (title.equals("承保时间：")) {
            holder.et_column_value.setInputType(InputType.TYPE_DATETIME_VARIATION_DATE);
        }
        if (title.equals("车架号：")) {
            holder.et_column_value.setInputType(InputType.TYPE_CLASS_TEXT);
        }
        if (title.equals("手机号：")) {
            holder.et_column_value.setInputType(InputType.TYPE_TEXT_VARIATION_PHONETIC | InputType.TYPE_CLASS_PHONE);
        }
        if (title.equals("商业险费用：")) {
            holder.et_column_value.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
        }
        if (title.equals("交强险费用：")) {
            holder.et_column_value.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
        }
        if (title.equals("驾乘险费用：")) {
            holder.et_column_value.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
        }
        if (title.equals("商业险费率：")) {
            holder.et_column_value.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
        }
        if (title.equals("交强险费率：")) {
            holder.et_column_value.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
        }
        if (title.equals("驾乘险费率：")) {
            holder.et_column_value.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
        }
        if (title.equals("返现：")) {
            holder.et_column_value.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
        }
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_column_name;
        private EditText et_column_value;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_column_name = itemView.findViewById(R.id.column_name);
            et_column_value = itemView.findViewById(R.id.column_value);
        }
    }

}

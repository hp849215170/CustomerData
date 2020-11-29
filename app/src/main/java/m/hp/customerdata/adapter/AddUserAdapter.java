package m.hp.customerdata.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
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
import m.hp.customerdata.entity.DetailedMsgBean;

public class AddUserAdapter extends RecyclerView.Adapter<AddUserAdapter.MyViewHolder> {

    private final List<DetailedMsgBean> mList;
    private final LayoutInflater mInflater;
    private final HashMap<String, String> hashMap = new HashMap<>();//存放item数据
    private boolean isAdd = true;

    public AddUserAdapter(Context mContext, List<DetailedMsgBean> mList) {
        this.mList = mList;
        mInflater = LayoutInflater.from(mContext);

    }

    public void isAdd(boolean isAdd) {
        this.isAdd = isAdd;
    }

    /**
     * 用于传递输入的数据
     *
     * @return 返回用户数据集合
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
        //解决EditText数据错乱问题
        //1、先移除ViewHolder里Item条目EditText的监听TextWatcher
        if (holder.et_column_value.getTag() != null && holder.et_column_value.getTag() instanceof TextWatcher) {
            //销毁TextWatcher实例
            holder.et_column_value.removeTextChangedListener((TextWatcher) holder.et_column_value.getTag());
        }

        //2、新建TextWatcher监听
        TextWatcher textWatcher = new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //光标移动到当前输入的文字后面
                //System.out.println("getSelectionEnd---->"+holder.et_column_value.getSelectionEnd());
                holder.et_column_value.setSelection(holder.et_column_value.getSelectionEnd());
            }

            @Override
            public void afterTextChanged(Editable s) {
                //3、保存当前数据的数据和item的位置Position
                addTextToList(position, s.toString());
            }
        };
        //设置输入数据类型
        //setMyInputType(holder, mList.get(position).getDetailedTitle());
        //4、把保存了位置的数据显示在对应的位置上
        holder.et_column_value.setText(hashMap.get(holder.tv_column_name.getText().toString()));
        //5、给EditText添加新建的TextWatcher监听
        holder.et_column_value.addTextChangedListener(textWatcher);
        //6、标记EditText
        holder.et_column_value.setTag(textWatcher);

        if (!isAdd) {
            //更新数据
            holder.et_column_value.setText(mList.get(position).getDetailedMessage());
        } else {
            //添加新数据
            holder.et_column_value.setHint(mList.get(position).getDetailedMessage());
        }
        //设置EditText输入数据类型
        setMyInputType(holder, mList.get(position).getDetailedTitle());
    }

    private void addTextToList(int position, String s) {
        String title = mList.get(position).getDetailedTitle();
        hashMap.put(title, s);
        //用于返回数据给其他地方用，与解决错乱没有关系
        getHashMap();
    }

    private void setMyInputType(MyViewHolder holder, String title) {

        if (title.equals("投保人")) {
            holder.et_column_value.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        }
        if (title.equals("车牌号")) {
            holder.et_column_value.setInputType(InputType.TYPE_CLASS_TEXT);
        }
        if (title.equals("终保时间")) {
            holder.et_column_value.setInputType(InputType.TYPE_DATETIME_VARIATION_DATE);
        }
        if (title.equals("承保时间")) {
            holder.et_column_value.setInputType(InputType.TYPE_DATETIME_VARIATION_DATE);
        }
        if (title.equals("车架号")) {
            holder.et_column_value.setInputType(InputType.TYPE_CLASS_TEXT);
        }
        if (title.equals("手机号")) {
            holder.et_column_value.setInputType(InputType.TYPE_TEXT_VARIATION_PHONETIC | InputType.TYPE_CLASS_PHONE);
        }
        if (title.equals("商业险费用")) {
            holder.et_column_value.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
        }
        if (title.equals("交强险费用")) {
            holder.et_column_value.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
        }
        if (title.equals("驾乘险费用")) {
            holder.et_column_value.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
        }
        if (title.equals("商业险费率")) {
            holder.et_column_value.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
        }
        if (title.equals("交强险费率")) {
            holder.et_column_value.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
        }
        if (title.equals("驾乘险费率")) {
            holder.et_column_value.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
        }
        if (title.equals("返现")) {
            holder.et_column_value.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
        }
        if (title.equals("客户来源")) {
            holder.et_column_value.setInputType(InputType.TYPE_CLASS_TEXT);
        }
        if (title.equals("备注")) {
            holder.et_column_value.setInputType(InputType.TYPE_CLASS_TEXT);
        }
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView tv_column_name;
        private final EditText et_column_value;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_column_name = itemView.findViewById(R.id.column_name);
            et_column_value = itemView.findViewById(R.id.column_value);
        }
    }

}

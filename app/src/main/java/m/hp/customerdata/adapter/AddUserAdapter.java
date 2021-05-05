package m.hp.customerdata.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
    //edittext的焦点位置
    private int etFocusPosition = -1;
    //edittext里的文字内容集合
    private SparseArray<String> etTextArray = new SparseArray<>();

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            //保存修改后的文字
            etTextArray.put(etFocusPosition, s.toString());
            addTextToList(etFocusPosition, s.toString());
        }
    };

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
    public synchronized void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tv_column_name.setText(mList.get(position).getDetailedTitle());
        holder.et_column_value.setHint(mList.get(position).getDetailedMessage());

        holder.et_column_value.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                etFocusPosition = position;
            }
        });


        if (!isAdd) {
            //更新数据
            holder.et_column_value.setHint(mList.get(position).getDetailedMessage());
        } else {
            //添加新数据
            holder.et_column_value.setText(etTextArray.get(position));
        }
        //设置EditText输入数据类型
        setMyInputType(holder, mList.get(position).getDetailedTitle());
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull MyViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.et_column_value.removeTextChangedListener(textWatcher);
        holder.et_column_value.clearFocus();
        if (etFocusPosition == holder.getAdapterPosition()) {

        }
    }

    @Override
    public void onViewAttachedToWindow(@NonNull MyViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.et_column_value.addTextChangedListener(textWatcher);
        if (etFocusPosition == holder.getAdapterPosition()) {
            holder.et_column_value.requestFocus();
            holder.et_column_value.setSelection(holder.et_column_value.getText().length());
        }
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

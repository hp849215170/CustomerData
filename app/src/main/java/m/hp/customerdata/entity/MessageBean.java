package m.hp.customerdata.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;

@Entity(tableName = "users_table")//数据库表名
@ParcelablePlease
public class MessageBean implements Parcelable {

    @ColumnInfo(name = "serial_number")
    int serialNumber;//序号
    @ColumnInfo(name = "car_number")
    String carNumber;//车牌号
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "user_name")
    String userName;//投保人
    @ColumnInfo(name = "last_date")
    String lastDate;//终保时间
    @ColumnInfo(name = "by_time")
    String buyTime;//承保时间
    @ColumnInfo(name = "car_serial_number")
    String carSerialNumber;//车架号
    @ColumnInfo(name = "phone")
    String phone;//手机号
    @ColumnInfo(name = "sy_price")
    double syPrice;//商业险费用
    @ColumnInfo(name = "jq_price")
    double jqPrice;//交强险费用
    @ColumnInfo(name = "jc_price")
    double jcPrice;//驾乘险费用
    @ColumnInfo(name = "s_rebate")
    double syRebate;//商业险费率
    @ColumnInfo(name = "jq_rebate")
    double jqRebate;//交强险费率
    @ColumnInfo(name = "jc_rebate")
    double jcRebate;//驾乘险费率
    @ColumnInfo(name = "cash_back")
    double cashBack;//返现多少
    @ColumnInfo(name = "type")
    String type;//客户来源
    @ColumnInfo(name = "remark")
    String remarks;//备注

    public int getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(int serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLastDate() {
        return lastDate;
    }

    public void setLastDate(String lastDate) {
        this.lastDate = lastDate;
    }

    public String getBuyTime() {
        return buyTime;
    }

    public void setBuyTime(String buyTime) {
        this.buyTime = buyTime;
    }

    public String getCarSerialNumber() {
        return carSerialNumber;
    }

    public void setCarSerialNumber(String carSerialNumber) {
        this.carSerialNumber = carSerialNumber;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public double getSyPrice() {
        return syPrice;
    }

    public void setSyPrice(double syPrice) {
        this.syPrice = syPrice;
    }

    public double getJqPrice() {
        return jqPrice;
    }

    public void setJqPrice(double jqPrice) {
        this.jqPrice = jqPrice;
    }

    public double getJcPrice() {
        return jcPrice;
    }

    public void setJcPrice(double jcPrice) {
        this.jcPrice = jcPrice;
    }

    public double getSyRebate() {
        return syRebate;
    }

    public void setSyRebate(double syRebate) {
        this.syRebate = syRebate;
    }

    public double getJqRebate() {
        return jqRebate;
    }

    public void setJqRebate(double jqRebate) {
        this.jqRebate = jqRebate;
    }

    public double getJcRebate() {
        return jcRebate;
    }

    public void setJcRebate(double jcRebate) {
        this.jcRebate = jcRebate;
    }

    public double getCashBack() {
        return cashBack;
    }

    public void setCashBack(double cashBack) {
        this.cashBack = cashBack;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     * 序列化对象传递数据
     *
     * @return
     */
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        MessageBeanParcelablePlease.writeToParcel(this, dest, flags);
    }

    public static final Creator<MessageBean> CREATOR = new Creator<MessageBean>() {
        public MessageBean createFromParcel(Parcel source) {
            MessageBean target = new MessageBean();
            MessageBeanParcelablePlease.readFromParcel(target, source);
            return target;
        }

        public MessageBean[] newArray(int size) {
            return new MessageBean[size];
        }
    };
}

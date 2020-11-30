package m.hp.customerdata.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Objects;

@Entity(tableName = "users_table")//数据库表名
public class UsersDataBean implements Serializable, Comparable<UsersDataBean> {
    @Ignore
    public static final long serialVersionUID = 1L;
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    int id;//序号
    @ColumnInfo(name = "car_number")
    String carNumber;//车牌号
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

    @Ignore
    public UsersDataBean() {
    }

    public UsersDataBean(String carNumber, @NonNull String userName, String lastDate, String buyTime,
                         String carSerialNumber, String phone, double syPrice, double jqPrice, double jcPrice,
                         double syRebate, double jqRebate, double jcRebate, double cashBack, String type, String remarks) {
        this.carNumber = carNumber;
        this.userName = userName;
        this.lastDate = lastDate;
        this.buyTime = buyTime;
        this.carSerialNumber = carSerialNumber;
        this.phone = phone;
        this.syPrice = syPrice;
        this.jqPrice = jqPrice;
        this.jcPrice = jcPrice;
        this.syRebate = syRebate;
        this.jqRebate = jqRebate;
        this.jcRebate = jcRebate;
        this.cashBack = cashBack;
        this.type = type;
        this.remarks = remarks;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    @NotNull
    public String getUserName() {
        return userName;
    }

    public void setUserName(@NotNull String userName) {
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
     * 排序
     *
     * @param usersDataBean 客户信息实体
     * @return 排序规则标志
     */

    @Override
    public int compareTo(UsersDataBean usersDataBean) {
        return this.userName.compareTo(usersDataBean.getUserName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsersDataBean that = (UsersDataBean) o;
        return id == that.id &&
                Double.compare(that.syPrice, syPrice) == 0 &&
                Double.compare(that.jqPrice, jqPrice) == 0 &&
                Double.compare(that.jcPrice, jcPrice) == 0 &&
                Double.compare(that.syRebate, syRebate) == 0 &&
                Double.compare(that.jqRebate, jqRebate) == 0 &&
                Double.compare(that.jcRebate, jcRebate) == 0 &&
                Double.compare(that.cashBack, cashBack) == 0 &&
                Objects.equals(carNumber, that.carNumber) &&
                userName.equals(that.userName) &&
                Objects.equals(lastDate, that.lastDate) &&
                Objects.equals(buyTime, that.buyTime) &&
                Objects.equals(carSerialNumber, that.carSerialNumber) &&
                Objects.equals(phone, that.phone) &&
                Objects.equals(type, that.type) &&
                Objects.equals(remarks, that.remarks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, carNumber, userName, lastDate, buyTime, carSerialNumber, phone, syPrice, jqPrice, jcPrice, syRebate, jqRebate, jcRebate, cashBack, type, remarks);
    }
}

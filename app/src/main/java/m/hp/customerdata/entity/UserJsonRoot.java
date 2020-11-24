package m.hp.customerdata.entity;

import java.util.ArrayList;
import java.util.List;

public class UserJsonRoot {
    String option;
    List<UsersDataBean> userList = new ArrayList<>();
    //不能删，fastjson结构用到
    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }
    //不能删，fastjson结构用到
    public List<UsersDataBean> getUserList() {
        return userList;
    }

    public void setUserList(List<UsersDataBean> userList) {
        this.userList = userList;
    }
}

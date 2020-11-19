package m.hp.customerdata.entity;

import java.util.ArrayList;
import java.util.List;

public class UserJsonRoot {
    String option;
    List<UsersDataBean> userList = new ArrayList<>();

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public List<UsersDataBean> getUserList() {
        return userList;
    }

    public void setUserList(List<UsersDataBean> userList) {
        this.userList = userList;
    }
}

package m.hp.customerdata.myevents;

import java.util.List;

import m.hp.customerdata.entity.UsersDataBean;

public class SendAllDataList {

    private List<UsersDataBean> userList;

    public SendAllDataList(List<UsersDataBean> userList) {
        this.userList = userList;
    }

    public List<UsersDataBean> getUserList(){
        return this.userList;
    }
}

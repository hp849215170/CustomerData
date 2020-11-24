package m.hp.customerdata.utils;

import android.annotation.SuppressLint;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import m.hp.customerdata.entity.DirectoryBean;

public class MyFileUtils {

    public List<DirectoryBean> getDirs(String dir) {

        File file = new File(dir);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        //DirectoryBean list 集合
        List<DirectoryBean> directoryBeanList = new ArrayList<>();
        //当前是文件夹
        if (file.isDirectory()) {
            //文件夹列表
            String[] files = file.list();
            for (String filesName : files) {
                //文件夹名
                File dirFile = new File(filesName);
                //当前是文件还是文件夹
                File testFile = new File(dir + "/" + filesName);
                //是文件夹保存文件夹名字和路径
                if (testFile.isDirectory()) {
                    //最后修改时间
                    //获取文件夹最后修改时间
                    long lastModified = dirFile.lastModified();
                    ////当前文件夹所在的绝对路径
                    String currentDir = dirFile.getAbsolutePath();
                    //格式化时间
                    String formatDate = simpleDateFormat.format(lastModified);
                    //把文件夹信息保存到DirectoryBean实体
                    DirectoryBean directoryBean = new DirectoryBean();
                    //文件夹名
                    directoryBean.setDirName(filesName);
                    //最后修改时间
                    directoryBean.setLastModifyTime(formatDate);
                    //当前文件夹所在的完整路径
                    directoryBean.setCurrentDir(dir + currentDir);
                    //添加到directoryBeanList
                    directoryBeanList.add(directoryBean);
                }
            }
            return directoryBeanList;
        }
        return null;
    }
}

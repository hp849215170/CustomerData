package m.hp.customerdata.poiexcel;

import android.annotation.SuppressLint;
import android.content.Context;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import m.hp.customerdata.entity.UsersDataBean;

public class ExportUsersDateExcel {
    //标题
    private final String[] titles = {"车牌号码", "投保人", "终保时间", "承保时间", "车架号", "手机号", "商业险", "交强险", "驾乘险", "商业险费率", "交强险费率", "驾乘险费率", "返现", "客户来源", "备注"};
    private final List<UsersDataBean> usersDataBeanList;
    private final ExportFinish exportFinish;
    private String outputPath;
    private final String savePath;

    public ExportUsersDateExcel(List<UsersDataBean> usersDataBeanList, ExportFinish exportFinish, String savePath) {
        this.usersDataBeanList = usersDataBeanList;
        this.exportFinish = exportFinish;
        this.savePath = savePath;
        thread.start();
    }

    /**
     * 线程执行完毕回调函数
     */
    public interface ExportFinish {
        void finishCallback(boolean ok, String exportPath);
    }

    /**
     * 开线程后台导出
     */
    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            exportFinish.finishCallback(exportExcel(), outputPath);
        }
    });

    /**
     * 导出Excel数据
     *
     * @return 导出结果
     */
    private boolean exportExcel() {

        XSSFWorkbook workbook = new XSSFWorkbook();
        //创建Excel表，工作表名为mysheet
        XSSFSheet sheet = workbook.createSheet(WorkbookUtil.createSafeSheetName("mysheet"));
        //加一行标题栏
        for (int r = 0; r < usersDataBeanList.size() + 1; r++) {
            //数据
            String[] value;
            if (r == 0) {
                //标题栏
                value = titles;
            } else {
                //从数据库获取到要到出的数据
                value = new String[]{usersDataBeanList.get(r-1).getCarNumber(), usersDataBeanList.get(r-1).getUserName(), usersDataBeanList.get(r-1).getLastDate(), usersDataBeanList.get(r-1).getBuyTime()
                        , usersDataBeanList.get(r-1).getCarSerialNumber(), usersDataBeanList.get(r-1).getPhone(), String.valueOf(usersDataBeanList.get(r-1).getSyPrice()), String.valueOf(usersDataBeanList.get(r-1).getJqPrice())
                        , String.valueOf(usersDataBeanList.get(r-1).getJcPrice()), String.valueOf(usersDataBeanList.get(r-1).getSyRebate()), String.valueOf(usersDataBeanList.get(r-1).getJqRebate())
                        , String.valueOf(usersDataBeanList.get(r-1).getJcRebate()), String.valueOf(usersDataBeanList.get(r-1).getCashBack()), usersDataBeanList.get(r-1).getType(), usersDataBeanList.get(r-1).getRemarks()};
            }
            Row row = sheet.createRow(r);
            for (int c = 0; c < 14; c++) {
                //数据栏
                row.createCell(c).setCellValue(value[c]);
            }
        }
        //导出Excel的文件名
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
        String formatDate = simpleDateFormat.format(new Date());
        //Excel文件名
        String outFileName = "user-data-" + formatDate + ".xlsx";
        //导出用户选择的目录
        File exportDir = new File(savePath);
        if (!exportDir.exists()) {
            exportDir.mkdir();
        }
        File file = new File(exportDir, outFileName);
        //保存的路径
        outputPath = file.getAbsolutePath();
        try {
            OutputStream outputStream = new FileOutputStream(outputPath);
            workbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}

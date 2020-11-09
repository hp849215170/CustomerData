package m.hp.customerdata.poiexcel;

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
    private String[] titles = {"车牌号码", "投保人", "终保时间", "承保时间", "车架号", "手机号", "商业险", "交强险", "驾乘险", "商业险费率", "交强险费率", "驾乘险费率", "返现", "客户来源", "备注"};
    //数据
    private String[] value;
    private List<UsersDataBean> usersDataBeanList;
    private Context context;
    private ExportFinish exportFinish;
    private String outputPath;
    private String savePath;

    public ExportUsersDateExcel(Context context, List<UsersDataBean> usersDataBeanList, ExportFinish exportFinish, String savePath) {
        this.usersDataBeanList = usersDataBeanList;
        this.context = context;
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
     * @return
     */
    private boolean exportExcel() {
        XSSFWorkbook workbook = new XSSFWorkbook();
        //创建Excel表，工作表名为mysheet
        XSSFSheet sheet = workbook.createSheet(WorkbookUtil.createSafeSheetName("mysheet"));
        for (int r = 0; r < usersDataBeanList.size(); r++) {
            //从数据库获取到要到出的数据
            value = new String[]{usersDataBeanList.get(r).getCarNumber(), usersDataBeanList.get(r).getUserName(), usersDataBeanList.get(r).getLastDate(), usersDataBeanList.get(r).getBuyTime()
                    , usersDataBeanList.get(r).getCarSerialNumber(), usersDataBeanList.get(r).getPhone(), String.valueOf(usersDataBeanList.get(r).getSyPrice()), String.valueOf(usersDataBeanList.get(r).getJqPrice())
                    , String.valueOf(usersDataBeanList.get(r).getJcPrice()), String.valueOf(usersDataBeanList.get(r).getSyRebate()), String.valueOf(usersDataBeanList.get(r).getJqRebate())
                    , String.valueOf(usersDataBeanList.get(r).getJcRebate()), String.valueOf(usersDataBeanList.get(r).getCashBack()), usersDataBeanList.get(r).getType(), usersDataBeanList.get(r).getRemarks()};
            Row row = sheet.createRow(r);
            for (int c = 0; c < 14; c++) {
                if (r == 0) {
                    //标题栏
                    row.createCell(c).setCellValue(titles[c]);
                } else {
                    //数据栏
                    row.createCell(c).setCellValue(value[c]);
                }
            }
        }
        //导出Excel的文件名
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
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

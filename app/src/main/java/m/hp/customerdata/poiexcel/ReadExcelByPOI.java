package m.hp.customerdata.poiexcel;

import android.util.Log;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import m.hp.customerdata.entity.UsersDataBean;

/**
 * POI解析Excel表格
 */
public class ReadExcelByPOI {

    private String excelFile;
    private ReadFinish finishCallback;

    public ReadExcelByPOI(String excelFile, ReadFinish finishCallback) {
        this.excelFile = excelFile;
        thread.start();
        this.finishCallback = finishCallback;
    }

    /**
     * 线程执行完毕回调函数
     */
    public interface ReadFinish {
        void finishCallback(List<UsersDataBean> usersDataBeanList);
    }

    /**
     * 开线程解析Excel，防止ANR
     */
    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            finishCallback.finishCallback(readExcel(excelFile));
        }
    });

    /**
     * 解析Excel
     *
     * @param excelFile
     * @return
     */
    public List<UsersDataBean> readExcel(String excelFile) {
        File file = new File(excelFile);

        if (!file.exists()) {
            Log.d("readExcel", "没有找到文件");
            return null;
        }
        //获取文件的扩展名
        String extString = excelFile.substring(excelFile.indexOf("."));
        try {
            InputStream stream = new FileInputStream(file);
            int rowCount = 0;
            Sheet sheet = null;
            FormulaEvaluator formulaEvaluator = null;
            if (extString.equals(".xls")) {//xls格式Excel
                HSSFWorkbook hssfWorkbook = new HSSFWorkbook(stream);
                sheet = hssfWorkbook.getSheetAt(0);
                rowCount = sheet.getPhysicalNumberOfRows();
                formulaEvaluator = hssfWorkbook.getCreationHelper().createFormulaEvaluator();
            } else if (extString.equals(".xlsx")) {//xlsx格式Excel
                //传入要加载的Excel xlsx文件
                XSSFWorkbook workbook = new XSSFWorkbook(stream);
                //读取工作表0，即sheet1表
                sheet = workbook.getSheetAt(0);
                //获取有数据的行数
                formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
                rowCount = sheet.getPhysicalNumberOfRows();
            } else {
                return null;
            }

            //用户数据集合
            List<UsersDataBean> usersDataBeanList = new ArrayList<>();
            for (int r = 1; r < rowCount; r++) {//第0行是标题，从第1行开始才是用户用户数据
                //遍历有数据的行
                Row row = sheet.getRow(r);
                //获取一行有数据的单元格格数
                int cellCount = row.getPhysicalNumberOfCells();
                //每个用户的数据
                UsersDataBean usersDataBean = new UsersDataBean();
                for (int c = 0; c < cellCount; c++) {
                    //获取每个单元格的数据
                    String value = getCellAsString(row, c, formulaEvaluator);
                   // System.out.println("第[" + r + "]行，第[" + c + "]个单元格：" + value);

                    switch (c) {
                        case 0:
                            usersDataBean.setCarNumber(value);
                            break;
                        case 1:
                            usersDataBean.setUserName(value);
                            break;
                        case 2:
                            usersDataBean.setLastDate(value);
                            break;
                        case 3:
                            usersDataBean.setBuyTime(value);
                            break;
                        case 4:
                            usersDataBean.setCarSerialNumber(value);
                            break;
                        case 5:
                            usersDataBean.setPhone(value);
                            break;
                        case 6:
                            usersDataBean.setSyPrice(Double.valueOf(value));
                            break;
                        case 7:
                            usersDataBean.setJqPrice(Double.valueOf(value));
                            break;
                        case 8:
                            usersDataBean.setJcPrice(Double.valueOf(value));
                            break;
                        case 9:
                            usersDataBean.setSyRebate(Double.valueOf(value));
                            break;
                        case 10:
                            usersDataBean.setJqRebate(Double.valueOf(value));
                            break;
                        case 11:
                            usersDataBean.setJcRebate(Double.valueOf(value));
                            break;
                        case 12:
                            usersDataBean.setCashBack(Double.valueOf(value));
                            break;
                        case 13:
                            usersDataBean.setType(value);
                            break;
                        case 14:
                            usersDataBean.setRemarks(value);
                            break;
                    }

                }
                usersDataBeanList.add(usersDataBean);
            }
            return usersDataBeanList;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取每个单元格数据
     *
     * @param row              行数
     * @param c                单元格
     * @param formulaEvaluator
     * @return 以字符串保存的单元格数据
     */
    private String getCellAsString(Row row, int c, FormulaEvaluator formulaEvaluator) {
        //格式化数据一个数字，不包括 0
        DecimalFormat decimalFormat = new DecimalFormat("#");
        String value = "";
        //获取单元格的数据
        Cell cell = row.getCell(c);
        CellValue cellValue = formulaEvaluator.evaluate(cell);
        //判断单元格数据的数据类型
        switch (cellValue.getCellType()) {
            case Cell.CELL_TYPE_BOOLEAN:
                value = "" + cellValue.getBooleanValue();
                break;
            case Cell.CELL_TYPE_NUMERIC:

                double numberValue = cellValue.getNumberValue();
                //判断单元格数值类型是否是日期
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    double date = cellValue.getNumberValue();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
                    value = format.format(HSSFDateUtil.getJavaDate(date));
                } else {
                    value = "" + decimalFormat.format(numberValue);
                }
                break;
            case Cell.CELL_TYPE_STRING:
                value = cellValue.getStringValue();
                break;
            default:
        }
        return value;
    }
}

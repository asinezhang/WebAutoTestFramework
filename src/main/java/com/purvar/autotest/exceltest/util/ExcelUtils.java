package com.purvar.autotest.exceltest.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.purvar.autotest.exceltest.bean.WorkbookType;

/**
 * Excel 工具类
 *
 * @author SoyaDokio
 * @date 2020-07-24
 */
public class ExcelUtils extends Hashtable<String, ArrayList<ArrayList<String>>> {

    private static final long serialVersionUID = -74782638817899185L;

    private static final Logger logger = LoggerFactory.getLogger(ExcelUtils.class);

    public void load(String filePath) {
        logger.debug("开始读取 Excel [{}]", filePath);
        int num = getNumberOfSheets(filePath);
        logger.debug("当前工作表内工作表的数量为 [{}]", num);
        for (int i = 0; i < num; i++) {
            Sheet sheet = getSheet(filePath, i);
            String sheetName = sheet.getSheetName();
            ArrayList<ArrayList<String>> sheetData = getSheetData(sheet);
            put(sheetName, sheetData);
        }
        logger.debug("读取 Excel 完成 [{}]", filePath);
    }

    public boolean isSheetExists(String sheetName) {
        return containsKey(sheetName);
    }

    public ArrayList<ArrayList<String>> getSheet(String sheetName) {
        return get(sheetName);
    }

    /**
     * 获取单元格的字符串形式的值
     *
     * @param cell
     * @return
     */
    private String getCellDataAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        String cellData = null;
        switch (cell.getCellType()) {
        case Cell.CELL_TYPE_BLANK:
            cellData = "";
            break;
        case Cell.CELL_TYPE_STRING:
            cellData = cell.getStringCellValue();
            break;
        case Cell.CELL_TYPE_NUMERIC:
        case Cell.CELL_TYPE_FORMULA:
        case Cell.CELL_TYPE_BOOLEAN:
            cell.setCellType(Cell.CELL_TYPE_STRING);
            cellData = cell.getStringCellValue();
            break;
        case Cell.CELL_TYPE_ERROR:
            // empty
        }
        return cellData;
    }

    /**
     * 获取工作表的类型，是2003格式（xls）还是2007格式（xlsx）
     *
     * @param filePath
     * @return
     */
    private WorkbookType getFileType(String filePath) {
        String exname = filePath.substring(filePath.lastIndexOf(".") + 1).toLowerCase();
        if ("xls".equalsIgnoreCase(exname)) {
            return WorkbookType.WOOKBOOK2003;
        } else if ("xlsx".equalsIgnoreCase(exname)) {
            return WorkbookType.WOOKBOOK2007;
        }
        return null;
    }

    /**
     * 获取当前工作表内工作表的数量
     *
     * @param filePath
     * @return
     */
    private int getNumberOfSheets(String filePath) {
        int numberOfSheets = -1;
        WorkbookType fileType = getFileType(filePath);
        switch (fileType) {
        case WOOKBOOK2003:
            HSSFWorkbook _2003wookbook = null;
            try {
                _2003wookbook = new HSSFWorkbook(getStream(filePath));
            } catch (IOException e) {
                logger.error("开启工作簿失败", e);
                System.exit(-1);
            }
            numberOfSheets = _2003wookbook.getNumberOfSheets();
            break;
        case WOOKBOOK2007:
            XSSFWorkbook _2007wookbook = null;
            try {
                _2007wookbook = new XSSFWorkbook(getStream(filePath));
            } catch (IOException e) {
                logger.error("开启工作簿失败", e);
                System.exit(-1);
            }
            numberOfSheets = _2007wookbook.getNumberOfSheets();
        }
        return numberOfSheets;
    }

    /**
     * 通过 sheet 索引获取指定工作簿的指定工作表对象
     *
     * @param filePath
     * @param sheetIndex
     * @return
     */
    private Sheet getSheet(String filePath, int sheetIndex) {
        Sheet sheet = null;
        WorkbookType fileType = getFileType(filePath);
        switch (fileType) {
        case WOOKBOOK2003:
            HSSFWorkbook _2003wookbook = null;
            try {
                _2003wookbook = new HSSFWorkbook(getStream(filePath));
            } catch (IOException e) {
                logger.error("开启工作簿失败", e);
                System.exit(-1);
            }
            sheet = _2003wookbook.getSheetAt(sheetIndex);
            break;
        case WOOKBOOK2007:
            XSSFWorkbook _2007wookbook = null;
            try {
                _2007wookbook = new XSSFWorkbook(getStream(filePath));
            } catch (IOException e) {
                logger.error("开启工作簿失败", e);
                System.exit(-1);
            }
            sheet = _2007wookbook.getSheetAt(sheetIndex);
        }
        return sheet;
    }

//    /**
//     * 通过 sheet 名称获取指定工作簿的指定工作表对象
//     *
//     * @param filePath
//     * @param sheetName
//     * @return
//     */
//    private Sheet getSheet(String filePath, String sheetName) {
//        Sheet sheet = null;
//        WorkbookType fileType = getFileType(filePath);
//        switch (fileType) {
//        case WOOKBOOK2003:
//            HSSFWorkbook _2003wookbook = null;
//            try {
//                _2003wookbook = new HSSFWorkbook(getStream(filePath));
//            } catch (IOException e) {
//                logger.error("开启工作簿失败", e);
//                System.exit(-1);
//            }
//            sheet = _2003wookbook.getSheet(sheetName);
//            break;
//        case WOOKBOOK2007:
//            XSSFWorkbook _2007wookbook = null;
//            try {
//                _2007wookbook = new XSSFWorkbook(getStream(filePath));
//            } catch (IOException e) {
//                logger.error("开启工作簿失败", e);
//                System.exit(-1);
//            }
//            sheet = _2007wookbook.getSheet(sheetName);
//        }
//        return sheet;
//    }

    /**
     * 获取指定工作表的数据
     *
     * @param sheet
     * @return
     * @throws Exception
     */
    private ArrayList<ArrayList<String>> getSheetData(Sheet sheet) {
        if (sheet == null) {
            return null;
        }
        ArrayList<ArrayList<String>> sheetData = new ArrayList<ArrayList<String>>();

        int rowMax = sheet.getLastRowNum() + 1;
        for (int i = 0; i < rowMax; i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                sheetData.add(new ArrayList<String>());
            } else {
                ArrayList<String> rowData = new ArrayList<String>();
                int cellMax = row.getLastCellNum();
                for (int j = 0; j < cellMax; j++) {
                    Cell cell = row.getCell(j);
                    String value = getCellDataAsString(cell);
                    rowData.add(value);
                }
                sheetData.add(rowData);
            }
        }
        return sheetData;
    }

    /**
     * 获取 IO 流
     *
     * @param filePath
     * @return
     * @throws Exception
     */
    private FileInputStream getStream(String filePath) throws FileNotFoundException {
        FileInputStream fi = null;
        try {
            fi = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            logger.error("未找到 Excel", e);
            System.exit(-1);
        }
        if (fi == null) {
            logger.error("文件不存在 - {}", filePath);
            System.exit(-1);
        }
        return fi;
    }

}

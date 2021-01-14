package com.purvar.autotest.exceltest.excel;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.purvar.autotest.exceltest.annotation.Bind;
import com.purvar.autotest.exceltest.bean.Instruction;
import com.purvar.autotest.exceltest.bean.SQL;
import com.purvar.autotest.exceltest.bean.Setting;
import com.purvar.autotest.exceltest.util.Constants;
import com.purvar.autotest.exceltest.util.ExcelUtils;

/**
 * 实体解析器，用以将 Excel 所得数据解析为 JavaBean
 *
 * @author SoyaDokio
 * @date 2020-07-27
 */
public class EntityParser {

    private static final Logger logger = LoggerFactory.getLogger(EntityParser.class);

    private static ExcelUtils excelUtils = new ExcelUtils();

    public void load(String filePath) {
        excelUtils.load(filePath);
    }

    /**
     * 获取名为 Instruction 的工作表中的所有键值对
     *
     * @return
     */
    public List<Instruction> getInstructions() {
        logger.debug("开始解析“Excel指令”工作表");
        List<Instruction> instructions = new ArrayList<Instruction>();
        if (excelUtils.isSheetExists(Constants.INSTRUCTION_SHEET_NAME)) {
            List<ArrayList<String>> sheetData = excelUtils.get(Constants.INSTRUCTION_SHEET_NAME);
            for (int i = 0; i < sheetData.size(); i++) {
                List<String> row = sheetData.get(i);
                if (row.isEmpty() || "No".equals(row.get(0))) {
                    continue;
                }
                if ("".equals(row.get(0)) || row.get(0)==null) {
                	logger.debug("第"+ String.valueOf(i+1) +"行的No为空");
                    continue;
                }
                if ("".equals(row.get(4)) || row.get(0)==null) {
                	logger.debug("第"+ String.valueOf(i+1) +"行的コマンド为空");
                    continue;
                }
                Instruction instruction = new Instruction();
                int no = Integer.parseInt(row.get(0));
                instruction.setNo(no);
                instruction.setPkgId(row.get(1));
                instruction.setPageId(row.get(2));
                instruction.setCaseId(row.get(3));
                instruction.setCommand(row.get(4));
                instruction.setSelectorType(row.get(5));
                instruction.setSelectorParam(row.get(6));
                instruction.setInputVal(row.get(7));
                instruction.setExpectedVal(row.get(8));
                instruction.setResult(row.get(9));
                if (!instruction.isEmpty()) {
                    instructions.add(instruction);
                }
            }
        }
        logger.debug("解析“Excel指令”工作表完成");
        return instructions;
    }

    /**
     * 获取名为 SQL 的工作表中的所有键值对
     *
     * @return
     */
    public List<SQL> getSqls() {
        logger.debug("开始解析 SQL 工作表");
        List<SQL> sqls = new ArrayList<SQL>();
        if (excelUtils.isSheetExists(Constants.SQL_SHEET_NAME)) {
            List<ArrayList<String>> sheetData = excelUtils.get(Constants.SQL_SHEET_NAME);
            for (int i = 0; i < sheetData.size(); i++) {
                List<String> row = sheetData.get(i);
                if (!row.isEmpty()) {
                    SQL sql = new SQL();
                    sql.setSql(row.get(0));
                    if (!sql.isEmpty()) {
                        sqls.add(sql);
                    }
                }
            }
        }
        logger.debug("解析 SQL 工作表完成");
        return sqls;
    }

    /**
     * 获取名为 setting 的工作表中的所有键值对
     *
     * @return
     */
    public Setting getSetting() {
        logger.debug("开始解析 Setting 工作表");
        Setting setting = new Setting();
        if (excelUtils.isSheetExists(Constants.SETTING_SHEET_NAME)) {
            List<ArrayList<String>> sheetData = excelUtils.get(Constants.SETTING_SHEET_NAME);
            Field[] fields = setting.getClass().getDeclaredFields();
            for (int i = 0; i < sheetData.size(); i++) {
                List<String> columns = sheetData.get(i);
                // 跳过空行
                if (columns.isEmpty()) {
                    continue;
                }
                String value = columns.size() < 2 ? "" : columns.get(1);

                // 利用反射将得到数据存入对象
                for (Field field : fields) {
                    boolean fieldHasAnno = field.isAnnotationPresent(Bind.class);
                    if (fieldHasAnno) {
                        String key = field.getAnnotation(Bind.class).key();
                        if (key.equals(columns.get(0))) {
                            field.setAccessible(true);
                            try {
                                field.set(setting, value);
                            } catch (IllegalArgumentException e) {
                                logger.error("非法参数异常", e);
                                System.exit(-1);
                            } catch (IllegalAccessException e) {
                                logger.error("非法访问异常", e);
                                System.exit(-1);
                            }
                            break;
                        }
                    }
                }
            }
        }
        logger.debug("解析 Setting 工作表完成");
        return setting;
    }

}

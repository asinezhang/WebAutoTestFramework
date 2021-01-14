package com.purvar.autotest.exceltest.core;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.purvar.autotest.exceltest.bean.Instruction;
import com.purvar.autotest.exceltest.bean.SQL;
import com.purvar.autotest.exceltest.excel.EntityParser;
import com.purvar.autotest.exceltest.util.Constants;
import com.purvar.autotest.exceltest.util.DBUtils;

/**
 * 启动器
 *
 * @author SoyaDokio
 * @date 2020-07-29
 */
public class Runner {

    static {
        // 指定 logback 日志配置文件所在
        System.setProperty("logback.configurationFile", "config/logback.xml");

        // 使用 slf4j 替代 Selenide 自带的 JUL
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }

    private static final Logger logger = LoggerFactory.getLogger(Runner.class);

    public static void main(String[] args) {
        if (args.length == 0) {
            logger.debug("缺少参数：“Excel指令”文件，形如：java -jar exceltest.jar c:\\filename.xlsx");
            System.exit(-1);
        }

        // 获取可执行 jar 包所在目录
//        String runtimePath = Runner.class.getProtectionDomain().getCodeSource().getLocation().getPath();
//        try {
//            runtimePath = java.net.URLDecoder.decode(runtimePath, "UTF-8");// 转化为 UTF-8 编码，以支持中文
//        } catch (UnsupportedEncodingException e) {
//            logger.error("程序异常：{}", e);
//            System.exit(-1);
//        }
//        int lastIndex = runtimePath.lastIndexOf("/") + 1;
//        runtimePath = runtimePath.substring(0, lastIndex);
//
//        runtimePath += args[0];    
//        logger.debug("已获取 EXCEL 文件：{}", runtimePath);
//
//        File file = new File(runtimePath);
//        if (!file.exists()) {
//            logger.error("不是绝对路径或当前目录下不存在该文件[{}]", args[0]);
//            System.exit(-1);
//        }
        String filepath = args[0];
        validateExcelFile(filepath);
        EntityParser entityParser = new EntityParser();
        // 读取 Excel 文件，暂存进对象备用
        entityParser.load(filepath);

        // 加载配置信息
        Constants.EXCEL_SETTING = entityParser.getSetting();

        // 加载需预执行的 SQL
        List<SQL> sqls = entityParser.getSqls();
        // 执行 SQL
        for (SQL sql : sqls) {
            DBUtils.executeUpdate(sql.getSql());
        }

        List<Instruction> instructions = entityParser.getInstructions();
        for (int i = 0; i < instructions.size(); i++) {
            Instruction instruction = instructions.get(i);
            if ("CAPTURE".equalsIgnoreCase(instruction.getCommand())) {
                String screenshotPath = instruction.getInputVal();
                InstructionExplainer.doCapture(screenshotPath);
            }
            if ("CHECKBOX".equalsIgnoreCase(instruction.getCommand())) {
                By selector = getSelector(instruction.getSelectorType(), instruction.getSelectorParam());
                String selected = instruction.getInputVal();
                if ("true".equalsIgnoreCase(selected)) {
                    InstructionExplainer.doCheckbox(selector, true);
                } else if ("false".equalsIgnoreCase(selected)) {
                    InstructionExplainer.doCheckbox(selector, false);
                } else {
                    logger.error("第 {} 行指令【入力値】栏位输入有误，应为“true”或“false”", instruction.getNo());
                    System.exit(-1);
                }
            }
            if ("CLICK".equalsIgnoreCase(instruction.getCommand())) {
                By selector = getSelector(instruction.getSelectorType(), instruction.getSelectorParam());
                InstructionExplainer.doClick(selector);
            }
            if ("CLOSE".equalsIgnoreCase(instruction.getCommand())) {
                InstructionExplainer.doClose();
            }
            if ("CONFIRM".equalsIgnoreCase(instruction.getCommand())) {
                InstructionExplainer.doConfirm();
            }
            if ("DOWNLOAD".equalsIgnoreCase(instruction.getCommand())) {
                String filePath = instruction.getInputVal();
                InstructionExplainer.doDownload(filePath);
            }
            if ("GET".equalsIgnoreCase(instruction.getCommand())) {
                By selector = getSelector(instruction.getSelectorType(), instruction.getSelectorParam());
                InstructionExplainer.doGet(selector);
            }
            if ("NAVIGATE".equalsIgnoreCase(instruction.getCommand())) {
                InstructionExplainer.doNavigate();
            }
            if ("OPEN".equalsIgnoreCase(instruction.getCommand())) {
                String absoluteUrl = instruction.getInputVal();
                InstructionExplainer.doOpen(absoluteUrl);
            }
            if ("RADIO".equalsIgnoreCase(instruction.getCommand())) {
                By selector = getSelector(instruction.getSelectorType(), instruction.getSelectorParam());
                InstructionExplainer.doRadio(selector);
            }
            if ("SELECT".equalsIgnoreCase(instruction.getCommand())) {
                By selector = getSelector(instruction.getSelectorType(), instruction.getSelectorParam());
                String inputVal = instruction.getInputVal();
                try {
                    InstructionExplainer.doSelect(selector, inputVal);
                } catch (Exception e) {
                    logger.error("第 {} 行指令【入力値】栏位输入有误，应以“{}”、“{}”或“{}”开头", instruction.getNo(), "INDEX=", "VALUE=", "TEXT=");
                    System.exit(-1);
                }
            }
            if ("SET".equalsIgnoreCase(instruction.getCommand())) {
                By selector = getSelector(instruction.getSelectorType(), instruction.getSelectorParam());
                String inputVal = instruction.getInputVal();
                InstructionExplainer.doSet(selector, inputVal);
            }
            if ("SLEEP".equalsIgnoreCase(instruction.getCommand())) {
                long milliseconds = Long.parseLong(instruction.getInputVal());
                InstructionExplainer.doSleep(milliseconds);
            }
            if ("SWITCH".equalsIgnoreCase(instruction.getCommand())) {
                String title = instruction.getInputVal();
                InstructionExplainer.doSwitch(title);
            }
            if ("WAIT".equalsIgnoreCase(instruction.getCommand())) {
                By selector = getSelector(instruction.getSelectorType(), instruction.getSelectorParam());
                InstructionExplainer.doWait(selector);
            }
        }

    }

    private static By getSelector(String selectorType, String selectorParam) {
        switch (selectorType) {
        case "ID":
            return By.id(selectorParam);
        case "NAME":
            return By.name(selectorParam);
        case "LINK":
            return By.linkText(selectorParam);
        case "CSS":
            return By.cssSelector(selectorParam);
        case "CLASS":
            return By.className(selectorParam);
        case "TAG":
            return By.tagName(selectorParam);
        case "XPATH":
            return By.xpath(selectorParam);
        default:
        }
        return null;
    }
    
    private static void validateExcelFile(String path) {
    	
    	 try {
    		 path = java.net.URLDecoder.decode(path, "UTF-8");// 转化为 UTF-8 编码，以支持中文
         } catch (UnsupportedEncodingException e) {
             logger.error("出现异常 ", e );
             System.exit(-1);
         }
         
         File file = new File(path);
         if (!file.exists()) {
             logger.error("当前目录下不存在该文件[{}]", path);
             System.exit(-1);
         }
    }

}

package com.purvar.autotest.exceltest.util;

import com.purvar.autotest.exceltest.bean.Setting;

/**
 * 常量工具类，启动后初始化，够程序各处调用
 *
 * @author SoyaDokio
 * @date 2020-07-28
 */
public class Constants {

    /**
     * “Excel指令”文件的“Excel指令”sheet 的名称
     */
    public static String INSTRUCTION_SHEET_NAME = PropertiesUtils.getString("INSTRUCTION_SHEET_NAME");

    /**
     * “Excel指令”文件的 SQL数据 sheet 的名称
     */
    public static String SQL_SHEET_NAME = PropertiesUtils.getString("SQL_SHEET_NAME");

    /**
     * “Excel指令”文件的 配置信息 sheet 的名称
     */
    public static String SETTING_SHEET_NAME = PropertiesUtils.getString("SETTING_SHEET_NAME");

    /**
     * Webdriver 超时时间
     */
    public static long WEBDRIVER_TIMEOUT = PropertiesUtils.getLong("WEBDRIVER_TIMEOUT");

    /**
     * 默认下载轮询间隔，单位毫秒
     */
    public static int DEFAULT_DOWNLOAD_POLLING_INTERVAL = PropertiesUtils.getInt("DEFAULT_DOWNLOAD_POLLING_INTERVAL");

    /**
     * 默认下载超时时间，单位毫秒
     */
    public static int DEFAULT_DOWNLOAD_TIMEOUT = PropertiesUtils.getInt("DEFAULT_DOWNLOAD_TIMEOUT");

    /**
     * 读取 Excel 的名为 setting 的工作表所得参数
     */
    public static Setting EXCEL_SETTING;

}

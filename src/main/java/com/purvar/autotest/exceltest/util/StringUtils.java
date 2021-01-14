package com.purvar.autotest.exceltest.util;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 * @author SoyaDokio
 * @date   2020-07-28
 */
public class StringUtils {

    /**
     * 为时间字符串添加斜杠分隔符
     * @param dataStr
     * @return
     */
    public static String formatDatestring(String dataStr) {
        return formatDatestring(dataStr, "/");
    }

    /**
     * 为时间字符串添加指定分隔符
     * @param dataStr
     * @param limiter
     * @return
     */
    public static String formatDatestring(String dataStr, String limiter) {
        Matcher matcher = Pattern.compile("^\\d+$").matcher(dataStr);
        if (!matcher.matches()) {
            return dataStr;
        }
        StringBuilder sbuf = new StringBuilder(dataStr);
        switch (dataStr.length()) {
        case 6:
            sbuf.insert(4, limiter);
        case 8:
            sbuf.insert(7, limiter);
        }
        return sbuf.toString();
    }

    /**
     * 根据文件名获取扩展/后缀名
     * @param filename
     * @return
     */
    public static String getFilenameExtension(String filename) {
        return filename.substring(filename.lastIndexOf("."));
    }

    /**
     * 为数字字符串添加逗号分隔符
     * 
     * @create 2020-01-28
     * 
     * @param number    需要添加逗号分隔符的数字字符串
     * @return          添加逗号分隔符后的数字字符串
     */
    public static String formatNumber(String number) {
        if (number == null) {
            return null;
        }
        if (Pattern.matches("^\\d+$", number.trim())) {
            return new DecimalFormat("#,###").format(Integer.valueOf(number.trim()));
        }
        return number;
    }

}

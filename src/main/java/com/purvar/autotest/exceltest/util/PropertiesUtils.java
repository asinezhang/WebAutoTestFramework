package com.purvar.autotest.exceltest.util;

/**
 * 配置文件工具集合类
 * @author SoyaDokio
 * @date   2020-07-24
 */
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

public class PropertiesUtils {

    private static final String CONFIG_PATH = "config/settings.config";
    private static Properties properties = new Properties();

    static {
        /* 为便于热修改将代码从static块提到方法init() */
        init();
    }

    private static void init() {
        Reader reader = null;
        try {
            reader = new InputStreamReader(PropertiesUtils.class.getClassLoader().getResourceAsStream(CONFIG_PATH));
            properties.load(reader);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String getString(String key) {
        if (!properties.containsKey(key)) {
            init();
        }
        return properties.getProperty(key).trim();
    }

    public static boolean getBoolean(String key) {
        if (!properties.containsKey(key)) {
            init();
        }
        String val = (String) properties.get(key);
        return Boolean.parseBoolean(val);
    }

    public static int getInt(String key) {
        if (!properties.containsKey(key)) {
            init();
        }
        String val = (String) properties.get(key);
        return Integer.parseInt(val);
    }

    public static long getLong(String key) {
        if (!properties.containsKey(key)) {
            init();
        }
        String val = (String) properties.get(key);
        return Long.parseLong(val);
    }

    public static float getFloat(String key) {
        if (!properties.containsKey(key)) {
            init();
        }
        String val = (String) properties.get(key);
        return Float.parseFloat(val);
    }

    public static double getDouble(String key) {
        if (!properties.containsKey(key)) {
            init();
        }
        String val = (String) properties.get(key);
        return Double.parseDouble(val);
    }

}

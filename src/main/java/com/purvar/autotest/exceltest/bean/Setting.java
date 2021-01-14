package com.purvar.autotest.exceltest.bean;

import com.purvar.autotest.exceltest.annotation.Bind;

public class Setting {

    /**
     * IE 设置中下载文件存放目录，绝对路径，注意：结尾需带斜杠
     */
    @Bind(key = "IE_DOWNLOAD_DIR")
    private String ieDownloadDir;

    /**
     * PDF 和 CSV 存放目录，绝对路径，注意：结尾需带斜杠
     */
    @Bind(key = "FILE_SAVE_DIR")
    private String fileSaveDir;

    /**
     * 页面截图存放目录，绝对路径，注意：结尾需带斜杠
     */
    @Bind(key = "SCREENSHOT_SAVE_DIR")
    private String screenshotSaveDir;

    /**
     * 浏览器类型
     */
    @Bind(key = "BROWSER_TYPE")
    private String browserType;

    /**
     * DB URL
     */
    @Bind(key = "DB_URL")
    private String dbURL;

    /**
     * DB 用户名
     */
    @Bind(key = "DB_USERNAME")
    private String dbUsername;

    /**
     * DB 密码
     */
    @Bind(key = "DB_PASSWORD")
    private String dbPassword;

    public String getIeDownloadDir() {
        return ieDownloadDir;
    }

    public String getFileSaveDir() {
        return fileSaveDir;
    }

    public String getScreenshotSaveDir() {
        return screenshotSaveDir;
    }

    public String getBrowserType() {
        return browserType;
    }

    public String getDbURL() {
        return dbURL;
    }

    public String getDbUsername() {
        return dbUsername;
    }

    public String getDbPassword() {
        return dbPassword;
    }

}

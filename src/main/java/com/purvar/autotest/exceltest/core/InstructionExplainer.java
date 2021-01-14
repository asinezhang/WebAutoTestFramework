package com.purvar.autotest.exceltest.core;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.SelenideWait;
import com.codeborne.selenide.WebDriverRunner;
import com.purvar.autotest.exceltest.util.Constants;
import com.purvar.autotest.exceltest.util.StringUtils;

/**
 * “Excel指令”解释器
 *
 * @author SoyaDokio
 * @date 2020-07-27
 */
public class InstructionExplainer {

    private static final Logger logger = LoggerFactory.getLogger(InstructionExplainer.class);

    static {
        // 设置失败超时时间
        Configuration.timeout = Constants.WEBDRIVER_TIMEOUT;
        // 不启用测试失败时自动截图
        Configuration.screenshots = false;
        // 设置截图根目录
        Configuration.reportsFolder = Constants.EXCEL_SETTING.getScreenshotSaveDir();
        // 不启用测试失败时保存页面源文件
        Configuration.savePageSource = false;
        // 启用启动 selenide 后即最大化浏览器窗口
        Configuration.startMaximized = true;
        // 设置浏览器类型
        Configuration.browser = Constants.EXCEL_SETTING.getBrowserType();

        // 设定浏览器驱动路径
        String browserProcess = null;
        String driverProcess = null;
        if ("ie".equalsIgnoreCase(Constants.EXCEL_SETTING.getBrowserType())) {
            // 有时 IE 原生 click 方法会失效，故使用 IE 时一律通过 JS 来点击
            Configuration.clickViaJs = true;

            System.setProperty("webdriver.ie.driver", "driver/IEDriverServer.exe");// ver 3.4.0.0 win32
            browserProcess = "iexplore.exe";
            driverProcess = "IEDriverServer.exe";
        } else if ("edge".equalsIgnoreCase(Constants.EXCEL_SETTING.getBrowserType())) {
            System.setProperty("webdriver.edge.driver", "driver/msedgedriver.exe");// ver 84.0.522.59 win64
            browserProcess = "msedge.exe";
            driverProcess = "msedgedriver.exe";
        } else if ("chrome".equalsIgnoreCase(Constants.EXCEL_SETTING.getBrowserType())) {
            System.setProperty("webdriver.chrome.driver", "driver/chromedriver.exe");// ver 84.0.4147.30 win32
            browserProcess = "chrome.exe";
            driverProcess = "chromedriver.exe";
        } else if ("firefox".equalsIgnoreCase(Constants.EXCEL_SETTING.getBrowserType())) {
            System.setProperty("webdriver.gecko.driver", "driver/geckodriver.exe");// ver 0.27.0 win64
            browserProcess = "firefox.exe";
            driverProcess = "geckodriver.exe";
        }
        // 启动前先杀掉浏览器及其驱动程序进程
        try {
            Process p1 = Runtime.getRuntime().exec("taskkill /F /IM " + browserProcess);
            p1.waitFor();
            Process p2 = Runtime.getRuntime().exec("taskkill /F /IM " + driverProcess);
            p2.waitFor();
        } catch (IOException | InterruptedException e) {
            logger.error("杀掉浏览器进程(" + browserProcess + ")及其驱动程序进程(" + driverProcess + ")时出现异常", e);
            System.exit(-1);
        }
    }

    private static SelenideWait selenideWait() {
        SelenideWait w = Selenide.Wait();
        w.ignoring(Throwable.class);
        return w;
    }

    /**
     * 对当前标签页截图并存储截图文件
     *
     * @param path 截图文件名，可带目录，不要后缀
     */
    public static void doCapture(String path) {
        String pngFile = Selenide.screenshot(path);
        logger.info("截图文件已保存：{}", pngFile);
    }

    /**
     * 操作指定复选框
     *
     * @param selector 指定元素的选择器
     * @param selected true：勾选；false：取消勾选
     */
    public static void doCheckbox(By selector, boolean selected) {
        selenideWait().until(ExpectedConditions.presenceOfAllElementsLocatedBy(selector));
        SelenideElement se = Selenide.$(selector);
        se.setSelected(selected);
        selenideWait().until(ExpectedConditions.elementSelectionStateToBe(selector, selected));
    }

    /**
     * 单击指定元素
     *
     * @param selector 指定元素的选择器
     */
    public static void doClick(By selector) {
        selenideWait().until(ExpectedConditions.elementToBeClickable(selector));
        SelenideElement se = Selenide.$(selector);
        se.click();
        Selenide.sleep(200);
    }

    /**
     * 关闭当前激活的标签页
     */
    public static void doClose() {
        Selenide.close();
        Selenide.sleep(200);
    }

    /**
     * 接受 Javascript 确认对话框（alert对话框时点“Yes”，confirm 对话框时点“OK”
     */
    public static void doConfirm() {
        selenideWait().until(ExpectedConditions.alertIsPresent());
        Selenide.confirm();
    }

    /**
     * 下载文件并按指定文件名存储
     *
     * @param filePath 文件名，可带目录，不要后缀；若为空则保持原文件名
     */
    public static void doDownload(String filePath) {
        // 清空临时下载目录（IE 设置中下载文件存放目录）
        File downloadDir = new File(Constants.EXCEL_SETTING.getIeDownloadDir());
        if (!downloadDir.exists()) {
            downloadDir.mkdirs();
        } else {
            File[] files = downloadDir.listFiles();
            if (files.length > 0) {
                for (File f : files) {
                    f.delete();
                }
            }
        }

        SelenideWait wait = new SelenideWait(WebDriverRunner.getAndCheckWebDriver(), Constants.DEFAULT_DOWNLOAD_TIMEOUT,
                Constants.DEFAULT_DOWNLOAD_POLLING_INTERVAL);
        logger.info("开始下载文件");
        wait.until(new Function<WebDriver, Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                File[] newFiles = downloadDir.listFiles();
                // 尚未开始下载
                if (newFiles.length == 0) {
                    // 模拟按键操作 Alt + S 以保存文件
                    try {
                        Robot robot = new Robot();
                        robot.delay(250);
                        robot.keyPress(KeyEvent.VK_ALT);
                        Thread.sleep(1000);
                        robot.keyPress(KeyEvent.VK_S);
                        robot.keyRelease(KeyEvent.VK_ALT);
                        robot.keyRelease(KeyEvent.VK_S);
                    } catch (AWTException | InterruptedException e) {
                        logger.error("下载时模拟按键出现异常", e);
                        System.exit(-1);
                    }
                    return false;
                }

                String filenameExtension = StringUtils.getFilenameExtension(newFiles[0].getName());
                // 正在下载
                if (".partial".equals(filenameExtension)) {
                    logger.debug("正在下載...");
                    return false;
                }

                // 下载完成
                // 准备存放下载所得文件
                String fileName = null;
                if (!"".equals(filePath)) {
                    fileName = filePath + filenameExtension;
                } else {
                    fileName = newFiles[0].getName();
                }
                File fileDest = new File(Constants.EXCEL_SETTING.getFileSaveDir() + fileName);
                // 为下载所得文件创建存放目录
                File fileDestDir = new File(fileDest.getParent());
                if (!fileDestDir.exists()) {
                    fileDestDir.mkdirs();
                    logger.info("创建文件夹：{}", fileDestDir.getAbsolutePath());
                }
                // 将下载所得文件由 IE 下载目录移动到文件存放目录
                File ieDownloadFile = new File(newFiles[0].getPath());
                ieDownloadFile.renameTo(fileDest);
                logger.info("下載完成，已保存至{}", fileDest.getPath());
                return true;
            }
        });
    }

    /**
     * 获取指定元素的 value 值
     *
     * @param selector 指定元素的选择器
     * @return 指定元素的 value 值
     */
    public static String doGet(By selector) {
        selenideWait().until(ExpectedConditions.presenceOfElementLocated(selector));
        SelenideElement se = Selenide.$(selector);
        return se.getValue();
    }

    // TODO
    public static void doNavigate() {
        System.out.println("doNavigate() is empty");
    }

    /**
     * 开启一个新的标签页并访问指定绝对 URL，若当前已有标签页处于激活状态，将不会打开新标签页
     *
     * @param absoluteUrl 绝对 URL
     */
    public static void doOpen(String absoluteUrl) {
        Selenide.open(absoluteUrl);
    }

    /**
     * 点选指定 radio
     *
     * @param selector 指定元素的选择器
     */
    public static void doRadio(By selector) {
        selenideWait().until(ExpectedConditions.presenceOfAllElementsLocatedBy(selector));
        SelenideElement se = Selenide.$(selector);
        se.selectRadio(se.attr("value"));
        selenideWait().until(ExpectedConditions.elementSelectionStateToBe(selector, true));
    }

    /**
     * 选择下拉菜单
     *
     * @param selector 指定元素的选择器
     * @param inputVal 下拉选项的选择方式（INDEX/VALUE/TEXT）和选择值，形如INDEX=0或VALUE=beijing或TEXT=北京
     * @throws Exception
     */
    public static void doSelect(By selector, String inputVal) throws Exception {
        selenideWait().until(ExpectedConditions.presenceOfAllElementsLocatedBy(selector));
        if (inputVal.startsWith("INDEX=")) {
            int index = Integer.parseInt(inputVal.substring(6).trim());
            selectOptionByIndex(selector, index);
        } else if (inputVal.startsWith("VALUE=")) {
            String value = inputVal.substring(6).trim();
            selectOptionByValue(selector, value);
        } else if (inputVal.startsWith("TEXT=")) {
            String text = inputVal.substring(5).trim();
            selectOptionByText(selector, text);
        } else {
            throw new Exception("【入力値】栏位输入有误");
        }
    }

    /**
     * 设置文本框的 value 值
     *
     * @param selector 指定元素的选择器
     * @param inputVal 预设置的 value 值
     */
    public static void doSet(By selector, String inputVal) {
        selenideWait().until(ExpectedConditions.presenceOfAllElementsLocatedBy(selector));
        SelenideElement se = Selenide.$(selector);
        if (!se.exists() || !se.isDisplayed() || !se.isEnabled()) {
            logger.error("Element is not useful");
            System.exit(-1);
        }
        se.setValue(inputVal);
        se.pressTab();
        Selenide.sleep(500);
    }

    /**
     * 线程按指定时长睡眠
     *
     * @param milliseconds 欲睡眠的时长，单位毫秒
     */
    public static void doSleep(long milliseconds) {
        Selenide.sleep(milliseconds);
    }

    /**
     * 切换至指定标题的标签页
     *
     * @param title 欲切换至激活状态的标签页的标题
     */
    public static void doSwitch(String title) {
        Selenide.switchTo().window(title);
        logger.debug("当前窗口: [{}]", Selenide.title());
    }

    /**
     * 等待指定元素，直至等到或超时
     *
     * @param selector 指定元素的选择器
     */
    public static void doWait(By selector) {
        selenideWait().until(ExpectedConditions.presenceOfElementLocated(selector));
    }

    /**
     * 根据 index 选择下拉菜单，并在刷新后确定该菜单确已选取指定 index
     *
     * @param seleniumSelector 需要操作的下拉菜单元素
     * @param index            下拉菜单的 index
     */
    private static void selectOptionByIndex(By seleniumSelector, int index) {
        selenideWait().until(ExpectedConditions.presenceOfAllElementsLocatedBy(seleniumSelector));
        SelenideElement se = Selenide.$(seleniumSelector);
        if (!se.exists() || !se.isDisplayed() || !se.isEnabled()) {
            logger.error("Element is not useful");
            System.exit(-1);
        }
        se.selectOption(index);
        se.pressTab();
        Selenide.sleep(100);
        selenideWait().until(new Function<WebDriver, Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                SelenideElement se2 = Selenide.$(seleniumSelector);
                if (!se2.exists() || !se2.isDisplayed() || !se2.isEnabled()) {
                    return false;
                }
                String selectedText = se2.getText();
                Select sel = new Select(driver.findElement(seleniumSelector));
                List<WebElement> elements = sel.getOptions();
                return elements.get(index).getText().equals(selectedText);
            }
        });
    }

    /**
     * 根据 text 选择下拉菜单，并在刷新后确定该菜单确已选取指定 text
     *
     * @param seleniumSelector 需要操作的下拉菜单元素
     * @param text             下拉菜单的 text
     */
    private static void selectOptionByText(By seleniumSelector, String text) {
        selenideWait().until(ExpectedConditions.presenceOfAllElementsLocatedBy(seleniumSelector));
        SelenideElement se = Selenide.$(seleniumSelector);
        if (!se.exists() || !se.isDisplayed() || !se.isEnabled()) {
            logger.error("Element is not useful");
            System.exit(-1);
        }
        se.selectOption(text);
        se.pressTab();
        Selenide.sleep(100);
        selenideWait().until(new Function<WebDriver, Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                SelenideElement se2 = Selenide.$(seleniumSelector);
                if (!se2.exists() || !se2.isDisplayed() || !se2.isEnabled()) {
                    return false;
                }
                String t = se2.getText();
                return t.equals(text);
            }
        });
    }

    /**
     * 根据 value 选择下拉菜单，并在刷新后确定该菜单确已选取指定 value
     *
     * @param seleniumSelector 需要操作的下拉菜单元素
     * @param value            下拉菜单的 value
     */
    private static void selectOptionByValue(By seleniumSelector, String value) {
        selenideWait().until(ExpectedConditions.presenceOfAllElementsLocatedBy(seleniumSelector));
        SelenideElement se = Selenide.$(seleniumSelector);
        if (!se.exists() || !se.isDisplayed() || !se.isEnabled()) {
            logger.error("Element is not useful");
            System.exit(-1);
        }
        se.selectOptionByValue(value);
        se.pressTab();
        Selenide.sleep(100);
        selenideWait().until(new Function<WebDriver, Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                SelenideElement se2 = Selenide.$(seleniumSelector);
                if (!se2.exists() || !se2.isDisplayed() || !se2.isEnabled()) {
                    return false;
                }
                String t = se2.getValue();
                return t.equals(value);
            }
        });
    }

}

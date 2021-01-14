package com.purvar.autotest.exceltest.bean;

/**
 * 单行“Excel指令”对象
 *
 * @author SoyaDokio
 * @date 2020-07-27
 */
public class Instruction {

    /**
     * No
     */
    private int no;

    /**
     * 包ID
     */
    private String pkgId;

    /**
     * 画面ID
     */
    private String pageId;

    /**
     * CASE ID
     */
    private String caseId;

    /**
     * コマンド
     */
    private String command;

    /**
     * 选择器类型
     */
    private String selectorType;

    /**
     * 选择器参数
     */
    private String selectorParam;

    /**
     * 入力値
     */
    private String inputVal;

    /**
     * 期待値
     */
    private String expectedVal;

    /**
     * 結果
     */
    private String result;

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getPkgId() {
        if (pkgId == null) {
            return null;
        }
        return pkgId;
    }

    public void setPkgId(String pkgId) {
        this.pkgId = pkgId;
    }

    public String getPageId() {
        if (pageId == null) {
            return null;
        }
        return pageId.trim();
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public String getCaseId() {
        if (caseId == null) {
            return null;
        }
        return caseId.trim();
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public String getCommand() {
        if (command == null) {
            return null;
        }
        return command.trim();
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getSelectorType() {
        if (selectorType == null) {
            return null;
        }
        return selectorType;
    }

    public void setSelectorType(String selectorType) {
        this.selectorType = selectorType;
    }

    public String getSelectorParam() {
        if (selectorParam == null) {
            return null;
        }
        return selectorParam.trim();
    }

    public void setSelectorParam(String oeSelectorParam) {
        this.selectorParam = oeSelectorParam;
    }

    public String getInputVal() {
        if (inputVal == null) {
            return null;
        }
        return inputVal.trim();
    }

    public void setInputVal(String inputVal) {
        this.inputVal = inputVal;
    }

    public String getExpectedVal() {
        if (expectedVal == null) {
            return null;
        }
        return expectedVal.trim();
    }

    public void setExpectedVal(String expectedVal) {
        this.expectedVal = expectedVal;
    }

    public String getResult() {
        if (result == null) {
            return null;
        }
        return result.trim();
    }

    public void setResult(String result) {
        this.result = result;
    }

    public boolean isEmpty() {
        return pkgId.isEmpty() && pageId.isEmpty() && caseId.isEmpty() && command.isEmpty()
                && selectorType.isEmpty() && selectorParam.isEmpty() && inputVal.isEmpty()
                && expectedVal.isEmpty() && result.isEmpty();
    }

}

package com.purvar.autotest.exceltest.bean;

/**
 * 单行 SQL 对象
 *
 * @author SoyaDokio
 * @date 2020-07-27
 */
public class SQL {

    /**
     * SQL 语句本身
     */
    private String sql;

    /**
     * 执行是否成功
     */
    private Boolean status;

    /**
     * 执行影响行数
     */
    private Long effectedRows;

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Boolean getIsSuccess() {
        return status;
    }

    public void setIsSuccess(Boolean isSuccess) {
        this.status = isSuccess;
    }

    public Long getEffectedRows() {
        return effectedRows;
    }

    public void setEffectedRows(Long effectedRows) {
        this.effectedRows = effectedRows;
    }

    public boolean isEmpty() {
        return sql.length() == 0;
    }

}

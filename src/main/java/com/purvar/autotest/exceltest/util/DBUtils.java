package com.purvar.autotest.exceltest.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBUtils {

    private static final Logger logger = LoggerFactory.getLogger(DBUtils.class);

    public static void executeUpdate(String sql) {
        Connection conn = null;
        try {
            // 1. 获得数据库连接
            logger.debug("连接数据库 {}", Constants.EXCEL_SETTING.getDbURL());
            conn = DriverManager.getConnection(Constants.EXCEL_SETTING.getDbURL(), Constants.EXCEL_SETTING.getDbUsername(),
                    Constants.EXCEL_SETTING.getDbPassword());
            // 2.操作数据库，实现增删改查
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql);
            logger.info("[√] {}", sql);
        } catch (SQLException e) {
            logger.info("[×] {}", sql);
            logger.error("SQL 执行时出现异常", e);
            System.exit(-1);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    logger.error("断开 DB 连接时出现错误", e);
                    System.exit(-1);
                }
            }
        }
    }

}

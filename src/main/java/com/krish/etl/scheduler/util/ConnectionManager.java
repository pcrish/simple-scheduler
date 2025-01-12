package com.krish.etl.scheduler.util;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionManager {
    default Connection getConnection() throws SQLException {
        return null;
    }
    default DataSource getDataSource() throws SQLException{
        return null;
    }
}

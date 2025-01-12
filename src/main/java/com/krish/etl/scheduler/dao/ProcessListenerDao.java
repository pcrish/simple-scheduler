package com.krish.etl.scheduler.dao;

import com.krish.etl.scheduler.pojo.SourceFile;
import com.krish.etl.scheduler.util.ConnectionManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProcessListenerDao {

    private final ConnectionManager connectionManager;

    public ProcessListenerDao(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public void runProcessListener() throws SQLException {
        String query = "call scheduler.proc_process_listener()";
        try (Connection connection = connectionManager.getConnection();
        Statement statement = connection.createStatement()) {
            statement.execute(query);
        }
    }
}

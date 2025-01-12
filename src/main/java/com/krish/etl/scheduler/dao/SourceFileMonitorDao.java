package com.krish.etl.scheduler.dao;

import com.krish.etl.scheduler.util.ConnectionManager;
import com.krish.etl.scheduler.pojo.SourceFile;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class SourceFileMonitorDao {

    private final ConnectionManager connectionManager;

    public SourceFileMonitorDao(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public List<SourceFile> getAvailableFiles() throws SQLException {
        String query = "SELECT file_name,file_uuid,modified_date FROM scheduler.source_file_monitor where is_available;";
        try (Connection connection = connectionManager.getConnection();
             ResultSet resultSet = connection.createStatement().executeQuery(query)) {
            List<SourceFile> availableFiles = new ArrayList<>();
            while(resultSet.next()){
                SourceFile sourceFile = new SourceFile();
                sourceFile.setFileName(resultSet.getString("file_name"));
                sourceFile.setFileuuid((UUID) resultSet.getObject("file_uuid"));
                sourceFile.setModifiedDate(resultSet.getDate("modified_date"));
                availableFiles.add(sourceFile);
            }
            return availableFiles;

        }
    }

    public void updateAvailableFiles(List<SourceFile> sourceFiles) throws SQLException {
        String truncateQuery = "truncate table scheduler.st_source_file_monitor;";
        String query = "insert into scheduler.st_source_file_monitor(file_id, file_code, file_name, file_path, file_uuid," +
                "modified_by, modified_date, available_from, readable, writeable, enabled,is_available,size) values(?,?,?,?,?,?,?,?,?,?,?,?,?);";
        try (Connection connection = connectionManager.getConnection();
                Statement statement = connection.createStatement();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             CallableStatement callableStatement = connection.prepareCall("call scheduler.proc_merge_source_file_monitor()")) {
            statement.execute(truncateQuery);
            AtomicInteger counter = new AtomicInteger(0);
            for(SourceFile sourceFile : sourceFiles){
                preparedStatement.setObject(1, sourceFile.getFileId());
                preparedStatement.setString(2, sourceFile.getFileCode());
                preparedStatement.setString(3, sourceFile.getFileName());
                preparedStatement.setString(4, sourceFile.getFilePath());
                preparedStatement.setObject(5, sourceFile.getFileuuid());
                preparedStatement.setString(6, sourceFile.getModified_by());
                preparedStatement.setTimestamp(7, new java.sql.Timestamp(sourceFile.getModifiedDate().getTime()));
                preparedStatement.setTimestamp(8, new java.sql.Timestamp(sourceFile.getAvailableFrom().getTime()));
                preparedStatement.setBoolean(9, sourceFile.getReadable());
                preparedStatement.setBoolean(10, sourceFile.getWritable());
                preparedStatement.setBoolean(11, sourceFile.getEnabled());
                preparedStatement.setBoolean(12, true);
                preparedStatement.setLong(13, sourceFile.getSize());
                preparedStatement.addBatch();
                if(counter.getAndIncrement() % 1000 == 0){
                    preparedStatement.executeBatch();
                }
            }
            preparedStatement.executeBatch();
            callableStatement.execute();

        }
    }
}

package com.krish.etl.scheduler.dao;

import com.krish.etl.scheduler.util.ConnectionManager;
import com.krish.etl.scheduler.pojo.SourceFile;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SourceFileDao {

    private final ConnectionManager connectionManager;

    public SourceFileDao(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public List<SourceFile> getSourceFiles() throws SQLException {
        String query = "SELECT * FROM scheduler.source_files where enabled;";
        try (Connection connection = connectionManager.getConnection();
             ResultSet resultSet = connection.createStatement().executeQuery(query)) {
            List<SourceFile> sourceFiles = new ArrayList<>();
            while(resultSet.next()){
                SourceFile sourceFile = new SourceFile();
                sourceFile.setFileId((UUID) resultSet.getObject("file_id"));
                sourceFile.setFileCode(resultSet.getString("file_code"));
                sourceFile.setFileName(resultSet.getString("file_name"));
                sourceFile.setFilePath(resultSet.getString("file_path"));
                sourceFile.setFilePattern(resultSet.getString("file_pattern"));
                sourceFile.setContact(resultSet.getString("contact"));
                sourceFile.setEnabled(resultSet.getBoolean("enabled"));
                sourceFiles.add(sourceFile);
            }
            return sourceFiles;

        }
    }
}

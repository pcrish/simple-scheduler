package com.krish.etl.scheduler.dao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.krish.etl.scheduler.pojo.DbJob;
import com.krish.etl.scheduler.util.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

public class RunDbJobDao {
    private final ConnectionManager connectionManager;

    public RunDbJobDao(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }
    public void runJob(DbJob dbJob) throws SQLException {
        String sql = "call proc_run_pgsql (?)";
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject parameters;
        if(dbJob.getParameter().isBlank() || dbJob.getParameter().isEmpty())
            parameters = new JsonObject();
        else
            parameters = gson.fromJson(dbJob.getParameter(),JsonObject.class);
        parameters.addProperty("jobId", dbJob.getJobId());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        parameters.addProperty("orderDate", simpleDateFormat.format(dbJob.getOrderDate()));

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, gson.toJson(parameters));
            statement.execute();
        }
    }

}

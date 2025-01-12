package com.krish.etl.scheduler.dao;

import com.krish.etl.scheduler.Job;
import com.krish.etl.scheduler.pojo.DbJob;
import com.krish.etl.scheduler.pojo.Health;
import com.krish.etl.scheduler.util.ConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class JobStatusDao {

    private static final Logger log = LoggerFactory.getLogger(JobStatusDao.class);
    private final ConnectionManager connectionManager;
    private final String markCompleteSql = "call scheduler.proc_job_completion(?, ?, ?, ?);";
    public JobStatusDao(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }


    public List<DbJob> getReadyProcesses() throws SQLException {
        String query = "select job_id, order_date, executable_type, executable_name, parameters\n" +
                "from scheduler.job_status\n" +
                "where status = 'Ready'\n" +
                "and executable_type <> 'FileWatcher'";

        try (Connection connection = connectionManager.getConnection();
             ResultSet resultSet = connection.createStatement().executeQuery(query)) {
            List<DbJob> dbJobs = new ArrayList<>();
            while(resultSet.next()){
                DbJob job = new DbJob();
                job.setJobId(resultSet.getInt("job_id"));
                job.setOrderDate(resultSet.getDate("order_date"));
                job.setExecutableType(resultSet.getString("executable_type"));
                job.setFullyQualifiedClassName(resultSet.getString("executable_name"));
                job.setParameter(resultSet.getString("parameters"));
                dbJobs.add(job);
            }
            return dbJobs;
        }

    }

    public void markSuccess(DbJob dbJob) throws SQLException {
        try (Connection connection = connectionManager.getConnection();
        PreparedStatement statement = connection.prepareStatement(markCompleteSql)) {
            statement.setInt(1, dbJob.getJobId());
            statement.setDate(2, new java.sql.Date(dbJob.getOrderDate().getTime()));
            statement.setString(3, "Success");
            statement.setString(4, null);
            statement.execute();
        }
    }

    public void markStarted(DbJob dbJob) throws SQLException {
        String statusQuery = """
                update scheduler.job_status
                    set status = 'Running',
                       status_description = 'In Progress',
                       started = now(),
                       audit_user = 'job_start'
                where job_id = ?
                and order_date = ?;
                """ ;
        String executionGroupQuery = """
                update scheduler.execution_group eg
                    set is_available = false,
                       used_by = ?
                from scheduler.execution_group_members egm
                where eg.group_id = egm.group_id
                and egm.job_id = ?
                """;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(statusQuery);
             PreparedStatement statementExecutionGroup = connection.prepareStatement(executionGroupQuery)
        ) {
            statement.setInt(1, dbJob.getJobId());
            statement.setDate(2, new java.sql.Date(dbJob.getOrderDate().getTime()));

            statementExecutionGroup.setLong(1, dbJob.getJobId());
            statementExecutionGroup.setLong(2, dbJob.getJobId());
            int count = statement.executeUpdate();
            statementExecutionGroup.executeUpdate();
            if(count != 1) {
                throw new SQLException("Failed to update job status");
            }
        }
    }

    public void markFailed(DbJob dbJob) throws SQLException {


        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(markCompleteSql)) {

            statement.setInt(1, dbJob.getJobId());
            statement.setDate(2, new java.sql.Date(dbJob.getOrderDate().getTime()));
            statement.setString(3, "Failed");
            statement.setString(4, dbJob.getError());
            statement.execute();
        }
    }

    public List<DbJob> getJobs(long jobId, String orderDate, String jobName, String jobTree, String jobStatus ,List<String> statusList) throws SQLException {
        String sql = """
       
                    select js.job_id,j.name, j.description,j.tree, js.order_date, js.status, js.status_description, js.started, js.completed,
                  js.retry, js.start_at, js.sla, js.error, js.executable_type, js.executable_name, js.parameters,j.schedule_type,
                  j.job_days,j.nth_day,j.nth_working_day
           from scheduler.job_status js
           inner join scheduler.jobs j
           on js.job_id = j.job_id
              where 1=1
       """;
        AtomicInteger counter = new AtomicInteger(0);
        if(jobId != 0) {
            sql += " and js.job_id = ? ";
            counter.incrementAndGet();
        }
        if (orderDate != null) {
            sql += " and js.order_date = ? ";
            counter.incrementAndGet();
        }
        if(jobName != null) {
            sql += " and j.name = ? ";
            counter.incrementAndGet();
        }
        if(jobTree != null) {
            sql += " and j.tree = ? ";
            counter.incrementAndGet();
        }
        if(jobStatus != null) {
            sql += " and js.status = ? ";
            //counter.incrementAndGet();
        }
        if (statusList != null && !statusList.isEmpty()) {
            sql += " AND js.status IN ("+ statusList.stream().map(s -> "?").collect(Collectors.joining(","))+")";
//            String placeholders = String.join(",", Collections.nCopies(statusList.size(), "?"));
//            sql += " AND js.status IN (" + placeholders + ")";
            counter.addAndGet(statusList.size());
        }
        sql += " order by js.order_date desc, js.job_id";
        System.out.println("sql Query"+sql);
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            int paramCount = 1;
            if(jobId != 0) {
                statement.setLong( paramCount++ , jobId);
            }
            if (orderDate != null) {
                statement.setDate(paramCount++, Date.valueOf(orderDate));
            }
            if(jobName != null) {
                statement.setString(paramCount++, jobName);
            }
            if(jobTree != null) {
                statement.setString(paramCount++, jobTree);
            }
            if(jobStatus != null) {
                statement.setString(paramCount++, jobStatus);
            }
            if (statusList != null && !statusList.isEmpty()) {
                for (int i = 0; i < statusList.size(); i++) {
                    statement.setString(paramCount++, statusList.get(i));
                }
            }
            ResultSet resultSet = statement.executeQuery();
            List<DbJob> jobs = new ArrayList<>();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            while(resultSet.next()) {
                DbJob job = new DbJob();
                job.setJobId(resultSet.getInt("job_id"));
                job.setName(resultSet.getString("name"));
                job.setDescription(resultSet.getString("description"));
                job.setTree(resultSet.getString("tree"));
                job.setOrderDate(resultSet.getDate("order_date"));
                job.setStatus(resultSet.getString("status"));
                job.setStatusDescription(resultSet.getString("status_description"));
                Timestamp started = resultSet.getTimestamp("started");
                Timestamp completed = resultSet.getTimestamp("completed");
                Timestamp sla = resultSet.getTimestamp("sla");
                Timestamp startAt = resultSet.getTimestamp("start_at");
                job.setStarted(started ==null ? null: simpleDateFormat.format(started));
                job.setCompleted(completed ==null ? null:simpleDateFormat.format(completed));
                job.setRetry(resultSet.getInt("retry"));
                job.setStartAt(simpleDateFormat.format(sla));
                job.setSla(simpleDateFormat.format(sla));
                job.setError(resultSet.getString("error"));
                job.setExecutableType(resultSet.getString("executable_type"));
                job.setFullyQualifiedClassName(resultSet.getString("executable_name"));
                job.setParameter(resultSet.getString("parameters"));
                job.setScheduleType(resultSet.getString("schedule_type"));
                job.setJobDays(resultSet.getString("job_days"));
                job.setNthDay(resultSet.getString("nth_day"));
                job.setNthWorkingDay(resultSet.getString("nth_working_day"));
                jobs.add(job);
            }
            return jobs;
        } catch (SQLException e) {
            log.error("Exception occurred while fetching jobs",e);
            throw new SQLException("Exception occurred while fetching jobs", e);
        }
    }

    public Map<String, Health> getJobHealth(){
        Map<String, Health> healthMap = new HashMap<>();
        try {
            getJobs(0,null,null,null,null, Arrays.asList("Failed","Late")).forEach(dbJob -> {
                Health jobHealth = new Health();
                jobHealth.setAvailable(false);
                jobHealth.setAction("Check the job");
                jobHealth.setMessage(String.format("Job %s is in %s state", dbJob.getJobId(), dbJob.getStatus()));
                jobHealth.setError(dbJob.getError());
                healthMap.put(String.format(dbJob.getName(), dbJob.getJobId()), jobHealth);
            });

        } catch (SQLException e) {
            Health health = new Health();
            health.setAvailable(false);
            health.setAction("Check db connection");
            health.setMessage(String.format("Unable to get job status from db"));
            health.setError(e.getMessage());
            healthMap.put("SchedulerJobStatus", health);
        }
        return healthMap;
    }
    public void updateStatusManually(DbJob dbJob) {
        System.out.println(dbJob.toString());
    }
}

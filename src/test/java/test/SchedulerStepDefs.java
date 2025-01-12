package test;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.PendingException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import junit.framework.Assert;

import java.sql.*;
import java.util.*;

public class SchedulerStepDefs {
    private final String CREATE_JOB = "\n" +
            "Insert into job.jobs(job_id,name,description,tree,executable_type,retry,odate_start_after,odate_sla,schedule_type,job_days,nth_day,is_active)" +
            " values(?,'test','test','test','dummy',10,false,false,?,?,?,?)";
    @Given("for the following job configurations")
    public void iHaveAScheduler(DataTable dataTable) {
        try (Connection connection = new PgDatasource().getConnection()) {
            connection.createStatement().executeUpdate("truncate table job.jobs");
            connection.createStatement().executeUpdate("truncate table job.job_status");
            PreparedStatement statement = connection.prepareStatement(CREATE_JOB);
            List<Map<String, String>> jobs = dataTable.asMaps();
            for (Map<String, String> job : jobs) {
                statement.setInt(1,Integer.parseInt(job.get("job_id")));
                statement.setString(2,job.get("schedule_type"));
                statement.setString(3,job.get("job_day"));
                statement.setString(4,job.get("nth_day"));
                statement.setBoolean(5,Boolean.parseBoolean(job.get("is_active")));
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Given executed");
        System.out.println(dataTable);

    }

    @When("scheduler is created for {string}")
    public void iStartTheScheduler(String date) {


        try (Connection connection = new PgDatasource().getConnection()) {
            connection.createStatement().executeQuery(String.format("Select job.create_daily_scheduler('%s'::date)",date));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Scheduler created");

    }

    @Then("only {string} should be created")
    public void iShouldSeeTheSchedulerRunning(String jobIdsCreated) {
        List<String> actualJobIds = new ArrayList<>();
        List<String> expectedJobIds = new ArrayList<>(List.of(jobIdsCreated.split(",")));
        Collections.sort(expectedJobIds);
        try (Connection connection = new PgDatasource().getConnection()) {
            ResultSet resultSet = connection.createStatement().executeQuery("Select job_id from job.job_status");
            while (resultSet.next()) {
                actualJobIds.add(resultSet.getString(1));
            }
            Collections.sort(actualJobIds);

            Assert.assertEquals(expectedJobIds,actualJobIds);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Scheduler created");
    }
}

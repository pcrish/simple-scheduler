package test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.krish.etl.scheduler.SimpleScheduler;
import com.krish.etl.scheduler.api.JobApi;
import io.cucumber.java.BeforeAll;
import io.muserver.ContextHandlerBuilder;
import io.muserver.MuServer;
import io.muserver.MuServerBuilder;
import io.muserver.handlers.ResourceHandlerBuilder;
import io.muserver.rest.RestHandlerBuilder;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import org.flywaydb.core.Flyway;

import java.sql.Connection;
import java.sql.SQLException;

public class TestSetup {

    private static MuServer muServer;

    @BeforeAll
    public static void setup() {
        try {
            System.out.println("setup started");
            PgDatasource pgDatasource = new PgDatasource();
            SimpleScheduler etlScheduler = SimpleScheduler
                    .newScheduler(pgDatasource)
                    .withObject("pg_db_connection_manager",pgDatasource)
                    .schedule();
            createSchedulerSchema();
            createJobs();
            etlScheduler.runSourceFileMonitror();
            etlScheduler.runProcessListener();
            System.out.println("setup completed");

            muServer = MuServerBuilder.httpServer()
                    .withHttpPort(8080)
                    .addHandler(RestHandlerBuilder.restHandler( new HealthHanlder(etlScheduler)))
                    .addHandler(ContextHandlerBuilder.context("scheduler")
                    .addHandler(ContextHandlerBuilder.context("api")
                            .addHandler(RestHandlerBuilder.restHandler( new JobApi(pgDatasource)))
                        ).addHandler(ResourceHandlerBuilder.fileOrClasspath("src/main/resources/web","/web"))
                    )
                    .start();
            System.out.println("Server started at port 8080"+muServer.uri());
        } catch (SQLException e) {
            throw new RuntimeException("Unable to create scheduler schema",e);
        }
    }

    private static void createSchedulerSchema() throws SQLException {
        System.out.println("createSchedulerSchema started");
        Flyway flyway = Flyway.configure()
                .defaultSchema("scheduler")
                .createSchemas(true)
                .locations("db/scheduler")
                .dataSource(new PgDatasource().getDataSource())
                .cleanDisabled(false)
                .load();

        flyway.clean();
        flyway.migrate();
        System.out.println("Migration completed");
        System.out.println("createSchedulerSchema completed");
        createTestData();
    }

    private static void createJobs() throws SQLException {
        System.out.println("createSchedulerSchema started");
        Flyway flyway = Flyway.configure()
                .defaultSchema("fx")
                .createSchemas(true)
                .locations("db/test")
                .dataSource(new PgDatasource().getDataSource())
                .cleanDisabled(false)
                .load();

        flyway.migrate();
        System.out.println("Migration completed");
        System.out.println("createSchedulerSchema completed");
    }

    public static void createTestData() {
        String sql1 = """
                select scheduler.create_etl_job(
                       %s,
                       'river_fx',
                         'loh',
                       NULL,
                       NULL,
                       '12:30',
                         'daily',
                         'mon,tue,wed,thu,fri',
                            NULL,
                            NULL,
                            20,
                            false,
                            false,
                            true,
                       'fx_loh.csv',
                        null,
                        'D:\\workspace\\sources\\files\\river',
                       'pcrish@gmail.com'
                       );
                
                
                
                """;

        String sql2 = """
                select scheduler.create_etl_job(
                       %s,
                       'river_fx',
                         'nyh',
                       NULL,
                       NULL,
                       '12:30',
                         'daily',
                         'mon,tue,wed,thu,fri',
                            NULL,
                            NULL,
                            20,
                            false,
                            false,
                            true,
                       'fx_nyh_????????.csv',
                         'expr select replace(''fx_nyh_????????.csv'',''????????'',to_char( order_date, ''yyyymmdd''))',
                        'D:\\workspace\\sources\\files\\river',
                       'pcrish@gmail.com'
                       );
                
                
                
                """;



        String sql3 = """
                select scheduler.create_etl_notification_job(
                       %s,
                       'flexrate_fx',
                         'loh',
                       NULL,
                       NULL,
                       '12:30',
                         'daily',
                         'mon,tue,wed,thu,fri',
                            NULL,
                            NULL,
                            20,
                            false,
                            false,
                            true,
                       'fx_flexrate.csv',
                        null,
                        'D:\\workspace\\sources\\files\\river',
                       'pcrish@gmail.com'
                       );
                
                
                
                """;

        String sql4 = """
                select scheduler.create_etl_notification_job(
                       %s,
                       'flexrate_fx',
                         'nyh',
                       NULL,
                       NULL,
                       '12:30',
                         'daily',
                         'mon,tue,wed,thu,fri',
                            NULL,
                            NULL,
                            20,
                            false,
                            false,
                            true,
                       'fx_flexrate_nyh_????????.csv',
                        'expr select replace(''fx_flexrate_nyh_????????.csv'',''????????'',to_char( order_date, ''yyyymmdd''))',
                        'D:\\workspace\\sources\\files\\river',
                       'pcrish@gmail.com'
                       );
                
                
                
                """;
       try(Connection connection = new PgDatasource().getConnection();
           var statement = connection.createStatement()) {
               statement.execute(String.format(sql1,1));
               statement.execute(String.format(sql2,10));
               statement.execute(String.format(sql3,20));
               statement.execute(String.format(sql4,30));
               statement.execute("""
                  insert into scheduler.execution_group (group_id, name, is_available)
                    values (1, 'Group 1', true);
                    insert into scheduler.execution_group_members (group_id, job_id)
                    values (1, 3),(1,12);
                  
""");
               statement.execute("select  scheduler.create_daily_scheduler('2024-11-19');");
           } catch (SQLException e) {
               throw new RuntimeException("Unable to create test data",e);
           }
        System.out.println("created Test Data completed");
    }

    @Path("/health")
    private static class HealthHanlder {
        private final SimpleScheduler etlScheduler;
        public HealthHanlder(SimpleScheduler etlScheduler) {
            this.etlScheduler = etlScheduler;
        }

        @GET
        @Produces("application/json")
        public Response getHealth(){
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonObject health = new JsonObject();
            health.addProperty("isAvailabe",checkHealth());
            health.add("dependencies",gson.toJsonTree(etlScheduler.getHealth()));
            return Response.ok(gson.toJson(health)).build();
        }

        private boolean checkHealth() {
            return etlScheduler.getHealth().entrySet().stream().filter(h -> !h.getValue().isAvailable()).count() == 0;
        }

    }
}

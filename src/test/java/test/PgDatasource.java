package test;

import com.krish.etl.scheduler.util.ConnectionManager;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

public class PgDatasource implements ConnectionManager {
    private String host = "localhost";
    private String port = "5432";
    private String user = "postgres";
    private String pwd = "remote";
    private String database = "postgres";
    private String url = "jdbc:postgresql://"+host+":"+port+"/"+database;


    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, pwd);
    }

    @Override
    public DataSource getDataSource() throws SQLException{
        return new DataSource() {
            @Override
            public Connection getConnection() throws SQLException {
                return DriverManager.getConnection(url, user, pwd);
            }

            @Override
            public Connection getConnection(String username, String password) throws SQLException {
                return DriverManager.getConnection(url, user, pwd);
            }

            @Override
            public PrintWriter getLogWriter() throws SQLException {
                return null;
            }

            @Override
            public void setLogWriter(PrintWriter out) throws SQLException {

            }

            @Override
            public void setLoginTimeout(int seconds) throws SQLException {

            }

            @Override
            public int getLoginTimeout() throws SQLException {
                return 0;
            }

            @Override
            public <T> T unwrap(Class<T> iface) throws SQLException {
                return null;
            }

            @Override
            public boolean isWrapperFor(Class<?> iface) throws SQLException {
                return false;
            }

            @Override
            public Logger getParentLogger() throws SQLFeatureNotSupportedException {
                return null;
            }
        };
    }


}

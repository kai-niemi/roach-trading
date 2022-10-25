package io.roach.trading;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.hibernate.engine.jdbc.connections.internal.ConnectionProviderInitiator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;

import io.roach.trading.util.ExcludeFromTest;

@Configuration
@EnableAutoConfiguration(exclude = {
        SecurityAutoConfiguration.class,
        DataSourceAutoConfiguration.class
})
@EntityScan(basePackageClasses = TradingApplication.class)
@ComponentScan(basePackages = "io.roach.trading",
        excludeFilters = {@ComponentScan.Filter(classes = ExcludeFromTest.class)})
public class TestApplication implements ApplicationRunner {
    @Autowired
    private DataSource dataSource;

    private void printDatabaseMetadata() {
        final Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("databaseVersion", databaseVersion());

        Connection connection = null;
        try {
            connection = DataSourceUtils.doGetConnection(dataSource);
            DatabaseMetaData metaData = connection.getMetaData();

            properties.put("URL", connection.getMetaData().getURL());
            properties.put("databaseProductName", metaData.getDatabaseProductName());
            properties.put("databaseMajorVersion", metaData.getDatabaseMajorVersion());
            properties.put("databaseMinorVersion", metaData.getDatabaseMinorVersion());
            properties.put("databaseProductVersion", metaData.getDatabaseProductVersion());
            properties.put("driverMajorVersion", metaData.getDriverMajorVersion());
            properties.put("driverMinorVersion", metaData.getDriverMinorVersion());
            properties.put("driverName", metaData.getDriverName());
            properties.put("driverVersion", metaData.getDriverVersion());
            properties.put("maxConnections", metaData.getMaxConnections());
            properties.put("defaultTransactionIsolation", metaData.getDefaultTransactionIsolation());
            properties.put("transactionIsolation", connection.getTransactionIsolation());
            properties.put("transactionIsolationName",
                    ConnectionProviderInitiator.toIsolationNiceName(connection.getTransactionIsolation()));

            properties.forEach((k, v) -> {
                System.out.printf("%s: %s\n", k, v);
            });
        } catch (SQLException ex) {
            // Ignore
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    private String databaseVersion() {
        try {
            return new JdbcTemplate(dataSource).queryForObject("select version()", String.class);
        } catch (DataAccessException e) {
            return "unknown";
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        printDatabaseMetadata();
    }
}

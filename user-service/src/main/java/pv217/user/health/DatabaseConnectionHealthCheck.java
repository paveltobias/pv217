package pv217.user.health;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Checks whether database is responding.
 *
 * Inspired by
 * https://hellokoding.com/java-application-health-check-with-prometheus-grafana-mysql-and-docker-compose/
 */
@Readiness
@ApplicationScoped
public class DatabaseConnectionHealthCheck implements HealthCheck {
    private static final Logger LOG = Logger.getLogger(DatabaseConnectionHealthCheck.class);

    @ConfigProperty(name = "quarkus.datasource.jdbc.url")
    private String databaseUrl;

    @ConfigProperty(name = "quarkus.datasource.username")
    private String databaseUsername;

    @ConfigProperty(name = "quarkus.datasource.password")
    private String databasePassword;

    private String hcName = "Custom User service database health check";

    @Override
    public HealthCheckResponse call() {

        try (Connection connection = DriverManager.getConnection(databaseUrl, databaseUsername, databasePassword)) {
            return HealthCheckResponse.up(hcName);
        } catch (SQLException e) {
            LOG.error(e);
        }

        LOG.error("Failed to establish connection to database, url: " + databaseUrl);

        return HealthCheckResponse.down(hcName);
    }
}

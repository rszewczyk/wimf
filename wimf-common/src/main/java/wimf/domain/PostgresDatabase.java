package wimf.domain;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;

/**
 * {@link Database} implementation backed by Postgres
 */
public final class PostgresDatabase implements Database {
    private final Jdbi jdbi;

    public PostgresDatabase(final String connection) {
        jdbi = Jdbi.create(connection);
        jdbi.installPlugins();
    }

    public RestaurantInspectionDao getRestaurantInspectionDao() {
        return new RestaurantInspectionDaoImpl(jdbi.open());
    }

    public void create() {
        try (final Handle handle = jdbi.open()) {
            handle.execute("CREATE TABLE IF NOT EXISTS restaurant_inspection (" +
                    "id SERIAL PRIMARY KEY, " +
                    "business_name VARCHAR, " +
                    "boro VARCHAR, " +
                    "grade VARCHAR, " +
                    "inspection_date TIMESTAMP, " +
                    "business_id VARCHAR, " +
                    "cuisine VARCHAR, " +
                    "violation_code VARCHAR, " +
                    "score INTEGER)");
        }
    }

    public void drop() {
        try (final Handle handle = jdbi.open()) {
            handle.execute("DROP TABLE IF EXISTS restaurant_inspection");
        }
    }
}

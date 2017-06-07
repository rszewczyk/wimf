package wimf.domain;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;

import javax.inject.Named;

/**
 * {@link Database} implementation backed by Postgres
 */
@Named
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
                    "violation_description VARCHAR, " +
                    "score INTEGER, " +
                    "inspection_type VARCHAR)");

            handle.execute("CREATE TABLE business (" +
                    "id SERIAL PRIMARY KEY, " +
                    "business_id VARCHAR, " +
                    "price VARCHAR, " +
                    "rating REAL)");
        }
    }

    public void drop() {
        try (final Handle handle = jdbi.open()) {
            handle.execute("DROP TABLE IF EXISTS restaurant_inspection");
            handle.execute("DROP TABLE IF EXISTS business");
        }
    }

    public static class Factory implements org.glassfish.hk2.api.Factory<PostgresDatabase> {
        private final String connection;

        Factory(final String connection) {
            this.connection = connection;
        }

        @Override
        public PostgresDatabase provide() {
            return new PostgresDatabase(connection);
        }

        @Override
        public void dispose(final PostgresDatabase db) {}
    }
}

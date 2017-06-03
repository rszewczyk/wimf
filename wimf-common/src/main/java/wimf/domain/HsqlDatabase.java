package wimf.domain;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;

/**
 * {@link Database} implementation backed by HSQL
 */
public final class HsqlDatabase implements Database {
    private final Jdbi jdbi;

    public HsqlDatabase() {
        jdbi = Jdbi.create("jdbc:hsqldb:mem:wimf");
        jdbi.installPlugins();
    }

    @Override
    public RestaurantInspectionDao getRestaurantInspectionDao() {
        return new RestaurantInspectionDaoImpl(jdbi.open());
    }

    @Override
    public void create() {
        try (final Handle handle = jdbi.open()) {
            handle.execute("CREATE TABLE restaurant_inspection (" +
                    "id INTEGER IDENTITY PRIMARY KEY, " +
                    "business_name VARCHAR(255), " +
                    "boro VARCHAR(255), " +
                    "grade VARCHAR(255), " +
                    "inspection_date TIMESTAMP, " +
                    "business_id VARCHAR(255), " +
                    "cuisine VARCHAR(255), " +
                    "violation_code VARCHAR(255), " +
                    "score INTEGER)");
        }
    }

    @Override
    public void drop() {
        try (final Handle handle = jdbi.open()) {
            handle.execute("DROP TABLE IF EXISTS restaurant_inspection");
        }
    }

    public static class Factory implements org.glassfish.hk2.api.Factory<HsqlDatabase> {
        Factory() {}

        @Override
        public HsqlDatabase provide() {
            return new HsqlDatabase();
        }

        @Override
        public void dispose(final HsqlDatabase db) {}
    }
}
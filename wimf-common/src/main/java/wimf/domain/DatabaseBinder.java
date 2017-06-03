package wimf.domain;

import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Singleton;

/**
 * HK2 binder for a {@link Database}
 */
public class DatabaseBinder extends AbstractBinder {
    private final Factory<? extends Database> factory;

    public static DatabaseBinder getPostgres(final String connection) {
        return new DatabaseBinder(new PostgresDatabase.Factory(connection));
    }

    public static DatabaseBinder getHsql() {
        return new DatabaseBinder(new HsqlDatabase.Factory());
    }

    private DatabaseBinder(final Factory<? extends Database> factory) {
        this.factory = factory;
    }

    protected void configure() {
        bindFactory(factory).to(Database.class).in(Singleton.class);
    }
}

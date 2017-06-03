package wimf.domain;

import static org.assertj.core.api.Assertions.assertThat;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.Before;
import org.junit.Test;

/**
 * tests for {@link DatabaseBinder}
 */
public class DatabaseBinderTests {
    private ServiceLocator locator;

    @Before
    public void before() {
        locator = ServiceLocatorUtilities.bind(DatabaseBinder.getHsql());
    }

    @Test
    public void it_binds_a_singleton() {
        final Database first = locator.getService(Database.class);
        final Database second = locator.getService(Database.class);

        assertThat(first).isNotNull();
        assertThat(second).isNotNull();
        assertThat(first).isSameAs(second);
    }
}

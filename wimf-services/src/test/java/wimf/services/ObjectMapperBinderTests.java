package wimf.services;

import static org.assertj.core.api.Assertions.assertThat;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.Before;
import org.junit.Test;

/**
 * test for {@link ObjectMapperBinder}
 */
public class ObjectMapperBinderTests {
    private ServiceLocator locator;

    @Before
    public void before() {
        locator = ServiceLocatorUtilities.bind(new ObjectMapperBinder());
    }

    @Test
    public void it_binds_a_singleton() {
        final ObjectMapper first = locator.getService(ObjectMapper.class);
        final ObjectMapper second = locator.getService(ObjectMapper.class);

        assertThat(first).isNotNull();
        assertThat(second).isNotNull();
        assertThat(first).isSameAs(second);
    }
}

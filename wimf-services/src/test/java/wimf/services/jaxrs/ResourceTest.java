package wimf.services.jaxrs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.hk2.utilities.Binder;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import wimf.services.ObjectMapperFactory;

import javax.ws.rs.core.Application;

public abstract class ResourceTest extends JerseyTest {
    abstract Binder[] getModules();

    @Override
    protected Application configure() {
        // always pick an available port
        forceSet(TestProperties.CONTAINER_PORT, "0");

        return new WimfResourceConfig(getModules()).getApplication();
    }

    @Override
    protected void configureClient(final ClientConfig config) {
        final ObjectMapper mapper = new ObjectMapperFactory().provide();
        final ObjectMapperProvider provider = new ObjectMapperProvider(mapper);
        config.register(provider);
    }
}

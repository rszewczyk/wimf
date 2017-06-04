package wimf.services.jaxrs;

import org.glassfish.hk2.utilities.Binder;
import org.glassfish.jersey.server.ResourceConfig;
import wimf.services.ObjectMapperBinder;

public class WimfResourceConfig extends ResourceConfig {
    public WimfResourceConfig(final Binder ...binders) {
        for(final Binder binder : binders) {
            register(binder);
        }
        register(new ObjectMapperBinder());
        register(RestaurantInspectionResource.class);
        register(RestaurantInspectionsSummaryResource.class);
        register(ObjectMapperProvider.class);
        register(ConstraintViolationExceptionMapper.class);
    }
}

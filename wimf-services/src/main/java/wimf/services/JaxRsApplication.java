package wimf.services;

import wimf.services.jaxrs.RestaurantInspectionResource;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.core.Application;

public class JaxRsApplication extends Application {
    private final Set<Class<?>> classes;

    JaxRsApplication() {
        final HashSet<Class<?>> c = new HashSet<>();
        c.add(RestaurantInspectionResource.class);
        classes = Collections.unmodifiableSet(c);
    }

    @Override
    public Set<Class<?>> getClasses() {
        return classes;
    }
}

package wimf.services.jaxrs;

import static org.assertj.core.api.Assertions.assertThat;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;
import wimf.domain.Database;
import wimf.domain.HsqlDatabase;
import wimf.domain.RestaurantInspection;
import wimf.domain.RestaurantInspectionDao;
import wimf.services.ObjectMapperFactory;
import wimf.services.dto.ListDTO;
import wimf.services.dto.RestaurantInspectionDTO;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.time.LocalDateTime;
import java.util.stream.IntStream;

/**
 * tests for {@link RestaurantInspectionResource}
 */
public class RestaurantInspectionResourceTests extends JerseyTest {
    private Database db;

    @Override
    protected Application configure() {
        // always pick an available port
        forceSet(TestProperties.CONTAINER_PORT, "0");

        db = new HsqlDatabase();
        db.drop();
        db.create();

        return new WimfResourceConfig(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(db).to(Database.class);
            }
        }).getApplication();
    }

    @Override
    protected void configureClient(final ClientConfig config) {
        final ObjectMapper mapper = new ObjectMapperFactory().provide();
        final ObjectMapperProvider provider = new ObjectMapperProvider(mapper);
        config.register(provider);
    }

    @Test
    public void it_returns_an_empty_list() {
        // given the database contains no inspections

        // when fetchPage
        final ListDTO<RestaurantInspectionDTO> inspections = target("/api/inspection")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .buildGet()
                .invoke(new GenericType<ListDTO<RestaurantInspectionDTO>>() { });

        // then returns empty page
        assertThat(inspections.total).isEqualTo(0);
        assertThat(inspections.items).isEmpty();
    }

    @Test
    public void it_returns_a_list_of_inspections() throws Exception {
        // given the database contains 5 inspections
        try(final RestaurantInspectionDao dao = db.getRestaurantInspectionDao()) {
            IntStream.range(0, 5).forEach(i -> dao.insert(getInspection()));
        }

        // when getFetch the first 2
        final ListDTO<RestaurantInspectionDTO> first = target("/api/inspection")
                .queryParam("limit", "2")
                .queryParam("offset", "0")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .buildGet()
                .invoke(new GenericType<ListDTO<RestaurantInspectionDTO>>() { });

        // then returns a non empty page
        assertThat(first.total).isEqualTo(5);
        assertThat(first.items).hasSize(2);
    }

    static RestaurantInspection getInspection() {
        return new RestaurantInspection(
                "name",
                "boro",
                "a",
                LocalDateTime.now(),
                "someID",
                "someCuisine",
                "1A",
                23);
    }
}

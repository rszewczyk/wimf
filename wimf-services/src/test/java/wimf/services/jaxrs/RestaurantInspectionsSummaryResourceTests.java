package wimf.services.jaxrs;

import static org.assertj.core.api.Assertions.assertThat;
import org.glassfish.hk2.utilities.Binder;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.junit.Test;
import wimf.domain.Database;
import wimf.domain.HsqlDatabase;
import wimf.domain.RestaurantInspection;
import wimf.domain.RestaurantInspectionDao;
import wimf.services.dto.RestaurantInspectionsSummaryDTO;

import javax.ws.rs.core.MediaType;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * tests for {@link RestaurantInspectionsSummaryResource}
 */
public class RestaurantInspectionsSummaryResourceTests extends ResourceTest {
    private final Database db;

    static final String PATH = "/api/summary";

    public RestaurantInspectionsSummaryResourceTests() throws Exception {
        super();

        db = new HsqlDatabase();
        db.drop();
        db.create();

        try(final RestaurantInspectionDao dao = db.getRestaurantInspectionDao()) {
            getInspections().forEach(i -> RestaurantInspection.save(dao, i));
        }
    }

    @Override
    Binder[] getModules() {
        return new Binder[]{
                new AbstractBinder() {
                    @Override
                    protected void configure() {
                        bind(db).to(Database.class);
                    }
                }
        };
    }

    @Test
    public void it_returns_a_summary() {
        // given the database contains 5 inspections

        // when get the summary
        final RestaurantInspectionsSummaryDTO summary = target(PATH)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .buildGet()
                .invoke(RestaurantInspectionsSummaryDTO.class);

        // then the total is 5
        assertThat(summary.total).isEqualTo(5);
        // and there are two inspections with grade A
        assertThat(summary.gradesByDate.get("A")).hasSize(2);
    }

    @Test
    public void it_returns_a_filtered_summary() {
        // given the database contains 5 inspections

        // when get the summary
        final RestaurantInspectionsSummaryDTO summary = target(PATH)
                .queryParam("filter", "boro=Boro 3")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .buildGet()
                .invoke(RestaurantInspectionsSummaryDTO.class);

        assertThat(summary.total).isEqualTo(2);
    }

    private static List<RestaurantInspection> getInspections() {
        return Arrays.asList(
                new RestaurantInspection(
                        "Business C",
                        "Boro 1",
                        "A",
                        LocalDateTime.of(2016, 3, 20, 12, 30),
                        "businessA",
                        "Food Type 3",
                        "1A",
                        "some really bad stuff",
                        23,
                        "type 1"),
                new RestaurantInspection(
                        "Business B",
                        "Boro 3",
                        "D",
                        LocalDateTime.of(2016, 7, 20, 12, 30),
                        "businessB",
                        "Food Type 1",
                        "3B",
                        "some really bad stuff",
                        16,
                        "type 2"),
                new RestaurantInspection(
                        "Business A",
                        "Boro 3",
                        "D",
                        LocalDateTime.of(2016, 4, 20, 12, 30),
                        "businessA",
                        "Food Type 3",
                        "3B",
                        "some really bad stuff",
                        7,
                        "type 1"),
                new RestaurantInspection(
                        "Business D",
                        "Boro 2",
                        "A",
                        LocalDateTime.of(2016, 12, 20, 12, 30),
                        "businessD",
                        "Food Type 2",
                        "1A",
                        "some really bad stuff",
                        7,
                        "type 1"),
                new RestaurantInspection(
                        "Business D",
                        "Boro 2",
                        "B",
                        LocalDateTime.of(2016, 1, 20, 12, 30),
                        "businessD",
                        "Food Type 1",
                        "2A",
                        "some really bad stuff",
                        14,
                        "type 3")
        );
    }
}

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
import wimf.services.dto.ResultSetDTO;
import wimf.services.dto.RestaurantInspectionDTO;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

/**
 * tests for {@link RestaurantInspectionResource}
 */
public class RestaurantInspectionResourceTests extends JerseyTest {
    private final Database db;

    public RestaurantInspectionResourceTests() throws Exception {
        super();

        db = new HsqlDatabase();
        db.drop();
        db.create();

        try(final RestaurantInspectionDao dao = db.getRestaurantInspectionDao()) {
            getInspections().forEach(i -> RestaurantInspection.save(dao, i));
        }
    }

    @Override
    protected Application configure() {
        // always pick an available port
        forceSet(TestProperties.CONTAINER_PORT, "0");

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
    public void it_returns_paginated_lists_of_inspections() {
        // given the database contains 5 inspections

        // when get the first 2
        final ResultSetDTO first = target("/api/inspection")
                .queryParam("limit", "2")
                .queryParam("offset", "0")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .buildGet()
                .invoke(ResultSetDTO.class);

        // then returns 2 inspections
        assertThat(first.items).hasSize(2);
        // and total is 5
        assertThat(first.total).isEqualTo(5);


        // when get the next 5
        final ResultSetDTO second = target("/api/inspection")
                .queryParam("limit", "5")
                .queryParam("offset", "2")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .buildGet()
                .invoke(ResultSetDTO.class);

        // then returns 3 inspections
        assertThat(second.items).hasSize(3);
        // and total is 5
        assertThat(second.total).isEqualTo(5);
    }

    @Test
    public void it_returns_filtered_lists_of_inspections() {
        // given the database contains 5 inspections

        // when get all filtered by grade = A
        final ResultSetDTO byGrade = target("/api/inspection")
                .queryParam("limit", "5")
                .queryParam("offset", "0")
                .queryParam("filter", "grade=A")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .buildGet()
                .invoke(ResultSetDTO.class);

        // then returns 2 inspections
        assertThat(byGrade.items).hasSize(2);
        // and total is 5
        assertThat(byGrade.total).isEqualTo(5);
        // and all inspections have a grade of A
        byGrade.items.forEach(i -> assertThat(i.grade).isEqualTo("A"));

        // when get all filtered by inspection_date > 2016-04-01, score = 7
        final LocalDateTime filterDate = LocalDateTime.of(2016, 4, 1, 0, 0);
        final ResultSetDTO byDateAndScore = target("/api/inspection")
                .queryParam("limit", "5")
                .queryParam("offset", "0")
                .queryParam("filter",
                        "score=7",
                        "inspection_date>" + filterDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .request(MediaType.APPLICATION_JSON_TYPE)
                .buildGet()
                .invoke(ResultSetDTO.class);

        // then returns 2 inspections
        assertThat(byDateAndScore.items).hasSize(2);
        // and total is 5
        assertThat(byDateAndScore.total).isEqualTo(5);
        // and all inspections have a score of 7 and occurred after 4/1/2016
        byDateAndScore.items.forEach(i -> {
            assertThat(i.score).isEqualTo(7);
            assertThat(i.inspectionDate).isAfter(filterDate);
        });

        // when get all filtered by boro = Boro 1|Boro3
        final ResultSetDTO byBoro = target("/api/inspection")
                .queryParam("limit", "5")
                .queryParam("offset", "0")
                .queryParam("filter", "boro=Boro 1", "boro=Boro 3")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .buildGet()
                .invoke(ResultSetDTO.class);

        // then returns 3 inspections
        assertThat(byBoro.items).hasSize(3);
        // and total is 5
        assertThat(byBoro.total).isEqualTo(5);
        // and all inspections were in either Boro 1 or Boro 3
        byBoro.items.forEach(i -> assertThat(i.boro)
                .isIn(Arrays.asList("Boro 1", "Boro 3")));
    }

    @Test
    public void it_returns_bad_request_for_invalid_filters() {
        // given the database contains 5 inspections

        // when get all with an improperly formatted filter
        final Response malformed = target("/api/inspection")
                .queryParam("limit", "5")
                .queryParam("offset", "0")
                .queryParam("filter", "grade-A")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        // then a 400 error is returned
        assertThat(malformed.getStatus()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());
        assertThat(malformed.readEntity(String.class)).contains("Malformed filter `grade-A`");

        // when get all with an unknown filter
        final Response unknownFilter = target("/api/inspection")
                .queryParam("limit", "5")
                .queryParam("offset", "0")
                .queryParam("filter", "foo=bar")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        // then a 400 error is returned
        assertThat(unknownFilter.getStatus()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());
        assertThat(unknownFilter.readEntity(String.class)).contains("Unknown filter field `foo`");

        // when get all with an invalid filter value (score must be an integer)
        final Response invalidValue = target("/api/inspection")
                .queryParam("limit", "5")
                .queryParam("offset", "0")
                .queryParam("filter", "score=bar")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        // then a 400 error is returned
        assertThat(invalidValue.getStatus()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());
        assertThat(invalidValue.readEntity(String.class)).contains("Invalid filter value `bar`");
    }

    @Test
    public void it_returns_sorted_lists_of_inspections() {
        // given the database contains 5 inspections

        // when get all sorted by date ascending
        final ResultSetDTO byDate = target("/api/inspection")
                .queryParam("limit", "5")
                .queryParam("offset", "0")
                .queryParam("sort", "inspection_date ASC")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .buildGet()
                .invoke(ResultSetDTO.class);

        // then returns 5 inspections
        assertThat(byDate.items).hasSize(5);
        // and total is 5
        assertThat(byDate.total).isEqualTo(5);
        // and the inspections are sorted by date, ascending
        for(int i = 1; i < byDate.items.size(); i++) {
            assertThat(byDate.items.get(i).inspectionDate)
                    .isAfter(byDate.items.get(i - 1).inspectionDate);
        }
    }

    @Test
    public void it_returns_bad_request_for_invalid_sort_params() {
        // given the database contains 5 inspections

        // when get all with improperly formatted sort
        // when get all with an improperly formatted filter
        final Response malformed = target("/api/inspection")
                .queryParam("limit", "5")
                .queryParam("offset", "0")
                .queryParam("sort", "inspection_date")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        // then a 400 error is returned
        assertThat(malformed.getStatus()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());
        assertThat(malformed.readEntity(String.class)).contains("Malformed sort parameter `inspection_date`");

        // when get all with an unknown filter
        final Response unknown = target("/api/inspection")
                .queryParam("limit", "5")
                .queryParam("offset", "0")
                .queryParam("sort", "foo ASC")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        // then a 400 error is returned
        assertThat(unknown.getStatus()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());
        assertThat(unknown.readEntity(String.class)).contains("Unknown sort field `foo`");

        // when get all with an invalid filter value (score must be an integer)
        final Response invalidOrder = target("/api/inspection")
                .queryParam("limit", "5")
                .queryParam("offset", "0")
                .queryParam("sort", "score DES")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        // then a 400 error is returned
        assertThat(invalidOrder.getStatus()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());
        assertThat(invalidOrder.readEntity(String.class)).contains("Invalid sort order `DES`");
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
                        23),
                new RestaurantInspection(
                        "Business B",
                        "Boro 3",
                        "D",
                        LocalDateTime.of(2016, 7, 20, 12, 30),
                        "businessB",
                        "Food Type 1",
                        "3B",
                        16),
                new RestaurantInspection(
                        "Business A",
                        "Boro 3",
                        "D",
                        LocalDateTime.of(2016, 4, 20, 12, 30),
                        "businessA",
                        "Food Type 3",
                        "3B",
                        7),
                new RestaurantInspection(
                        "Business D",
                        "Boro 2",
                        "A",
                        LocalDateTime.of(2016, 12, 20, 12, 30),
                        "businessD",
                        "Food Type 2",
                        "1A",
                        7),
                new RestaurantInspection(
                        "Business D",
                        "Boro 2",
                        "B",
                        LocalDateTime.of(2016, 1, 20, 12, 30),
                        "businessD",
                        "Food Type 1",
                        "2A",
                        14)
        );
    }
}

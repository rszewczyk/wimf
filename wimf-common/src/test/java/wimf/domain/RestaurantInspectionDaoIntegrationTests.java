package wimf.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * test for {@link RestaurantInspectionDao}
 */
public class RestaurantInspectionDaoIntegrationTests {
    private Database db;
    private RestaurantInspectionDao dao;

    @Before
    public void before() {
        db = new HsqlDatabase();
        db.create();

        dao = db.getRestaurantInspectionDao();
    }

    @Test
    public void it_inserts_inspections() {
        // given some test inspections (TEST_DATA) and an empty database
        assertThat(dao.fetchPage(10, 10, Collections.emptyList())).isEmpty();

        // when we insert them
        TEST_DATA.forEach(dao::insert);

        // then they are in the database
        assertThat(dao.fetchPage(10, 0, Collections.emptyList())).hasSize(2);
    }

    @Test
    public void it_fetches_pages() {
        // given there are two inspections in the database
        TEST_DATA.forEach(dao::insert);
        assertThat(dao.fetchPage(10, 0, Collections.emptyList())).hasSize(2);

        // when we fetch a page of size 1
        final List<RestaurantInspection> firstPage = dao.fetchPage(1, 0, Collections.emptyList());

        // then we get 1 result
        assertThat(firstPage).hasSize(1);

        // when we fetch a page of size 2 at the next offset
        final List<RestaurantInspection> secondPage = dao.fetchPage(2, 1, Collections.emptyList());

        // then we get 1 result
        assertThat(secondPage).hasSize(1);
    }

    @Test
    public void it_gets_a_grade_date_aggregation() {
        // given there are two inspections in the database
        TEST_DATA.forEach(dao::insert);
        assertThat(dao.fetchPage(10, 0, Collections.emptyList())).hasSize(2);

        // when get grades aggregated by inspection_date
        final List<RestaurantInspectionsSummary.Aggregation<LocalDateTime>> aggs =
            dao.getGradeDateAggregation("inspection_date", Collections.emptyList());

        // then we get two aggregations
        assertThat(aggs).hasSize(2);
    }

    @After
    public void after() {
        db.drop();
    }

    private static final List<RestaurantInspection> TEST_DATA =
            Arrays.asList(
                    new RestaurantInspection(
                            "business1",
                            "boro1",
                            "A",
                            LocalDateTime.of(2016, 3, 1, 0, 0),
                            "b1",
                            "Donuts",
                            "1A",
                            10,
                            "inspection type 1"
                    ),
                    new RestaurantInspection(
                            "business2",
                            "boro2",
                            "B",
                            LocalDateTime.of(2016, 5, 1, 0, 0),
                            "b2",
                            "Pizza",
                            "1C",
                            14,
                            "inspection type 1"
                    )
            );
}

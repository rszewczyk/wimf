package wimf.domain;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * test for {@link RestaurantInspectionDao}
 */
public class RestaurantInspectionDaoIntegrationTest {
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
        assertThat(dao.fetchPage(10, 10)).isEmpty();

        // when we insert them
        TEST_DATA.forEach(dao::insert);

        // then they are in the database
        assertThat(dao.fetchPage(10, 0)).hasSize(2);
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
                            LocalDateTime.now(),
                            "b1",
                            "Donuts",
                            "1A",
                            10
                    ),
                    new RestaurantInspection(
                            "business2",
                            "boro2",
                            "B",
                            LocalDateTime.now(),
                            "b2",
                            "Pizza",
                            "1C",
                            14
                    )
            );
}

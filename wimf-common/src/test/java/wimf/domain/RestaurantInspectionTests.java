package wimf.domain;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDateTime;

/**
 * tests for {@link RestaurantInspection}
 */
public class RestaurantInspectionTests {
    private RestaurantInspectionDao dao;

    @Before
    public void before() {
        dao = mock(RestaurantInspectionDao.class);
    }

    @Test
    public void it_correctly_passes_validation_and_saves_in_db() {
        RestaurantInspection inspection = new RestaurantInspection(
                "some business",
                "some boro",
                "A",
                LocalDateTime.now(),
                "some business ID",
                "some cuisine",
                "some violation code",
                "some really bad stuff",
                23,
                "some inspection type");

        // when save is called
        RestaurantInspection.save(dao, inspection);

        // then no exception has been thrown

        // and the inspection has been saved in the database
        verify(dao, times(1)).insert(inspection);
    }

    @Test
    public void it_correctly_fails_validation_and_is_not_saved_in_db() {
        // boro cannot be a blank string
        RestaurantInspection inspection = new RestaurantInspection(
                "some business",
                "", // boro can't be blank
                "A",
                LocalDateTime.now(),
                "some business ID",
                "some cuisine",
                "some violation code",
                "some really bad stuff",
                23,
                "some inspection type");

        // when save is called then a Validation error occurs
        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> RestaurantInspection.save(dao, inspection));

        // and the inspection was not saved in the database
        verify(dao, times(0)).insert(any(RestaurantInspection.class));
    }
}

package wimf.domain;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDateTime;

/**
 * tests for {@link RestaurantInspection}
 */
public class RestaurantInspectionTest {
    private Validator validator;
    private RestaurantInspectionDAO dao;

    @Before
    public void before() {
        dao = mock(RestaurantInspectionDAO.class);
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void it_correctly_passes_validation_and_saves_in_db() {
        RestaurantInspection.Builder rib = RestaurantInspection.newBuilder()
                .businessName("some business")
                .boro("some boro")
                .grade("A")
                .inspectionDate(LocalDateTime.now())
                .businessID("some business ID")
                .cuisine("some cuisine")
                .violationCode("some violation code")
                .score(23);

        // when save is called
        RestaurantInspection inspection = rib.save(dao, validator);

        // then no exception has been thrown

        // and the inspection has been saved in the database
        verify(dao, times(1)).insert(inspection);
    }

    @Test
    public void it_correctly_fails_validation_and_is_not_saved_in_db() {
        RestaurantInspection.Builder rib = RestaurantInspection.newBuilder()
                .businessName("some business")
                .boro("") // cannot be blank
                .grade("A")
                .inspectionDate(LocalDateTime.now())
                .businessID("some business ID")
                .cuisine("some cuisine")
                .violationCode("some violation code")
                .score(13);

        // when save is called then a Validation error occurs
        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> rib.save(dao, validator));

        // and the inspection was not saved in the database
        verify(dao, times(0)).insert(any(RestaurantInspection.class));
    }
}

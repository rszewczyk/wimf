package wimf.domain;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;

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
        // given the properties
        String businessName = "some business";
        String boro = "some boro";

        RestaurantInspection.Builder rib = RestaurantInspection.newBuilder()
                .businessName(businessName)
                .boro(boro);

        // when save is called
        rib.save(dao, validator);

        // then no exception has been thrown

        // and the inspection has been saved in the database
        verify(dao, times(1)).insert(businessName, boro);
    }

    @Test
    public void it_correctly_fails_validation_and_is_not_saved_in_db() {
        // given the properties
        String businessName = "some business";
        String boro = "";

        RestaurantInspection.Builder rib = RestaurantInspection.newBuilder()
                .businessName(businessName)
                .boro(boro);

        // when save is called then a Validation error occurs
        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> rib.save(dao, validator));

        // and the inspection was not saved in the database
        verify(dao, times(0)).insert(businessName, boro);
    }
}

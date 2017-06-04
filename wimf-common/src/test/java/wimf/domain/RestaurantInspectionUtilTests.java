package wimf.domain;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;

import java.util.Arrays;

/**
 * tests for {@link RestaurantInspectionUtil}
 */
public class RestaurantInspectionUtilTests {
    @Test
    public void it_creates_the_correct_where_clause() {
        final String clause = RestaurantInspectionUtil
                .getWhereClause(Arrays.asList("foo1=4", "foo2=3", "foo1=6", "foo3<10"));

        assertThat(clause).isEqualTo("foo3<:filter3 AND (foo1=:filter0 OR foo1=:filter2) AND (foo2=:filter1)");
    }
}

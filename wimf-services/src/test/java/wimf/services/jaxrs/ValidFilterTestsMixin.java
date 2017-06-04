package wimf.services.jaxrs;

import org.junit.Test;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.assertj.core.api.Assertions.assertThat;

// TODO: this doesn't work with JUnit 4 - but might potentially work with http://junit.org/junit5
public interface ValidFilterTestsMixin {
    String getPath();
    WebTarget target(final String path);

    @Test
    default void it_returns_bad_request_for_invalid_filters() {
        // given the database contains 5 inspections

        // when get all with an improperly formatted filter
        final Response malformed = target(getPath())
                .queryParam("limit", "5")
                .queryParam("offset", "0")
                .queryParam("filter", "grade-A")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        // then a 400 error is returned
        assertThat(malformed.getStatus()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());
        assertThat(malformed.readEntity(String.class)).contains("Malformed filter `grade-A`");

        // when get all with an unknown filter
        final Response unknownFilter = target(getPath())
                .queryParam("limit", "5")
                .queryParam("offset", "0")
                .queryParam("filter", "foo=bar")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        // then a 400 error is returned
        assertThat(unknownFilter.getStatus()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());
        assertThat(unknownFilter.readEntity(String.class)).contains("Unknown filter field `foo`");

        // when get all with an invalid filter value (score must be an integer)
        final Response invalidValue = target(getPath())
                .queryParam("limit", "5")
                .queryParam("offset", "0")
                .queryParam("filter", "score=bar")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        // then a 400 error is returned
        assertThat(invalidValue.getStatus()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());
        assertThat(invalidValue.readEntity(String.class)).contains("Invalid filter value `bar`");
    }
}

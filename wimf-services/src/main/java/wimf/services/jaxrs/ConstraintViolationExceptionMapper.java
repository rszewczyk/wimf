package wimf.services.jaxrs;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.stream.Collectors;

/**
 * Maps validation errors to Bad Request
 */
@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
    @Override
    public Response toResponse(final ConstraintViolationException exception) {
        return Response
                .status(Response.Status.BAD_REQUEST)
                .type(MediaType.TEXT_PLAIN)
                .entity(exception.getMessage() + ": " +
                        String.join(" ", exception.getConstraintViolations()
                                .stream()
                                .map(ConstraintViolation::getMessage)
                                .collect(Collectors.toList())))
                .build();
    }
}

package wimf.services.jaxrs;

import wimf.domain.Database;
import wimf.domain.RestaurantInspectionDao;
import wimf.domain.RestaurantInspectionsSummary;
import wimf.domain.SummaryParams;
import wimf.services.dto.RestaurantInspectionsSummaryDTO;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/api/summary")
public class RestaurantInspectionsSummaryResource {
    private final Database db;

    @Inject
    RestaurantInspectionsSummaryResource(final Database db) {
        this.db = db;
    }

    @QueryParam("filter")
    private List<String> filter;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public RestaurantInspectionsSummaryDTO get() throws Exception {
        try (final RestaurantInspectionDao dao = db.getRestaurantInspectionDao()) {
            return RestaurantInspectionsSummaryDTO.fromModel(
                    RestaurantInspectionsSummary.get(dao, new SummaryParams(filter)));
        }
    }
}

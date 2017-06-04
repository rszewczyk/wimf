package wimf.services.jaxrs;

import wimf.domain.Database;
import wimf.domain.QueryParams;
import wimf.domain.RestaurantInspection;
import wimf.domain.RestaurantInspectionDao;
import wimf.services.dto.RestaurantInspectionDTO;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/api/inspection")
public class RestaurantInspectionResource {
    private final Database db;

    @Inject
    RestaurantInspectionResource(Database db) {
        this.db = db;
    }

    @QueryParam("limit") @DefaultValue("0")
    private int limit;

    @QueryParam("offset") @DefaultValue("0")
    private int offset;

    @QueryParam("sort")
    private List<String> sort;

    @QueryParam("filter")
    private List<String> filter;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<RestaurantInspectionDTO> getPage() throws Exception {

        try(final RestaurantInspectionDao dao = db.getRestaurantInspectionDao()) {
            return RestaurantInspectionDTO.fromModels(
                    RestaurantInspection.query(dao, new QueryParams(limit, offset, sort, filter)));
        }
    }
}

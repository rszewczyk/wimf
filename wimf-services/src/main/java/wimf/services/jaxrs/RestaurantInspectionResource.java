package wimf.services.jaxrs;

import wimf.domain.Database;
import wimf.domain.RestaurantInspectionDao;
import wimf.services.dto.ListDTO;
import wimf.services.dto.RestaurantInspectionDTO;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/api/inspection")
public class RestaurantInspectionResource {
    private final Database db;

    @Inject
    RestaurantInspectionResource(Database db) {
        this.db = db;
    }

    @DefaultValue("50") @QueryParam("limit") int limit;
    @DefaultValue("0") @QueryParam("offset") int offset;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ListDTO<RestaurantInspectionDTO> getPage() throws Exception {

        try(final RestaurantInspectionDao dao = db.getRestaurantInspectionDao()) {
            return RestaurantInspectionDTO.fromModels(dao.count(), dao.fetchPage(limit, offset));
        }
    }
}

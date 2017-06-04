package wimf.services.jaxrs;

import wimf.domain.Database;
import wimf.domain.RestaurantInspection;
import wimf.domain.RestaurantInspectionDao;
import wimf.services.dto.ResultSetDTO;
import wimf.services.dto.RestaurantInspectionDTO;
import wimf.services.dto.InspectionQueryDTO;

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

    @BeanParam
    private InspectionQueryDTO query;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ResultSetDTO getPage() throws Exception {

        try(final RestaurantInspectionDao dao = db.getRestaurantInspectionDao()) {
            return RestaurantInspectionDTO.fromModels(
                    RestaurantInspection.count(dao),
                    RestaurantInspection.getPage(dao, query.toPageParams()));
        }
    }
}

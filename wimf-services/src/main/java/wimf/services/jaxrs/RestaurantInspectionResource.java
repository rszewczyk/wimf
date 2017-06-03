package wimf.services.jaxrs;

import wimf.domain.Database;
import wimf.domain.RestaurantInspectionDao;
import wimf.services.dto.ListDTO;
import wimf.services.dto.RestaurantInspectionDTO;
import wimf.services.dto.WimfQueryDto;

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
    private WimfQueryDto query;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ListDTO<RestaurantInspectionDTO> getPage() throws Exception {

        try(final RestaurantInspectionDao dao = db.getRestaurantInspectionDao()) {
            return RestaurantInspectionDTO.fromModels(dao.count(), dao.fetchPage(query.limit, query.offset));
        }
    }
}

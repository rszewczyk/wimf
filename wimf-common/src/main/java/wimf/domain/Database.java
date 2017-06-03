package wimf.domain;

import org.jvnet.hk2.annotations.Contract;

@Contract
public interface Database {
    public void create();
    public void drop();
    public RestaurantInspectionDao getRestaurantInspectionDao();
}

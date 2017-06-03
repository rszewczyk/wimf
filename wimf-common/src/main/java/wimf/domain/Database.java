package wimf.domain;

import org.jvnet.hk2.annotations.Contract;

@Contract
public interface Database {
    void create();
    void drop();
    RestaurantInspectionDao getRestaurantInspectionDao();
}

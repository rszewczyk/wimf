package wimf.domain;

public interface Database {
    public void create();
    public void drop();
    public RestaurantInspectionDao getRestaurantInspectionDao();
}

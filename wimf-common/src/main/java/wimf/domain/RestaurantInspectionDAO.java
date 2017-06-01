package wimf.domain;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

public interface RestaurantInspectionDAO {
    @SqlUpdate("create table restaurant_inspection (id serial primary key, business_name varchar, boro varchar)")
    void createTable();

    @SqlUpdate("drop table restaurant_inspection")
    void dropTable();

    @SqlUpdate("insert into restaurant_inspection (business_name, boro) values (:businessName, :boro)")
    void insert(@Bind("businessName") String businessName, @Bind("boro") String borough);
}

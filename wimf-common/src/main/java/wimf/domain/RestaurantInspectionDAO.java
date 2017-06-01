package wimf.domain;

import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

public interface RestaurantInspectionDAO {
    @SqlUpdate(
            "CREATE TABLE restaurant_inspection (" +
                "id SERIAL PRIMARY KEY, " +
                "business_name VARCHAR, " +
                "boro VARCHAR, " +
                "grade VARCHAR, " +
                "inspection_date TIMESTAMP, " +
                "business_id VARCHAR, " +
                "cuisine VARCHAR, " +
                "violation_code VARCHAR, " +
                "score INTEGER)")
    void createTable();

    @SqlUpdate("DROP TABLE IF EXISTS restaurant_inspection")
    void dropTable();

    @SqlUpdate(
            "INSERT INTO restaurant_inspection (" +
                "business_name, " +
                "boro, " +
                "grade, " +
                "inspection_date, " +
                "business_id, " +
                "cuisine, " +
                "violation_code, " +
                "score) " +
            "VALUES (:businessName, :boro, :grade, :inspectionDate, :businessID, :cuisine, :violationCode, :score)")
    void insert(@BindBean RestaurantInspection inspection);
}

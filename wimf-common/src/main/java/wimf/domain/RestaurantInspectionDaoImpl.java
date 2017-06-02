package wimf.domain;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public final class RestaurantInspectionDaoImpl implements RestaurantInspectionDao {
    private final RestaurantInspectionJdbiDao dao;
    private final Handle handle;

    RestaurantInspectionDaoImpl(final Handle handle) {
        dao = handle.attach(RestaurantInspectionJdbiDao.class);
        this.handle = handle;
    }

    public void insert(final RestaurantInspection inspection) {
        dao.insert(inspection);
    }

    public List<RestaurantInspection> fetchPage(final int limit, final int offset) {
        return dao.fetchPage(limit, offset);
    }

    public void close() {
        handle.close();
    }

    public interface RestaurantInspectionJdbiDao {
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

        @RegisterRowMapper(RestaurantInspectionMapper.class)
        @SqlQuery("SELECT * from restaurant_inspection LIMIT :limit OFFSET :offset")
        List<RestaurantInspection> fetchPage(@Bind("limit") int limit, @Bind("offset") int offset);
    }

    static public class RestaurantInspectionMapper implements RowMapper<RestaurantInspection> {
        @Override
        public RestaurantInspection map(final ResultSet r, final StatementContext ctx) throws SQLException {
            return new RestaurantInspection(r.getString("business_name"),
                    r.getString("boro"),
                    r.getString("grade"),
                    r.getTimestamp("inspection_date").toLocalDateTime(),
                    r.getString("business_id"),
                    r.getString("cuisine"),
                    r.getString("violation_code"),
                    r.getInt("score"));
        }
    }
}

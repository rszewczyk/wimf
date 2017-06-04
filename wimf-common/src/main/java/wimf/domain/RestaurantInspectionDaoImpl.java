package wimf.domain;

import com.google.common.base.Strings;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.customizer.BindMap;
import org.jdbi.v3.sqlobject.customizer.Define;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public final class RestaurantInspectionDaoImpl extends RestaurantInspectionDao {
    private final RestaurantInspectionJdbiDao dao;
    private final Handle handle;

    RestaurantInspectionDaoImpl(final Handle handle) {
        dao = handle.attach(RestaurantInspectionJdbiDao.class);
        this.handle = handle;
    }

    @Override
    protected void insert(final RestaurantInspection inspection) {
        dao.insert(inspection);
    }

    @Override
    protected List<RestaurantInspection> fetchPage(final int limit,
                                                   final int offset,
                                                   final List<String> sort) {
        return dao.fetchPage(limit, offset, RestaurantInspectionUtil.getOrderByClause(sort));
    }

    @Override
    protected List<RestaurantInspection> fetchPage(final int limit,
                                                   final int offset,
                                                   final List<String> sort,
                                                   final List<String> filter) {

        final String wc = RestaurantInspectionUtil.getWhereClause(filter);

        return dao.fetchPage(
                limit,
                offset,
                RestaurantInspectionUtil.getOrderByClause(sort),
                Strings.isNullOrEmpty(wc) ? "" : "WHERE " + wc,
                RestaurantInspectionUtil.getWhereValues(filter));
    }

    @Override
    protected long count() {
        return dao.count();
    }

    @Override
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
                "VALUES (:businessName, :boro, :grade, :inspectionDate, " +
                        ":businessID, :cuisine, :violationCode, :score)")
        void insert(@BindBean RestaurantInspection inspection);

        @RegisterRowMapper(RestaurantInspectionMapper.class)
        @SqlQuery("SELECT * from restaurant_inspection ORDER BY <order> LIMIT :limit OFFSET :offset")
        List<RestaurantInspection> fetchPage(@Bind("limit") int limit,
                                             @Bind("offset") int offset,
                                             @Define("order") String order);

        @RegisterRowMapper(RestaurantInspectionMapper.class)
        @SqlQuery("SELECT * from restaurant_inspection <where> ORDER BY <order> LIMIT :limit OFFSET :offset")
        List<RestaurantInspection> fetchPage(@Bind("limit") int limit,
                                             @Bind("offset") int offset,
                                             @Define("order") String order,
                                             @Define("where") String where,
                                             @BindMap Map<String, Object> whereVals);

        @RegisterRowMapper(CountMapper.class)
        @SqlQuery("SELECT count(*) from restaurant_inspection")
        long count();
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

    static public class CountMapper implements RowMapper<Long> {
        @Override
        public Long map(final ResultSet r, final StatementContext ctx) throws SQLException {
            return r.getLong("count");
        }
    }
}

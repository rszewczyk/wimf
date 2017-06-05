package wimf.domain;

import com.google.common.base.Strings;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.jdbi.v3.sqlobject.SqlObject;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.customizer.BindMap;
import org.jdbi.v3.sqlobject.customizer.Define;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

final class RestaurantInspectionDaoImpl extends RestaurantInspectionDao {
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
    protected long count(final List<String> filter) {
        final String wc = RestaurantInspectionUtil.getWhereClause(filter);

        return dao.count(
                Strings.isNullOrEmpty(wc) ? "" : "WHERE " + wc,
                RestaurantInspectionUtil.getWhereValues(filter));
    }

    @Override
    protected long countGrades(final List<String> filter) {
        final String wc = RestaurantInspectionUtil.getWhereClause(filter);

        return dao.countGrades(
                Strings.isNullOrEmpty(wc) ? "" : "WHERE " + wc,
                RestaurantInspectionUtil.getWhereValues(filter));
    }

    @Override
    protected List<RestaurantInspectionsSummary.Aggregation<String>> countTerms(final String field, final List<String> filter) {
        final String wc = RestaurantInspectionUtil.getWhereClause(filter);

        return dao.countTerms(
                field,
                Strings.isNullOrEmpty(wc) ? "" : "WHERE " + wc,
                RestaurantInspectionUtil.getWhereValues(filter));
    }

    @Override
    protected List<RestaurantInspectionsSummary.Aggregation<String>> getGradeStringAggregation(final String aggName,
                                                                                               final List<String> userFilter,
                                                                                               final String grade) {
        final String wc = RestaurantInspectionUtil.getWhereClause(userFilter);
        return dao.getGradeAggregation(
                aggName,
                Strings.isNullOrEmpty(wc)
                        ? "WHERE grade='" + grade + "'"
                        : "WHERE grade='" + grade + "' AND (" + wc + ")",
                "count DESC",
                RestaurantInspectionUtil.getWhereValues(userFilter));
    }

    @Override
    protected List<RestaurantInspectionsSummary.Aggregation<LocalDateTime>> getGradeDateAggregation(final String aggName,
                                                                                                    final List<String> userFilter,
                                                                                                    final String grade) {
        final String wc = RestaurantInspectionUtil.getWhereClause(userFilter);
        return dao.getGradeDateAggregation(
                aggName,
                Strings.isNullOrEmpty(wc)
                        ? "WHERE grade='" + grade + "'"
                        : "WHERE grade='" + grade + "' AND (" + wc + ")",
                "agg ASC",
                RestaurantInspectionUtil.getWhereValues(userFilter));
    }

    @Override
    public void close() {
        handle.close();
    }

    private static final String GRADE_AGG_QUERY =
            "SELECT <select> agg, count(*) count " +
            "FROM (" +
                "SELECT inspection_date, business_id, grade, score, boro, cuisine, inspection_type " +
                "FROM restaurant_inspection " +
                "<where> " +
                "GROUP BY inspection_date, business_id, grade, score, boro, cuisine, inspection_type " +
            ") sub " +
            "GROUP BY agg ORDER BY <order>";

    public interface RestaurantInspectionJdbiDao extends SqlObject {
        @SqlUpdate(
                "INSERT INTO restaurant_inspection (" +
                        "business_name, " +
                        "boro, " +
                        "grade, " +
                        "inspection_date, " +
                        "business_id, " +
                        "cuisine, " +
                        "violation_code, " +
                        "score, " +
                        "inspection_type) " +
                "VALUES (:businessName, :boro, :grade, :inspectionDate, " +
                        ":businessID, :cuisine, :violationCode, :score, :inspectionType)")
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
        @SqlQuery("SELECT count(*) from restaurant_inspection <where>")
        long count(@Define("where") String where, @BindMap Map<String, Object> whereVals);

        @RegisterRowMapper(CountMapper.class)
        @SqlQuery("SELECT count(*) " +
                  "FROM (" +
                      "SELECT inspection_date, business_id, grade, score, boro, cuisine, inspection_type " +
                      "FROM restaurant_inspection " +
                      "<where> " +
                      "GROUP BY inspection_date, business_id, grade, score, boro, cuisine, inspection_type " +
                  ") sub")
        long countGrades(@Define("where") String where, @BindMap Map<String, Object> whereVals);

        @RegisterRowMapper(StringAggregationMapper.class)
        @SqlQuery("SELECT <select> agg, count(*) count FROM restaurant_inspection <where> GROUP BY agg")
        List<RestaurantInspectionsSummary.Aggregation<String>> countTerms(@Define("select") String aggName,
                                                                          @Define("where") String where,
                                                                          @BindMap Map<String, Object> whereVals);

        @RegisterRowMapper(StringAggregationMapper.class)
        @SqlQuery(GRADE_AGG_QUERY)
        List<RestaurantInspectionsSummary.Aggregation<String>> getGradeAggregation(@Define("select") String select,
                                                                                   @Define("where") String where,
                                                                                   @Define("order") String order,
                                                                                   @BindMap Map<String, Object> whereVals);

        @RegisterRowMapper(TimestampAggregationMapper.class)
        @SqlQuery(GRADE_AGG_QUERY)
        List<RestaurantInspectionsSummary.Aggregation<LocalDateTime>> getGradeDateAggregation(@Define("select") String select,
                                                                                              @Define("where") String where,
                                                                                              @Define("order") String order,
                                                                                              @BindMap Map<String, Object> whereVals);
    }

    static public class RestaurantInspectionMapper implements RowMapper<RestaurantInspection> {
        @Override
        public RestaurantInspection map(final ResultSet r, final StatementContext ctx)
                throws SQLException {

            return new RestaurantInspection(
                    r.getString("business_name"),
                    r.getString("boro"),
                    r.getString("grade"),
                    r.getTimestamp("inspection_date").toLocalDateTime(),
                    r.getString("business_id"),
                    r.getString("cuisine"),
                    r.getString("violation_code"),
                    r.getInt("score"),
                    r.getString("inspection_type"));
        }
    }

    static public class CountMapper implements RowMapper<Long> {
        @Override
        public Long map(final ResultSet r, final StatementContext ctx) throws SQLException {
            return r.getLong("count");
        }
    }

    static public class StringAggregationMapper
            implements RowMapper<RestaurantInspectionsSummary.Aggregation<String>> {

        @Override
        public RestaurantInspectionsSummary.Aggregation<String> map(final ResultSet r, final StatementContext ctx) throws SQLException {
            return new RestaurantInspectionsSummary.Aggregation<>(
                    r.getString("agg"),
                    r.getLong("count")
            );
        }
    }

    static public class TimestampAggregationMapper
            implements RowMapper<RestaurantInspectionsSummary.Aggregation<LocalDateTime>> {

        @Override
        public RestaurantInspectionsSummary.Aggregation<LocalDateTime> map(final ResultSet r, final StatementContext ctx) throws SQLException {
            return new RestaurantInspectionsSummary.Aggregation<>(
                    r.getTimestamp("agg").toLocalDateTime(),
                    r.getLong("count")
            );
        }
    }
}

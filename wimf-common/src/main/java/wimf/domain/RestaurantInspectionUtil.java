package wimf.domain;

import com.google.common.collect.ImmutableSet;

import java.time.LocalDateTime;
import java.util.*;

final class RestaurantInspectionUtil {
    static final String FILTER_SPLITTER = "((?<=[<>=!])|(?=[<>=!]))";
    static final String SORT_SPLITTER = "[ ]";
    static final String SORT_ASC = "ASC";
    static final String SORT_DESC = "DESC";

    /**
     * White list of fields that are allowed to appear as column names in order by/where clauses
     */
    final static Set<String> VALID_FIELDS = ImmutableSet.of("inspection_date", "boro",
            "business_id", "cuisine", "violation_code", "score", "grade",
            "business_name", "inspection_type");

    /**
     * builds an order by clause
     *
     * @param sort
     * @return
     */
    static String getOrderByClause(final List<String> sort) {
        if (sort.isEmpty()) {
            return "inspection_date DESC";
        }

        return String.join(" ", sort);
    }

    /**
     * builds a parameterized where clause
     *
     * @param filter
     * @return
     */
    static String getWhereClause(final List<String> filter) {
        if (filter.isEmpty()) {
            return "";
        }

        final Map<String, List<String>> grouped = new LinkedHashMap<>();
        final List<String> clauses = new ArrayList<>();

        for(int i = 0; i < filter.size(); i++) {
            final String[] tokens = filter.get(i).split(FILTER_SPLITTER);
            final String filterName = tokens[0];
            final String op = tokens[1].equals("!") ? "!=" : tokens[1];

            // clause is the name, op and a named parameter - e.g. `filter_name<:filter1`
            final String clause = filterName + op + ":" + "filter" + i;

            // group equals/not equals filters by name so that they can be combined with OR
            if (op.equals("=") || op.equals("!=")) {
                if (!grouped.containsKey(filterName)) {
                    grouped.put(filterName, new ArrayList<>());
                }
                grouped.get(filterName).add(clause);
            } else {
                clauses.add(clause);
            }
        }

        for (List<String> vals : grouped.values()) {
            clauses.add("(" + String.join(" OR ", vals) + ")");
        }

        return String.join(" AND ", clauses);
    }

    /**
     * Gathers the filter values into a map keyed by the named parameter (:filter1, :filter2, etc)
     *
     * @param filter
     * @return
     */
    static Map<String, Object> getWhereValues(List<String> filter) {
        final Map<String, Object> vals = new HashMap<>();

        if (filter.isEmpty()) {
            return vals;
        }

        for (int i = 0; i < filter.size(); i++) {
            final String[] tokens = filter.get(i).split(FILTER_SPLITTER);
            final String key = tokens[0];
            final String val = tokens[2];
            final String filterKey = "filter" + i;

            if (VALID_FIELDS.contains(key)) {
                switch (key) {
                    case "inspection_date":
                        vals.put(filterKey, LocalDateTime.parse(val));
                        break;
                    case "score":
                        vals.put(filterKey, Integer.parseInt(val));
                        break;
                    default:
                        vals.put(filterKey, val);
                        break;
                }
            }
        }

        return vals;
    }
}

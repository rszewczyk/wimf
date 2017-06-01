package wimf.ingest;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import io.reactivex.Observable;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

import javax.validation.Validation;
import javax.validation.Validator;

public class App {
    @Parameter(names = "--max-inspections", description = "The maximum number of inspections to ingest")
    protected int maxInspections = -1;

    @Parameter(names="--max-inspections-page", description = "The maximum number of inspections to fetch in a single Open New York API call")
    protected int maxInspectionsPage = 10_000;

    @Parameter(names="--drop", description = "Drop the database before starting ingest")
    protected boolean drop = false;

    @Parameter(names = "--database-address", description = "Hostname (and optional port) to a Postgres database")
    protected String dbHost = "postgres:5432";

    @Parameter(names = "--database-name", description = "The name of the database")
    protected  String dbName = "postgres";

    @Parameter(names = "--database-user", description = "The database user")
    protected String dbUser = "postgres";

    @Parameter(names = "--help", description = "Display usage then exit", help = true)
    private boolean help = false;

    public static void main(String[] argv) {
        final App app = new App();

        final JCommander jc = JCommander.newBuilder()
                .addObject(app)
                .build();

        try {
            jc.parse(argv);
        } catch (ParameterException e) {
            e.usage();
            System.exit(1);
        }

        if (app.help) {
            jc.usage();
            System.exit(1);
        }

        app.run();
    }

    private void run() {
        final Jdbi jdbi = Jdbi.create("jdbc:postgresql://" + dbHost + "/" + dbName + "?user=" + dbUser);
        jdbi.installPlugin(new SqlObjectPlugin());

        try (final Handle handle = jdbi.open()) {
            final wimf.domain.RestaurantInspectionDAO dao =
                    handle.attach(wimf.domain.RestaurantInspectionDAO.class);

            if (drop) {
                dao.dropTable();
                dao.createTable();
            }
        }

        try (final Handle handle = jdbi.open()) {
            final wimf.domain.RestaurantInspectionDAO dao =
                    handle.attach(wimf.domain.RestaurantInspectionDAO.class);

            final int pageSize = maxInspectionsPage > maxInspections
                    ? maxInspections
                    : maxInspectionsPage;

            final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

            final Observable<RestaurantInspection> inspections = maxInspections > 0
                    ? new RestaurantInspectionConsumer(pageSize).getAll().take(maxInspections)
                    : new RestaurantInspectionConsumer(pageSize).getAll();

            inspections.forEach(ri ->
                    wimf.domain.RestaurantInspection.newBuilder()
                            .boro(ri.boro)
                            .businessName(ri.businessName)
                            .inspectionDate(ri.inspectionDate)
                            .grade(ri.grade)
                            .businessID(ri.businessID)
                            .cuisine(ri.cuisine)
                            .violationCode(ri.violationCode)
                            .score(ri.score)
                            .save(dao, validator));

        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        System.out.println("Done ingest");
    }
}
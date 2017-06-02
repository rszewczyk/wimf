package wimf.ingest;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import io.reactivex.Observable;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import wimf.domain.Database;
import wimf.domain.PostgresDatabase;
import wimf.domain.RestaurantInspectionDao;

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

    public static void main(String[] argv) throws InterruptedException {
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

    private void run() throws InterruptedException {
        final String connection = "jdbc:postgresql://" + dbHost + "/" + dbName + "?user=" + dbUser;

         // hacky stand in for real service discovery - make sure we can connect within a reasonable amount of time
        final Jdbi jdbi = Jdbi.create(connection);
        int retry = 6;
        long backOff = 500;
        do {
            try (final Handle handle = jdbi.open()) {
                // we connected successfully
                retry = 0;
            } catch (final Throwable e) {
                if (retry == 1) {
                    System.out.printf("Failed to connect to database: %s\n", e.getMessage());
                    System.exit(1);
                }

                Thread.sleep(backOff);
                backOff *= 2;
                retry--;
            }
        } while(retry > 0);

        final Database db = new PostgresDatabase(connection);

        try {
            if (drop) {
                db.drop();
            }
            db.create();
        } catch (Exception e) {
            System.out.printf("Failed to initialize database: %s\n", e.getMessage());
        }

        final boolean fetchAll = maxInspections < 0;

        final int pageSize = !fetchAll && maxInspectionsPage > maxInspections
                ? maxInspections
                : maxInspectionsPage;

        final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        try (final RestaurantInspectionDao dao = db.getRestaurantInspectionDao()) {
            final Observable<RestaurantInspection> inspections = fetchAll
                    ? new RestaurantInspectionConsumer(pageSize).getAll()
                    : new RestaurantInspectionConsumer(pageSize).getAll().take(maxInspections);

            inspections.forEach(ri ->
                    wimf.domain.RestaurantInspection.save(
                            new wimf.domain.RestaurantInspection(
                                    ri.businessName,
                                    ri.boro,
                                    ri.grade,
                                    ri.inspectionDate,
                                    ri.businessID,
                                    ri.cuisine,
                                    ri.violationCode,
                                    ri.score
                            ),
                            dao,
                            validator
                    ));

        } catch (final Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        System.out.println("Done ingest");
    }
}
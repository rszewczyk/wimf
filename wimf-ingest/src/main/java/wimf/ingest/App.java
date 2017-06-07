package wimf.ingest;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import io.reactivex.Observable;
import io.reactivex.observables.ConnectableObservable;
import org.assertj.core.util.Strings;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wimf.domain.*;
import wimf.domain.Business;

import java.util.stream.Collectors;

import static wimf.domain.RestaurantInspection.save;

public class App {

    private static final Logger log = LoggerFactory.getLogger(App.class);

    @Parameter(names = "--max-inspections", description = "The maximum number of inspections to ingest")
    protected int maxInspections = 0;

    @Parameter(names="--max-inspections-page", description = "The maximum number of inspections to fetch in a single Open New York API call")
    protected int maxInspectionsPage = 10_000;

    @Parameter(names = "--max-yelp-records", description = "The maximum number of yelp records to ingest")
    protected int maxYelpRecords = 0;

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

    private String yelpToken = "";

    public static void main(String[] argv) throws Exception {
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

        app.yelpToken = System.getenv("YELP_API_TOKEN");

        if (app.help) {
            jc.usage();
            System.exit(1);
        }

        app.run();
    }

    private void run() throws Exception {
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
                    log.error("Failed to connect to database: {}", e.getMessage());
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
            log.error("Failed to initialize database: {}", e.getMessage());
        }

        final boolean fetchAllInspection = maxInspections <= 0;

        final int pageSize = !fetchAllInspection && maxInspectionsPage > maxInspections
                ? maxInspections
                : maxInspectionsPage;

        log.info("Starting ingest of {} NY Open data records.", fetchAllInspection ? "all" : maxInspections);

        try (final RestaurantInspectionDao dao = db.getRestaurantInspectionDao()) {
            final Observable<RestaurantInspection> inspections = (fetchAllInspection
                    ? new RestaurantInspectionConsumer(pageSize).getAll()
                    : new RestaurantInspectionConsumer(pageSize).getAll().take(maxInspections));

            inspections.buffer(1000).forEach(rib ->
                    save(dao, rib.stream().map(RestaurantInspection::asModel).collect(Collectors.toList())));

            log.info("NY Open data ingest complete.");

            if (!Strings.isNullOrEmpty(yelpToken)) {
                final boolean fetchAllYelp = maxYelpRecords <= 0;
                log.info("Starting ingest of {} Yelp records", fetchAllYelp ? "all" : maxYelpRecords);

                YelpListingConsumer yelpListingConsumer = new YelpListingConsumer(yelpToken);

                yelpListingConsumer
                        .getListings(inspections.distinct(ri -> ri.businessID))
                        .buffer(10)
                        .forEach(b -> Business.save(dao, b));
            }

            // TODO: error handling for a failed inspection
        }

        log.info("Ingest completed.");
    }
}
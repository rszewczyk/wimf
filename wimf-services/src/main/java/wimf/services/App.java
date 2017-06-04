package wimf.services;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.RuntimeDelegate;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import wimf.domain.DatabaseBinder;
import wimf.services.jaxrs.WimfResourceConfig;

public class App {
    @Parameter(names = "--port", description = "The port on which the API endpoints will listen for requests")
    protected int port = 80;

    @Parameter(names = "--database-address", description = "Hostname (and optional port) to a Postgres database")
    protected String dbHost = "postgres:5432";

    @Parameter(names = "--database-name", description = "The name of the database")
    protected  String dbName = "postgres";

    @Parameter(names = "--database-user", description = "The database user")
    protected String dbUser = "postgres";

    @Parameter(names = "--help", description = "Display usage then exit", help = true)
    private boolean help = false;

    public static void main(String[] argv) throws IOException, InterruptedException {
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

        Thread.currentThread().join();
    }

    private void run() throws IOException {
        final String connection = "jdbc:postgresql://" + dbHost + "/" + dbName + "?user=" + dbUser;

        final Application jerseyApp =
                new WimfResourceConfig(DatabaseBinder.get(connection)).getApplication();

        final URI baseUri = UriBuilder.fromUri("http://localhost/").port(port).build();

        final Server server = JettyHttpContainerFactory.createServer(baseUri, ResourceConfig.forApplication(jerseyApp));

        try {
            server.start();
            server.join();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

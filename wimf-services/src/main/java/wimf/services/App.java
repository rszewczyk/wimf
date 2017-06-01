package wimf.services;

import java.io.IOException;
import java.net.InetSocketAddress;

import javax.ws.rs.ext.RuntimeDelegate;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class App {
    @Parameter(names = "--port", description = "The port on which the API endpoints will listen for requests")
    protected int port = 80;

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
        final HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> server.stop(0)));

        final HttpHandler handler = RuntimeDelegate
                .getInstance()
                .createEndpoint(new JaxRsApplication(), HttpHandler.class);

        server.createContext("/", handler);

        server.start();
    }
}

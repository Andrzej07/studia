
import dao.Dao;
import dao.MongoDao;
import model.Account;
import model.User;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.jaxws.JaxwsHandler;
import org.glassfish.grizzly.servlet.FilterRegistration;
import org.glassfish.grizzly.servlet.ServletRegistration;
import org.glassfish.grizzly.servlet.WebappContext;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import resource.AccountsEndpoint;
import service.AccountsService;
import soapservice.SoapService;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App extends ResourceConfig {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(App.class);
    private static final URI baseUri = UriBuilder.fromUri("http://0.0.0.0").port(8080).build();
    private static final String REST_URL = "/rest";
    private static final String SOAP_URL = "/soap";

    public App() {
        register(AccountsEndpoint.class);
    }

    public static void main(String[] args) throws IOException {
        final HttpServer server = new HttpServer();
        final NetworkListener listener = new NetworkListener("grizzly", baseUri.getHost(), baseUri.getPort());
        listener.setSecure(false);
        server.addListener(listener);

        WebappContext webappContext = new WebappContext("WebappContext", REST_URL);
        ServletRegistration registration = webappContext.addServlet("ServletContainer", ServletContainer.class);
        registration.addMapping("/*");
        registration.setInitParameter("javax.ws.rs.Application", App.class.getName());
        registration.setInitParameter("com.sun.jersey.api.json.POJOMappingFeature", "true");

        FilterRegistration fr = webappContext.addFilter("FilterAuth", new BasicAuthenticationFilter());
        fr.addMappingForUrlPatterns(null, "/*");
        webappContext.deploy(server);


        HttpHandler httpHandler = new JaxwsHandler(new SoapService());
        server.getServerConfiguration().addHttpHandler(httpHandler, SOAP_URL);

        log.info("Get WSDL for SOAP service: {}{}{}", baseUri, SOAP_URL, "?WSDL" );


        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                server.shutdownNow();
            }
        }));

        Logger l = Logger.getLogger("org.glassfish.grizzly.http.server.HttpHandler");
        l.setLevel(Level.FINE);
        l.setUseParentHandlers(false);
        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.ALL);
        l.addHandler(ch);

        try {
            server.start();
            System.out.println("Press any key to stop the server...");
            System.in.read();
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}

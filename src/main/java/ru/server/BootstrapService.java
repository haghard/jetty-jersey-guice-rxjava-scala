package ru.server;

import com.google.inject.*;
import com.google.inject.servlet.GuiceServletContextListener;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import ru.server.filters.RequestLogFilter;
import ru.server.resources.SimpleResource;
import ru.server.services.ScalaWebService;
import ru.server.services.api.WebService;
import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;

public class BootstrapService {

  private static final int JETTY_MAX_THREADS = 11;
  private static final int JETTY_MIN_THREADS = 11;
  private static final int JETTY_THREAD_IDLE_TIMEOUT = 1000 * 60 * 60;

  /*
   * curl -i -H  "Accept: application/json" GET http://localhost:9000/wiki/1
   *
   */
  public static void main(String[] args) throws Exception {
    final QueuedThreadPool threadPool =
        new QueuedThreadPool(JETTY_MAX_THREADS, JETTY_MIN_THREADS, JETTY_THREAD_IDLE_TIMEOUT);
    threadPool.setName("jetty-io-worker");

    final Server server = new Server(threadPool);


    final ServerConnector connector = new ServerConnector(server);
    connector.setPort(9000);
    connector.setHost("0.0.0.0");
    server.setConnectors(new Connector[] { connector });

    // Create a servlet context and add the jersey servlet.
    final ServletContextHandler context = new ServletContextHandler(server, "/");

    // Add our Guice listener that includes our bindings
    context.addEventListener(new GuiceServletConfig());
    context.addEventListener(new JavaUtilLoggerRedirector());

    context.addFilter(RequestLogFilter.class, "/*", null);

    // Then add GuiceFilter and configure the server to
    // reroute all requests through this filter.
    //context.addFilter(GuiceFilter.class, "/*", null);

    context.addServlet(DefaultServlet.class, "/");

    context.addServlet(new ServletHolder(new GeneralServlet(ScalaWebService.apply())), "/video/*");
    //
    context.addServlet(new ServletHolder(FaultTolerantServlet.apply()), "/io/*");

    //curl http://localhost:9000/hystrix.stream
    context.addServlet(HystrixMetricsStreamServlet.class, "/hystrix.stream");

    server.setStopAtShutdown(true);

    // Start the server
    Runtime.getRuntime().addShutdownHook(new Thread(() -> { server.getStopAtShutdown(); }));
    server.start();
    server.join();
  }

  static class GuiceServletConfig extends GuiceServletContextListener {
    final Module resources = binder -> {
      binder.bind(SimpleResource.class).in(Singleton.class);
      binder.bind(WebService.class).to(ScalaWebService.class);
    };

    @Override
    protected Injector getInjector() {
      return Guice.createInjector(resources, new JerseyServletModule() {
        @Override
        protected void configureServlets() {
          //hook Jackson into Jersey as the POJO <-> JSON mapper
          bind(JacksonJsonProvider.class).in(Scopes.SINGLETON);
          // Route all requests through GuiceContainer
          serve("/wiki/*").with(GeneralServlet.class);
          serve("/*").with(GuiceContainer.class);
        }
      });
    }
  }
}
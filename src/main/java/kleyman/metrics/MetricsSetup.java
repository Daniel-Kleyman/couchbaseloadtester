package kleyman.metrics;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.micrometer.prometheus.PrometheusConfig;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class MetricsSetup {
    private static final PrometheusMeterRegistry prometheusRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

    public MetricsSetup() {
        setupMetrics();
    }

    public static void setupMetrics() {
        try {
            // Create an HttpServer instance that listens on port 8080
            HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);
            // Define a /metrics endpoint
            server.createContext("/metrics", exchange -> {
                // Prepare the response by scraping the Prometheus registry
                String response = prometheusRegistry.scrape();
                exchange.getResponseHeaders().set("Content-Type", "text/plain");
                exchange.sendResponseHeaders(200, response.length());
                // Write the response
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            });
            // Start the server
            server.start();
        } catch (Exception e) {

        }

    }


    public static MeterRegistry getRegistry() {
        return prometheusRegistry;
    }
}
//
//import com.sun.net.httpserver.HttpServer;
//import io.micrometer.core.instrument.MeterRegistry;
//import io.micrometer.prometheus.PrometheusMeterRegistry;
//import io.micrometer.prometheus.PrometheusConfig;
//import io.prometheus.client.exporter.HTTPServer;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.IOException;
//import java.io.OutputStream;
//import java.net.InetSocketAddress;
//
///**
// * This class is responsible for setting up and providing access to a Prometheus-based metrics registry
// * using Micrometer. It initializes the PrometheusMeterRegistry with default configurations and allows
// * retrieval of the registry for recording metrics.
// */
//public class MetricsSetup {
//
//    private static final Logger logger = LoggerFactory.getLogger(MetricsSetup.class);
//    private static PrometheusMeterRegistry registry;
//    private static HTTPServer metricsServer;
//
//    public MetricsSetup() {
//        setupMetrics();
//    }
//
//    public static void setupMetrics() {
//        registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
//
//        try {
//            // Create and start the HTTP server with the registry
//            metricsServer = new HTTPServer(8081);
//
//            logger.info("Metrics server started on port 8081.");
//        } catch (IOException e) {
//            logger.error("Failed to start metrics server", e);
//        }
//    }
//
//    public static MeterRegistry getRegistry() {
//        return registry;
//    }
//
//    public static void stopMetricsServer() {
//        if (metricsServer != null) {
//            metricsServer.close();
//            logger.info("Metrics server stopped.");
//        }
//    }
//}

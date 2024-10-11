package kleyman.metrics;

import com.sun.net.httpserver.HttpServer;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.micrometer.prometheus.PrometheusConfig;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.net.InetSocketAddress;

/**
 * Sets up a Prometheus metrics server on port 8081.
 * Provides a /metrics endpoint to expose application metrics.
 */
public class MetricsSetup {
    @Getter
    private static final PrometheusMeterRegistry prometheusRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
    private static final Logger logger = LoggerFactory.getLogger(MetricsSetup.class.getName());
    private static HttpServer server;

    public MetricsSetup() {
        setupMetrics();
    }

    public static void setupMetrics() {
        try {
            // Create an HttpServer instance that listens on port 8081
            server = HttpServer.create(new InetSocketAddress(8081), 0);
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

            server.start();
            logger.info("Metrics server started on port 8081");
        } catch (Exception e) {
            logger.error("Failed to start metrics server", e);
        }
    }

    public static void stopMetricsServer() {
        if (server != null) {
            server.stop(0);
            logger.info("Metrics server stopped.");
        }
    }

   // public static MeterRegistry getRegistry() {
//        return prometheusRegistry;
//    }
}


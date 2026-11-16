package shop;

import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.prometheus.client.exporter.HTTPServer;

import java.io.IOException;

public class MetricsServer {
    public static final PrometheusMeterRegistry REGISTRY = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

    public static void start() {
        try {
            // Запускаем HTTP-сервер на порту 8080
            new HTTPServer(8081);
            System.out.println("Metrics server started on http://localhost:8080/metrics");
        } catch (IOException e) {
            System.err.println("Failed to start metrics server: " + e.getMessage());
        }
    }
}
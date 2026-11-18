package shop;

import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.prometheus.client.exporter.HTTPServer;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.Properties;

public class MetricsServer {
    public static final PrometheusMeterRegistry REGISTRY = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

    public static void start() {
        try {
            Properties props = new Properties();
            InputStream input = MetricsServer.class.getClassLoader().getResourceAsStream("application.properties");
            int port = 8081;
            if (input != null) {
                props.load(input);
                String portStr = props.getProperty("server.port");
                if (portStr != null) {
                    port = Integer.parseInt(portStr);
                }
            }

            // Исправленный конструктор
            new HTTPServer(new InetSocketAddress(port), REGISTRY.getPrometheusRegistry(), true);
            System.out.println("Metrics server started on http://localhost:" + port + "/metrics");
        } catch (IOException e) {
            System.err.println("Failed to start metrics server: " + e.getMessage());
        }
    }
}
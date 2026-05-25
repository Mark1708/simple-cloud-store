package cloud.store.gateway;

import io.vertx.core.json.JsonObject;
import java.util.Optional;

public class GatewayConfig {

    private final int port;
    private final int catalogPort;
    private final String catalogHost;
    private final int inventoryPort;
    private final String inventoryHost;

    public GatewayConfig(JsonObject config) {
        this.port = parseIntProperty("http.port", "HTTP_PORT", config);
        this.catalogHost = requireStringProperty("catalog.host", "CATALOG_HOST", config);
        this.catalogPort = parseIntProperty("catalog.port", "CATALOG_PORT", config);
        this.inventoryHost = requireStringProperty("inventory.host", "INVENTORY_HOST", config);
        this.inventoryPort = parseIntProperty("inventory.port", "INVENTORY_PORT", config);
    }

    public int getPort() {
        return port;
    }

    public int getCatalogPort() {
        return catalogPort;
    }

    public String getCatalogHost() {
        return catalogHost;
    }

    public int getInventoryPort() {
        return inventoryPort;
    }

    public String getInventoryHost() {
        return inventoryHost;
    }

    private static String requireStringProperty(String configKey, String envKey, JsonObject config) {
        return Optional.ofNullable(config.getString(envKey))
                .or(() -> Optional.ofNullable(config.getString(configKey)))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Required configuration missing: set one of " + configKey + " or " + envKey));
    }

    private static int parseIntProperty(String configKey, String envKey, JsonObject config) {
        String value = requireStringProperty(configKey, envKey, config);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Invalid integer value for " + configKey + "/" + envKey + ": '" + value + "'", e);
        }
    }
}

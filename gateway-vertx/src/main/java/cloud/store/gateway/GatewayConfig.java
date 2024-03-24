package cloud.store.gateway;

import io.vertx.core.json.JsonObject;

import java.util.Optional;

public class GatewayConfig {

  private Integer port;
  private Integer catalogPort;
  private String catalogHost;
  private Integer inventoryPort;
  private String inventoryHost;

  public GatewayConfig(JsonObject config) {
    this.port = Integer.parseInt(getProperties("http.port", "HTTP_PORT", config));
    this.catalogHost = getProperties("catalog.host", "CATALOG_HOST", config);
    this.catalogPort = Integer.parseInt(getProperties("catalog.port", "CATALOG_PORT", config));
    this.inventoryHost = getProperties("inventory.host", "INVENTORY_HOST", config);
    this.inventoryPort = Integer.parseInt(getProperties("inventory.port", "INVENTORY_PORT", config));
  }

  public Integer getPort() {
    return port;
  }

  public Integer getCatalogPort() {
    return catalogPort;
  }

  public String getCatalogHost() {
    return catalogHost;
  }

  public Integer getInventoryPort() {
    return inventoryPort;
  }

  public String getInventoryHost() {
    return inventoryHost;
  }

  public static String getProperties(String configKey, String envKey, JsonObject config) {
    return Optional.ofNullable(config.getString(envKey))
      .orElse(
        Optional.ofNullable(config.getString(configKey))
          .orElseThrow(() -> new RuntimeException("Please set one of variables: " + configKey + ", " + envKey))
      );
  }
}

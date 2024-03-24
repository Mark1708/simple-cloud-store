package cloud.store.gateway;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.vertx.core.Launcher;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.rxjava3.config.ConfigRetriever;
import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.ext.web.Router;
import io.vertx.rxjava3.ext.web.RoutingContext;
import io.vertx.rxjava3.ext.web.client.WebClient;
import io.vertx.rxjava3.ext.web.client.predicate.ResponsePredicate;
import io.vertx.rxjava3.ext.web.codec.BodyCodec;
import io.vertx.rxjava3.ext.web.handler.CorsHandler;
import io.vertx.rxjava3.ext.web.handler.StaticHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class GatewayVerticle extends AbstractVerticle {
  private static final Logger log = LoggerFactory.getLogger(GatewayVerticle.class);

  private WebClient catalog;
  private WebClient inventory;

  public static void main(String[] args) {
    Launcher.executeCommand("run", GatewayVerticle.class.getName());
  }

  @Override
  public void start() {
    String logFactory = System.getProperty("org.vertx.logger-delegate-factory-class-name");
    if (logFactory == null) {
      System.setProperty("org.vertx.logger-delegate-factory-class-name",
        "io.vertx.core.logging.SLF4JLogDelegateFactory");
    }

    ConfigRetriever retriever = ConfigRetriever.create(vertx);

    retriever.getConfig().subscribe(config -> {
        GatewayConfig gwConf = new GatewayConfig(config);

        catalog = getClient(gwConf.getCatalogHost(), gwConf.getCatalogPort(), "Catalog");
        inventory = getClient(gwConf.getInventoryHost(), gwConf.getInventoryPort(), "Inventory");

        Router router = getRouter();

        runServer(router, gwConf.getPort());
      },
      e -> {
        throw new RuntimeException("Clients creation error", e);
      }
    );
  }

  private void runServer(Router router, Integer serverPort) {
    vertx.createHttpServer()
      .requestHandler(router)
      .listen(serverPort);

    log.info("Server is running on port " + serverPort);
  }

  private Router getRouter() {
    log.info("Create route");
    Router router = Router.router(vertx);
    router.route().handler(CorsHandler.create("*").allowedMethod(HttpMethod.GET));
    router.get("/*").handler(StaticHandler.create("assets"));
    router.get("/health").handler(this::health);
    router.get("/api/products").handler(this::products);
    return router;
  }

  private WebClient getClient(String host, Integer port, String serviceName) {
    WebClientOptions inventoryOptions = new WebClientOptions()
      .setDefaultHost(host)
      .setDefaultPort(port);
    WebClient webClient = WebClient.create(vertx, inventoryOptions);

    log.info("{} Service Endpoint: {}:{}", serviceName, host, port);
    return webClient;
  }

  private void products(RoutingContext rc) {
    catalog
      .get("/api/catalog")
      .expect(ResponsePredicate.SC_OK)
      .as(BodyCodec.jsonArray())
      .rxSend()
      .map(resp -> {
        List<JsonObject> listOfProducts = new ArrayList<>();
        for (Object product : resp.body()) {
          listOfProducts.add((JsonObject) product);
        }
        return listOfProducts;
      })
      .flatMap(products -> Observable.fromIterable(products)
        .flatMapSingle(this::getAvailabilityFromInventory)
        .collect(JsonArray::new, JsonArray::add)
      )
      .subscribe(
        list -> rc.response().end(list.encodePrettily()),
        error -> rc.response().setStatusCode(500).end(
          new JsonObject().put("error", error.getMessage()
          ).toString())
      );
  }

  private Single<JsonObject> getAvailabilityFromInventory(JsonObject product) {
    return inventory
      .get("/api/inventory/" + product.getString("itemId"))
      .as(BodyCodec.jsonObject())
      .rxSend()
      .map(resp -> {
        if (resp.statusCode() != 200) {
          log.warn("Inventory error for {}: status code {}",
            product.getString("itemId"), resp.statusCode());
          return product.copy();
        }
        return product.copy().put("availability",
          new JsonObject().put("quantity", resp.body().getInteger("quantity")));
      });
  }

  private void health(RoutingContext rc) {
    catalog.get("/").rxSend()
      .subscribe(
        catalogCallOk -> {
          inventory.get("/").rxSend()
            .subscribe(
              inventoryCallOk -> rc.response().setStatusCode(200).end(new JsonObject().put("status", "UP").toString()),
              error -> rc.response().setStatusCode(503).end(new JsonObject().put("status", "DOWN").toString())
            );
        },
        error -> rc.response().setStatusCode(503).end(new JsonObject().put("status", "DOWN").toString())
      );
  }
}

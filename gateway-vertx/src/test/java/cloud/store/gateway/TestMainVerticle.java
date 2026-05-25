package cloud.store.gateway;

import io.vertx.core.Vertx;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
public class TestMainVerticle {

    @BeforeEach
    void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
        Checkpoint checkpoint = testContext.checkpoint();
        vertx.deployVerticle(new GatewayVerticle())
                .onComplete(testContext.succeeding(id -> checkpoint.flag()));
    }

    @Test
    void verticle_deployed(Vertx vertx, VertxTestContext testContext) throws Throwable {
        testContext.completeNow();
    }
}

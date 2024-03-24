package cloud.store;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class InventoryResourceTest {
    @Test
    void testInventoryEndpoint() {
        given()
                .when().get("/api/inventory/329299")
                .then()
                .statusCode(200)
                .body(is("{\"id\":329299,\"quantity\":35}"));
    }

}
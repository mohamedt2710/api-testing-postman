package com.mohamed.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

/**
 * RestAssured versions of the main Restful Booker checks from the Postman collection.
 * Only the behaviour that works as documented is asserted here.
 * The 3 known API defects are in ../bug-reports.md, not in these tests.
 * Each test creates and cleans up its own booking, so tests do not depend on each other.
 */
public class RestfulBookerTest {

    private static final String BOOKING_JSON =
            "{\"firstname\":\"Test\",\"lastname\":\"Tester\",\"totalprice\":150,\"depositpaid\":true,"
                    + "\"bookingdates\":{\"checkin\":\"2026-12-01\",\"checkout\":\"2026-12-05\"},"
                    + "\"additionalneeds\":\"Breakfast\"}";

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";
        // The API rejects a Content-Type with a charset suffix, so stop RestAssured adding one
        RestAssured.config = RestAssured.config().encoderConfig(
                io.restassured.config.EncoderConfig.encoderConfig()
                        .appendDefaultContentCharsetToContentTypeIfUndefined(false));
    }

    private String token() {
        return given().contentType(ContentType.JSON)
                .body("{\"username\":\"admin\",\"password\":\"password123\"}")
                .post("/auth")
                .then().statusCode(200).extract().path("token");
    }

    private int createBooking() {
        return given().contentType(ContentType.JSON).accept("application/json")
                .body(BOOKING_JSON)
                .post("/booking")
                .then().statusCode(200).extract().path("bookingid");
    }

    private void deleteBooking(int id) {
        given().header("Cookie", "token=" + token()).delete("/booking/" + id);
    }

    @Test
    public void healthCheckReturnsCreated() {
        given().get("/ping").then().statusCode(201);
    }

    @Test
    public void validCredentialsReturnToken() {
        given().contentType(ContentType.JSON)
                .body("{\"username\":\"admin\",\"password\":\"password123\"}")
                .post("/auth")
                .then().statusCode(200).body("token", notNullValue());
    }

    @Test
    public void badCredentialsReturnNoToken() {
        given().contentType(ContentType.JSON)
                .body("{\"username\":\"admin\",\"password\":\"wrong\"}")
                .post("/auth")
                .then().statusCode(200)
                .body("reason", equalTo("Bad credentials"))
                .body("token", not(notNullValue()));
    }

    @Test
    public void createdBookingStoresSentData() {
        int id = createBooking();
        try {
            given().accept("application/json").get("/booking/" + id)
                    .then().statusCode(200)
                    .body("firstname", equalTo("Test"))
                    .body("lastname", equalTo("Tester"))
                    .body("totalprice", equalTo(150))
                    .body("depositpaid", equalTo(true))
                    .body("bookingdates.checkin", equalTo("2026-12-01"))
                    .body("bookingdates.checkout", equalTo("2026-12-05"));
        } finally {
            deleteBooking(id);
        }
    }

    @Test
    public void nonexistentBookingReturns404() {
        given().get("/booking/99999999").then().statusCode(404);
    }

    @Test
    public void bookingListIsNotEmpty() {
        given().accept("application/json").get("/booking")
                .then().statusCode(200).body("size()", greaterThan(0));
    }

    @Test
    public void filterByNameFindsCreatedBooking() {
        int id = createBooking();
        try {
            given().accept("application/json")
                    .queryParam("firstname", "Test").queryParam("lastname", "Tester")
                    .get("/booking")
                    .then().statusCode(200).body("bookingid", hasItem(id));
        } finally {
            deleteBooking(id);
        }
    }

    @Test
    public void updateWithoutTokenIsForbidden() {
        int id = createBooking();
        try {
            given().contentType(ContentType.JSON).accept("application/json")
                    .body(BOOKING_JSON)
                    .put("/booking/" + id)
                    .then().statusCode(403);
        } finally {
            deleteBooking(id);
        }
    }

    @Test
    public void partialUpdateChangesOnlyGivenFields() {
        int id = createBooking();
        try {
            given().contentType(ContentType.JSON).accept("application/json")
                    .header("Cookie", "token=" + token())
                    .body("{\"firstname\":\"Changed\"}")
                    .patch("/booking/" + id)
                    .then().statusCode(200)
                    .body("firstname", equalTo("Changed"))
                    .body("lastname", equalTo("Tester"));
        } finally {
            deleteBooking(id);
        }
    }

    @Test
    public void deletedBookingIsGone() {
        int id = createBooking();
        given().header("Cookie", "token=" + token()).delete("/booking/" + id)
                .then().statusCode(201);
        given().get("/booking/" + id).then().statusCode(404);
    }
}

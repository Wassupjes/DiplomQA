package data;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class APIHelper {
    public APIHelper() {
    }

    private static final RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri(System.getProperty("sut.url"))
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    public static String requestToCard(CardData card, String endpoint) {
        return given()
                .filter(new AllureRestAssured())
                .spec(requestSpec)
                .body(card)
                .when()
                .post(endpoint)
                .then()
                .log().body()
                .statusCode(200)
                .extract().jsonPath().getString("status");
    }
}
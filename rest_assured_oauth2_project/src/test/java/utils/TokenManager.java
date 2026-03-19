package utils;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import pojo.LoginRequest;

import static io.restassured.RestAssured.given;

public class TokenManager {

    public static String getToken() {
        LoginRequest loginPayload = new LoginRequest(
                ConfigLoader.get("email"),
                ConfigLoader.get("password")
        );

        Response res = given()
                .contentType(ContentType.JSON)
                .body(loginPayload)
                .when()
                .post(ConfigLoader.get("baseURI") + "/api/login")
                .then()
                .statusCode(200)
                .extract().response();

        return res.jsonPath().getString("token");
    }
}

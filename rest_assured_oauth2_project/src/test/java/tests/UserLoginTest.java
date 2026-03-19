package tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ConfigLoader;
import utils.LogRestAssuredFilter;

import static io.restassured.RestAssured.given;

public class UserLoginTest {

    private static final Logger logger = LoggerFactory.getLogger(UserLoginTest.class);

    @BeforeClass
    public void setup() {
        logger.info("Setting up REST Assured base URI and logging filters for UserLoginTest");
        RestAssured.baseURI = ConfigLoader.get("baseURI");
        RestAssured.filters(new LogRestAssuredFilter());
    }

    @Test
    public void testUserLoginStandalone() {
        logger.info("Starting testUserLoginStandalone flow");
        
        given()
            .accept(ContentType.JSON)
            .queryParam("usrname", "test")
            .queryParam("password", "uuuu")
        .when()
            .get("/user/login")
        .then()
            // The swagger API states 'username' is required. 
            // By passing 'usrname', we expect a 400 Bad Request.
            // Petstore actually ignores it and returns 200, so this will fail!
            .statusCode(400)
            .body("message", org.hamcrest.Matchers.containsString("logged in user session:"));
            
        logger.info("testUserLoginStandalone completed successfully");
    }
}

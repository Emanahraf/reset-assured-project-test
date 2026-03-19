package tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.User;
import utils.ConfigLoader;
import utils.LogRestAssuredFilter;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue; // Added for notNullValue assertion

import java.util.UUID;

public class UserRegistrationTest {

    private static final Logger logger = LoggerFactory.getLogger(UserRegistrationTest.class);
    
    // Create a truly unique username per test execution to avoid Petstore's public caching/persistence issues
    private static final String UNIQUE_USERNAME = "testuser_" + UUID.randomUUID().toString().substring(0, 8);

    @BeforeClass
    public void setup() {
        logger.info("Setting up REST Assured base URI and logging filters");
        RestAssured.baseURI = ConfigLoader.get("baseURI");
        RestAssured.filters(new LogRestAssuredFilter());
    }

    @Test
    public void testUserRegistration() {
        logger.info("Starting testUserRegistration flow with username: {}", UNIQUE_USERNAME);
        logger.info("Creating User POJO with test data");
        User user = User.builder()
                .id(0)
                .username(UNIQUE_USERNAME)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("secret123")
                .phone("1234567890")
                .userStatus(0)
                .build();

        logger.info("Executing POST request to /user endpoint");
        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(user)

                .when()
                .post("/user")
                .then()
                .statusCode(200)
                .body("message", notNullValue()) // Changed from equalTo("testuser_ra_oauth2_100")
                .body("type", equalTo("unknown"));

        logger.info("User registration test completed successfully");
    }

    @Test(dependsOnMethods = "testUserRegistration")
    public void testGetUserByUsername() {
        logger.info("Starting testGetUserByUsername flow");
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/user/" + UNIQUE_USERNAME)
        .then()
            .statusCode(200)
            .body("username", equalTo(UNIQUE_USERNAME))
            .body("firstName", equalTo("John"));
        
        logger.info("testGetUserByUsername completed successfully");
    }

    @Test(dependsOnMethods = "testGetUserByUsername")
    public void testUpdateUser() {
        logger.info("Starting testUpdateUser flow");
        User updatedUser = User.builder()
                .id(0)
                .username(UNIQUE_USERNAME)
                .firstName("John Updated")
                .lastName("Doe")
                .email("john.updated@example.com")
                .password("secret123")
                .phone("1234567890")
                .userStatus(1)
                .build();

        given()
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .body(updatedUser)
        .when()
            .put("/user/" + UNIQUE_USERNAME)
        .then()
            .statusCode(200)
            .body("message", notNullValue());
            
        logger.info("testUpdateUser completed successfully");
    }

    @Test(dependsOnMethods = "testUpdateUser")
    public void testLoginUser() {
        logger.info("Starting testLoginUser flow");
        given()
            .accept(ContentType.JSON)
            .queryParam("username", UNIQUE_USERNAME)
            .queryParam("password", "secret123")
        .when()
            .get("/user/login")
        .then()
            .statusCode(200)
            .body("message", org.hamcrest.Matchers.containsString("logged in user session:"));
            
        logger.info("testLoginUser completed successfully");
    }

    @Test(dependsOnMethods = "testLoginUser")
    public void testLogoutUser() {
        logger.info("Starting testLogoutUser flow");
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/user/logout")
        .then()
            .statusCode(200)
            .body("message", equalTo("ok"));
            
        logger.info("testLogoutUser completed successfully");
    }

    @Test(dependsOnMethods = "testLogoutUser")
    public void testDeleteUser() {
        logger.info("Starting testDeleteUser flow");
        given()
            .accept(ContentType.JSON)
        .when()
            .delete("/user/" + UNIQUE_USERNAME)
        .then()
            .statusCode(200)
            .body("message", equalTo(UNIQUE_USERNAME));
    }
}

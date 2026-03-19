package utils;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogRestAssuredFilter implements Filter {

    private static final Logger log = LogManager.getLogger(LogRestAssuredFilter.class);

    @Override
    public Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext ctx) {
        log.info("Request URI: {}", requestSpec.getURI());
        log.info("Request Method: {}", requestSpec.getMethod());
        log.info("Request Headers: {}", requestSpec.getHeaders());
        
        if (requestSpec.getBody() != null) {
            log.info("Request Body: {}", requestSpec.getBody().toString());
        }

        Response response = ctx.next(requestSpec, responseSpec);

        log.info("Response Status Code: {}", response.statusCode());
        log.info("Response Headers: {}", response.headers());
        log.info("Response Body: {}", response.getBody().asPrettyString());

        return response;
    }
}

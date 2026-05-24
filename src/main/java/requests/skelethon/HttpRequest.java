package requests.skelethon;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.RestAssured.given;

public class HttpRequest {

    private final RequestSpecification requestSpecification;
    private final ResponseSpecification responseSpecification;

    public HttpRequest(RequestSpecification requestSpecification,
                       ResponseSpecification responseSpecification) {
        this.requestSpecification = requestSpecification;
        this.responseSpecification = responseSpecification;
    }

    public ValidatableResponse get(Endpoint endpoint) {
        return given()
                .spec(requestSpecification)
                .get(endpoint.getUrl())
                .then()
                .spec(responseSpecification);
    }

    public ValidatableResponse post(Endpoint endpoint) {
        return given()
                .spec(requestSpecification)
                .post(endpoint.getUrl())
                .then()
                .spec(responseSpecification);
    }

    public ValidatableResponse post(Endpoint endpoint, Object body) {
        return given()
                .spec(requestSpecification)
                .body(body)
                .post(endpoint.getUrl())
                .then()
                .spec(responseSpecification);
    }

    public ValidatableResponse put(Endpoint endpoint, Object body) {
        return given()
                .spec(requestSpecification)
                .body(body)
                .put(endpoint.getUrl())
                .then()
                .spec(responseSpecification);
    }

    public ValidatableResponse delete(Endpoint endpoint) {
        return given()
                .spec(requestSpecification)
                .delete(endpoint.getUrl())
                .then()
                .spec(responseSpecification);
    }
}
package requests.skelethon;

import io.restassured.response.ValidatableResponse;

public class CrudRequester {

    private final HttpRequest httpRequest;

    public CrudRequester(HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    public ValidatableResponse createWithoutBody(Endpoint endpoint) {
        return httpRequest.post(endpoint);
    }

    public ValidatableResponse get(Endpoint endpoint) {
        return httpRequest.get(endpoint);
    }

    public ValidatableResponse delete(Endpoint endpoint) {
        return httpRequest.delete(endpoint);
    }
}
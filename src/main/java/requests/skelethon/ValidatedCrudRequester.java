package requests.skelethon;

import io.restassured.response.ValidatableResponse;
import models.BaseModel;

public class ValidatedCrudRequester<T extends BaseModel> {

    private final HttpRequest httpRequest;

    public ValidatedCrudRequester(HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    public ValidatableResponse create(Endpoint endpoint, T model) {
        return httpRequest.post(endpoint, model);
    }

    public ValidatableResponse update(Endpoint endpoint, T model) {
        return httpRequest.put(endpoint, model);
    }
}
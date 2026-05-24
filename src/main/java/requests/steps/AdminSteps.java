package requests.steps;

import generators.RandomModelGenerator;
import models.CreateUserRequest;
import models.CreateUserResponse;
import models.LoginUserRequest;
import requests.skelethon.Endpoint;
import requests.skelethon.HttpRequest;
import requests.skelethon.ValidatedCrudRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

public class AdminSteps {

    public CreateUserResponse createUser(CreateUserRequest createUserRequest) {
        return new ValidatedCrudRequester<CreateUserRequest>(
                new HttpRequest(
                        RequestSpecs.adminSpec(),
                        ResponseSpecs.entityWasCreated()
                )
        )
                .create(Endpoint.CREATE_USER, createUserRequest)
                .extract()
                .as(CreateUserResponse.class);
    }

    public CreateUserRequest createUser() {
        CreateUserRequest createUserRequest = RandomModelGenerator.getUserCreateRequest();
        createUser(createUserRequest);
        return createUserRequest;
    }

    public String loginUser(LoginUserRequest loginUserRequest) {
        return new ValidatedCrudRequester<LoginUserRequest>(
                new HttpRequest(
                        RequestSpecs.unauthSpec(),
                        ResponseSpecs.requestReturnsOK()
                )
        )
                .create(Endpoint.LOGIN, loginUserRequest)
                .extract()
                .header("Authorization");
    }

    public String createUserAndGetToken() {
        CreateUserRequest createUserRequest = createUser();

        LoginUserRequest loginUserRequest = LoginUserRequest.builder()
                .username(createUserRequest.getUsername())
                .password(createUserRequest.getPassword())
                .build();

        return loginUser(loginUserRequest);
    }
}
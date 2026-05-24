package iteration1.api;

import models.CreateUserRequest;
import models.LoginUserRequest;
import org.junit.jupiter.api.Test;
import requests.skelethon.Endpoint;
import requests.skelethon.HttpRequest;
import requests.skelethon.ValidatedCrudRequester;
import requests.steps.AdminSteps;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginUserTest extends BaseTest {

    private final AdminSteps adminSteps = new AdminSteps();

    @Test
    public void adminCanGenerateAuthTokenTest() {
        LoginUserRequest loginUserRequest = LoginUserRequest.builder()
                .username("admin")
                .password("admin")
                .build();

        String authHeader = new ValidatedCrudRequester<LoginUserRequest>(
                new HttpRequest(
                        RequestSpecs.unauthSpec(),
                        ResponseSpecs.requestReturnsOK()
                )
        )
                .create(Endpoint.LOGIN, loginUserRequest)
                .extract()
                .header("Authorization");

        assertThat(authHeader).isNotNull();
    }

    @Test
    public void userCanGenerateAuthTokenTest() {
        CreateUserRequest userRequest = adminSteps.createUser();

        LoginUserRequest loginUserRequest = LoginUserRequest.builder()
                .username(userRequest.getUsername())
                .password(userRequest.getPassword())
                .build();

        String authHeader = new ValidatedCrudRequester<LoginUserRequest>(
                new HttpRequest(
                        RequestSpecs.unauthSpec(),
                        ResponseSpecs.requestReturnsOK()
                )
        )
                .create(Endpoint.LOGIN, loginUserRequest)
                .extract()
                .header("Authorization");

        assertThat(authHeader).isNotNull();
    }
}
package iteration1.api;

import models.AccountResponse;
import models.CreateUserRequest;
import org.junit.jupiter.api.Test;
import requests.skelethon.Endpoint;
import requests.skelethon.HttpRequest;
import requests.skelethon.CrudRequester;
import requests.steps.AdminSteps;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateAccountTest extends BaseTest {

    private final AdminSteps adminSteps = new AdminSteps();

    @Test
    public void userCanCreateAccountTest() {
        CreateUserRequest userRequest = adminSteps.createUser();

        AccountResponse createdAccount = new CrudRequester(
                new HttpRequest(
                        RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                        ResponseSpecs.entityWasCreated()
                )
        )
                .createWithoutBody(Endpoint.CREATE_ACCOUNT)
                .extract()
                .as(AccountResponse.class);

        assertThat(createdAccount).isNotNull();
        assertThat(createdAccount.getId()).isPositive();
        assertThat(createdAccount.getAccountNumber()).isNotBlank();
        assertThat(createdAccount.getBalance()).isZero();
    }
}
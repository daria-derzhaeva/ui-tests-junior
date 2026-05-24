package iteration1.api;

import generators.RandomModelGenerator;
import models.CreateUserRequest;
import models.CreateUserResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import requests.skelethon.Endpoint;
import requests.skelethon.HttpRequest;
import requests.skelethon.ValidatedCrudRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateUserTest extends BaseTest {

    @Test
    public void adminCanCreateUserWithCorrectData() {
        CreateUserRequest createUserRequest = RandomModelGenerator.getUserCreateRequest();

        CreateUserResponse createUserResponse = new ValidatedCrudRequester<CreateUserRequest>(
                new HttpRequest(
                        RequestSpecs.adminSpec(),
                        ResponseSpecs.entityWasCreated()
                )
        )
                .create(Endpoint.CREATE_USER, createUserRequest)
                .extract()
                .as(CreateUserResponse.class);

        assertThat(createUserResponse.getUsername())
                .isEqualTo(createUserRequest.getUsername());

        assertThat(createUserResponse.getRole())
                .isEqualTo(createUserRequest.getRole());

        assertThat(createUserResponse.getId())
                .isPositive();

        assertThat(createUserResponse.getPassword())
                .isNotBlank();
    }

    public static Stream<Arguments> userInvalidData() {
        return Stream.of(
                Arguments.of("   ", "Password33$", "USER", "Username cannot be blank"),
                Arguments.of("ab", "Password33$", "USER", "Username must be between 3 and 15 characters"),
                Arguments.of("abc$", "Password33$", "USER", "Username must contain only letters, digits, dashes, underscores, and dots"),
                Arguments.of("abc%", "Password33$", "USER", "Username must contain only letters, digits, dashes, underscores, and dots")
        );
    }

    @MethodSource("userInvalidData")
    @ParameterizedTest
    public void adminCanNotCreateUserWithInvalidData(
            String username,
            String password,
            String role,
            String expectedErrorMessage
    ) {
        CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .username(username)
                .password(password)
                .role(role)
                .build();

        String actualErrorMessage = new ValidatedCrudRequester<CreateUserRequest>(
                new HttpRequest(
                        RequestSpecs.adminSpec(),
                        ResponseSpecs.requestReturnsBadRequest()
                )
        )
                .create(Endpoint.CREATE_USER, createUserRequest)
                .extract()
                .asString();

        assertThat(actualErrorMessage)
                .contains(expectedErrorMessage);
    }
}
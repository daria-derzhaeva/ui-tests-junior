package iteration2;

import generators.RandomData;
import iteration1.api.BaseTest;
import models.UpdateProfileNameResponse;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import requests.steps.AdminSteps;
import requests.steps.UserSteps;
import specs.ResponseSpecs;

public class ChangeProfileNameTest extends BaseTest {

    private final AdminSteps adminSteps = new AdminSteps();

    @Test
    public void userCanChangeProfileNameToValidName() {
        String userToken = adminSteps.createUserAndGetToken();
        UserSteps userSteps = new UserSteps(userToken);

        String expectedName = RandomData.getValidName();

        UpdateProfileNameResponse response = userSteps.updateProfileName(expectedName);

        String actualName = userSteps.getProfileName();

        softly.assertThat(response.getMessage())
                .isEqualTo(ResponseSpecs.PROFILE_UPDATED_SUCCESSFULLY);

        softly.assertThat(response.getCustomer().getName())
                .isEqualTo(expectedName);

        softly.assertThat(actualName)
                .isEqualTo(expectedName);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "Darya",
            "Darya Darya Darya",
            "Darya D1",
            "Darya $arya",
            "DaryaDarya"
    })
    public void userCannotChangeProfileNameToInvalidName(String invalidName) {
        String userToken = adminSteps.createUserAndGetToken();
        UserSteps userSteps = new UserSteps(userToken);

        String nameBeforeUpdate = userSteps.getProfileName();

        userSteps.updateProfileNameWithBadRequest(invalidName)
                .body(Matchers.equalTo(ResponseSpecs.INVALID_PROFILE_NAME_MESSAGE));

        String nameAfterUpdate = userSteps.getProfileName();

        softly.assertThat(nameAfterUpdate)
                .isEqualTo(nameBeforeUpdate);
    }
}
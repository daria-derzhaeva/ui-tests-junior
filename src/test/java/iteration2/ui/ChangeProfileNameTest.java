package iteration2.ui;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import generators.RandomData;
import models.CreateUserRequest;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import requests.steps.AdminSteps;
import specs.RequestSpecs;

import java.util.Map;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.switchTo;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class ChangeProfileNameTest {

    private final AdminSteps adminSteps = new AdminSteps();

    @BeforeAll
    public static void setupSelenoid() {
        Configuration.remote = "http://localhost:4444/wd/hub";
        Configuration.baseUrl = "http://192.168.100.170:3000";
        Configuration.browser = "chrome";
        Configuration.browserSize = "1920x1080";
        Configuration.timeout = 10000;

        Configuration.browserCapabilities.setCapability("selenoid:options",
                Map.of("enableVNC", true, "enableLog", true)
        );
    }

    @Test
    public void userCanChangeProfileNameToValidNameTest() {
        CreateUserRequest user = adminSteps.createUser();

        String expectedName = RandomData.getValidName();

        login(user.getUsername(), user.getPassword());

        openProfile();

        $("input[placeholder='Enter new name']")
                .shouldBe(Condition.visible)
                .setValue(expectedName);

        $(Selectors.withText("Save Changes"))
                .shouldBe(Condition.visible)
                .click();

        Alert alert = switchTo().alert();

        assertThat(alert.getText())
                .contains("Name updated successfully!");

        alert.accept();

        String profileName = getProfileName(user);

        assertThat(profileName)
                .isEqualTo(expectedName);
    }

    @Test
    public void userCanNotChangeProfileNameWithInvalidFormatTest() {
        CreateUserRequest user = adminSteps.createUser();

        String nameBeforeUpdate = getProfileName(user);

        login(user.getUsername(), user.getPassword());

        openProfile();

        $("input[placeholder='Enter new name']")
                .shouldBe(Condition.visible)
                .setValue("John Smith1");

        $(Selectors.withText("Save Changes"))
                .shouldBe(Condition.visible)
                .click();

        Alert alert = switchTo().alert();

        assertThat(alert.getText())
                .contains("Name must contain two words with letters only");

        alert.accept();

        String nameAfterUpdate = getProfileName(user);

        assertThat(nameAfterUpdate)
                .isEqualTo(nameBeforeUpdate);
    }

    private void login(String username, String password) {
        Selenide.open("/login");

        $(Selectors.byAttribute("placeholder", "Username"))
                .shouldBe(Condition.visible)
                .setValue(username);

        $(Selectors.byAttribute("placeholder", "Password"))
                .shouldBe(Condition.visible)
                .setValue(password);

        $("button")
                .shouldBe(Condition.visible)
                .click();

        $(Selectors.byText("User Dashboard"))
                .shouldBe(Condition.visible);
    }

    private void openProfile() {
        Selenide.open("/edit-profile");

        $(Selectors.withText("Edit Profile"))
                .shouldBe(Condition.visible);

        $("input[placeholder='Enter new name']")
                .shouldBe(Condition.visible);
    }

    private String getProfileName(CreateUserRequest user) {
        return given()
                .spec(RequestSpecs.authAsUser(user.getUsername(), user.getPassword()))
                .get("http://localhost:4111/api/v1/customer/profile")
                .then().assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .path("name");
    }
}
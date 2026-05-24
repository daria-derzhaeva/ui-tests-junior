package iteration1.ui;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import models.CreateUserRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import requests.steps.AdminSteps;

import java.util.Map;

import static com.codeborne.selenide.Selenide.$;

public class LoginUserTest {

    private final AdminSteps adminSteps = new AdminSteps();

    @BeforeAll
    public static void setupSelenoid() {
        Configuration.remote = "http://localhost:4444/wd/hub";
        Configuration.baseUrl = "http://host.docker.internal:3000";
        Configuration.browser = "chrome";
        Configuration.browserSize = "1920x1080";
        Configuration.timeout = 10000;

        Configuration.browserCapabilities.setCapability("selenoid:options",
                Map.of("enableVNC", true, "enableLog", true)
        );
    }

    @Test
    public void adminCanLoginWithCorrectDataTest() {
        CreateUserRequest admin = CreateUserRequest.builder()
                .username("admin")
                .password("admin")
                .build();

        login(admin.getUsername(), admin.getPassword());

        $(Selectors.byText("Admin Panel"))
                .shouldBe(Condition.visible);
    }

    @Test
    public void userCanLoginWithCorrectDataTest() {
        CreateUserRequest user = adminSteps.createUser();

        login(user.getUsername(), user.getPassword());

        $(Selectors.byClassName("welcome-text"))
                .shouldBe(Condition.visible)
                .shouldHave(Condition.text("Welcome, noname!"));
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
    }
}
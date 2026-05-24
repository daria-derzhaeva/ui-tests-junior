package iteration2.ui;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import models.CreateAccountResponse;
import models.CreateUserRequest;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import requests.steps.AdminSteps;
import specs.RequestSpecs;

import java.util.Map;

import static com.codeborne.selenide.Selenide.*;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class DepositMoneyTest {

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
    public void userCanDepositMoneyToOwnAccountTest() {
        CreateUserRequest user = adminSteps.createUser();

        createAccount(user);

        CreateAccountResponse[] accountsBeforeDeposit = getUserAccounts(user);
        assertThat(accountsBeforeDeposit).hasSize(1);

        double balanceBeforeDeposit = accountsBeforeDeposit[0].getBalance();

        login(user.getUsername(), user.getPassword());

        openDepositForm();

        $("select")
                .shouldBe(Condition.visible)
                .selectOption(1);

        $("input[placeholder='Enter amount']")
                .shouldBe(Condition.visible)
                .setValue("200.00");

        clickDepositSubmitButton();

        Alert alert = switchTo().alert();

        assertThat(alert.getText())
                .contains("Successfully deposited");

        alert.accept();

        CreateAccountResponse[] accountsAfterDeposit = getUserAccounts(user);

        assertThat(accountsAfterDeposit[0].getBalance())
                .isEqualTo(balanceBeforeDeposit + 200.00);

        assertThat(accountsAfterDeposit[0].getTransactions())
                .isNotEmpty();

        assertThat(accountsAfterDeposit[0].getTransactions().toString())
                .contains("DEPOSIT");
    }

    @Test
    public void userCanDepositMaximumAllowedAmountTest() {
        CreateUserRequest user = adminSteps.createUser();

        createAccount(user);

        CreateAccountResponse[] accountsBeforeDeposit = getUserAccounts(user);
        assertThat(accountsBeforeDeposit).hasSize(1);

        double balanceBeforeDeposit = accountsBeforeDeposit[0].getBalance();

        login(user.getUsername(), user.getPassword());

        openDepositForm();

        $("select")
                .shouldBe(Condition.visible)
                .selectOption(1);

        $("input[placeholder='Enter amount']")
                .shouldBe(Condition.visible)
                .setValue("5000.00");

        clickDepositSubmitButton();

        Alert alert = switchTo().alert();

        assertThat(alert.getText())
                .contains("Successfully deposited");

        alert.accept();

        CreateAccountResponse[] accountsAfterDeposit = getUserAccounts(user);

        assertThat(accountsAfterDeposit[0].getBalance())
                .isEqualTo(balanceBeforeDeposit + 5000.00);

        assertThat(accountsAfterDeposit[0].getTransactions())
                .isNotEmpty();

        assertThat(accountsAfterDeposit[0].getTransactions().toString())
                .contains("DEPOSIT");
    }

    @Test
    public void userCanNotDepositAmountGreaterThanMaximumAllowedAmountTest() {
        CreateUserRequest user = adminSteps.createUser();

        createAccount(user);

        CreateAccountResponse[] accountsBeforeDeposit = getUserAccounts(user);
        assertThat(accountsBeforeDeposit).hasSize(1);

        double balanceBeforeDeposit = accountsBeforeDeposit[0].getBalance();

        login(user.getUsername(), user.getPassword());

        openDepositForm();

        $("select")
                .shouldBe(Condition.visible)
                .selectOption(1);

        $("input[placeholder='Enter amount']")
                .shouldBe(Condition.visible)
                .setValue("5000.01");

        clickDepositSubmitButton();

        Alert alert = switchTo().alert();

        assertThat(alert.getText())
                .contains("❌ Please deposit less or equal to 5000");

        alert.accept();

        CreateAccountResponse[] accountsAfterDeposit = getUserAccounts(user);

        assertThat(accountsAfterDeposit[0].getBalance())
                .isEqualTo(balanceBeforeDeposit);
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

    private void openDepositForm() {
        $(Selectors.withText("Deposit Money"))
                .shouldBe(Condition.visible)
                .click();

        $("input[placeholder='Enter amount']")
                .shouldBe(Condition.visible);
    }

    private void clickDepositSubmitButton() {
        $$("button")
                .filterBy(Condition.text("Deposit"))
                .last()
                .shouldBe(Condition.visible)
                .click();
    }

    private void createAccount(CreateUserRequest user) {
        given()
                .spec(RequestSpecs.authAsUser(user.getUsername(), user.getPassword()))
                .post("http://localhost:4111/api/v1/accounts")
                .then().assertThat()
                .statusCode(HttpStatus.SC_CREATED);
    }

    private CreateAccountResponse[] getUserAccounts(CreateUserRequest user) {
        return given()
                .spec(RequestSpecs.authAsUser(user.getUsername(), user.getPassword()))
                .get("http://localhost:4111/api/v1/customer/accounts")
                .then().assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .as(CreateAccountResponse[].class);
    }

    @Test
    public void userCanNotSubmitDepositWithoutSelectedAccountTest() {
        CreateUserRequest user = adminSteps.createUser();

        createAccount(user);

        CreateAccountResponse[] accountsBeforeDeposit = getUserAccounts(user);
        double balanceBeforeDeposit = accountsBeforeDeposit[0].getBalance();

        login(user.getUsername(), user.getPassword());

        openDepositForm();

        $("input[placeholder='Enter amount']")
                .shouldBe(Condition.visible)
                .setValue("200.00");

        clickDepositSubmitButton();

        Alert alert = switchTo().alert();

        assertThat(alert.getText())
                .contains("Please select an account");

        alert.accept();

        CreateAccountResponse[] accountsAfterDeposit = getUserAccounts(user);

        assertThat(accountsAfterDeposit[0].getBalance())
                .isEqualTo(balanceBeforeDeposit);
    }

    @Test
    public void userCanNotSubmitDepositWithBlankAmountTest() {
        CreateUserRequest user = adminSteps.createUser();

        createAccount(user);

        CreateAccountResponse[] accountsBeforeDeposit = getUserAccounts(user);
        double balanceBeforeDeposit = accountsBeforeDeposit[0].getBalance();

        login(user.getUsername(), user.getPassword());

        openDepositForm();

        $("select")
                .shouldBe(Condition.visible)
                .selectOption(1);

        clickDepositSubmitButton();

        Alert alert = switchTo().alert();

        assertThat(alert.getText())
                .contains("Please enter a valid amount");

        alert.accept();

        CreateAccountResponse[] accountsAfterDeposit = getUserAccounts(user);

        assertThat(accountsAfterDeposit[0].getBalance())
                .isEqualTo(balanceBeforeDeposit);
    }
}
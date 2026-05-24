package iteration2.ui;

import com.codeborne.selenide.*;
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

public class TransferMoneyTest {

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
    public void userCanTransferMoneyWithValidAmountTest() {
        CreateUserRequest sender = adminSteps.createUser();
        CreateUserRequest receiver = adminSteps.createUser();

        createAccount(sender);
        createAccount(receiver);

        CreateAccountResponse senderAccountBeforeTransfer = getUserAccounts(sender)[0];
        CreateAccountResponse receiverAccountBeforeTransfer = getUserAccounts(receiver)[0];

        deposit(sender, senderAccountBeforeTransfer.getId(), 1000.00);

        senderAccountBeforeTransfer = getUserAccounts(sender)[0];
        receiverAccountBeforeTransfer = getUserAccounts(receiver)[0];

        double senderBalanceBeforeTransfer = senderAccountBeforeTransfer.getBalance();
        double receiverBalanceBeforeTransfer = receiverAccountBeforeTransfer.getBalance();

        login(sender.getUsername(), sender.getPassword());

        $(Selectors.withText("Make a Transfer"))
                .shouldBe(Condition.visible)
                .click();

        $("input[placeholder='Enter recipient name']")
                .shouldBe(Condition.visible);

        $("select")
                .shouldBe(Condition.visible)
                .selectOption(1);

        $("input[placeholder='Enter recipient name']")
                .shouldBe(Condition.visible)
                .setValue(receiver.getUsername());

        $("input[placeholder='Enter recipient account number']")
                .shouldBe(Condition.visible)
                .setValue(receiverAccountBeforeTransfer.getAccountNumber());

        $("input[placeholder='Enter amount']")
                .shouldBe(Condition.visible)
                .setValue("100.00");

        $("input[type='checkbox']")
                .shouldBe(Condition.visible)
                .click();

        $(Selectors.withText("Send Transfer"))
                .shouldBe(Condition.visible)
                .click();

        Alert alert = switchTo().alert();

        assertThat(alert.getText()).contains("Successfully transferred");

        alert.accept();

        CreateAccountResponse senderAccountAfterTransfer = getUserAccounts(sender)[0];
        CreateAccountResponse receiverAccountAfterTransfer = getUserAccounts(receiver)[0];

        assertThat(senderAccountAfterTransfer.getBalance())
                .isEqualTo(senderBalanceBeforeTransfer - 100.00);

        assertThat(receiverAccountAfterTransfer.getBalance())
                .isEqualTo(receiverBalanceBeforeTransfer + 100.00);
    }

    @Test
    public void userCanTransferMaximumAllowedAmountTest() {
        CreateUserRequest sender = adminSteps.createUser();
        CreateUserRequest receiver = adminSteps.createUser();

        createAccount(sender);
        createAccount(receiver);

        CreateAccountResponse senderAccountBeforeDeposit = getUserAccounts(sender)[0];
        CreateAccountResponse receiverAccountBeforeTransfer = getUserAccounts(receiver)[0];

        deposit(sender, senderAccountBeforeDeposit.getId(), 10000.00);

        CreateAccountResponse senderAccountBeforeTransfer = getUserAccounts(sender)[0];

        double senderBalanceBeforeTransfer = senderAccountBeforeTransfer.getBalance();
        double receiverBalanceBeforeTransfer = receiverAccountBeforeTransfer.getBalance();

        login(sender.getUsername(), sender.getPassword());

        $(Selectors.withText("Make a Transfer"))
                .shouldBe(Condition.visible)
                .click();

        $("select")
                .shouldBe(Condition.visible)
                .selectOption(1);

        $("input[placeholder='Enter recipient name']")
                .shouldBe(Condition.visible)
                .setValue(receiver.getUsername());

        $("input[placeholder='Enter recipient account number']")
                .shouldBe(Condition.visible)
                .setValue(receiverAccountBeforeTransfer.getAccountNumber());

        $("input[placeholder='Enter amount']")
                .shouldBe(Condition.visible)
                .setValue("10000.00");

        $("input[type='checkbox']")
                .shouldBe(Condition.visible)
                .click();

        $(Selectors.withText("Send Transfer"))
                .shouldBe(Condition.visible)
                .click();

        Alert alert = switchTo().alert();

        assertThat(alert.getText()).contains("Successfully transferred");

        alert.accept();

        CreateAccountResponse senderAccountAfterTransfer = getUserAccounts(sender)[0];
        CreateAccountResponse receiverAccountAfterTransfer = getUserAccounts(receiver)[0];

        assertThat(senderAccountAfterTransfer.getBalance())
                .isEqualTo(senderBalanceBeforeTransfer - 10000.00);

        assertThat(receiverAccountAfterTransfer.getBalance())
                .isEqualTo(receiverBalanceBeforeTransfer + 10000.00);
    }

    @Test
    public void userCanNotTransferAmountGreaterThanMaximumAllowedAmountTest() {
        CreateUserRequest sender = adminSteps.createUser();
        CreateUserRequest receiver = adminSteps.createUser();

        createAccount(sender);
        createAccount(receiver);

        CreateAccountResponse senderAccountBeforeDeposit = getUserAccounts(sender)[0];
        CreateAccountResponse receiverAccountBeforeTransfer = getUserAccounts(receiver)[0];

        deposit(sender, senderAccountBeforeDeposit.getId(), 10000.00);

        CreateAccountResponse senderAccountBeforeTransfer = getUserAccounts(sender)[0];

        double senderBalanceBeforeTransfer = senderAccountBeforeTransfer.getBalance();
        double receiverBalanceBeforeTransfer = receiverAccountBeforeTransfer.getBalance();

        login(sender.getUsername(), sender.getPassword());

        $(Selectors.withText("Make a Transfer"))
                .shouldBe(Condition.visible)
                .click();

        $("select")
                .shouldBe(Condition.visible)
                .selectOption(1);

        $("input[placeholder='Enter recipient name']")
                .shouldBe(Condition.visible)
                .setValue(receiver.getUsername());

        $("input[placeholder='Enter recipient account number']")
                .shouldBe(Condition.visible)
                .setValue(receiverAccountBeforeTransfer.getAccountNumber());

        $("input[placeholder='Enter amount']")
                .shouldBe(Condition.visible)
                .setValue("10000.01");

        $("input[type='checkbox']")
                .shouldBe(Condition.visible)
                .click();

        $(Selectors.withText("Send Transfer"))
                .shouldBe(Condition.visible)
                .click();

        Alert alert = switchTo().alert();

        assertThat(alert.getText()).contains("Invalid transfer");

        alert.accept();

        CreateAccountResponse senderAccountAfterTransfer = getUserAccounts(sender)[0];
        CreateAccountResponse receiverAccountAfterTransfer = getUserAccounts(receiver)[0];

        assertThat(senderAccountAfterTransfer.getBalance())
                .isEqualTo(senderBalanceBeforeTransfer);

        assertThat(receiverAccountAfterTransfer.getBalance())
                .isEqualTo(receiverBalanceBeforeTransfer);
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

    private void createAccount(CreateUserRequest user) {
        given()
                .spec(RequestSpecs.authAsUser(user.getUsername(), user.getPassword()))
                .post("http://localhost:4111/api/v1/accounts")
                .then().assertThat()
                .statusCode(HttpStatus.SC_CREATED);
    }

    private void deposit(CreateUserRequest user, long accountId, double amount) {
        given()
                .spec(RequestSpecs.authAsUser(user.getUsername(), user.getPassword()))
                .contentType("application/json")
                .body(Map.of(
                        "id", accountId,
                        "balance", amount
                ))
                .post("http://localhost:4111/api/v1/accounts/deposit")
                .then().assertThat()
                .statusCode(HttpStatus.SC_OK);
    }

    private CreateAccountResponse[] getUserAccounts(CreateUserRequest user) {
        return given()
                .spec(RequestSpecs.authAsUser(user.getUsername(), user.getPassword()))
                .get("http://localhost:4111/api/v1/customer/accounts")
                .then().assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract().as(CreateAccountResponse[].class);
    }
}
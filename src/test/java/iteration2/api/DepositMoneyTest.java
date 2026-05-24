package iteration2;

import generators.RandomData;
import generators.TestConstants;
import models.AccountResponse;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import requests.steps.AdminSteps;
import requests.steps.UserSteps;

import java.util.stream.Stream;

public class DepositMoneyTest extends BaseTest {

    private final AdminSteps adminSteps = new AdminSteps();

    @Test
    public void userCanDepositMoneyToOwnExistingAccount() {
        String userToken = adminSteps.createUserAndGetToken();
        UserSteps userSteps = new UserSteps(userToken);

        AccountResponse account = userSteps.createAccount();

        double balanceBeforeDeposit = userSteps.getAccountBalance(account.getId());
        double depositAmount = RandomData.getDepositAmount();

        AccountResponse depositResponse = userSteps.depositMoney(account.getId(), depositAmount);

        double balanceAfterDeposit = userSteps.getAccountBalance(account.getId());

        softly.assertThat(depositResponse.getId())
                .isEqualTo(account.getId());

        softly.assertThat(depositResponse.getBalance())
                .isEqualTo(balanceBeforeDeposit + depositAmount);

        softly.assertThat(balanceAfterDeposit)
                .isEqualTo(balanceBeforeDeposit + depositAmount);
    }

    @Test
    public void userCanDepositMaximumAllowedAmount() {
        String userToken = adminSteps.createUserAndGetToken();
        UserSteps userSteps = new UserSteps(userToken);

        AccountResponse account = userSteps.createAccount();

        double balanceBeforeDeposit = userSteps.getAccountBalance(account.getId());

        AccountResponse depositResponse = userSteps.depositMoney(
                account.getId(),
                TestConstants.MAX_DEPOSIT_ALLOWED_AMOUNT
        );

        double balanceAfterDeposit = userSteps.getAccountBalance(account.getId());

        softly.assertThat(depositResponse.getId())
                .isEqualTo(account.getId());

        softly.assertThat(depositResponse.getBalance())
                .isEqualTo(balanceBeforeDeposit + TestConstants.MAX_DEPOSIT_ALLOWED_AMOUNT);

        softly.assertThat(balanceAfterDeposit)
                .isEqualTo(balanceBeforeDeposit + TestConstants.MAX_DEPOSIT_ALLOWED_AMOUNT);
    }

    @ParameterizedTest
    @MethodSource("invalidDepositAmounts")
    public void userCannotDepositInvalidAmount(double amount) {
        String userToken = adminSteps.createUserAndGetToken();
        UserSteps userSteps = new UserSteps(userToken);

        AccountResponse account = userSteps.createAccount();

        double balanceBeforeDeposit = userSteps.getAccountBalance(account.getId());

        userSteps.depositMoneyWithBadRequest(account.getId(), amount);

        double balanceAfterDeposit = userSteps.getAccountBalance(account.getId());

        softly.assertThat(balanceAfterDeposit)
                .isEqualTo(balanceBeforeDeposit);
    }

    @Test
    public void userCannotDepositMoneyToNonExistingAccount() {
        String userToken = adminSteps.createUserAndGetToken();
        UserSteps userSteps = new UserSteps(userToken);

        userSteps.depositMoneyWithCustomResponse(
                        TestConstants.NON_EXISTING_ACCOUNT_ID,
                        RandomData.getDepositAmount()
                )
                .statusCode(Matchers.anyOf(
                        Matchers.equalTo(HttpStatus.SC_BAD_REQUEST),
                        Matchers.equalTo(HttpStatus.SC_NOT_FOUND),
                        Matchers.equalTo(HttpStatus.SC_FORBIDDEN)
                ));
    }

    @Test
    public void userCannotDepositMoneyToAnotherUsersAccount() {
        String firstUserToken = adminSteps.createUserAndGetToken();
        String secondUserToken = adminSteps.createUserAndGetToken();

        UserSteps firstUserSteps = new UserSteps(firstUserToken);
        UserSteps secondUserSteps = new UserSteps(secondUserToken);

        AccountResponse secondUserAccount = secondUserSteps.createAccount();

        double balanceBeforeDeposit = secondUserSteps.getAccountBalance(secondUserAccount.getId());

        firstUserSteps.depositMoneyWithCustomResponse(
                        secondUserAccount.getId(),
                        RandomData.getDepositAmount()
                )
                .statusCode(Matchers.anyOf(
                        Matchers.equalTo(HttpStatus.SC_BAD_REQUEST),
                        Matchers.equalTo(HttpStatus.SC_FORBIDDEN)
                ));

        double balanceAfterDeposit = secondUserSteps.getAccountBalance(secondUserAccount.getId());

        softly.assertThat(balanceAfterDeposit)
                .isEqualTo(balanceBeforeDeposit);
    }

    private static Stream<Double> invalidDepositAmounts() {
        return Stream.of(
                TestConstants.NEGATIVE_DEPOSIT_AMOUNT,
                TestConstants.ZERO_AMOUNT
        );
    }
}
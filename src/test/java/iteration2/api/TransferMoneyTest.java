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

public class TransferMoneyTest extends BaseTest {

    private final AdminSteps adminSteps = new AdminSteps();

    @Test
    public void userCanTransferMoneyToAnotherAccount() {
        String senderToken = adminSteps.createUserAndGetToken();
        String receiverToken = adminSteps.createUserAndGetToken();

        UserSteps senderSteps = new UserSteps(senderToken);
        UserSteps receiverSteps = new UserSteps(receiverToken);

        AccountResponse senderAccount = senderSteps.createAccount();
        AccountResponse receiverAccount = receiverSteps.createAccount();

        double transferAmount = RandomData.getTransferAmount();
        double depositAmount = transferAmount + RandomData.getDepositAmount();

        senderSteps.depositMoney(senderAccount.getId(), depositAmount);

        double senderBalanceBeforeTransfer = senderSteps.getAccountBalance(senderAccount.getId());
        double receiverBalanceBeforeTransfer = receiverSteps.getAccountBalance(receiverAccount.getId());

        senderSteps.transferMoney(senderAccount.getId(), receiverAccount.getId(), transferAmount);

        double senderBalanceAfterTransfer = senderSteps.getAccountBalance(senderAccount.getId());
        double receiverBalanceAfterTransfer = receiverSteps.getAccountBalance(receiverAccount.getId());

        softly.assertThat(senderBalanceAfterTransfer)
                .isEqualTo(senderBalanceBeforeTransfer - transferAmount);

        softly.assertThat(receiverBalanceAfterTransfer)
                .isEqualTo(receiverBalanceBeforeTransfer + transferAmount);
    }

    @Test
    public void userCanTransferMaximumAllowedAmount() {
        String senderToken = adminSteps.createUserAndGetToken();
        String receiverToken = adminSteps.createUserAndGetToken();

        UserSteps senderSteps = new UserSteps(senderToken);
        UserSteps receiverSteps = new UserSteps(receiverToken);

        AccountResponse senderAccount = senderSteps.createAccount();
        AccountResponse receiverAccount = receiverSteps.createAccount();

        senderSteps.depositMoney(senderAccount.getId(), TestConstants.MAX_DEPOSIT_ALLOWED_AMOUNT);
        senderSteps.depositMoney(senderAccount.getId(), TestConstants.MAX_DEPOSIT_ALLOWED_AMOUNT);

        double senderBalanceBeforeTransfer = senderSteps.getAccountBalance(senderAccount.getId());
        double receiverBalanceBeforeTransfer = receiverSteps.getAccountBalance(receiverAccount.getId());

        senderSteps.transferMoney(
                senderAccount.getId(),
                receiverAccount.getId(),
                TestConstants.MAX_TRANSFER_ALLOWED_AMOUNT
        );

        double senderBalanceAfterTransfer = senderSteps.getAccountBalance(senderAccount.getId());
        double receiverBalanceAfterTransfer = receiverSteps.getAccountBalance(receiverAccount.getId());

        softly.assertThat(senderBalanceAfterTransfer)
                .isEqualTo(senderBalanceBeforeTransfer - TestConstants.MAX_TRANSFER_ALLOWED_AMOUNT);

        softly.assertThat(receiverBalanceAfterTransfer)
                .isEqualTo(receiverBalanceBeforeTransfer + TestConstants.MAX_TRANSFER_ALLOWED_AMOUNT);
    }

    @ParameterizedTest
    @MethodSource("invalidTransferAmounts")
    public void userCannotTransferInvalidAmount(double amount) {
        String senderToken = adminSteps.createUserAndGetToken();
        String receiverToken = adminSteps.createUserAndGetToken();

        UserSteps senderSteps = new UserSteps(senderToken);
        UserSteps receiverSteps = new UserSteps(receiverToken);

        AccountResponse senderAccount = senderSteps.createAccount();
        AccountResponse receiverAccount = receiverSteps.createAccount();

        senderSteps.depositMoney(senderAccount.getId(), TestConstants.MAX_DEPOSIT_ALLOWED_AMOUNT);

        double senderBalanceBeforeTransfer = senderSteps.getAccountBalance(senderAccount.getId());
        double receiverBalanceBeforeTransfer = receiverSteps.getAccountBalance(receiverAccount.getId());

        senderSteps.transferMoneyWithBadRequest(senderAccount.getId(), receiverAccount.getId(), amount);

        double senderBalanceAfterTransfer = senderSteps.getAccountBalance(senderAccount.getId());
        double receiverBalanceAfterTransfer = receiverSteps.getAccountBalance(receiverAccount.getId());

        softly.assertThat(senderBalanceAfterTransfer)
                .isEqualTo(senderBalanceBeforeTransfer);

        softly.assertThat(receiverBalanceAfterTransfer)
                .isEqualTo(receiverBalanceBeforeTransfer);
    }

    @Test
    public void userCannotTransferAmountGreaterThanSenderAccountBalance() {
        String senderToken = adminSteps.createUserAndGetToken();
        String receiverToken = adminSteps.createUserAndGetToken();

        UserSteps senderSteps = new UserSteps(senderToken);
        UserSteps receiverSteps = new UserSteps(receiverToken);

        AccountResponse senderAccount = senderSteps.createAccount();
        AccountResponse receiverAccount = receiverSteps.createAccount();

        senderSteps.depositMoney(senderAccount.getId(), TestConstants.SMALL_DEPOSIT_AMOUNT);

        double senderBalanceBeforeTransfer = senderSteps.getAccountBalance(senderAccount.getId());
        double receiverBalanceBeforeTransfer = receiverSteps.getAccountBalance(receiverAccount.getId());

        senderSteps.transferMoneyWithBadRequest(
                senderAccount.getId(),
                receiverAccount.getId(),
                TestConstants.MORE_THAN_BALANCE_TRANSFER_AMOUNT
        );

        double senderBalanceAfterTransfer = senderSteps.getAccountBalance(senderAccount.getId());
        double receiverBalanceAfterTransfer = receiverSteps.getAccountBalance(receiverAccount.getId());

        softly.assertThat(senderBalanceAfterTransfer)
                .isEqualTo(senderBalanceBeforeTransfer);

        softly.assertThat(receiverBalanceAfterTransfer)
                .isEqualTo(receiverBalanceBeforeTransfer);
    }

    @Test
    public void userCannotTransferFromNonExistingSenderAccount() {
        String userToken = adminSteps.createUserAndGetToken();
        UserSteps userSteps = new UserSteps(userToken);

        AccountResponse receiverAccount = userSteps.createAccount();

        double receiverBalanceBeforeTransfer = userSteps.getAccountBalance(receiverAccount.getId());

        userSteps.transferMoneyWithCustomResponse(
                        TestConstants.NON_EXISTING_ACCOUNT_ID,
                        receiverAccount.getId(),
                        RandomData.getTransferAmount()
                )
                .statusCode(Matchers.anyOf(
                        Matchers.equalTo(HttpStatus.SC_BAD_REQUEST),
                        Matchers.equalTo(HttpStatus.SC_NOT_FOUND),
                        Matchers.equalTo(HttpStatus.SC_FORBIDDEN)
                ));

        double receiverBalanceAfterTransfer = userSteps.getAccountBalance(receiverAccount.getId());

        softly.assertThat(receiverBalanceAfterTransfer)
                .isEqualTo(receiverBalanceBeforeTransfer);
    }

    @Test
    public void userCannotTransferToNonExistingReceiverAccount() {
        String userToken = adminSteps.createUserAndGetToken();
        UserSteps userSteps = new UserSteps(userToken);

        AccountResponse senderAccount = userSteps.createAccount();

        double transferAmount = RandomData.getTransferAmount();
        double depositAmount = transferAmount + RandomData.getDepositAmount();

        userSteps.depositMoney(senderAccount.getId(), depositAmount);

        double senderBalanceBeforeTransfer = userSteps.getAccountBalance(senderAccount.getId());

        userSteps.transferMoneyWithCustomResponse(
                        senderAccount.getId(),
                        TestConstants.NON_EXISTING_ACCOUNT_ID,
                        transferAmount
                )
                .statusCode(Matchers.anyOf(
                        Matchers.equalTo(HttpStatus.SC_BAD_REQUEST),
                        Matchers.equalTo(HttpStatus.SC_NOT_FOUND)
                ));

        double senderBalanceAfterTransfer = userSteps.getAccountBalance(senderAccount.getId());

        softly.assertThat(senderBalanceAfterTransfer)
                .isEqualTo(senderBalanceBeforeTransfer);
    }

    private static Stream<Double> invalidTransferAmounts() {
        return Stream.of(
                TestConstants.MORE_THAN_MAX_TRANSFER_AMOUNT,
                TestConstants.NEGATIVE_TRANSFER_AMOUNT,
                TestConstants.ZERO_AMOUNT
        );
    }
}
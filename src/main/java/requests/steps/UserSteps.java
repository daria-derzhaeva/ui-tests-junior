package requests.steps;

import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ValidatableResponse;
import models.AccountResponse;
import models.CustomerProfileResponse;
import models.DepositMoneyRequest;
import models.TransferMoneyRequest;
import models.UpdateProfileNameRequest;
import models.UpdateProfileNameResponse;
import requests.skelethon.CrudRequester;
import requests.skelethon.Endpoint;
import requests.skelethon.HttpRequest;
import requests.skelethon.ValidatedCrudRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.List;

public class UserSteps {

    private final String userAuthHeader;

    public UserSteps(String userAuthHeader) {
        this.userAuthHeader = userAuthHeader;
    }

    public AccountResponse createAccount() {
        return accountRequesterWithCreatedResponse()
                .createWithoutBody(Endpoint.CREATE_ACCOUNT)
                .extract()
                .as(AccountResponse.class);
    }

    public AccountResponse depositMoney(long accountId, double amount) {
        DepositMoneyRequest request = DepositMoneyRequest.builder()
                .id(accountId)
                .balance(amount)
                .build();

        return depositRequesterWithOkResponse()
                .create(Endpoint.DEPOSIT, request)
                .extract()
                .as(AccountResponse.class);
    }

    public ValidatableResponse depositMoneyWithCustomResponse(long accountId, double amount) {
        DepositMoneyRequest request = DepositMoneyRequest.builder()
                .id(accountId)
                .balance(amount)
                .build();

        return depositRequesterWithoutStatusCode()
                .create(Endpoint.DEPOSIT, request);
    }

    public ValidatableResponse depositMoneyWithBadRequest(long accountId, double amount) {
        DepositMoneyRequest request = DepositMoneyRequest.builder()
                .id(accountId)
                .balance(amount)
                .build();

        return depositRequesterWithBadRequestResponse()
                .create(Endpoint.DEPOSIT, request);
    }

    public ValidatableResponse transferMoney(long senderAccountId,
                                             long receiverAccountId,
                                             double amount) {
        TransferMoneyRequest request = TransferMoneyRequest.builder()
                .senderAccountId(senderAccountId)
                .receiverAccountId(receiverAccountId)
                .amount(amount)
                .build();

        return transferRequesterWithOkResponse()
                .create(Endpoint.TRANSFER, request);
    }

    public ValidatableResponse transferMoneyWithBadRequest(long senderAccountId,
                                                           long receiverAccountId,
                                                           double amount) {
        TransferMoneyRequest request = TransferMoneyRequest.builder()
                .senderAccountId(senderAccountId)
                .receiverAccountId(receiverAccountId)
                .amount(amount)
                .build();

        return transferRequesterWithBadRequestResponse()
                .create(Endpoint.TRANSFER, request);
    }

    public ValidatableResponse transferMoneyWithCustomResponse(long senderAccountId,
                                                               long receiverAccountId,
                                                               double amount) {
        TransferMoneyRequest request = TransferMoneyRequest.builder()
                .senderAccountId(senderAccountId)
                .receiverAccountId(receiverAccountId)
                .amount(amount)
                .build();

        return transferRequesterWithoutStatusCode()
                .create(Endpoint.TRANSFER, request);
    }

    public UpdateProfileNameResponse updateProfileName(String name) {
        UpdateProfileNameRequest request = UpdateProfileNameRequest.builder()
                .name(name)
                .build();

        return profileRequesterWithOkResponse()
                .update(Endpoint.CUSTOMER_PROFILE, request)
                .extract()
                .as(UpdateProfileNameResponse.class);
    }

    public ValidatableResponse updateProfileNameWithBadRequest(String name) {
        UpdateProfileNameRequest request = UpdateProfileNameRequest.builder()
                .name(name)
                .build();

        return profileRequesterWithBadRequestResponse()
                .update(Endpoint.CUSTOMER_PROFILE, request);
    }

    public CustomerProfileResponse getProfile() {
        return accountRequesterWithOkResponseForGet()
                .get(Endpoint.CUSTOMER_PROFILE)
                .extract()
                .as(CustomerProfileResponse.class);
    }

    public String getProfileName() {
        return getProfile().getName();
    }

    public List<AccountResponse> getAccounts() {
        return accountRequesterWithOkResponseForGet()
                .get(Endpoint.CUSTOMER_ACCOUNTS)
                .extract()
                .as(new TypeRef<List<AccountResponse>>() {});
    }

    public double getAccountBalance(long accountId) {
        return getAccounts()
                .stream()
                .filter(account -> account.getId() == accountId)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Account with id " + accountId + " was not found"))
                .getBalance();
    }

    private CrudRequester accountRequesterWithCreatedResponse() {
        return new CrudRequester(
                new HttpRequest(
                        RequestSpecs.authAsUserSpec(userAuthHeader),
                        ResponseSpecs.entityWasCreated()
                )
        );
    }

    private CrudRequester accountRequesterWithOkResponseForGet() {
        return new CrudRequester(
                new HttpRequest(
                        RequestSpecs.authAsUserSpec(userAuthHeader),
                        ResponseSpecs.requestReturnsOK()
                )
        );
    }

    private ValidatedCrudRequester<DepositMoneyRequest> depositRequesterWithOkResponse() {
        return new ValidatedCrudRequester<>(
                new HttpRequest(
                        RequestSpecs.authAsUserSpec(userAuthHeader),
                        ResponseSpecs.requestReturnsOK()
                )
        );
    }

    private ValidatedCrudRequester<DepositMoneyRequest> depositRequesterWithBadRequestResponse() {
        return new ValidatedCrudRequester<>(
                new HttpRequest(
                        RequestSpecs.authAsUserSpec(userAuthHeader),
                        ResponseSpecs.requestReturnsBadRequest()
                )
        );
    }

    private ValidatedCrudRequester<DepositMoneyRequest> depositRequesterWithoutStatusCode() {
        return new ValidatedCrudRequester<>(
                new HttpRequest(
                        RequestSpecs.authAsUserSpec(userAuthHeader),
                        ResponseSpecs.withoutStatusCode()
                )
        );
    }

    private ValidatedCrudRequester<TransferMoneyRequest> transferRequesterWithOkResponse() {
        return new ValidatedCrudRequester<>(
                new HttpRequest(
                        RequestSpecs.authAsUserSpec(userAuthHeader),
                        ResponseSpecs.requestReturnsOK()
                )
        );
    }

    private ValidatedCrudRequester<TransferMoneyRequest> transferRequesterWithBadRequestResponse() {
        return new ValidatedCrudRequester<>(
                new HttpRequest(
                        RequestSpecs.authAsUserSpec(userAuthHeader),
                        ResponseSpecs.requestReturnsBadRequest()
                )
        );
    }

    private ValidatedCrudRequester<TransferMoneyRequest> transferRequesterWithoutStatusCode() {
        return new ValidatedCrudRequester<>(
                new HttpRequest(
                        RequestSpecs.authAsUserSpec(userAuthHeader),
                        ResponseSpecs.withoutStatusCode()
                )
        );
    }

    private ValidatedCrudRequester<UpdateProfileNameRequest> profileRequesterWithOkResponse() {
        return new ValidatedCrudRequester<>(
                new HttpRequest(
                        RequestSpecs.authAsUserSpec(userAuthHeader),
                        ResponseSpecs.requestReturnsOK()
                )
        );
    }

    private ValidatedCrudRequester<UpdateProfileNameRequest> profileRequesterWithBadRequestResponse() {
        return new ValidatedCrudRequester<>(
                new HttpRequest(
                        RequestSpecs.authAsUserSpec(userAuthHeader),
                        ResponseSpecs.requestReturnsBadRequest()
                )
        );
    }
}
package requests.skelethon;

public enum Endpoint {

    CREATE_USER("/api/v1/admin/users"),
    LOGIN("/api/v1/auth/login"),
    CREATE_ACCOUNT("/api/v1/accounts"),
    CUSTOMER_ACCOUNTS("/api/v1/customer/accounts"),
    DEPOSIT("/api/v1/accounts/deposit"),
    TRANSFER("/api/v1/accounts/transfer"),
    CUSTOMER_PROFILE("/api/v1/customer/profile");

    private final String url;

    Endpoint(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
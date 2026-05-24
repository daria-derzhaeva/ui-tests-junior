package generators;

public class TestConstants {

    public static final int NON_EXISTING_ACCOUNT_ID = 999999;

    public static final double ZERO_AMOUNT = 0.0;
    public static final double NEGATIVE_DEPOSIT_AMOUNT = -200.0;
    public static final double NEGATIVE_TRANSFER_AMOUNT = -100.0;

    public static final double MAX_DEPOSIT_ALLOWED_AMOUNT = 5000.00;
    public static final double MAX_TRANSFER_ALLOWED_AMOUNT = 10000.00;
    public static final double MORE_THAN_MAX_TRANSFER_AMOUNT = 10000.01;

    public static final double SMALL_DEPOSIT_AMOUNT = 100.00;
    public static final double MORE_THAN_BALANCE_TRANSFER_AMOUNT = 1000.00;

    private TestConstants() {
    }
}
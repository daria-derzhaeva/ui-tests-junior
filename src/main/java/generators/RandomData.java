package generators;

import configs.Config;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class RandomData {

    private static final Random RANDOM = new Random();

    private static final List<String> FIRST_NAMES = List.of(
            "Darya", "Anna", "Maria", "Sofia", "Alina", "Elena"
    );

    private static final List<String> LAST_NAMES = List.of(
            "Der", "Ivanova", "Petrova", "Smirnova", "Sidorova", "Volkova"
    );

    private RandomData() {
    }

    public static String getUsername() {
        return "user_" + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, GeneratingRule.USERNAME_RANDOM_LENGTH);
    }

    public static String getPassword() {
        return Config.getDefaultPassword();
    }

    public static String getValidName() {
        return FIRST_NAMES.get(RANDOM.nextInt(FIRST_NAMES.size()))
                + " "
                + LAST_NAMES.get(RANDOM.nextInt(LAST_NAMES.size()));
    }

    public static double getDepositAmount() {
        return GeneratingRule.MIN_DEPOSIT_AMOUNT
                + RANDOM.nextInt(GeneratingRule.MAX_DEPOSIT_AMOUNT - GeneratingRule.MIN_DEPOSIT_AMOUNT);
    }

    public static double getTransferAmount() {
        return GeneratingRule.MIN_TRANSFER_AMOUNT
                + RANDOM.nextInt(GeneratingRule.MAX_TRANSFER_AMOUNT - GeneratingRule.MIN_TRANSFER_AMOUNT);
    }

    public static String getEmail() {
        return getUsername() + "@test.com";
    }
}
package rangiffler.utils;

import com.github.javafaker.Faker;

import javax.annotation.Nonnull;

public class RandomDataUtils {

    private static final Faker faker = new Faker();

    @Nonnull
    public static String randomUsername() {
        return faker.name().username();
    }

    @Nonnull
    public static String randomName() {
        return faker.name().firstName();
    }

    @Nonnull
    public static String randomName(int minLength, int maxLength) {
        if (minLength < 1 || maxLength < minLength) {
            throw new IllegalArgumentException("Invalid length range: minLength must be ≥ 1 and maxLength ≥ minLength");
        }

        String name;
        do {
            name = faker.name().firstName();
        } while (name.length() < minLength || name.length() > maxLength);

        return name;
    }

    @Nonnull
    public static String randomLastname() {
        return faker.name().lastName();
    }

    @Nonnull
    public static String randomPassword() {
        return faker.internet().password();
    }

    @Nonnull
    public static String randomPassword(int min, int max) {
        return faker.internet().password(min, max);
    }

    @Nonnull
    public static String randomUUID() {
        return faker.internet().uuid();
    }

}

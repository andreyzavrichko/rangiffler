package rangiffler.jupiter.annotation;

import rangiffler.model.testdata.CountryCodes;
import rangiffler.model.testdata.UserAvatars;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)

public @interface CreateUser {
    String username() default "";

    String password() default "";

    CountryCodes countryCode() default CountryCodes.US;

    UserAvatars avatar() default UserAvatars.BEE;

    Friend[] friends() default {};

    WithPhoto[] photos() default {};
}
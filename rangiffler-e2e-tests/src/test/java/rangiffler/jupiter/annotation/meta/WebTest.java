
package rangiffler.jupiter.annotation.meta;


import io.qameta.allure.junit5.AllureJunit5;
import org.junit.jupiter.api.extension.ExtendWith;
import rangiffler.jupiter.extension.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ExtendWith({
        GuiceExtension.class,
        AllureJunit5.class,
        UserExtension.class,
        CreateExtrasUserExtension.class,
        ApiLoginExtension.class
})
public @interface WebTest {
}
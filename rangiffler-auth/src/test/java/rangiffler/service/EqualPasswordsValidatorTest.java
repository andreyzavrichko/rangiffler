package rangiffler.service;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rangiffler.model.RegistrationModel;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EqualPasswordsValidatorTest {

  private final EqualPasswordsValidator validatorUnderTest = new EqualPasswordsValidator();

  @Test
  void shouldReturnTrueWhenPasswordsMatch(@Mock ConstraintValidatorContext context) {
    RegistrationModel registration = new RegistrationModel("user", "password123", "password123");

    boolean result = validatorUnderTest.isValid(registration, context);

    assertTrue(result, "Expected passwords to match");
  }

  @Test
  void shouldReturnFalseWhenPasswordsDoNotMatch(
          @Mock ConstraintValidatorContext context,
          @Mock ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder,
          @Mock ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext nodeContext) {

    when(context.buildConstraintViolationWithTemplate(any()))
            .thenReturn(violationBuilder);

    when(violationBuilder.addPropertyNode(eq("password")))
            .thenReturn(nodeContext);

    RegistrationModel registration = new RegistrationModel("user", "password123", "password321");

    boolean result = validatorUnderTest.isValid(registration, context);

    assertFalse(result, "Expected passwords to be different");
  }
}

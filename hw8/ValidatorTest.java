import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ValidatorTest {

  @Test
  void testValidObject() {
    User validUser = new User("John Doe", 25, "john.doe@example.com");
    ValidationResult result = Validator.validate(validUser);

    assertTrue(result.isValid());
    assertEquals(0, result.getErrors().size());
  }

  @Test
  void testNullObject() {
    ValidationResult result = Validator.validate(null);

    assertFalse(result.isValid());
    assertTrue(result.getErrors().getFirst().contains("cannot be null"));
  }

  @Test
  void testNotNullValidation() {
    User user = new User(null, 25, "test@example.com");
    ValidationResult result = Validator.validate(user);

    assertFalse(result.isValid());
    assertTrue(result.getErrors().getFirst().contains("Name cannot be null"));
  }

  @Test
  void testSizeValidation() {
    // Слишком короткое имя
    User user1 = new User("J", 25, "test@example.com");
    ValidationResult result1 = Validator.validate(user1);
    assertFalse(result1.isValid());
    assertTrue(result1.getErrors().getFirst().contains("between 2 and 50"));

    // Слишком длинное имя
    User user2 = new User("J".repeat(51), 25, "test@example.com");
    ValidationResult result2 = Validator.validate(user2);
    assertFalse(result2.isValid());
  }

  @Test
  void testRangeValidation() {
    // Возраст меньше минимума
    User user1 = new User("John", 17, "test@example.com");
    ValidationResult result1 = Validator.validate(user1);
    assertFalse(result1.isValid());
    assertTrue(result1.getErrors().getFirst().contains("between 18 and 120"));

    // Возраст больше максимума
    User user2 = new User("John", 121, "test@example.com");
    ValidationResult result2 = Validator.validate(user2);
    assertFalse(result2.isValid());
  }

  @Test
  void testEmailValidation() {
    // Невалидный email
    User user = new User("John", 25, "invalid-email");
    ValidationResult result = Validator.validate(user);

    assertFalse(result.isValid());
    assertTrue(result.getErrors().getFirst().contains("Invalid email format"));
  }

  @Test
  void testMultipleErrors() {
    // Объект с несколькими ошибками
    User user = new User(null, 17, "invalid-email");
    ValidationResult result = Validator.validate(user);

    assertFalse(result.isValid());
    assertEquals(3, result.getErrors().size());
  }

  @Test
  void testBorderlineCases() {
    // Граничные значения для @Size
    User user1 = new User("Jo", 25, "test@example.com"); // min граница
    User user2 = new User("J".repeat(50), 25, "test@example.com"); // max граница

    assertTrue(Validator.validate(user1).isValid());
    assertTrue(Validator.validate(user2).isValid());

    // Граничные значения для @Range
    User user3 = new User("John", 18, "test@example.com"); // min граница
    User user4 = new User("John", 120, "test@example.com"); // max граница

    assertTrue(Validator.validate(user3).isValid());
    assertTrue(Validator.validate(user4).isValid());
  }
}
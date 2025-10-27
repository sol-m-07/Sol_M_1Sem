import java.lang.reflect.Field;
import java.util.regex.Pattern;

public class Validator {
  private static final String EMAIL_REGEX =
    "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
  private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

  public static ValidationResult validate(Object object) {
    ValidationResult result = new ValidationResult();

    if (object == null) {
      result.addError("Validated object cannot be null");
      return result;
    }

    Class<?> clazz = object.getClass();
    Field[] fields = clazz.getDeclaredFields();

    for (Field field : fields) {
      field.setAccessible(true);

      try {
        Object fieldValue = field.get(object);
        validateField(field, fieldValue, result);

      } catch (IllegalAccessException e) {
        result.addError("Cannot access field: " + field.getName());
      }
    }

    return result;
  }

  private static void validateField(Field field, Object fieldValue, ValidationResult result) {
    if (field.isAnnotationPresent(NotNull.class)) {
      NotNull notNull = field.getAnnotation(NotNull.class);
      if (fieldValue == null) {
        result.addError(notNull.message());
        return;
      }
    }

    if (field.isAnnotationPresent(Size.class) && fieldValue instanceof String) {
      Size size = field.getAnnotation(Size.class);
      String stringValue = (String) fieldValue;
      int length = stringValue.length();

      if (length < size.min() || length > size.max()) {
        result.addError(size.message());
      }
    }

    if (field.isAnnotationPresent(Range.class) && fieldValue != null) {
      Range range = field.getAnnotation(Range.class);

      if (fieldValue instanceof Number) {
        long numericValue = ((Number) fieldValue).longValue();
        if (numericValue < range.min() || numericValue > range.max()) {
          result.addError(range.message());
        }
      }
    }

    if (field.isAnnotationPresent(Email.class) && fieldValue instanceof String) {
      Email email = field.getAnnotation(Email.class);
      String emailValue = (String) fieldValue;

      if (!EMAIL_PATTERN.matcher(emailValue).matches()) {
        result.addError(email.message());
      }
    }
  }
}

import java.util.ArrayList;
import java.util.List;

public class ValidationResult {
  private boolean isValid;
  private List<String> errors;

  public ValidationResult() {
    this.errors = new ArrayList<>();
    this.isValid = true;
  }

  public void addError(String error) {
    this.errors.add(error);
    this.isValid = false;
  }

  public boolean isValid() {
    return isValid;
  }

  public List<String> getErrors() {
    return new ArrayList<>(errors);
  }

  @Override
  public String toString() {
    if (isValid) {
      return "Validation passed successfully";
    } else {
      return "Validation failed with errors: " + String.join(", ", errors);
    }
  }
}

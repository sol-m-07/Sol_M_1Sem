public class User {
  @NotNull(message = "Name cannot be null")
  @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
  private String name;

  @Range(min = 18, max = 120, message = "Age must be between 18 and 120")
  private int age;

  @Email(message = "Invalid email format")
  private String email;

  public User() {}

  public User(String name, int age, String email) {
    this.name = name;
    this.age = age;
    this.email = email;
  }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public int getAge() { return age; }
  public void setAge(int age) { this.age = age; }
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
}

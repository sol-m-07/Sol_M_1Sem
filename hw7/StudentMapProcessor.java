import java.util.*;
import java.util.stream.Collectors;

public class StudentMapProcessor {

  public static void main(String[] args) {
    HashMap<Integer, Student> hashMap = new HashMap<>();

    hashMap.put(1, new Student(1, "Иван Иванов", 4.5));
    hashMap.put(2, new Student(2, "Петр Петров", 3.8));
    hashMap.put(3, new Student(3, "Мария Сидорова", 4.9));
    hashMap.put(4, new Student(4, "Анна Козлова", 3.2));
    hashMap.put(5, new Student(5, "Сергей Смирнов", 4.7));
    hashMap.put(6, new Student(6, "Ольга Орлова", 3.9));

    TreeMap<Integer, Student> treeMap = new TreeMap<>(Collections.reverseOrder());
    treeMap.putAll(hashMap);

    System.out.println("HashMap (ключ - id студента):");
    for (Map.Entry<Integer, Student> entry : hashMap.entrySet()) {
      System.out.println("ID: " + entry.getKey() + " -> " + entry.getValue());
    }

    System.out.println("\nTreeMap (сортировка по убыванию id):");
    for (Map.Entry<Integer, Student> entry : treeMap.entrySet()) {
      System.out.println("ID: " + entry.getKey() + " -> " + entry.getValue());
    }

    System.out.println("\nСтуденты с оценкой от 4.0 до 5.0:");
    List<Student> studentsInRange = findStudentsByGradeRange(hashMap, 4.0, 5.0);
    studentsInRange.forEach(System.out::println);

    System.out.println("\nТоп 3 студента с наибольшими ID:");
    List<Student> topStudents = getTopNStudents(treeMap, 3);
    topStudents.forEach(System.out::println);
  }

  /**
   * Возвращает список студентов с оценкой в заданном диапазоне.
   *
   * @param map карта студентов
   * @param minGrade минимальная оценка (включительно)
   * @param maxGrade максимальная оценка (включительно)
   * @return список студентов, удовлетворяющих условию
   */
  public static List<Student> findStudentsByGradeRange(Map<Integer, Student> map,
                                                       double minGrade, double maxGrade) {
    return map.values().stream()
      .filter(student -> student.getGrade() >= minGrade && student.getGrade() <= maxGrade)
      .collect(Collectors.toList());
  }

  /**
   * Возвращает N студентов с наибольшими id
   *
   * @param map TreeMap с сортировкой по убыванию id
   * @param n количество студентов для возврата
   * @return список из N студентов с наибольшими id
   */
  public static List<Student> getTopNStudents(TreeMap<Integer, Student> map, int n) {
    return map.entrySet().stream()
      .limit(n)
      .map(Map.Entry::getValue)
      .collect(Collectors.toList());
  }

  public static List<Student> getTopNStudentsAlternative(TreeMap<Integer, Student> map, int n) {
    List<Student> result = new ArrayList<>();
    int count = 0;

    for (Map.Entry<Integer, Student> entry : map.entrySet()) {
      if (count >= n) {
        break;
      }
      result.add(entry.getValue());
      count++;
    }

    return result;
  }
}

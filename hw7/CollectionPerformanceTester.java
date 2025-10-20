import org.junit.Test;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CollectionPerformanceTester {

  private static final int ELEMENT_COUNT = 10000;

  @Test
  public void collectionPerformanceTester() {
    System.out.printf("%-20s %-15s %-15s%n", "Операция", "ArrayList (мс)", "LinkedList (мс)");
    System.out.println("------------------------------------------------------------------");

    long arrayListAddEndTime = testAddToEnd(new ArrayList<>());
    long linkedListAddEndTime = testAddToEnd(new LinkedList<>());
    System.out.printf("%-20s %-15d %-15d%n", "Добавление в конец", arrayListAddEndTime, linkedListAddEndTime);

    long arrayListAddStartTime = testAddToStart(new ArrayList<>());
    long linkedListAddStartTime = testAddToStart(new LinkedList<>());
    System.out.printf("%-20s %-15d %-15d%n", "Добавление в начало", arrayListAddStartTime, linkedListAddStartTime);

    long arrayListInsertMiddleTime = testInsertInMiddle(new ArrayList<>());
    long linkedListInsertMiddleTime = testInsertInMiddle(new LinkedList<>());
    System.out.printf("%-20s %-15d %-15d%n", "Вставка в середину", arrayListInsertMiddleTime, linkedListInsertMiddleTime);

    ArrayList<Integer> arrayListForAccess = new ArrayList<>();
    LinkedList<Integer> linkedListForAccess = new LinkedList<>();
    fillList(arrayListForAccess);
    fillList(linkedListForAccess);

    long arrayListAccessTime = testAccessByIndex(arrayListForAccess);
    long linkedListAccessTime = testAccessByIndex(linkedListForAccess);
    System.out.printf("%-20s %-15d %-15d%n", "Доступ по индексу", arrayListAccessTime, linkedListAccessTime);

    ArrayList<Integer> arrayListForRemoveStart = new ArrayList<>();
    LinkedList<Integer> linkedListForRemoveStart = new LinkedList<>();
    fillList(arrayListForRemoveStart);
    fillList(linkedListForRemoveStart);

    long arrayListRemoveStartTime = testRemoveFromStart(arrayListForRemoveStart);
    long linkedListRemoveStartTime = testRemoveFromStart(linkedListForRemoveStart);
    System.out.printf("%-20s %-15d %-15d%n", "Удаление из начала", arrayListRemoveStartTime, linkedListRemoveStartTime);

    ArrayList<Integer> arrayListForRemoveEnd = new ArrayList<>();
    LinkedList<Integer> linkedListForRemoveEnd = new LinkedList<>();
    fillList(arrayListForRemoveEnd);
    fillList(linkedListForRemoveEnd);

    long arrayListRemoveEndTime = testRemoveFromEnd(arrayListForRemoveEnd);
    long linkedListRemoveEndTime = testRemoveFromEnd(linkedListForRemoveEnd);
    System.out.printf("%-20s %-15d %-15d%n", "Удаление из конца", arrayListRemoveEndTime, linkedListRemoveEndTime);
  }

  private long testAddToEnd(List<Integer> list) {
    long startTime = System.currentTimeMillis();

    for (int i = 0; i < ELEMENT_COUNT; i++) {
      list.add(i);
    }

    long endTime = System.currentTimeMillis();
    return endTime - startTime;
  }

  private long testAddToStart(List<Integer> list) {
    long startTime = System.currentTimeMillis();

    for (int i = 0; i < ELEMENT_COUNT; i++) {
      list.add(0, i);
    }

    long endTime = System.currentTimeMillis();
    return endTime - startTime;
  }

  private long testInsertInMiddle(List<Integer> list) {
    for (int i = 0; i < 100; i++) {
      list.add(i);
    }

    long startTime = System.currentTimeMillis();

    for (int i = 0; i < ELEMENT_COUNT; i++) {
      int middleIndex = list.size() / 2;
      list.add(middleIndex, i);
    }

    long endTime = System.currentTimeMillis();
    return endTime - startTime;
  }

  private long testAccessByIndex(List<Integer> list) {
    long startTime = System.currentTimeMillis();

    for (int i = 0; i < ELEMENT_COUNT; i++) {
      int index = i % list.size();
      Integer element = list.get(index);
    }

    long endTime = System.currentTimeMillis();
    return endTime - startTime;
  }

  private long testRemoveFromStart(List<Integer> list) {
    long startTime = System.currentTimeMillis();

    while (!list.isEmpty()) {
      list.remove(0);
    }

    long endTime = System.currentTimeMillis();
    return endTime - startTime;
  }

  private long testRemoveFromEnd(List<Integer> list) {
    long startTime = System.currentTimeMillis();

    while (!list.isEmpty()) {
      list.remove(list.size() - 1);
    }

    long endTime = System.currentTimeMillis();
    return endTime - startTime;
  }

  private void fillList(List<Integer> list) {
    for (int i = 0; i < ELEMENT_COUNT; i++) {
      list.add(i);
    }
  }
}

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Unit-тесты для класса CustomArrayList.
 * Тестирует все основные методы и функциональность.
 */
class CustomArrayListTest {

  private CustomList<String> list;
  private CustomList<Integer> intList;

  /**
   * Инициализация тестовых данных перед каждым тестом.
   */
  @BeforeEach
  void setUp() {
    list = new CustomArrayList<>();
    intList = new CustomArrayList<>();
  }

  /**
   * Тестирует добавление элементов в список.
   */
  @Test
  void testAdd() {
    assertTrue(list.isEmpty());
    assertEquals(0, list.size());

    list.add("first");
    list.add("second");

    assertFalse(list.isEmpty());
    assertEquals(2, list.size());
  }

  /**
   * Тестирует добавление null элемента (должно бросать исключение).
   */
  @Test
  void testAddNullThrowsException() {
    IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> list.add(null)
    );
    assertEquals("Element cannot be null", exception.getMessage());
  }

  /**
   * Тестирует получение элементов по индексу.
   */
  @Test
  void testGet() {
    list.add("first");
    list.add("second");
    list.add("third");

    assertEquals("first", list.get(0));
    assertEquals("second", list.get(1));
    assertEquals("third", list.get(2));
  }

  /**
   * Тестирует получение элемента по невалидному индексу.
   */
  @Test
  void testGetWithInvalidIndexThrowsException() {
    list.add("test");

    assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
    assertThrows(IndexOutOfBoundsException.class, () -> list.get(1));
  }

  /**
   * Тестирует удаление элементов по индексу.
   */
  @Test
  void testRemove() {
    list.add("first");
    list.add("second");
    list.add("third");

    String removed = list.remove(1);

    assertEquals("second", removed);
    assertEquals(2, list.size());
    assertEquals("first", list.get(0));
    assertEquals("third", list.get(1));
  }

  /**
   * Тестирует удаление элемента по невалидному индексу.
   */
  @Test
  void testRemoveWithInvalidIndexThrowsException() {
    list.add("test");

    assertThrows(IndexOutOfBoundsException.class, () -> list.remove(-1));
    assertThrows(IndexOutOfBoundsException.class, () -> list.remove(1));
  }

  /**
   * Тестирует удаление из пустого списка.
   */
  @Test
  void testRemoveFromEmptyListThrowsException() {
    assertThrows(IndexOutOfBoundsException.class, () -> list.remove(0));
  }

  /**
   * Тестирует проверку пустоты списка.
   */
  @Test
  void testIsEmpty() {
    assertTrue(list.isEmpty());

    list.add("element");
    assertFalse(list.isEmpty());

    list.remove(0);
    assertTrue(list.isEmpty());
  }

  /**
   * Тестирует динамическое расширение списка.
   */
  @Test
  void testDynamicExpansion() {
    // Создаем список с маленькой начальной емкостью
    CustomList<Integer> smallList = new CustomArrayList<>();

    smallList.add(1);
    smallList.add(2);
    smallList.add(3);
    smallList.add(4);
    smallList.add(5); // Должно произойти расширение

    assertEquals(5, smallList.size());
    assertEquals(1, smallList.get(0));
    assertEquals(2, smallList.get(1));
    assertEquals(3, smallList.get(2));
    assertEquals(4, smallList.get(3));
    assertEquals(5, smallList.get(4));
  }

  /**
   * Тестирует смещение элементов при удалении.
   */
  @Test
  void testShiftAfterRemove() {
    list.add("A");
    list.add("B");
    list.add("C");
    list.add("D");

    list.remove(1); // Удаляем "B"

    assertEquals(3, list.size());
    assertEquals("A", list.get(0));
    assertEquals("C", list.get(1));
    assertEquals("D", list.get(2));

    list.remove(0); // Удаляем "A"

    assertEquals(2, list.size());
    assertEquals("C", list.get(0));
    assertEquals("D", list.get(1));
  }

  /**
   * Тестирует работу итератора.
   */
  @Test
  void testIterator() {
    list.add("one");
    list.add("two");
    list.add("three");

    Iterator<String> iterator = ((CustomArrayList<String>) list).iterator();

    assertTrue(iterator.hasNext());
    assertEquals("one", iterator.next());
    assertEquals("two", iterator.next());
    assertEquals("three", iterator.next());
    assertFalse(iterator.hasNext());
  }

  /**
   * Тестирует исключение при вызове next() у итератора без элементов.
   */
  @Test
  void testIteratorNoSuchElementException() {
    Iterator<String> iterator = ((CustomArrayList<String>) list).iterator();
    assertThrows(NoSuchElementException.class, iterator::next);
  }

  /**
   * Тестирует работу итератора с пустым списком.
   */
  @Test
  void testIteratorWithEmptyList() {
    Iterator<String> iterator = ((CustomArrayList<String>) list).iterator();
    assertFalse(iterator.hasNext());
  }

  /**
   * Комплексный тест с различными операциями.
   */
  @Test
  void testComplexOperations() {
    // Добавляем элементы
    for (int i = 0; i < 10; i++) {
      intList.add(i);
    }

    assertEquals(10, intList.size());
    assertEquals(0, intList.get(0));
    assertEquals(9, intList.get(9));

    // Удаляем несколько элементов
    intList.remove(0); // Удаляем 0
    intList.remove(4); // Удаляем 5 (после смещения)

    assertEquals(8, intList.size());
    assertEquals(1, intList.get(0));
    assertEquals(6, intList.get(4));

    // Проверяем, что элементы сместились правильно
    for (int i = 0; i < intList.size(); i++) {
      assertNotNull(intList.get(i));
    }
  }
}

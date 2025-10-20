import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Кастомная реализация динамического массива (аналог ArrayList).
 * Поддерживает автоматическое расширение при заполнении и основные операции со списком.
 *
 * @param <A> тип элементов в списке
 */
public class CustomArrayList<A> implements CustomList<A> {

  /**
   * Внутренний массив для хранения элементов списка.
   * Размер массива определяется полем {@code capacity}.
   */
  private Object[] elements;

  /**
   * Текущее количество элементов в списке.
   */
  private int size;

  /**
   * Текущая ёмкость внутреннего массива.
   * Увеличивается при необходимости с коэффициентом 1.5.
   */
  private int capacity;

  /**
   * Создаёт новый CustomArrayList с начальной ёмкостью по умолчанию (4 элемента).
   * Инициализирует пустой список с нулевым размером.
   */
  public CustomArrayList(){
    elements = new Object[4];
    size = 0;
    capacity = 4;
  }

  /**
   * Добавляет элемент в конец списка.
   * Если текущий массив заполнен, автоматически увеличивает его емкость.
   *
   * @param element элемент для добавления
   * @throws IllegalArgumentException если переданный элемент равен null
   */
  @Override
  public void add(A element) {
    if (element == null){
      throw new IllegalArgumentException("Element cannot be null");
    }
    if (capacity <= size) {
      capacity = capacity * 3 / 2;
      Object[] newElements = new Object[capacity];

      for (int i = 0; i < size; i++) {
        newElements[i] = elements[i];
      }

      elements = newElements;
    }

    elements[size] = element;
    size++;
  }

  /**
   * Возвращает элемент по указанному индексу.
   *
   * @param index индекс элемента
   * @return элемент по указанному индексу
   * @throws IndexOutOfBoundsException если индекс выходит за границы списка
   */
  @Override
  public A get(int index) {
    if (index < 0 || index >= size) {
      throw new IndexOutOfBoundsException("Index: " + index + ", size: " + size);
    }
    return (A) elements[index];
  }

  /**
   * Удаляет элемент по указанному индексу и возвращает его.
   * После удаления сдвигает все последующие элементы влево.
   *
   * @param index индекс элемента для удаления
   * @return удаленный элемент
   * @throws IndexOutOfBoundsException если индекс выходит за границы списка
   */
  @Override
  public A remove(int index) {
    if (index < 0 || index >= size) {
      throw new IndexOutOfBoundsException("Index: " + index + ", size: " + size);
    }
    A element = (A) elements[index];

    for (int i = index; i < size - 1; i++) {
      elements[i] = elements[i+1];
    }
    size--;
    elements[size] = null;

    return element;
  }

  /**
   * Возвращает количество элементов в списке.
   *
   * @return количество элементов в списке
   */
  @Override
  public int size() {
    return size;
  }

  /**
   * Проверяет, пуст ли список.
   *
   * @return true если список пуст, false в противном случае
   */
  @Override
  public boolean isEmpty() {
    return size == 0;
  }

  /**
   * Возвращает итератор для последовательного обхода элементов списка.
   *
   * @return итератор для списка
   */
  public Iterator<A> iterator() {
    return new CustomArrayListIterator();
  }

  /**
   * Внутренний класс итератора для CustomArrayList.
   * Реализует основные операции итератора.
   */
  private class CustomArrayListIterator implements Iterator<A> {

    /**
     * Текущая позиция итератора.
     */
    private int currentIndex = 0;

    /**
     * Проверяет, есть ли следующий элемент в списке.
     *
     * @return true если есть следующий элемент, false в противном случае
     */
    @Override
    public boolean hasNext() {
      return currentIndex < size;
    }

    /**
     * Возвращает следующий элемент в списке и перемещает итератор.
     *
     * @return следующий элемент
     * @throws NoSuchElementException если следующего элемента нет
     */
    @Override
    @SuppressWarnings("unchecked")
    public A next() {
      if (!hasNext()) {
        throw new NoSuchElementException("No more elements in the list");
      }
      return (A) elements[currentIndex++];
    }
  }
}

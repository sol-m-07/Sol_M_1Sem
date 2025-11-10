import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

class TextFileAnalyzerTest {

  @Test
  void testAnalyzeFile() throws IOException {
    TextFileAnalyzer analyzer = new TextFileAnalyzer();

    Path testFile = Files.createTempFile("test", ".txt");
    Files.write(testFile, Arrays.asList("Hello world!", "This is test."));

    TextFileAnalyzer.AnalysisResult result = analyzer.analyzeFile(testFile.toString());

    assertEquals(2, result.getLineCount(), "Количество строк должно быть 2");
    assertEquals(5, result.getWordCount(), "Количество слов должно быть 5");
    assertEquals(26, result.getCharCount(), "Количество символов должно быть 26");

    assertTrue(result.getCharFrequency().get('H') >= 1, "Символ 'H' должен встречаться минимум 1 раз");
    assertTrue(result.getCharFrequency().get('e') >= 2, "Символ 'e' должен встречаться минимум 2 раза");
    assertTrue(result.getCharFrequency().get(' ') >= 4, "Пробелы должны встречаться минимум 4 раза");

    Files.deleteIfExists(testFile);
  }

  @Test
  void testSaveAnalysisResult() throws IOException {
    TextFileAnalyzer analyzer = new TextFileAnalyzer();

    Map<Character, Integer> charFrequency = new HashMap<>();
    charFrequency.put('H', 1);
    charFrequency.put('e', 2);
    charFrequency.put('l', 3);
    charFrequency.put('o', 2);
    charFrequency.put(' ', 4);

    TextFileAnalyzer.AnalysisResult result = new TextFileAnalyzer.AnalysisResult(2, 5, 26, charFrequency);

    Path outputFile = Files.createTempFile("analysis", ".txt");
    analyzer.saveAnalysisResult(result, outputFile.toString());

    assertTrue(Files.exists(outputFile), "Файл должен существовать");
    assertTrue(Files.size(outputFile) > 0, "Размер файла должен быть больше 0");

    String content = Files.readString(outputFile);
    assertTrue(content.contains("Количество строк: 2"), "Должно содержать количество строк");
    assertTrue(content.contains("Количество слов: 5"), "Должно содержать количество слов");
    assertTrue(content.contains("Количество символов: 26"), "Должно содержать количество символов");
    assertTrue(content.contains("Частота символов"), "Должно содержать раздел частоты символов");

    Files.deleteIfExists(outputFile);
  }

  @Test
  void testAnalyzeEmptyFile() throws IOException {
    TextFileAnalyzer analyzer = new TextFileAnalyzer();

    Path emptyFile = Files.createTempFile("empty", ".txt");
    Files.write(emptyFile, Arrays.asList(""));

    TextFileAnalyzer.AnalysisResult result = analyzer.analyzeFile(emptyFile.toString());

    assertEquals(1, result.getLineCount(), "Пустой файл должен иметь 1 строку");
    assertEquals(0, result.getWordCount(), "Пустой файл должен иметь 0 слов");
    assertEquals(0, result.getCharCount(), "Пустой файл должен иметь 0 символов");

    Files.deleteIfExists(emptyFile);
  }
}

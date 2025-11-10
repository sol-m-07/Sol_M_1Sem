import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;

class FileProcessorTest {

  @Test
  void testSplitAndMergeFile() throws IOException {
    FileProcessor processor = new FileProcessor();

    Path testFile = Files.createTempFile("test", ".dat");
    byte[] testData = new byte[1500]; // 1.5KB данных
    new Random().nextBytes(testData);
    Files.write(testFile, testData);

    String outputDir = Files.createTempDirectory("parts").toString();
    List<Path> parts = processor.splitFile(testFile.toString(), outputDir, 500);

    assertEquals(3, parts.size(), "Должно быть создано 3 части");

    assertEquals(500, Files.size(parts.get(0)), "Первая часть должна быть 500 байт");
    assertEquals(500, Files.size(parts.get(1)), "Вторая часть должна быть 500 байт");
    assertEquals(500, Files.size(parts.get(2)), "Третья часть должна быть 500 байт");

    String fileName = testFile.getFileName().toString();
    assertTrue(parts.get(0).toString().endsWith(fileName + ".part1"));
    assertTrue(parts.get(1).toString().endsWith(fileName + ".part2"));
    assertTrue(parts.get(2).toString().endsWith(fileName + ".part3"));

    Path mergedFile = Files.createTempFile("merged", ".dat");
    processor.mergeFiles(parts, mergedFile.toString());

    assertArrayEquals(Files.readAllBytes(testFile), Files.readAllBytes(mergedFile));

    Files.deleteIfExists(testFile);
    Files.deleteIfExists(mergedFile);
    for (Path part : parts) {
      Files.deleteIfExists(part);
    }
    Files.deleteIfExists(Path.of(outputDir));
  }

  @Test
  void testSplitFileWithExactSize() throws IOException {
    FileProcessor processor = new FileProcessor();

    Path testFile = Files.createTempFile("exact", ".dat");
    byte[] testData = new byte[1000];
    new Random().nextBytes(testData);
    Files.write(testFile, testData);

    String outputDir = Files.createTempDirectory("exact_parts").toString();
    List<Path> parts = processor.splitFile(testFile.toString(), outputDir, 500);

    assertEquals(2, parts.size());
    assertEquals(500, Files.size(parts.get(0)));
    assertEquals(500, Files.size(parts.get(1)));

    Files.deleteIfExists(testFile);
    for (Path part : parts) {
      Files.deleteIfExists(part);
    }
    Files.deleteIfExists(Path.of(outputDir));
  }

  @Test
  void testSplitFileWithSmallLastPart() throws IOException {
    FileProcessor processor = new FileProcessor();

    Path testFile = Files.createTempFile("small", ".dat");
    byte[] testData = new byte[1200];
    new Random().nextBytes(testData);
    Files.write(testFile, testData);

    String outputDir = Files.createTempDirectory("small_parts").toString();
    List<Path> parts = processor.splitFile(testFile.toString(), outputDir, 500);

    assertEquals(3, parts.size());
    assertEquals(500, Files.size(parts.get(0)));
    assertEquals(500, Files.size(parts.get(1)));
    assertEquals(200, Files.size(parts.get(2)));

    Files.deleteIfExists(testFile);
    for (Path part : parts) {
      Files.deleteIfExists(part);
    }
    Files.deleteIfExists(Path.of(outputDir));
  }

  @Test
  void testMergeWithNonExistentParts() {
    FileProcessor processor = new FileProcessor();

    Path nonExistentPart = Path.of("nonexistent.part1");

    assertThrows(NoSuchFileException.class, () -> {
      processor.mergeFiles(List.of(nonExistentPart), "output.dat");
    });
  }

  @Test
  void testSplitNonExistentFile() {
    FileProcessor processor = new FileProcessor();

    assertThrows(NoSuchFileException.class, () -> {
      processor.splitFile("nonexistent.dat", "output", 100);
    });
  }
}

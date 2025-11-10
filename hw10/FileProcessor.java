import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class FileProcessor {

  /**
   * Разбивает файл на части указанного размера
   *
   * @param sourcePath путь к исходному файлу
   * @param outputDir директория для сохранения частей
   * @param partSize размер каждой части в байтах
   * @return список путей к созданным частям
   */
  public List<Path> splitFile(String sourcePath, String outputDir, int partSize) throws IOException {
    List<Path> partPaths = new ArrayList<>();
    Path sourceFile = Paths.get(sourcePath);

    if (!Files.exists(sourceFile)) {
      throw new NoSuchFileException("Исходный файл не существует: " + sourcePath);
    }

    Path outputDirectory = Paths.get(outputDir);
    if (!Files.exists(outputDirectory)) {
      Files.createDirectories(outputDirectory);
    }

    String fileName = sourceFile.getFileName().toString();

    try (FileChannel sourceChannel = FileChannel.open(sourceFile, StandardOpenOption.READ)) {
      long fileSize = sourceChannel.size();
      int partNumber = 1;
      long bytesRead = 0;

      ByteBuffer buffer = ByteBuffer.allocate(partSize);

      while (bytesRead < fileSize) {
        String partFileName = String.format("%s.part%d", fileName, partNumber);
        Path partFile = outputDirectory.resolve(partFileName);

        try (FileChannel destChannel = FileChannel.open(partFile,
          StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {

          buffer.clear();
          int readBytes = sourceChannel.read(buffer);

          if (readBytes > 0) {
            buffer.flip();
            destChannel.write(buffer);
            bytesRead += readBytes;
            partPaths.add(partFile);
          }
        }

        partNumber++;
      }
    }

    return partPaths;
  }

  /**
   * Объединяет части файла обратно в один файл
   *
   * @param partPaths список путей к частям файла (в правильном порядке)
   * @param outputPath путь для результирующего файла
   */
  public void mergeFiles(List<Path> partPaths, String outputPath) throws IOException {
    if (partPaths == null || partPaths.isEmpty()) {
      throw new IllegalArgumentException("Список частей не может быть пустым");
    }

    for (Path part : partPaths) {
      if (!Files.exists(part)) {
        throw new NoSuchFileException("Часть файла не существует: " + part);
      }
    }

    Path outputFile = Paths.get(outputPath);

    try (FileChannel outputChannel = FileChannel.open(outputFile,
      StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {

      ByteBuffer buffer = ByteBuffer.allocate(8192);

      for (Path part : partPaths) {
        try (FileChannel partChannel = FileChannel.open(part, StandardOpenOption.READ)) {
          long partSize = partChannel.size();
          long bytesTransferred = 0;

          while (bytesTransferred < partSize) {
            buffer.clear();
            int readBytes = partChannel.read(buffer);

            if (readBytes == -1) {
              break;
            }

            buffer.flip();
            outputChannel.write(buffer);
            bytesTransferred += readBytes;
          }
        }
      }
    }
  }
}

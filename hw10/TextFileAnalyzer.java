import java.io.*;
import java.util.*;

public class TextFileAnalyzer {

  public static class AnalysisResult {
    private final long lineCount;
    private final long wordCount;
    private final long charCount;
    private final Map<Character, Integer> charFrequency;

    public AnalysisResult(long lineCount, long wordCount, long charCount, Map<Character, Integer> charFrequency) {
      this.lineCount = lineCount;
      this.wordCount = wordCount;
      this.charCount = charCount;
      this.charFrequency = charFrequency;
    }

    public long getLineCount() { return lineCount; }
    public long getWordCount() { return wordCount; }
    public long getCharCount() { return charCount; }
    public Map<Character, Integer> getCharFrequency() { return charFrequency; }

    @Override
    public String toString() {
      return String.format(
        "AnalysisResult{lineCount=%d, wordCount=%d, charCount=%d, charFrequency=%s}",
        lineCount, wordCount, charCount, charFrequency
      );
    }
  }

  public AnalysisResult analyzeFile(String filePath) throws IOException {
    long lineCount = 0;
    long wordCount = 0;
    long charCount = 0;
    Map<Character, Integer> charFrequency = new HashMap<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
      String line;

      while ((line = reader.readLine()) != null) {
        lineCount++;
        charCount += line.length();

        String[] words = line.trim().split("\\s+");
        if (!line.trim().isEmpty()) {
          wordCount += words.length;
        }

        for (char c : line.toCharArray()) {
          charFrequency.put(c, charFrequency.getOrDefault(c, 0) + 1);
        }
      }
    }

    return new AnalysisResult(lineCount, wordCount, charCount, charFrequency);
  }

  public void saveAnalysisResult(AnalysisResult result, String outputPath) throws IOException {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
      writer.write("Результаты анализа файла:\n");
      writer.write("==========================\n");
      writer.write("Количество строк: " + result.getLineCount() + "\n");
      writer.write("Количество слов: " + result.getWordCount() + "\n");
      writer.write("Количество символов: " + result.getCharCount() + "\n");
      writer.write("\nЧастота символов:\n");

      List<Character> sortedChars = new ArrayList<>(result.getCharFrequency().keySet());
      sortedChars.sort(Character::compareTo);

      for (char c : sortedChars) {
        String charDisplay = (c == '\n') ? "\\n" :
          (c == '\t') ? "\\t" :
            (c == '\r') ? "\\r" :
              (c == ' ') ? "пробел" :
                String.valueOf(c);
        writer.write("'" + charDisplay + "': " + result.getCharFrequency().get(c) + "\n");
      }
    }
  }
}

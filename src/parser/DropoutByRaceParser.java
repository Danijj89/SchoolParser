package parser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DropoutByRaceParser extends AbstractSchoolsParser {

  private static final String year = "2016";

  public DropoutByRaceParser(Appendable ap) throws IllegalArgumentException {
    super(ap);
  }

  @Override
  public List<String> parse(String fileName) throws IllegalStateException {
    List<String> result = new ArrayList<>();
    BufferedReader reader;
    try {
      reader = new BufferedReader(new FileReader(fileName));
    } catch (FileNotFoundException e) {
      throw new IllegalStateException("File " + fileName + " not found");
    }
    String line;
    try {
      line = reader.readLine();
      while (line != null) {
        String[] values = line.split(",");
        result.add(year);
        for (int i = 0; i < 8; i ++) {
          result.add(values[i]);
        }
        this.ap.append(year + ", "
            + Arrays.asList(values).stream().collect(Collectors.joining(", ")) + "\n");
        line = reader.readLine();
      }
    } catch (IOException e) {
      throw new IllegalStateException("I/O error");
    }
    return result;
  }

  @Override
  public String preparedStatement() {
    return "UPDATE enrollment_dropout_by_race SET num_dropout = (num_enrollment * ? / 100) "
        + "WHERE school_id = ? and race_id = ? and year = ?";
  }

  @Override
  public int numVariableToSet() {
    return 4;
  }

  public static void main(String[] args) {
    try {
      FileWriter f = new FileWriter("parsed_dropout2016.txt");
      DropoutByRaceParser p = new DropoutByRaceParser(f);
      p.parse("dropout2016.csv");
      f.flush();
    } catch (IOException e) {
      throw new IllegalArgumentException(e.getMessage());
    }
  }
}

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

/*
public class DistrictLatLongParser extends AbstractSchoolsParser {

  public DistrictLatLongParser(Appendable ap) {
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
        for (int i = 0; i < values.length; i ++) {
          result.add(values[i]);
        }
        this.ap.append(Arrays.stream(values).collect(Collectors.joining(", ")) + "\n");
        line = reader.readLine();
      }
    } catch (IOException e) {
      throw new IllegalStateException("I/O error");
    }
    return result;
  }

  @Override
  public String preparedStatement() {
    return "UPDATE school SET latitude = ?"
        + "WHERE school_name = ?";
  }

  @Override
  public int numVariableToSet() {
    return 2;
  }

  public static void main(String[] args) {
    try {
      FileWriter f = new FileWriter("school_lat_long.txt");
      DistrictLatLongParser p = new DistrictLatLongParser(f);
      p.parse("school_lat_long.csv");
      f.flush();
    } catch (IOException e) {
      throw new IllegalArgumentException(e.getMessage());
    }
  }
}
*/
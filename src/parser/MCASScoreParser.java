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
/**
 * Represents an implementation of a CSV for SQL parser.
 * It is used to parse MCAS scores for the schools database project.
 * NOTE:
 * Need to set the year manually.
 */

/*
public class MCASScoreParser extends AbstractSchoolsParser {

  // The year of the MCAS exam.
  private static final String year = "2017";
*/
  /**
   * Constructs an instance of this mcas score parser.
   *
   * @param ap the appendable to append to.
   * @throws IllegalArgumentException if the given appendable is null.
   */

  /*
  public MCASScoreParser(Appendable ap) throws IllegalArgumentException {
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
    // array of scores : [advanced, proficient, NI, fail]
    int[] scores = new int[4];
    double tempSGP = 0;
    int numSGP = 0;
    int counter = 0;
    try {
      line = reader.readLine();
      while (line != null) {
        String[] values = line.split(",");
        String[] name = values[0].split(" - ");
        values[0] = name[1].trim();
        if (counter == 3) {
          result.add(values[0]);
          result.add(year);
          result.add(Integer.toString(scores[0]));
          result.add(Integer.toString(scores[1]));
          result.add(Integer.toString(scores[2]));
          result.add(Integer.toString(scores[3]));
          double sgp = Math.round((tempSGP / numSGP) * 100.0) / 100.0;
          result.add(Double.toString(sgp));
          this.ap.append(values[0] + ", " + year + ", " + Arrays.stream(scores).boxed()
              .map((n) -> Integer.toString(n)).collect(Collectors.joining(", "))
              + ", " + sgp + "\n");
          tempSGP = 0;
          numSGP = 0;
          scores = new int[4];
          counter = 0;
        }
        scores[0] += Integer.valueOf(values[1]);
        scores[1] += Integer.valueOf(values[2]);
        scores[2] += Integer.valueOf(values[3]);
        scores[3] += Integer.valueOf(values[4]);
        System.out.println(values[0]);
        if (!values[5].contains("N/A") && !values[5].equals(" ") && !values[5].equals("")) {
          tempSGP += Double.valueOf(values[5]);
          numSGP += 1;
        }
        counter += 1;
        line = reader.readLine();
      }
      if (!(counter == 3)) {
        throw new IllegalArgumentException("the counter should have been 3");
      }
    } catch (IOException e) {
      throw new IllegalStateException("I/O error");
    }
    return result;
  }

  @Override
  public String preparedStatement() {
    return "INSERT INTO mcas_score VALUES (?,?,?,?,?,?,?)";
  }

  @Override
  public int numVariableToSet() {
    return 7;
  }

  public static void main(String[] args) {
    try {
      FileWriter f = new FileWriter("parsed_mcas2017.txt");
      MCASScoreParser p = new MCASScoreParser(f);
      p.parse("mcas2017.csv");
      f.flush();
    } catch (IOException e) {
      throw new IllegalArgumentException(e.getMessage());
    }
  }
}
*/
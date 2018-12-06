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
 * It is used to parse enrollment by race data for the schools database project.
 * NOTE:
 * It is necessary to set the year manually.
 */
public class EnrollmentByRaceParser extends AbstractSchoolsParser {

  // The year of the enrollment data.
  // The year means the fall semester of the year, i.e. 2013 is the academic year 2013-2014.
  private static final String year = "2016";

  /**
   * Constructs an instance of this parser.
   *
   * @param ap the appendable to be append to.
   * @throws IllegalArgumentException if the appendable is null.
   */
  public EnrollmentByRaceParser(Appendable ap) throws IllegalArgumentException {
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
      int[] numEnrollment = new int[7];
      int counter = 0;
      line = reader.readLine();
      String prevName = "";
      int grade;
      while (line != null) {
        // values[school name, grade, total, Afr. american, Asian, hispanic, multi, nat. american,
        // nat. hawaiian, white]
        String[] values = line.split(",");
        if (!values[1].equals("K") && !values[1].equals("PK") && !values[1].contains("SP")) {
          String[] tempGrade = values[1].split("\\.");
          grade = Integer.valueOf(tempGrade[1]);
          if (grade > 8 && grade < 13) {
            System.out.println(values[0]);
            if (!values[0].equals(prevName)) {
              if (counter > 1) {
                result.add(values[0]);
                result.add(year);
                for (int i = 0; i < numEnrollment.length; i++) {
                  result.add(Integer.toString(numEnrollment[i]));
                }
                this.ap.append(values[0] + ", " + year + ", " + Arrays.stream(numEnrollment)
                    .boxed().map((n) -> Integer.toString(n))
                    .collect(Collectors.joining(", ")) + "\n");
                counter = 0;
                numEnrollment = new int[7];
              }
              for (int i = 0; i < numEnrollment.length; i ++) {
                if (values[2].equals("***")) {
                  numEnrollment[i] += 0;
                }
                else {
                  numEnrollment[i] += Integer.valueOf(values[i + 2]);
                }
              }
              counter += 1;
              prevName = values[0];
            }
            else {
              for (int i = 0; i < numEnrollment.length; i ++) {
                if (values[2].equals("***")) {
                  numEnrollment[i] += 0;
                }
                else {
                  numEnrollment[i] += Integer.valueOf(values[i + 2]);
                }
              }
              counter += 1;
            }
          }
        }
        line = reader.readLine();
      }
      if (!(counter == 4)) {
        throw new IllegalArgumentException("the counter should have been 4");
      }
    } catch (IOException e) {
      throw new IllegalStateException("I/O error");
    }
    return result;
  }

  @Override
  public String preparedStatement() {
    return "INSERT INTO enrollment_dropout_by_race(school_id, race_id, year, num_enrollment)"
        + " VALUES (?, ?, ?, ?)";
  }

  @Override
  public int numVariableToSet() {
    return 4;
  }

  public static void main(String[] args) {
    try {
      FileWriter f = new FileWriter("parsed_enrollment2016.txt");
      EnrollmentByRaceParser p = new EnrollmentByRaceParser(f);
      p.parse("enrollment2016.csv");
      f.flush();
    } catch (IOException e) {
      throw new IllegalArgumentException(e.getMessage());
    }
  }
}

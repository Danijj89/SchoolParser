package parser.projects.schools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import parser.AbstractSchoolsParser;
import parser.MySQLCParser;

/**
 * Represents an implementation of {@link MySQLCParser} used to parse district names for
 * the schools database project.
 */
public final class DistrictNameParser extends AbstractSchoolsParser {

  /**
   * Parses the district name CSV file and appends it to the Appendable.
   * Each row is represented by the name of a district separated by an int value representing
   * if the school is a high school or not. Any number > 0 is a high school.
   * Eliminates multiple occurrences of the same name and ignores those with 0
   * as second values.
   * Each row in the result contain: DistrictName.
   *
   * @return the list of district names.
   * @throws IllegalStateException if the inputs or output has problems.
   */
  @Override
  protected List<String> helpParse() {
    List<String> result = new ArrayList<>();
    String line;
    String prevName = "";
    try {
      line = this.reader.readLine();
      while (line != null) {
        String[] values = line.split(",");
        if (!values[0].equals(prevName) && Integer.valueOf(values[1]) > 0) {
          result.add(values[0]);
          this.fw.append(values[0] + "\n");
          prevName = values[0];
        }
        line = this.reader.readLine();
      }
    } catch (IOException e) {
      throw new IllegalStateException("I/O error");
    }
    return result;
  }

  /**
   * Gets the list of statements that are used to  to populate the database.
   * Statements by index:
   * (0) Inserts the district names.
   * @return the list of statements
   */
  @Override
  public List<String> preparedStatement() {
    List<String> result = new ArrayList<>();
    result.add("INSERT INTO district(district_name) VALUES (?)");
    return result;
  }

  @Override
  public List<Integer> numVariableToSet() {
    List<Integer> result = new ArrayList<>();
    result.add(1);
    return result;
  }
}

package parser.projects.schools;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.SQLException;
import java.util.Iterator;
import parser.MySQLCParser;

/**
 * Represents an implementation of {@link MySQLCParser} used to parse district names for
 * the schools database project.
 */
public final class DistrictNameParser extends AbstractSchoolsParser {

  /**
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
  protected void helpParse() throws IllegalStateException {
    String line;
    String prevName = "";
    try {
      line = this.reader.readLine();
      while (line != null) {
        String[] values = line.split(",");
        if (!values[0].equals(prevName) && Integer.valueOf(values[1]) > 0) {
          this.parsedResult.add(values[0]);
          prevName = values[0];
        }
        line = this.reader.readLine();
      }
    } catch (IOException e) {
      throw new IllegalStateException("I/O error");
    }
  }

  @Override
  protected int numRowValues() {
    return 1;
  }

  @Override
  public void updateDB(String driver, String connectionPath) throws IllegalStateException {
    this.connect(driver, connectionPath);
    Iterator<String> iter = this.parsedResult.iterator();
    try {
      this.preparedStatement =
          this.connection.prepareStatement("INSERT INTO district(district_name) VALUES (?)");
      while (iter.hasNext()) {
        this.preparedStatement.setString(1, iter.next());
        this.preparedStatement.executeUpdate();
      }
    } catch (SQLException e) {
      throw new IllegalStateException(e.getMessage());
    }
    this.closeConnection();
  }
}

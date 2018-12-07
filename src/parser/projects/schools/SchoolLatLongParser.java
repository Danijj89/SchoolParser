package parser.projects.schools;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;


/**
 * Represents an implementation of {@link AbstractSchoolsParser} to parse and insert schools
 * latitude and longitude data for the schools database project.
 */
public class SchoolLatLongParser extends AbstractSchoolsParser {

  /**
   * Parses the latitude and longitude of schools.
   * Each row in the result contain: SchoolName, latitude, longitude.
   */
  @Override
  protected void helpParse() {
    String line;
    try {
      line = this.reader.readLine();
      while (line != null) {
        String[] values = line.split(",");
        for (int i = 0; i < values.length; i ++) {
          this.parsedResult.add(values[i]);
        }
        line = reader.readLine();
      }
    } catch (IOException e) {
      throw new IllegalStateException("I/O error");
    }
  }

  @Override
  protected int numRowValues() {
    return 3;
  }

  @Override
  public void updateDB(String driver, String connectionPath) {
    this.connect(driver, connectionPath);
    Iterator<String> iter = this.parsedResult.iterator();
    try {
      while (iter.hasNext()) {
        this.preparedStatement = this.connection.prepareStatement(
            "UPDATE school SET latitude = ? WHERE school_name = ?");
        String schoolName = iter.next();
        String lat = iter.next();
        this.preparedStatement.setString(1, lat);
        this.preparedStatement.setString(2, schoolName);
        this.preparedStatement.executeUpdate();
        this.preparedStatement = this.connection.prepareStatement(
            "UPDATE school SET longitude = ? WHERE school_name = ?");
        String lon = iter.next();
        preparedStatement.setString(1, lon);
        preparedStatement.setString(2, schoolName);
        preparedStatement.executeUpdate();
      }
    } catch (SQLException e) {
      throw new IllegalStateException(e.getMessage());
    }
    this.closeConnection();
  }
}

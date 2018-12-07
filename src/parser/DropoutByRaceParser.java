package parser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import parser.projects.schools.AbstractSchoolsParser;

/**
 * Represents and implementation of {@link AbstractSchoolsParser} used to parse the number of
 * dropout students for the schools database project.
 */
public class DropoutByRaceParser extends AbstractSchoolsParser {
  // The year of the dropout data.
  // The year means the fall semester of the year, i.e. 2013 is the academic year 2013-2014.
  private final int year;

  /**
   * Create an instance of this dropout parser with the specific academic year of the data.
   *
   * @param year year of the dropout data.
   */
  public DropoutByRaceParser(int year) {
    super();
    this.year = year;
  }

  /**
   * Parses the number of dropout students for each district.
   * Each row in the result contain:
   * Year, DistrictName, N. Afr. American, N. Asian, N. Hispanic, N. Multi, N. Nat. American,
   * N. Nat. Hawaiian, N. White
   */
  @Override
  protected void helpParse() {
    String line;
    try {
      line = this.reader.readLine();
      while (line != null) {
        String[] values = line.split(",");
        this.parsedResult.add(Integer.toString(this.year));
        for (int i = 0; i < 8; i ++) {
          this.parsedResult.add(values[i]);
        }
        line = this.reader.readLine();
      }
    } catch (IOException e) {
      throw new IllegalStateException("I/O error");
    }
  }

  @Override
  protected int numRowValues() {
    return 9;
  }

  @Override
  public void updateDB(String driver, String connectionPath) {
    this.connect(driver, connectionPath);
    Iterator<String> iter = this.parsedResult.iterator();
    try {
      this.preparedStatement = this.connection.prepareStatement(
          "UPDATE enrollment_dropout_by_race SET num_dropout = (num_enrollment * ? / 100) "
              + "WHERE school_id = ? and race_id = ? and year = ?");
      while (iter.hasNext()) {
        String year = iter.next();
        String districtName = iter.next();
        System.out.println(districtName);
        String[] dropoutRates = new String[7];
        for (int i = 0; i < 7; i ++) {
          dropoutRates[i] = iter.next();
        }
        this.resultSet = statement.executeQuery(
            "SELECT * FROM school JOIN district USING (district_id) "
                + "WHERE district_name = '" + districtName + "'");
        if (this.resultSet.next()) {
          String schoolId = this.resultSet.getString("school_id");
          for (int race = 0; race < 7; race ++) {
            this.preparedStatement.setString(1, dropoutRates[race]);
            this.preparedStatement.setString(2, schoolId);
            this.preparedStatement.setString(3, Integer.toString(race + 1));
            this.preparedStatement.setString(4, year);
            this.preparedStatement.executeUpdate();
          }
        }
      }
    } catch (SQLException e) {
      throw new IllegalStateException(e.getMessage());
    }
    this.closeConnection();
  }
}
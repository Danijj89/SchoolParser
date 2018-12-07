package parser.projects.schools;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;

/**
 * Represents an implementation of {@link AbstractSchoolsParser} used to parse enrollment
 * by race data for the schools database project.
 */
public class EnrollmentByRaceParser extends AbstractSchoolsParser {
  // The year of the enrollment data.
  // The year means the fall semester of the year, i.e. 2013 is the academic year 2013-2014.
  private final int year;

  /**
   * Construct an instance of this parser with the year of the enrollment data.
   *
   * @param year the year of the enrollement.
   */
  public EnrollmentByRaceParser(int year) {
    super();
    this.year = year;
  }

  /**
   * Parses the number of enrolled students for each school on a specific year.
   * Each row in the result contain:
   * SchoolName, Year, N. Afr. American, N. Asian, N. Hispanic, N. multi, N. Nat. American,
   * N. Nat. Hawaiian, N. White
   *
   * Eliminates grades that are not from [9-12] as they are not high-schools.
   * Eliminates those with special grades: 'K', 'PK', 'SP'
   * Sums up the number of enrolled students for each school from grade 9 to 12.
   */
  @Override
  protected void helpParse() {
    String line;
    try {
      int[] numEnrollment = new int[7];
      int counter = 0;
      line = reader.readLine();
      String prevName = "";
      int grade;
      while (line != null) {
        // values[school name, grade, Afr. american, Asian, hispanic, multi, nat. american,
        // nat. hawaiian, white]
        String[] values = line.split(",");

        // Ignores special grades
        if (!values[1].equals("K") && !values[1].equals("PK") && !values[1].contains("SP")) {
          String[] tempGrade = values[1].split("\\.");
          grade = Integer.valueOf(tempGrade[1]);
          // Check if it is a high school (9<=Grade<=12)
          if (grade > 8 && grade < 13) {
            if (!values[0].equals(prevName)) {
              if (counter > 1) {
                this.parsedResult.add(values[0]);
                this.parsedResult.add(Integer.toString(this.year));
                for (int i = 0; i < numEnrollment.length; i++) {
                  this.parsedResult.add(Integer.toString(numEnrollment[i]));
                }
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
          "INSERT INTO enrollment_dropout_by_race(school_id, race_id, year, num_enrollment)"
              + " VALUES (?, ?, ?, ?)");
      while (iter.hasNext()) {
        String schoolName = iter.next();
        String year = iter.next();
        this.resultSet = statement.executeQuery(
            "SELECT * FROM school WHERE school_name = '" + schoolName + "'");
        if (this.resultSet.next()) {

          this.preparedStatement.setString(1, this.resultSet.getString("school_id"));
          for (int i = 0; i < 7; i ++) {
            preparedStatement.setString(1, resultSet.getString("school_id"));
            preparedStatement.setString(2, Integer.toString(i + 1));
            preparedStatement.setString(3, year);
            preparedStatement.setString(4, iter.next());
            preparedStatement.executeUpdate();
          }
        }
      }
    } catch (SQLException e) {
      throw new IllegalStateException(e.getMessage());
    }
    this.closeConnection();
  }
}

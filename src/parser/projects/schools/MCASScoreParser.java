package parser.projects.schools;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents an implementation of {@link AbstractSchoolsParser} used to parse MCAS scores
 * for the schools database project.
 * NOTE:
 * Need to set the year manually.
 */
public class MCASScoreParser extends AbstractSchoolsParser {

  // The year of the MCAS exam.
  private final int year;

  /**
   * Constructs an instance of this parser with an exam year.
   *
   * @param year the exam year.
   */
  public MCASScoreParser(int year) {
    super();
    this.year = year;
  }

  /**
   * Parses the MCAS score data for the schools database project.
   * Each row in the result contain:
   * SchoolName, Exam Year, N. Advanced, N. Proficient, N. Need Improvement, N. Fail, SGP
   * This parses adds the row where the school names are the same and take average of SGP score.
   *
   * @throws IllegalArgumentException if the file does not contain 3 column per school.
   * @throws IllegalStateException if the input or output has problems.
   */
  @Override
  protected void helpParse() throws IllegalArgumentException, IllegalStateException {
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
        // Separate district name with school name
        String[] name = values[0].split(" - ");
        values[0] = name[1].trim();

        // Every 3 rows add the cumulative result
        if (counter == 3) {
          this.parsedResult.add(values[0]);
          this.parsedResult.add(Integer.toString(this.year));
          this.parsedResult.add(Integer.toString(scores[0]));
          this.parsedResult.add(Integer.toString(scores[1]));
          this.parsedResult.add(Integer.toString(scores[2]));
          this.parsedResult.add(Integer.toString(scores[3]));
          double sgp = Math.round((tempSGP / numSGP) * 100.0) / 100.0;
          this.parsedResult.add(Double.toString(sgp));

          // Reset accumulators
          tempSGP = 0;
          numSGP = 0;
          scores = new int[4];
          counter = 0;
        }

        scores[0] += Integer.valueOf(values[1]);
        scores[1] += Integer.valueOf(values[2]);
        scores[2] += Integer.valueOf(values[3]);
        scores[3] += Integer.valueOf(values[4]);

        if (!values[5].contains("N/A") && !values[5].equals(" ") && !values[5].equals("")) {
          tempSGP += Double.valueOf(values[5]);
          numSGP += 1;
        }
        counter += 1;
        line = reader.readLine();
      }
      // Check that every school had exactly 3 columns
      if (!(counter == 3)) {
        throw new IllegalArgumentException("the counter should have been 3");
      }
    } catch (IOException e) {
      throw new IllegalStateException("I/O error");
    }
  }

  @Override
  protected int numRowValues() {
    return 7;
  }

  @Override
  public void updateDB(String driver, String connectionPath) {
    this.connect(driver, connectionPath);
    Iterator<String> iter = this.parsedResult.iterator();
    try {
      this.preparedStatement = this.connection.prepareStatement(
          "INSERT INTO mcas_score VALUES (?,?,?,?,?,?,?)");
      while (iter.hasNext()) {
        this.resultSet = statement.executeQuery(
            "SELECT * FROM school WHERE school_name = '" + iter.next() + "'");
        if (this.resultSet.next()) {
          this.preparedStatement.setString(1, this.resultSet.getString("school_id"));
          for (int i = 2; i < 8; i ++) {
            this.preparedStatement.setString(i, iter.next());
          }
          this.preparedStatement.executeUpdate();
        }
      }
    } catch (SQLException e) {
      throw new IllegalStateException(e.getMessage());
    }
    this.closeConnection();
  }
}

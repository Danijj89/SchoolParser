package parser.projects.schools;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.SQLException;
import java.util.Iterator;

/**
 * Represents an implementation of a {@link AbstractSchoolsParser} used to parse the school names
 * for the schools database project.
 */
public class SchoolNameParser extends AbstractSchoolsParser {

  /**
   * Parses the school names for the schools database project.
   * This parser eliminates the school name that are repeated and the rows that contain the voice
   * 'District Results'.
   * Each row in the result contain: DistrictName, SchoolName, SchoolType(Charter or Public).
   *
   * @throws IllegalStateException if the input or output has problems.
   */
  @Override
  protected void helpParse() throws IllegalStateException {
    String line;
    try {
      line = this.reader.readLine();
      while (line != null) {
        String[] values = line.split(",");
        // Skip the rows that have the voice 'District Results'
        if (!values[1].equals("District Results")) {
          this.parsedResult.add(values[0]);
          this.parsedResult.add(values[1]);
          // Determine if it is a Charter or Public school
          if (values[1].contains("Charter")) {
            this.parsedResult.add("charter");
          }
          else {
            this.parsedResult.add("public");
          }
        }
        line = this.reader.readLine();
      }
    } catch (IOException e) {
      throw new IllegalStateException("I/O error");
    }
  }

  /**
   * Saves each school name with its district name and school type into a new row.
   *
   * @param path the file path to save the file to.
   * @throws IllegalStateException if it is unable to write the file.
   */
  @Override
  public void save(String path) throws IllegalStateException {
    try {
      Writer fw = new FileWriter(path);
      String[] data = this.parsedResult.toArray(new String[this.parsedResult.size()]);
      for (int i = 0; i < data.length; i += 3) {
        fw.write(data[i] + ", ");
        fw.write(data[i + 1]);
        fw.write(", " + data[i + 2] + "\n");
      }
      fw.flush();
    } catch (IOException e) {
      throw new IllegalStateException("Unable to write to file");
    }
  }

  @Override
  public void updateDB(String driver, String connectionPath) {
    this.connect(driver, connectionPath);
    Iterator<String> iter = this.parsedResult.iterator();
    try {
      this.preparedStatement = this.connection.prepareStatement(
          "INSERT INTO school(district_id, school_name, school_type) VALUES (?,?,?)");
      while (iter.hasNext()) {
        this.resultSet = this.statement.executeQuery(
            "SELECT * FROM district WHERE district_name = '" + iter.next() + "'");
        if (resultSet.next()) {
          this.preparedStatement.setString(1, this.resultSet.getString("district_id"));
          this.preparedStatement.setString(2, iter.next());
          this.preparedStatement.setString(3, iter.next());
          this.preparedStatement.executeUpdate();
        }
      }
    } catch (SQLException e) {
      throw new IllegalStateException(e.getMessage());
    }
    this.closeConnection();
  }
}


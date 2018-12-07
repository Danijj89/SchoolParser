package parser.projects.schools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import parser.MySQLCParser;

/**
 * Represents an abstract class for a {@link MySQLCParser} implementation used to parse
 * data for the schools database project.
 * Uses a {@link BufferedReader} to read from a text file.
 */
public abstract class AbstractSchoolsParser implements MySQLCParser {
  protected BufferedReader reader;
  protected final List<String> parsedResult;
  protected Connection connection = null;
  protected Statement statement = null;
  protected PreparedStatement preparedStatement = null;
  protected ResultSet resultSet = null;

  /**
   * Represents the {@code super} constructor of this parser.
   * Initializes the reader to be temporarily {@code null}.
   */
  public AbstractSchoolsParser() throws IllegalArgumentException {
    this.reader = null;
    this.parsedResult = new ArrayList<>();
  }

  @Override
  public void save(String path) {
    try {
      Writer fw = new FileWriter(path);
      Iterator<String> iter = this.parsedResult.iterator();
      int numValuesForRow = this.numRowValues();
      while (iter.hasNext()) {
        List<String> tempResult = new ArrayList<>();
        for (int i = 0; i < numValuesForRow; i += 1) {
          tempResult.add(iter.next());
        }
        fw.write(tempResult.stream().collect(Collectors.joining(", ")) + "\n");
      }
      fw.flush();
    } catch (IOException e) {
      throw new IllegalStateException ("Unable to write to file");
    } catch (IndexOutOfBoundsException e) {
      throw new IllegalArgumentException("Parsed file has incorrect row lengths");
    }
  }

  /**
   * Retuns the number of values for each row after the file has been parsed.
   *
   * @return the number of values for each row.
   */
  protected abstract int numRowValues();

  @Override
  public void parse(String fileName) throws IllegalStateException {
    this.setFileReader(fileName);
    this.helpParse();
  }

  /**
   * Helper method that sets the {@link Readable} of this class to read from a file.
   *
   * @param fileName the file to read from.
   * @throws IllegalStateException if it is unable to read from the file.
   */
  private void setFileReader(String fileName) throws IllegalStateException {
    try {
      this.reader = new BufferedReader(new FileReader(fileName));
    } catch (IOException e) {
      throw new IllegalStateException("Reader unable to read from the file");
    }
  }

  /**
   * Helper method that parses a file into a list of individual values.
   */
  protected abstract void helpParse();

  @Override
  public abstract void updateDB(String driver, String connectionPath);

  /**
   * Setup connection with the database.
   *
   * @param driver the driver used to connect.
   * @param connectionPath the connection path to the database.
   * @throws IllegalStateException if it is unable to connect.
   */
  protected void connect(String driver, String connectionPath) throws IllegalStateException {
    try {
      // Setup the driver
      Class.forName(driver).newInstance();

      // Setup the connection with the DB
      this.connection = DriverManager.getConnection(connectionPath);

      // Statements allow to issue SQL queries to the database
      this.statement = this.connection.createStatement();

    } catch (Exception e) {
      throw new IllegalStateException(e.getMessage());
    }
  }

  /**
   * Closes all parts of the connection if they are open.
   *
   * @throws IllegalStateException should not happen.
   */
  protected void closeConnection() throws IllegalStateException {
    try {
      if (this.resultSet != null) {
        this.resultSet.close();
      }
      if (this.statement != null) {
        this.statement.close();
      }
      if (this.connection != null) {
        this.connection.close();
      }
    } catch (SQLException e) {
      throw new IllegalStateException("Closed them all: Should never happen");
    }
  }
}

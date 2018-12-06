package parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Represents an abstract class for a {@link MySQLCParser} implementation used to parse
 * data for the schools database project.
 * Uses a {@link FileWriter} to save the result of the parsing.
 * Uses a {@link BufferedReader} to read from a text file.
 */
public abstract class AbstractSchoolsParser implements MySQLCParser {
  protected FileWriter fw;
  protected BufferedReader reader;
  protected Connection connection = null;
  protected Statement statement = null;
  protected PreparedStatement preparedStatement = null;
  protected ResultSet resultSet = null;

  /**
   * Represents the {@code super} constructor of this parser.
   * Initializes the reader to be temporarily {@code null}.
   */
  public AbstractSchoolsParser() throws IllegalArgumentException {
    this.fw = null;
    this.reader = null;
  }

  @Override
  public void save(String path) {
    try {
      this.fw = new FileWriter(path);
      fw.flush();
    } catch (IOException e) {
      throw new IllegalStateException("Unable to append to this Appendable");
    }
  }

  @Override
  public List<String> parse(String fileName) throws IllegalStateException {
    this.setFileReader(fileName);
    return this.helpParse();
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
   *
   * @return the list of individual values.
   */
  protected abstract List<String> helpParse();

  @Override
  public abstract List<String> preparedStatement();

  @Override
  public abstract List<Integer> numVariableToSet();

  /**
   * Setup connection with the database.
   *
   * @param driver the driver used to connect.
   * @param connectionPath the connection path to the database.
   */
  protected void connect(String driver, String connectionPath) {
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
   */
  protected void closeConnection() {
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

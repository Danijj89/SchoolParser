package parser;

import java.util.List;

/**
 * Represents an interface that extends the functionality of a parser to update a Database.
 */
public interface MySQLCParser extends Parser {

  /**
   * Execute the updates to a database with the data parsed.
   * NOTE:
   * A file needs to be parsed before any data can be inserted.
   * If no file is parsed before this method call, no updates will be performed.
   *
   * @param driver the driver used by the connection.
   * @param connectionPath the path to the database.
   */
  void updateDB(String driver, String connectionPath);
}

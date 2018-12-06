package parser;

import java.util.List;

/**
 * Represents an interface to parse files in order to update an sql database.
 * Provide functionalities to hold prepared statements used for updating the database and
 * to return the number of variables that need to be set in each of those statements.
 * INVARIANTS:
 * (i) The length of the list returned by {@code preparedStatement} is the same as the one returned
 * from {@code numVariableToSet}.
 * (ii) Each index in {@code numVariableToSet} refers exactly to the number of variables that
 * the statement in the same index in {@code preparedStatement} has to set.
 */
public interface MySQLCParser extends Parser {

  /**
   * Returns a list of sql command to be executed.
   *
   * @return the list of sql command.
   */
  List<String> preparedStatement();

  /**
   * Returns a list of number of variables that are needed to be set for each
   * prepared statement.
   *
   * @return the list of number of variables to be set.
   */
  List<Integer> numVariableToSet();
}

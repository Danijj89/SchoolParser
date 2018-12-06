package parser;

import java.util.List;

/**
 * Represents a file parser interface.
 * Provides the functionality to save the file that has been parsed.
 */
public interface Parser {

  /**
   * Parses a file into a list of individual values.
   *
   * @param fileName the path to the file to be parsed.
   * @return a list of individual values.
   * @throws IllegalStateException if it is unable to parse.
   */
  List<String> parse(String fileName) throws IllegalStateException;

  /**
   * Saves the parsed data to file.
   *
   * @param path the path of the file.
   */
  void save(String path);
}

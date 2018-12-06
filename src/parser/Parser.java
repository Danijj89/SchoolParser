package parser;

/**
 * Represents a file parser interface.
 * Provides the functionality to save the file that has been parsed.
 */
public interface Parser {

  /**
   * Parses a file.
   *
   * @param fileName the path to the file to be parsed.
   * @throws IllegalStateException if it is unable to parse.
   */
  void parse(String fileName) throws IllegalStateException;

  /**
   * Saves the parsed data to file.
   * If the file is not parsed first, this methods saves an empty file.
   *
   * @param path the path of the file.
   */
  void save(String path);
}

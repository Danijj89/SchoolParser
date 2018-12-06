package parser.projects.schools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import parser.AbstractSchoolsParser;

/**
 * Represents an implementation of a {@link AbstractSchoolsParser} used to parse the school names
 * for the schools database project.
 */
public class SchoolNameParser extends AbstractSchoolsParser {

  /**
   * Constructs an instance of this parser.
   *
   * @param ap the appendable to append to.
   * @throws IllegalArgumentException if the appendable is null.
   */
  public SchoolNameParser(Appendable ap) throws IllegalArgumentException {
    super(ap);
  }

  /**
   * Parses the school names for the schools database project.
   * This parser eliminates the school name that are repeated and the rows that contain the voice
   * 'District Results'.
   * Each row in the result contain: DistrictName, SchoolName, SchoolType(Charter or Public).
   *
   * @return the list of school names.
   * @throws IllegalStateException if the input or output has problems.
   */
  @Override
  protected List<String> helpParse() {
    List<String> result = new ArrayList<>();
    String line;
    try {
      line = this.reader.readLine();
      while (line != null) {
        String[] values = line.split(",");
        // Skip the rows that have the voice 'District Results'
        if (!values[1].equals("District Results")) {
          result.add(values[0]);
          result.add(values[1]);
          this.ap.append(values[0] + ", " + values[1]);
          // Determine if it is a Charter or Public school
          if (values[1].contains("Charter")) {
            result.add("charter");
            this.ap.append(", charter\n");
          }
          else {
            result.add("public");
            this.ap.append(", public\n");
          }
        }
        line = this.reader.readLine();
      }
    } catch (IOException e) {
      throw new IllegalStateException("I/O error");
    }
    return result;
  }

  /**
   * Gets the list of statements that are used to  to populate the database.
   * Statements by index:
   * (0) finds the districtid by matching a given district name.
   * (1) inserts school names, district id and school type into the school table.
   *
   * @return the list of statements.
   */
  @Override
  public List<String> preparedStatement() {
    List<String> result = new ArrayList<>();
    result.add("SELECT * FROM district WHERE district_name = ?");
    result.add("INSERT INTO school(district_id, school_name, school_type) VALUES (?,?,?)");
    return result;
  }

  @Override
  public List<Integer> numVariableToSet() {
    List<Integer> result = new ArrayList<>();
    result.add(1);
    result.add(3);
    return result;
  }
}

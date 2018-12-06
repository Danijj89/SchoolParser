package connector;

import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.sql.PreparedStatement;
import java.util.function.Function;
import parser.AbstractSchoolsParser;
import parser.projects.schools.DistrictNameParser;

public class DistrictNames {

  public static void main(String[] args) {
    HashMap<String, Function<Appendable, AbstractSchoolsParser>> parsers = new HashMap<>();
    parsers.put("district_name", (ap) -> new DistrictNameParser(ap));
    Connection connect = null;
    Statement statement = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    try {
      // Setup the driver
      Class.forName("com.mysql.cj.jdbc.Driver").newInstance();

      // Setup the connection with the DB
      connect = DriverManager.getConnection(
          "jdbc:mysql://localhost:3306/schools?user=root&password=sesame");

      // Statements allow to issue SQL queries to the database
      statement = connect.createStatement();

      List<String> values = Arrays.asList(args);
      Iterator<String> iter = values.iterator();

      String toBeParsedFile = null;
      Appendable ap = System.out;
      String parser = null;

      while (iter.hasNext()) {
        String command = iter.next();
        if (iter.hasNext()) {
          switch (command) {
            case "-in":
              toBeParsedFile = iter.next();
              break;
            case "-out":
              ap = new FileWriter(iter.next());
              break;
            case "-parser":
              parser = iter.next();
              break;
            default:
              throw new IllegalArgumentException("Bad inputs");
          }
        }
      }
      if (!(toBeParsedFile == null || parser == null)) {
        AbstractSchoolsParser p = parsers.get(parser).apply(ap);
        List<String> data = p.parse(toBeParsedFile);
        if (ap instanceof FileWriter) {
          ((FileWriter) ap).flush();
        }
        Iterator<String> iter2 = data.iterator();
        preparedStatement = connect.prepareStatement(p.preparedStatement());
        while (iter2.hasNext()) {
          for (int i = 0; i < p.numVariableToSet(); i ++) {
            preparedStatement.setString(i + 1, iter2.next());
          }
          preparedStatement.executeUpdate();
        }
      }
      else {
        throw new IllegalArgumentException("Given no file to be parsed");
      }
    } catch (Exception e) {
      throw new IllegalArgumentException(e.getMessage());
    } finally {
      try {
        if (resultSet != null) {
          resultSet.close();
        }

        if (statement != null) {
          statement.close();
        }

        if (connect != null) {
          connect.close();
        }
      } catch (Exception e) {
        throw new IllegalStateException("Closed them all: Should never happen");
      }
    }
  }
}

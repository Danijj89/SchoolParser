package parser;

import java.util.HashMap;
import java.util.Map;
import parser.projects.schools.DistrictNameParser;
import parser.projects.schools.SchoolNameParser;

public class SchoolsParser {

  public static void main(String[] args) {
    Map<String, MySQLCParser> parsers = new HashMap<>();
    parsers.put("district_name", new DistrictNameParser());
    parsers.put("school_name", new SchoolNameParser());
    Map<String, String> arguments = new HashMap<>();
    if (Math.floorMod(args.length, 2) != 0) {
      throw new IllegalArgumentException("Ill formed command");
    }
    for (int i = 0; i < args.length; i += 2) {
      arguments.put(args[i], args[i + 1]);
    }
    if (!arguments.containsKey("-parser") || arguments.get("-parser") == null
        || !arguments.containsKey("-in") || arguments.get("-in") == null) {
      throw new IllegalArgumentException("Missing a parser/file to be parsed");
    }
    String parserType = arguments.get("-parser");
    if (!parsers.containsKey(parserType)) {
      throw new IllegalArgumentException("Given parser does not exist");
    }
    MySQLCParser parser = parsers.get(parserType);
    parser.parse(arguments.get("-in"));
    parser.updateDB("com.mysql.cj.jdbc.Driver",
        "jdbc:mysql://localhost:3306/schools?user=root&password=sesame");
    if (arguments.containsKey("-out") && arguments.get("-out") != null) {
      parser.save(arguments.get("-out"));
    }
  }
}

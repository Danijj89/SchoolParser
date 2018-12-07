package parser.projects.schools;

import java.util.HashMap;
import java.util.Map;
import parser.DropoutByRaceParser;
import parser.MySQLCParser;

public class SchoolsParser {

  public static void main(String[] args) {
    Map<String, MySQLCParser> parsers = new HashMap<>();
    parsers.put("district_name", new DistrictNameParser());
    parsers.put("school_name", new SchoolNameParser());
    parsers.put("mcas_score2013", new MCASScoreParser(2013));
    parsers.put("mcas_score2014", new MCASScoreParser(2014));
    parsers.put("mcas_score2015", new MCASScoreParser(2015));
    parsers.put("mcas_score2016", new MCASScoreParser(2016));
    parsers.put("mcas_score2017", new MCASScoreParser(2017));
    parsers.put("lat_long", new SchoolLatLongParser());
    parsers.put("enrollment2013", new EnrollmentByRaceParser(2013));
    parsers.put("enrollment2014", new EnrollmentByRaceParser(2014));
    parsers.put("enrollment2015", new EnrollmentByRaceParser(2015));
    parsers.put("enrollment2016", new EnrollmentByRaceParser(2016));
    parsers.put("dropout2013", new DropoutByRaceParser(2013));
    parsers.put("dropout2014", new DropoutByRaceParser(2014));
    parsers.put("dropout2015", new DropoutByRaceParser(2015));
    parsers.put("dropout2016", new DropoutByRaceParser(2016));

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

package org.teeschke.kicktipp.timeseries.group.scrape;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.teeschke.kicktipp.timeseries.group.Group;
import org.teeschke.kicktipp.timeseries.group.Match;

import java.util.Arrays;

public class KicktippScraper {

  private final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");

  public Group scrapeGroup(String groupName){
    Group group = new Group();
    group.orderedUsernames = Arrays.asList("dante", "Dirle", "Benni");
    group.orderedMatches = Arrays.asList(
        createMatch1(),
        createMatch2(),
        createMatch3(),
        createMatch4()
    );
    return group;
  }

  private Match createMatch4() {
    Match match = new Match();
    match.kickoffTime = FORMATTER.parseDateTime("2016-06-14 18:00");
    match.teamA = "AUT";
    match.teamB = "HUN";
    match.orderedPlayerPoints = Arrays.asList(0, 0, 0);
    return match;
  }

  private Match createMatch3() {
    Match match = new Match();
    match.kickoffTime = FORMATTER.parseDateTime("2016-06-13 21:00");
    match.teamA = "BEL";
    match.teamB = "ITA";
    match.orderedPlayerPoints = Arrays.asList(0, 4, 2);
    return match;
  }

  private Match createMatch2() {
    Match match = new Match();
    match.kickoffTime = FORMATTER.parseDateTime("2016-06-13 18:00");
    match.teamA = "IRL";
    match.teamB = "SWE";
    match.orderedPlayerPoints = Arrays.asList(0, 4, 4);
    return match;
  }

  private Match createMatch1() {
    Match match = new Match();
    match.kickoffTime = FORMATTER.parseDateTime("2016-06-13 15:00");
    match.teamA = "ESP";
    match.teamB = "CZE";
    match.orderedPlayerPoints = Arrays.asList(2, 0, 2);
    return match;
  }

}

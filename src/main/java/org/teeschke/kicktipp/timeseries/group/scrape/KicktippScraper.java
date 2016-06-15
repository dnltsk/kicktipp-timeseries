package org.teeschke.kicktipp.timeseries.group.scrape;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.teeschke.kicktipp.timeseries.group.Group;
import org.teeschke.kicktipp.timeseries.group.Match;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KicktippScraper {

  private final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");

  public Group scrapeGroup(String groupName) {
    Group group = new Group();
    group.orderedUsernames = Arrays.asList("dante", "Dirle", "Benni");
    ArrayList<Match> orderedMatches = new ArrayList<>();
    orderedMatches.add(createMatch1());
    orderedMatches.add(createMatch2());
    orderedMatches.add(createMatch3());
    orderedMatches.add(createMatch4());
    group.orderedMatches = calcIntegrals(orderedMatches);
    return group;
  }

  private ArrayList<Match> calcIntegrals(ArrayList<Match> orderedMatches) {
    for (int i = 1; i < orderedMatches.size(); i++) {
      List<Integer> playerPointsBefore = orderedMatches.get(i - 1).orderedPlayerPoints;
      List<Integer> playerPoints = orderedMatches.get(i).orderedPlayerPoints;
      for (int j = 0; j < playerPoints.size(); j++) {
        Integer integral =  playerPoints.get(j) + playerPointsBefore.get(j);
        playerPoints.set(j, integral);
      }
    }
    return orderedMatches;
  }

  private Match createMatch4() {
    Match match = new Match();
    match.kickoffTime = FORMATTER.parseDateTime("2016-06-14 18:00");
    match.teamA = "AUT";
    match.teamB = "HUN";
    match.orderedPlayerPoints = Arrays.asList(2, 4, 2);
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

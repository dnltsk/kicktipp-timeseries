package org.teeschke.kicktipp.timeseries.kicktipp.scrape;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.teeschke.kicktipp.timeseries.kicktipp.Group;
import org.teeschke.kicktipp.timeseries.kicktipp.Match;

import java.util.ArrayList;
import java.util.Collections;

@Service
public class KicktippGroupScraper {

  private final DateTimeFormatter KICKTIPP_TIME_FORMAT = DateTimeFormat.forPattern("dd/MM/yy HH:mm");
  private final int MATCH_TABLE_INDEX = 0;
  private final int SCORE_TABLE_INDEX = 1;

  public Group scrapeGroup(Document doc) {
    Group group = new Group();
    group.orderedUsernames = scrapeOrderedUsernames(doc);
    group.orderedMatches = scrapeOrderedMatches(doc);
    group.orderedScores = scrapeOrderedScores(doc, group.orderedMatches.size());
    return orderGroupByUsername(group);
  }

  private Group orderGroupByUsername(Group group) {
    ArrayList<String> newOrderedUsernames = new ArrayList<> (group.orderedUsernames);
    Collections.sort(newOrderedUsernames);

    ArrayList<ArrayList<Double>> newOrderedScores = new ArrayList<> ();
    for(String alphabeticalOrderedUsername : newOrderedUsernames){
      int index = group.orderedUsernames.indexOf(alphabeticalOrderedUsername);
      newOrderedScores.add(group.orderedScores.get(index));
    }
    group.orderedUsernames = newOrderedUsernames;
    group.orderedScores = newOrderedScores;
    return group;
  }

  private ArrayList<ArrayList<Double>> scrapeOrderedScores(Document doc, int numberOfMatches) {

    ArrayList<ArrayList<Double>> orderedScores = new ArrayList<>();

    Elements tables = doc.select("table.nw.kicktipp-tabs");
    Element memberTable = tables.get(SCORE_TABLE_INDEX);
    Integer numberOfPointWinners = memberTable.select("tr.sptsieger").size();
    Elements memberRows= memberTable.select("tr.o, tr.e");
    for(Element memberRow : memberRows){
      ArrayList<Double> memberScores = parseScoresFromMemberRow(numberOfMatches, memberRow);
      memberScores.add(parseBonusScore(memberRow, numberOfPointWinners));
      orderedScores.add(memberScores);
    }
    return orderedScores;
  }

  private Double parseBonusScore(Element memberRow, Integer numberOfPointWinners) {
    if(memberRow.hasClass("sptsieger")){
      return 1.0 / numberOfPointWinners.floatValue();
    }
    return 0.0;
  }

  private ArrayList<Double> parseScoresFromMemberRow(int numberOfMatches, Element memberRow) {
    Elements memberCols = memberRow.select("td");
    ArrayList<Double> memberScoresList = new ArrayList<>();
    int columnOffset = 3;
    for (int i = columnOffset; i < columnOffset+numberOfMatches-1; i++) {
      Elements memberScore = memberCols.get(i).select("sub.p");
      if(memberScore.isEmpty()){
        memberScoresList.add(0.0);
        continue;
      }
      String scoreText = memberScore.get(0).text();
      if (scoreText.trim().isEmpty()) {
        memberScoresList.add(0.0);
      }else{
        memberScoresList.add(Double.parseDouble(scoreText.trim()));
      }
    }
    return memberScoresList;
  }

  private ArrayList<Match> scrapeOrderedMatches(Document doc) {
    Elements tables = doc.select("table.nw.kicktipp-tabs");
    Element matchTable = tables.get(MATCH_TABLE_INDEX);
    Elements matchRows= matchTable.select("tr.o, tr.e");
    ArrayList<Match> matches = new ArrayList<>();
    for(Element matchRow : matchRows){
      Match match = new Match();
      Elements matchCols = matchRow.select("td");
      match.kickoffTime = DateTime.parse(matchCols.get(0).text(), KICKTIPP_TIME_FORMAT);
      match.title = scrapeTitle(matchCols);
      matches.add(match);
    }
    matches.add(createEmptyBonusMatch());
    return matches;
  }

  private Match createEmptyBonusMatch() {
    Match emptyBonusMatch = new Match();
    emptyBonusMatch.title = "Winner-Of-The-Day Bonus";
    return emptyBonusMatch;
  }

  private String scrapeTitle(Elements matchCols) {
    String teamA = matchCols.get(1).text();
    String teamB = matchCols.get(2).text();
    String result = matchCols.get(4).text();
    return teamA + "-" + teamB;
  }

  private ArrayList<String> scrapeOrderedUsernames(Document doc) {
    Elements usernameCells = doc.select(".mg_class");
    ArrayList<String> usernames = new ArrayList<>();
    for(Element usernameCell : usernameCells){
      usernames.add(usernameCell.text());
    }
    return usernames;
  }



}

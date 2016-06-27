package org.teeschke.kicktipp.timeseries.scrape;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.teeschke.kicktipp.timeseries.Group;
import org.teeschke.kicktipp.timeseries.Match;

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
    group.orderedMatches = scrapeOrderedMatchesAbbreviations(doc);
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

  private ArrayList<Match> scrapeOrderedMatchesAbbreviations(Document doc) {
    Elements tables = doc.select("table.nw.kicktipp-tabs");
    Element matchTable = tables.get(SCORE_TABLE_INDEX);
    Elements matchAbbreviationRows= matchTable.select("thead tr");
    Element rowA = matchAbbreviationRows.get(0);
    Element rowB = matchAbbreviationRows.get(2);
    Elements abbreviationsA = rowA.select(".nw acronym");
    Elements abbreviationsB = rowB.select(".nw acronym");

    ArrayList<Match> matches = new ArrayList<>();
    for(int i=0; i<abbreviationsA.size(); i++){
      Match match = new Match();
      String abbreviationA = abbreviationsA.get(i).text();
      String abbreviationB = abbreviationsB.get(i).text();
      abbreviationA = replaceUnknown(abbreviationA);
      abbreviationB = replaceUnknown(abbreviationB);
      match.title = abbreviationA + " - " + abbreviationB;
      matches.add(match);
    }
    matches.add(createEmptyBonusMatch());
    return matches;
  }

  private Match createEmptyBonusMatch() {
    Match emptyBonusMatch = new Match();
    emptyBonusMatch.title = "Day Bonus";
    return emptyBonusMatch;
  }

  private String scrapeTitle(Elements matchCols) {
    String teamA = matchCols.get(1).text();
    String teamB = matchCols.get(2).text();
    String result = matchCols.get(4).text();
    teamA = replaceUnknown(teamA);
    teamB = replaceUnknown(teamB);
    return teamA + " - " + teamB;
  }

  private String replaceUnknown(String teamB) {
    return teamB.replaceAll("unknown", "/").replaceAll("---", "/");
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

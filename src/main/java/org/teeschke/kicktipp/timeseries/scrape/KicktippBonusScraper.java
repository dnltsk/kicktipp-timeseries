package org.teeschke.kicktipp.timeseries.scrape;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.teeschke.kicktipp.timeseries.Group;
import org.teeschke.kicktipp.timeseries.Match;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;

@Service
public class KicktippBonusScraper {

  private final DateTimeFormatter KICKTIPP_TIME_FORMAT = DateTimeFormat.forPattern("dd/MM/yy HH:mm");
  private final int MATCH_TABLE_INDEX = 0;
  private final int SCORE_TABLE_INDEX = 1;

  public Group scrapeGroupBonus(Document doc) throws MalformedURLException {
    Group groupBonus = new Group();
    groupBonus.orderedUsernames = scrapeOrderedUsernames(doc);
    groupBonus.orderedMatches = scrapeOrderedBonusMatches(doc);
    groupBonus.orderedScores = scrapeOrderedBonusScores(doc, groupBonus.orderedMatches.size());
    return orderGroupByUsername(groupBonus);
  }

  private ArrayList<String> scrapeOrderedUsernames(Document doc) {
    Elements usernameCells = doc.select(".mg_class");
    ArrayList<String> usernames = new ArrayList<>();
    for(Element usernameCell : usernameCells){
      usernames.add(usernameCell.text());
    }
    return usernames;
  }

  private ArrayList<Match> scrapeOrderedBonusMatches(Document doc) {
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
      addThreeExtraSemiFinalMatches(matches, match);
    }
    matches.add(createEmptyBonusMatch());
    return matches;
  }

  private void addThreeExtraSemiFinalMatches(ArrayList<Match> matches, Match match) {
    if(match.title.equals("SF Bonus")){
      matches.add(match);
      matches.add(match);
      matches.add(match);
    }
  }

  private Match createEmptyBonusMatch() {
    Match emptyBonusMatch = new Match();
    emptyBonusMatch.title = "Winner-Of-The-Bonus Bonus";
    return emptyBonusMatch;
  }

  private ArrayList<ArrayList<Double>> scrapeOrderedBonusScores(Document doc, int numberOfMatches) {
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

  private String scrapeTitle(Elements matchCols) {
    String abbreviation = matchCols.get(2).text()+" Bonus";
    return abbreviation;
  }

  private Double parseBonusScore(Element memberRow, Integer numberOfPointWinners) {
    if(memberRow.hasClass("sptsieger")){
      return 1.0 / numberOfPointWinners.floatValue();
    }
    return 0.0;
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
}

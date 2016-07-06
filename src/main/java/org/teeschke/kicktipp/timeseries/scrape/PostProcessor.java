package org.teeschke.kicktipp.timeseries.scrape;

import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class PostProcessor {

  public ArrayList<ArrayList<Double>> calcIntegrals(ArrayList<ArrayList<Double>> orderedScores) {
    for(ArrayList<Double> memberScores : orderedScores){
      for (int i = 1; i < memberScores.size(); i++) {
        Double integral =  memberScores.get(i-1) + memberScores.get(i);
        memberScores.set(i, integral);
      }
    }
    return orderedScores;
  }

  public ArrayList<ArrayList<Integer>> calcPositions(ArrayList<ArrayList<Double>> orderedScores) {
    ArrayList<ArrayList<Integer>> orderedPositions = new ArrayList<>();
    for (int memberIndex = 0; memberIndex < orderedScores.size(); memberIndex++) {
      ArrayList<Double> memberScores = orderedScores.get(memberIndex);
      ArrayList<Integer> newMemberPositions = new ArrayList<>();
      for (int matchIndex = 1; matchIndex < memberScores.size(); matchIndex++) {
        Double memberScore = memberScores.get(matchIndex);
        Integer position = 1 + countMembersWithHigherScore(orderedScores, matchIndex, memberScore);
        newMemberPositions.add(position);
      }
      orderedPositions.add(newMemberPositions);
    }
    return orderedPositions;
  }

  private int countMembersWithHigherScore(ArrayList<ArrayList<Double>> orderedScores, int matchIndex, Double memberScore) {
    int count = 0;
    for(ArrayList<Double> otherMemberScores : orderedScores){
      if(otherMemberScores.get(matchIndex) > memberScore){
        count++;
      }
    }
    return count;
  }
}

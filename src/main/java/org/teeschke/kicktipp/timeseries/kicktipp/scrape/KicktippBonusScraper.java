package org.teeschke.kicktipp.timeseries.kicktipp.scrape;

import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import org.teeschke.kicktipp.timeseries.kicktipp.Group;

import java.net.MalformedURLException;
import java.util.ArrayList;

@Service
public class KicktippBonusScraper {

  public Group scrapeGroupBonus(Document doc) throws MalformedURLException {
    Group groupBonus = new Group();
    groupBonus.orderedScores = scrapeOrderedBonusScores(doc);
    return groupBonus;
  }

  private ArrayList<ArrayList<Double>> scrapeOrderedBonusScores(Document doc) {
    return null;
  }
}

package org.teeschke.kicktipp.timeseries.scrape;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.teeschke.kicktipp.timeseries.Group;
import org.teeschke.kicktipp.timeseries.utils.GroupNotFoundException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Logger;

@Service
public class KicktippScraper {

  private static final Logger LOG = Logger.getLogger(KicktippScraper.class.getName());

  private final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");

  @Autowired
  private KicktippGroupScraper groupScraper;

  @Autowired
  private KicktippBonusScraper bonusScraper;

  @Autowired
  private KicktippLinkScraper linkScraper;

  @Autowired
  private PostProcessor postProcessor;

  public Group scrapeWholeGroupTimeseries(String groupName) throws IOException, GroupNotFoundException {

    /* GENERAL */
    URL linkToNextPage = getLinkFirstPageUrl(groupName);
    ArrayList<Group> groups = new ArrayList<>();
    while(linkToNextPage != null){
      LOG.info("scraping "+linkToNextPage);
      KicktippPage page = scrapeGroupTimeseries(linkToNextPage, groupName);
      groups.add(page.group);
      linkToNextPage = page.linkToNextPage;
    }

    /* BONUS */
    String linkToBonusPage = "https://www.kicktipp.com/" + groupName + "/tippuebersicht?sortBy=GESAMTPUNKTE&wertung=einzelwertung&teilnehmerSucheName=&rankingGruppeId=0&tippspieltagIndex=0&tippspieltagIndexTippspieltageTab=5&language=en_GB";
    Document doc = Jsoup.connect(linkToBonusPage.toString()).get();
    Group orderedGroupBonus = bonusScraper.scrapeGroupBonus(doc);
    groups.add(orderedGroupBonus);

    /* MERGE */
    Group mergedGroup = mergeGroups(groups);


    mergedGroup.orderedScores = postProcessor.calcIntegrals(mergedGroup.orderedScores);
    mergedGroup.orderedPositions = postProcessor.calcPositions(mergedGroup.orderedScores);
    return mergedGroup;
  }

  private KicktippPage scrapeGroupTimeseries(URL linkToFirstPage, String groupName) throws IOException, GroupNotFoundException {
    Document doc;
    try {
      doc = Jsoup.connect(linkToFirstPage.toString()).get();
    }catch(HttpStatusException e){
      throw new GroupNotFoundException(groupName);
    }
    KicktippPage page = new KicktippPage();
    page.group = groupScraper.scrapeGroup(doc);
    page.linkToNextPage = linkScraper.scrapeLinkToNextPage(doc, groupName);
    page.linkToPage = linkToFirstPage;
    return page;
  }

  private Group mergeGroups(ArrayList<Group> groups) {
    Group mergedIntegrals = new Group();
    mergedIntegrals.orderedUsernames = groups.get(0).orderedUsernames;
    mergedIntegrals.orderedMatches = new ArrayList<>();
    /* init empty list */
    mergedIntegrals.orderedScores = new ArrayList<>();
    for(int i=0; i<mergedIntegrals.orderedUsernames.size(); i++){
      mergedIntegrals.orderedScores.add(new ArrayList<>());
    }
    /* fill list */
    for(Group group : groups){
      for(int i=0; i<mergedIntegrals.orderedScores.size(); i++){
        mergedIntegrals.orderedScores.get(i).addAll(group.orderedScores.get(i));
      }
      mergedIntegrals.orderedMatches.addAll(group.orderedMatches);
    }
    return mergedIntegrals;
  }

  private URL getLinkFirstPageUrl(String groupName) throws MalformedURLException {
    return new URL("https://www.kicktipp.com/"+groupName+"/tippuebersicht?language=en_GB&rankingGruppeId=0&sortBy=gesamtpunkte&teilnehmerSucheName=&wertung=einzelwertung&tippspieltagIndex=1");
  }

}

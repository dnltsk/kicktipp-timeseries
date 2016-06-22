package org.teeschke.kicktipp.timeseries.kicktipp.scrape;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;

@Service
public class KicktippLinkScraper {

  private final String KICKTIPP_BASE_PATH = "http://www.kicktipp.com/";

  public URL scrapeLinkToNextPage(Document doc, String groupName) throws MalformedURLException {
    Elements nextLinks = doc.select(".kicktipp-prevnext-next a");
    if(nextLinks.isEmpty()){
      return null;
    }
    URL nextLink = new URL(KICKTIPP_BASE_PATH + groupName + "/" + nextLinks.first().attr("href"));
    return appendLanguage(nextLink);
  }

  public URL scrapeLinkToBonusPage(Document doc, String groupName) throws MalformedURLException {
    Elements tabNaviItems = doc.select("ul.tabs-nav li");
    if (tabNaviItems.isEmpty()) {
      return null;
    }
    if (tabNaviItems.last().text().trim().equalsIgnoreCase("Bonus")) {
      URL url = new URL(KICKTIPP_BASE_PATH + groupName + "/" + tabNaviItems.last().select("a").attr("href"));
      return appendLanguage(url);
    }
    return null;
  }

  private URL appendLanguage(URL someKicktippLink) throws MalformedURLException {
    if(!someKicktippLink.getQuery().contains("language=")){
      return new URL(someKicktippLink.toString() + "&language=en_GB");
    }
    return someKicktippLink;
  }

}

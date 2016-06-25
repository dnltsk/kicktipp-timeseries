package org.teeschke.kicktipp.timeseries.scrape;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class KicktippLinkScraperTest {

  private KicktippLinkScraper linkScraper;

  private Document METEOGOAL_FIRST_PAGE;

  @BeforeClass
  private void initGroupScraper() throws IOException {
    linkScraper = new KicktippLinkScraper();
  }

  @BeforeClass
  private void readTestFiles() throws IOException {
    METEOGOAL_FIRST_PAGE = Jsoup.parse(
        Paths.get("src", "test", "resources", "METEOGOAL_FIRST_PAGE.html").toFile(), "UTF-8");
  }

  @Test
  public void next_link_can_be_found() throws Exception {
    URL url = linkScraper.scrapeLinkToNextPage(METEOGOAL_FIRST_PAGE, "dummy");
    assertThat(url.toString()).isEqualTo("http://www.kicktipp.com/dummy/tippuebersicht?rankingGruppeId=0&teilnehmerSucheName=&wertung=einzelwertung&sortBy=gesamtpunkte&tippspieltagIndex=2&language=en_GB");
  }

  @Test
  public void bonus_link_can_be_found() throws Exception {
    URL url = linkScraper.scrapeLinkToBonusPage(METEOGOAL_FIRST_PAGE, "dummy");
    assertThat(url.toString()).isEqualTo("http://www.kicktipp.com/dummy/tippuebersicht?sortBy=GESAMTPUNKTE&wertung=einzelwertung&teilnehmerSucheName=&rankingGruppeId=0&tippspieltagIndex=0&tippspieltagIndexTippspieltageTab=1&language=en_GB");
  }
}
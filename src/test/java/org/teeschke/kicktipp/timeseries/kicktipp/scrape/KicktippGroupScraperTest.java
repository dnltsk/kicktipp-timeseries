package org.teeschke.kicktipp.timeseries.kicktipp.scrape;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.teeschke.kicktipp.timeseries.kicktipp.Group;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ComponentScan("org.teeschke.kicktipp.timeseries")
//@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes=ApplicationTests.class)
public class KicktippGroupScraperTest {//extends ApplicationTests {

  //@Autowired
  private KicktippGroupScraper groupScraper;

  private Document METEOGOAL_FIRST_PAGE;

  @BeforeClass
  private void initGroupScraper() throws IOException {
    groupScraper = new KicktippGroupScraper();
  }
  @BeforeClass
  private void readTestFiles() throws IOException {
    METEOGOAL_FIRST_PAGE = Jsoup.parse(
        Paths.get("src", "test", "resources", "METEOGOAL_FIRST_PAGE.html").toFile(), "UTF-8");
  }

  @Test
  public void complete_group_can_be_scraped() throws Exception {
    Group group = groupScraper.scrapeGroup(METEOGOAL_FIRST_PAGE);
    assertThat(group).isNotNull();
    assertThat(group.orderedUsernames).isNotNull();
    assertThat(group.orderedUsernames).isNotEmpty();
    assertThat(group.orderedUsernames).hasSize(44);
  }

  @Test
  public void complete_matches_can_be_scraped() throws Exception {
    Group group = groupScraper.scrapeGroup(METEOGOAL_FIRST_PAGE);
    assertThat(group).isNotNull();
    assertThat(group.orderedMatches).isNotNull();
    assertThat(group.orderedMatches).isNotEmpty();
    assertThat(group.orderedMatches).hasSize(8);
  }

}
package org.teeschke.kicktipp.timeseries.kicktipp.scrape;

import org.assertj.core.api.Assertions;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.teeschke.kicktipp.timeseries.kicktipp.Group;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Paths;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class KicktippBonusScraperTest {

  private KicktippBonusScraper bonusScraper;

  private Document METEOGOAL_BONUS_PAGE;

  @BeforeClass
  private void initGroupScraper() throws IOException {
    bonusScraper = new KicktippBonusScraper();
  }
  @BeforeClass
  private void readTestFiles() throws IOException {
    METEOGOAL_BONUS_PAGE = Jsoup.parse(
        Paths.get("src", "test", "resources", "METEOGOAL_BONUS_PAGE.html").toFile(), "UTF-8");
  }

  @Test
  public void complete_group_can_be_scraped() throws Exception {
    Group group = bonusScraper.scrapeGroupBonus(METEOGOAL_BONUS_PAGE);
    Assertions.assertThat(group).isNotNull();
    Assertions.assertThat(group.orderedUsernames).isNotNull();
    Assertions.assertThat(group.orderedUsernames).isNotEmpty();
    Assertions.assertThat(group.orderedUsernames).hasSize(44);
  }

  @Test
  public void matches_are_scraped_correctly() throws Exception {
    Group groupBonus = bonusScraper.scrapeGroupBonus(METEOGOAL_BONUS_PAGE);
    assertThat(groupBonus).isNotNull();
    assertThat(groupBonus.orderedMatches).isNotNull();
    assertThat(groupBonus.orderedMatches).hasSize(13);
  }

  @Test
  public void scores_are_scraped_correctly() throws Exception {
    Group groupBonus = bonusScraper.scrapeGroupBonus(METEOGOAL_BONUS_PAGE);
    assertThat(groupBonus).isNotNull();
    assertThat(groupBonus.orderedScores).isNotNull();
    assertThat(groupBonus.orderedScores).hasSize(44);
    assertThat(groupBonus.orderedScores.get(0)).hasSize(13);
  }

  @Test
  public void winner_of_the_match_is_scraped_correctly() throws Exception {
    Group groupBonus = bonusScraper.scrapeGroupBonus(METEOGOAL_BONUS_PAGE);
    int PIOTR_INDEX = 19;
    int WINNER_OF_THE_MATCH_INDEX = 12;
    assertThat(groupBonus.orderedScores.get(PIOTR_INDEX).get(WINNER_OF_THE_MATCH_INDEX)).isEqualTo(1.0);
  }
}
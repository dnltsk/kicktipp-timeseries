package org.teeschke.kicktipp.timeseries.cache;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.teeschke.kicktipp.timeseries.ApplicationConfiguration;
import org.teeschke.kicktipp.timeseries.Group;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationConfiguration.class, KicktippCacheManagerTest.class})
public class KicktippCacheManagerTest{

  @Autowired
  private GroupCacheManager cacheManager;

  /* disabled because spring context not loadable (needed for autowireig ) */
  @Ignore @Test
  public void cached_groupdata_can_be_loaded() throws Exception {
    Group groupFromCache = cacheManager.getGroupFromCache("meteogoal");
    assertThat(groupFromCache).isNotNull();
  }

  /* disabled because spring context not loadable (needed for autowireig ) */
  @Ignore @Test
  public void cached_groupdata_can_be_updated() throws Exception {
  }
}
package org.teeschke.kicktipp.timeseries;

import org.springframework.test.context.ContextConfiguration;
import org.teeschke.kicktipp.timeseries.config.ApplicationConfig;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(classes = {ApplicationConfig.class, SpringInstantiationTest.class})
public class SpringInstantiationTest {

  @Test
  public void check_autowiring_works_when_startup() throws Exception {
    assertThat(true).isTrue();
  }
}

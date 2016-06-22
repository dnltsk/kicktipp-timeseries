package org.teeschke;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.teeschke.kicktipp.timeseries.Application;

@RunWith(value = SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = Application.class)
@ActiveProfiles()
//@ComponentScan(basePackages = "org.teeschke.kicktipp.timeseries")
public class ApplicationTests {

	@Test
	public void contextLoads() {
	}

}

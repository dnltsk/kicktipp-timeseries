package org.teeschke.kicktipp.timeseries;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "org.teeschke.kicktipp.timeseries")
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}

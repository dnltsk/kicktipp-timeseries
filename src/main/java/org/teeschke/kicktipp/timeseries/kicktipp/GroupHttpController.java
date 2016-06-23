package org.teeschke.kicktipp.timeseries.kicktipp;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.teeschke.kicktipp.timeseries.kicktipp.scrape.KicktippScraper;

import java.io.IOException;

import static org.springframework.http.HttpStatus.OK;

@RestController
public class GroupHttpController {

  private final String APPLICATION_JSON = "application/json";

  @Autowired
  private KicktippScraper scraper;

  @RequestMapping(value = "/group", method = RequestMethod.GET, produces=APPLICATION_JSON)
  @ResponseBody
  public ResponseEntity<String> group(
      @RequestParam(value = "groupName", required = true) String groupName
  ) throws IOException {
    groupName = groupName.toLowerCase();
    Group group = scraper.scrapeWholeGroupTimeseries(groupName);
    return new ResponseEntity<>(new ObjectMapper().writeValueAsString(group), createResponseHeaders(), OK);
  }

  protected HttpHeaders createResponseHeaders() {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.set("Content-Type", APPLICATION_JSON);
    return httpHeaders;
  }

}

package org.teeschke.kicktipp.timeseries;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.teeschke.kicktipp.timeseries.cache.GroupCacheManager;
import org.teeschke.kicktipp.timeseries.controlling.ControllingManager;
import org.teeschke.kicktipp.timeseries.scrape.KicktippScraper;

import java.io.IOException;

import static org.springframework.http.HttpStatus.OK;

@RestController
public class GroupHttpController {

  private final String APPLICATION_JSON = "application/json";

  @Autowired
  private GroupCacheManager cacher;

  @Autowired
  private KicktippScraper scraper;

  @Autowired
  private ControllingManager controllingManager;

  @RequestMapping(value = "/group", method = RequestMethod.GET, produces=APPLICATION_JSON)
  @ResponseBody
  public ResponseEntity<String> group(
      @RequestParam(value = "groupName", required = true) String groupName
  ) throws IOException {
    groupName = groupName.toLowerCase();
    applicationControlling(groupName);
    return getCachedOrScraped(groupName);
  }

  private ResponseEntity<String> getCachedOrScraped(@RequestParam(value = "groupName", required = true) String groupName) throws IOException {
    /* use cached */
    Group cachedGroup = cacher.getGroupFromCache(groupName);
    if(cachedGroup != null){
      return new ResponseEntity<>(new ObjectMapper().writeValueAsString(cachedGroup), createResponseHeaders(), OK);
    }
    /* on demand scrape */
    Group loadedGroup = scraper.scrapeWholeGroupTimeseries(groupName);
    cacher.addGroupInfoCache(groupName, loadedGroup);
    return new ResponseEntity<>(new ObjectMapper().writeValueAsString(loadedGroup), createResponseHeaders(), OK);
  }

  private void applicationControlling(@RequestParam(value = "groupName", required = true) String groupName) {
    controllingManager.increment(groupName);
  }

  protected HttpHeaders createResponseHeaders() {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.set("Content-Type", APPLICATION_JSON);
    return httpHeaders;
  }

}

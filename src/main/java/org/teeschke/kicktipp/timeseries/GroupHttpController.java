package org.teeschke.kicktipp.timeseries;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.teeschke.kicktipp.timeseries.cache.GroupCacheManager;
import org.teeschke.kicktipp.timeseries.controlling.ControllingManager;
import org.teeschke.kicktipp.timeseries.scrape.KicktippScraper;
import org.teeschke.kicktipp.timeseries.utils.GeneralGroupException;
import org.teeschke.kicktipp.timeseries.utils.GroupNotFoundException;

import java.io.IOException;

import static org.springframework.http.HttpStatus.OK;

@RestController
public class GroupHttpController {

  protected final Logger LOG = LoggerFactory.getLogger(GroupHttpController.class);

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
  ) throws GeneralGroupException {
    groupName = groupName.toLowerCase();
    try {
      applicationControlling(groupName);
      return getCachedOrScraped(groupName);
    } catch (Exception e) {
      throw new GeneralGroupException(groupName);
    }
  }

  @ExceptionHandler(GroupNotFoundException.class)
  public ResponseEntity handleGroupNotFoundException(GroupNotFoundException ex) {
    return new ResponseEntity<>(new ErrorMessage(ex.groupName), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(GeneralGroupException.class)
  public ResponseEntity handleGeneralException(GeneralGroupException ex) {
    return new ResponseEntity<>(new ErrorMessage(ex.groupName), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private ResponseEntity<String> getCachedOrScraped(@RequestParam(value = "groupName", required = true) String groupName) throws IOException, GroupNotFoundException {
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

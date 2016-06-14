package org.teeschke.group;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.teeschke.group.scrape.KicktippScraper;

import static org.springframework.http.HttpStatus.OK;

@RestController
public class GroupHttpController {

  private final String APPLICATION_JSON = "application/json";

  @RequestMapping(value = "/group", method = RequestMethod.GET, produces=APPLICATION_JSON)
  @ResponseBody
  public ResponseEntity<String> group(
      @RequestParam(value = "groupName", required = true) String groupName
  ) throws JsonProcessingException {
    Group group = loadGroup(groupName);
    return new ResponseEntity<>(new ObjectMapper().writeValueAsString(group), createResponseHeaders(), OK);
  }

  private Group loadGroup(String groupName) {
    return new KicktippScraper().scrapeGroup(groupName);
  }

  protected HttpHeaders createResponseHeaders() {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.set("Content-Type", APPLICATION_JSON);
    return httpHeaders;
  }

}

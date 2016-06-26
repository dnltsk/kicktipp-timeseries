package org.teeschke.kicktipp.timeseries.controlling;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ControllingManager {

  @Value("${application-controlling.enabled}")
  private Boolean applicationControllingEnabled;

  @Autowired
  private ControllingDb controllingDb;

  public void increment(String groupName){
    if(applicationControllingEnabled) {
      controllingDb.insertRequest(groupName);
    }
  }

}

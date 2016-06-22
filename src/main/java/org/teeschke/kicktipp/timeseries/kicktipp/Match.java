package org.teeschke.kicktipp.timeseries.kicktipp;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.joda.time.DateTime;

public class Match {

  public String title;

  @JsonSerialize(using = CustomDateSerializer.class)
  public DateTime kickoffTime;

}

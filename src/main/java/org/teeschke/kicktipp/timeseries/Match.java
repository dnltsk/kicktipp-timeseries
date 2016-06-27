package org.teeschke.kicktipp.timeseries;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.joda.time.DateTime;
import org.teeschke.kicktipp.timeseries.utils.CustomDateDeserializer;
import org.teeschke.kicktipp.timeseries.utils.CustomDateSerializer;

public class Match {

  public String title;

  @JsonSerialize(using = CustomDateSerializer.class)
  @JsonDeserialize(using = CustomDateDeserializer.class)
  public DateTime kickoffTime;

}

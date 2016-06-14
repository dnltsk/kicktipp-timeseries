package org.teeschke.group;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.joda.time.DateTime;

import java.util.List;

public class Match {

  public String teamA;
  public String teamB;
  public List<Integer> orderedPlayerPoints;

  @JsonSerialize(using = CustomDateSerializer.class)
  public DateTime kickoffTime;

}

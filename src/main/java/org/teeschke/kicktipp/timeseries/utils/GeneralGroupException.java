package org.teeschke.kicktipp.timeseries.utils;

public class GeneralGroupException extends Exception {

  public String groupName;

  public GeneralGroupException(String groupName) {
    super("groupName >"+groupName+"< not found");
    this.groupName = groupName;
  }
}

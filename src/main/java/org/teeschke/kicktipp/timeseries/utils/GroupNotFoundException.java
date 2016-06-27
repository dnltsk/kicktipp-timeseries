package org.teeschke.kicktipp.timeseries.utils;

public class GroupNotFoundException extends GeneralGroupException {

  public String groupName;

  public GroupNotFoundException(String groupName) {
    super("groupName >"+groupName+"< not found");
    this.groupName = groupName;
  }
}

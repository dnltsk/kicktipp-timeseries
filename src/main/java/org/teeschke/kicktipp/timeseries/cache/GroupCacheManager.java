package org.teeschke.kicktipp.timeseries.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.teeschke.kicktipp.timeseries.Group;

import java.io.IOException;

@Service
public class GroupCacheManager {

  @Value("${cache.max-age-in-minutes}")
  private int maxAgeOfCacheInMinutes;

  @Autowired
  private GroupCacheDb groupCacheDb;

  public Group getGroupFromCache(String groupName) throws IOException {
    CachedGroupData cachedGroupData = groupCacheDb.loadCachedGroup(groupName);
    if(cachedGroupData == null){
      return null;
    }
    DateTime now = new DateTime(DateTimeZone.UTC);
    DateTime insertPlusAge = cachedGroupData.insertedAt.plusMinutes(maxAgeOfCacheInMinutes);

    if(now.isAfter(insertPlusAge)){
      return null;
    }
    return new ObjectMapper().readValue(cachedGroupData.groupDataAsJson, Group.class);
  }

  public void addGroupInfoCache(String groupName, Group group) throws JsonProcessingException {
    CachedGroupData cachedGroupData = new CachedGroupData();
    cachedGroupData.groupName = groupName;
    cachedGroupData.insertedAt = new DateTime(DateTimeZone.UTC);
    cachedGroupData.groupDataAsJson = new ObjectMapper().writeValueAsString(group);
    groupCacheDb.overwriteCachedGroup(cachedGroupData);
  }

}

package org.teeschke.kicktipp.timeseries.cache;

import org.springframework.beans.factory.annotation.Value;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class CacheDbConfig {

  @Value("${cache.host}")
  private String cacheHost;

  @Value("${cache.port}")
  private Integer cachePort;

  @Value("${cache.dbname}")
  private String cacheDbName;

  @Value("${cache.schema}")
  private String cacheSchema;

  @Value("${cache.user}")
  private String cacheUser;

  @Value("${cache.pass}")
  private String cachePass;

  public CacheDbConfig() {
    try {
      Class.forName("org.postgresql.Driver");
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  public Connection getSoccerConnection() throws SQLException{
      return DriverManager.getConnection(
          "jdbc:postgresql://"+ cacheHost + ":"+ cachePort +"/"+ cacheDbName+"?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory",
          cacheUser,
          cachePass);
  }

}

package org.teeschke.kicktipp.timeseries.controlling;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.stereotype.Service;
import org.teeschke.kicktipp.timeseries.DbConfig;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

@Service
public class ControllingDb extends DbConfig{

  public void insertRequest(String groupName){
    StringBuilder sql = new StringBuilder();
    sql.append("  INSERT INTO ");
    sql.append("    kicktipp_controlling ");
    sql.append("    (id, access_time, group_name) ");
    sql.append("  VALUES ( ");
    sql.append("    nextval('kicktipp_controlling_seq'), ");
    sql.append("    '" + new Timestamp(new DateTime(DateTimeZone.UTC).getMillis()) + "', ");
    sql.append("    '" + groupName.replaceAll("'", "''") + "' ");
    sql.append("  ) ");
    System.out.println(sql.toString());

    Connection conn = null;
    Statement stmt = null;
    try {

      conn = getSoccerConnection();
      conn.setAutoCommit(false);
      stmt = conn.createStatement();
      stmt.executeUpdate(sql.toString());
      conn.commit();

    } catch (SQLException e) {
      if(conn!=null){
        try {
          conn.rollback();
        } catch (SQLException e1) {
          System.err.println("Error during rollback: "+e1.getLocalizedMessage());
        }
      }
      System.err.println(e.getLocalizedMessage());
    } finally {
      closeStuff(conn, stmt, null);
    }
  }


}

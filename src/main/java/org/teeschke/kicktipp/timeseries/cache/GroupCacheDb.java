package org.teeschke.kicktipp.timeseries.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.TimeZone;

@Service
public class GroupCacheDb extends CacheDbConfig {

  public CachedGroupData loadCachedGroup(String groupName){
    StringBuilder sql = new StringBuilder();
    sql.append("  select ");
    sql.append("    groupname, ");
    sql.append("    inserted_at, ");
    sql.append("    data ");
    sql.append("  from ");
    sql.append("    kicktipp_cache ");
    sql.append("  where ");
    sql.append("    groupname = '" + groupName.replaceAll("'", "''") + "' ");
    System.out.println(sql.toString());

    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;
    try {

      conn = getSoccerConnection();
      stmt = conn.createStatement();
      rs = stmt.executeQuery(sql.toString());
      if (rs.next()) {
        CachedGroupData cachedGroupData = new CachedGroupData();
        cachedGroupData.insertedAt = new DateTime(rs.getTimestamp("inserted_at", createUtcConverter()), DateTimeZone.UTC);
        cachedGroupData.groupName = rs.getString("groupname");
        cachedGroupData.groupDataAsJson = rs.getString("data");
        return cachedGroupData;
      }
    } catch (SQLException e) {
      System.err.println("error while select " + sql.toString());
      System.err.println(e.getLocalizedMessage());
    } finally {
      closeStuff(conn, stmt, rs);
    }
    return null;
  }

  private Calendar createUtcConverter() {
    Calendar cal = Calendar.getInstance();
    cal.setTimeZone(TimeZone.getTimeZone("UTC"));
    return cal;
  }

  public void overwriteCachedGroup(CachedGroupData cachedGroup) {
    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;
    try {

      conn = getSoccerConnection();
      conn.setAutoCommit(false);
      deleteOldCache(conn, cachedGroup.groupName);
      insertNewCache(conn, cachedGroup);
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
    } catch (JsonProcessingException e) {
      System.err.println(e.getLocalizedMessage());
    } finally {
      closeStuff(conn, stmt, rs);
    }
  }

  private void deleteOldCache(Connection conn, String groupName) throws SQLException {
    StringBuilder sql = new StringBuilder();
    sql.append("  DELETE FROM ");
    sql.append("    kicktipp_cache ");
    sql.append("  WHERE ");
    sql.append("    groupname = '" + groupName.replaceAll("'", "''") + "' ");
    System.out.println(sql.toString());

    Statement stmt = conn.createStatement();
    stmt.executeUpdate(sql.toString());
  }

  private void insertNewCache(Connection conn, CachedGroupData cachedGroupData) throws JsonProcessingException, SQLException {
    StringBuilder sql = new StringBuilder();
    sql.append("  INSERT INTO ");
    sql.append("    kicktipp_cache ");
    sql.append("  VALUES ( ");
    sql.append("    '" + cachedGroupData.groupName.replaceAll("'", "''") + "', ");
    sql.append("    '" + cachedGroupData.insertedAt.toString().replaceAll("'", "''") + "', ");
    sql.append("    '" + cachedGroupData.groupDataAsJson.replaceAll("'", "''") + "' ");
    sql.append("  ) ");
    System.out.println(sql.toString());

    Statement stmt = conn.createStatement();
    stmt.executeUpdate(sql.toString());
  }

  private static void closeStuff(Connection conn, Statement stmt, ResultSet rs) {
    try {
      if (rs != null && !rs.isClosed()) {
        rs.close();
      }
      if (stmt != null && !stmt.isClosed()) {
        stmt.close();
      }
      if (conn != null && !conn.isClosed()) {
        conn.close();
      }
      while (isConnActive(conn)) {
        try {
          System.err.println("Conn not closed -> wait 1 sec");
          Thread.sleep(1_000);
          if (conn != null && !conn.isClosed()) {
            conn.close();
          }
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private static boolean isConnActive(Connection conn) throws SQLException {
    return conn != null && !conn.isClosed();
  }

}

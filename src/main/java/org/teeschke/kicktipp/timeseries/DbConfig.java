package org.teeschke.kicktipp.timeseries;

import org.springframework.beans.factory.annotation.Value;

import java.sql.*;

public class DbConfig {

  @Value("${db.host}")
  private String host;

  @Value("${db.port}")
  private Integer port;

  @Value("${db.dbname}")
  private String dbName;

  @Value("${db.user}")
  private String user;

  @Value("${db.pass}")
  private String pass;

  public DbConfig() {
    try {
      Class.forName("org.postgresql.Driver");
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  public Connection getSoccerConnection() throws SQLException{
      return DriverManager.getConnection(
          "jdbc:postgresql://"+ host + ":"+ port +"/"+ dbName +"?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory",
          user,
          pass);
  }

  protected static void closeStuff(Connection conn, Statement stmt, ResultSet rs) {
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

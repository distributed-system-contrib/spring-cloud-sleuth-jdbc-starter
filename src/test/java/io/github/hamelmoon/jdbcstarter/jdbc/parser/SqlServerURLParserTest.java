package io.github.hamelmoon.jdbcstarter.jdbc.parser;

import static org.junit.Assert.*;

import io.github.hamelmoon.jdbcstarter.jdbc.ConnectionInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class SqlServerURLParserTest {

  @MockBean
  private SqlServerURLParser parser;

  @Test
  public void parseUrlTest() {
    SqlServerURLParser parser = new SqlServerURLParser();
    String testUrl = "jdbc:sqlserver://localhost:1433;user=sa;password=test;databaseName=AdventureWorks;integratedSecurity=true;applicationName=MyApp;";
    ConnectionInfo info = parser.parse(testUrl);

    assertEquals(info.getDbType(), "sqlserver");
    assertEquals(info.getDbUser(), "sa");
    assertEquals(info.getDbInstance(), "AdventureWorks");
    assertEquals(info.getDbPeer(), "localhost:1433");
  }
}
/*
 * Copyright 2017-2019 The OpenTracing Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.hamelmoon.jdbcstarter.jdbc.parser;

import io.github.hamelmoon.jdbcstarter.jdbc.ConnectionInfo;

public class SqlServerURLParser extends AbstractURLParser {

  private static final int DEFAULT_PORT = 1433;
  private static final String DB_TYPE = "sqlserver";

  //syntax
  //jdbc:sqlserver://[serverName[\instanceName][:portNumber]][;property=value[;property=value]]
  //https://docs.microsoft.com/ko-kr/sql/connect/jdbc/building-the-connection-url?view=sql-server-2017

  private String applicationName;
  private String user;
  private String password;
  private String databaseName;
  private int connectTimeout;
  private int connectRetryCount;
  private boolean integratedSecurity;
  private boolean persistSecurityInfo;

  @Override
  protected URLLocation fetchDatabaseHostsIndexRange(String url) {
    int hostLabelStartIndex = url.indexOf("//");
    int hostLabelEndIndex = url.indexOf(";", hostLabelStartIndex + 2);
    if (hostLabelEndIndex == -1) {
      hostLabelEndIndex = url.length();
    }
    return new URLLocation(hostLabelStartIndex + 2, hostLabelEndIndex);
  }

  @Override
  protected URLLocation fetchDatabaseNameIndexRange(String url) {
    int databaseStartTag = url.lastIndexOf("databaseName");
    int databaseEndTag = url.indexOf(";", databaseStartTag + 2);
    return new URLLocation(databaseStartTag + 2, databaseEndTag);
  }

  @Override
  public ConnectionInfo parse(String url) {
    String[] parts = url.split(";");
    //TODO: consideration of reflect
    //Field[] declaredFields = SqlserverURLParser.class.getClass().getDeclaredFields();

    for (String s : parts) {
      if (s.contains("=")) {
        String[] keyValue = s.split("=");
        switch (keyValue[0]){
          case "user":
            user = keyValue[1];
            break;
          case "password":
            password = keyValue[1];
            break;
          case "applicationName":
            applicationName = keyValue[1];
            break;
          case "databaseName":
            databaseName = keyValue[1];
            break;
          case "connectTimeout":
            connectTimeout = Integer.valueOf(keyValue[1]);
            break;
          case "connectRetryCount":
            connectRetryCount = Integer.valueOf(keyValue[1]);
            break;
          case "persistSecurityInfo":
            persistSecurityInfo = Boolean.valueOf(keyValue[1]);
            break;
          case "integratedSecurity":
            integratedSecurity = Boolean.valueOf(keyValue[1]);
            break;
          default:
            break;
        }
      }
    }

    URLLocation location = fetchDatabaseHostsIndexRange(parts[0]);
    String hosts = url.substring(location.startIndex(), location.endIndex());
    String[] hostAndPort = hosts.split(":");
    if (hostAndPort.length != 1) {
      return new ConnectionInfo.Builder(hostAndPort[0], Integer.valueOf(hostAndPort[1]))
          .dbType(DB_TYPE).dbUser(user).dbInstance(databaseName).build();
    } else {
      return new ConnectionInfo.Builder(hostAndPort[0], DEFAULT_PORT).dbType(DB_TYPE).dbUser(user)
          .dbInstance(databaseName).build();
    }
  }
}
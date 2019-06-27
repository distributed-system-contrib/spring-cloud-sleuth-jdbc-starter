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
package io.github.hamelmoon.jdbcstarter.jdbc;

import brave.Span;
import brave.Tracer;
import java.util.Set;


class JdbcTracingUtils {

  static final String COMPONENT_NAME = "java-jdbc";

  /**
   * Opentracing standard tag https://github.com/opentracing/specification/blob/master/semantic_conventions.md
   */
  static Span buildSpan(String operationName,
      String sql,
      ConnectionInfo connectionInfo,
      boolean withActiveSpanOnly,
      Set<String> ignoredStatements,
      Tracer tracer) {

    if (withActiveSpanOnly && tracer.currentSpan() == null) {
        return tracer.newTrace();
    } else if (ignoredStatements != null && ignoredStatements.contains(sql)) {
        return tracer.newTrace();
    }

    Span span = tracer.nextSpan().name(operationName);
    decorate(span, sql, connectionInfo);

    return span;
  }

  private static void decorate(Span span,
      String sql,
      ConnectionInfo connectionInfo) {
    span.tag("COMPONENT" ,COMPONENT_NAME);
    span.tag("DB_STATEMENT" ,sql);

    if (connectionInfo.getDbType() != null) {
      span.tag("DB_TYPE" ,connectionInfo.getDbType());
    }
    if (connectionInfo.getDbPeer() != null) {
      span.tag("PEER_ADDRESS", connectionInfo.getDbPeer());
    }
    if (connectionInfo.getDbInstance() != null) {
      span.tag("DB_INSTANCE", connectionInfo.getDbInstance());
    }
    if (connectionInfo.getDbUser() != null) {
      span.tag("DB_USER", connectionInfo.getDbUser());
    }
  }

  static void onError(Throwable throwable, Span span) {
    span.tag("ERROR", Boolean.TRUE.toString());
    if (throwable != null) {
      span.tag("event", "ERROR");
      span.tag("error.object", throwable.toString());
    }
  }
}

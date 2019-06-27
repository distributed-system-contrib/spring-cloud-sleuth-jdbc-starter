package io.github.hamelmoon.jdbcstarter.aspect;

import brave.Tracer;
import io.github.hamelmoon.jdbcstarter.jdbc.ConnectionInfo;
import io.github.hamelmoon.jdbcstarter.jdbc.TracingConnection;
import io.github.hamelmoon.jdbcstarter.jdbc.parser.URLParser;
import java.sql.Connection;
import java.util.Set;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class JdbcAspect {

  private final boolean withActiveSpanOnly;
  private final Set<String> ignoredStatements;

  private final Tracer tracer;

  public JdbcAspect(boolean withActiveSpanOnly, Set<String> ignoredStatements, Tracer tracer) {
    this.withActiveSpanOnly = withActiveSpanOnly;
    this.ignoredStatements = ignoredStatements;
    this.tracer = tracer;
  }

  @Around("execution(java.sql.Connection *.getConnection(..)) && target(javax.sql.DataSource)")
  public Object getConnection(final ProceedingJoinPoint pjp) throws Throwable {
    Connection conn = (Connection) pjp.proceed();
    if (conn instanceof TracingConnection ||
        conn.isWrapperFor(TracingConnection.class)) {
      return conn;
    }
    String url = conn.getMetaData().getURL();
    ConnectionInfo connectionInfo = URLParser.parser(url);
    return new TracingConnection(conn, connectionInfo, withActiveSpanOnly, ignoredStatements, tracer);
  }
}

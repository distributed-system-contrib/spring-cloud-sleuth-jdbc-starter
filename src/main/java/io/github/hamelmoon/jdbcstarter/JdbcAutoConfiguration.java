package io.github.hamelmoon.jdbcstarter;

import brave.Tracing;
import io.github.hamelmoon.jdbcstarter.aspect.JdbcAspect;
import io.github.hamelmoon.jdbcstarter.properties.JdbcTracingProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.sleuth.autoconfig.TraceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Role;

@Configuration
//@ConditionalOnBean(Tracing.class)
@ConditionalOnProperty(value = "spring.sleuth.jdbc.enabled", havingValue = "true",  matchIfMissing = true)
@EnableConfigurationProperties(JdbcTracingProperties.class)
@AutoConfigureBefore(TraceAutoConfiguration.class)
@EnableAspectJAutoProxy
public class JdbcAutoConfiguration {

  private static final Logger log = LoggerFactory
      .getLogger(JdbcAutoConfiguration.class.getName());


  @Bean
  @ConditionalOnMissingBean
  //@ConditionalOnClass(name = "org.aspectj.lang.ProceedingJoinPoint")
  public JdbcAspect jdbcAspect(JdbcTracingProperties jdbcTracingProperties, Tracing tracing) {
    log.info("----------------- jdbcAspect Activated ---------------");
    return new JdbcAspect(
        jdbcTracingProperties.isWithActiveSpanOnly(),
        jdbcTracingProperties.getIgnoreStatements(),
        tracing.tracer());

  }
}

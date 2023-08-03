package com.itrailmpool.itrailmpoolviewer.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
@PropertySources({
        @PropertySource(value = "classpath:database.properties")
})
@EnableTransactionManagement
public class PostgresConfig extends AbstractJdbcConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostgresConfig.class);

    @Bean
    NamedParameterJdbcOperations operations(HikariDataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }


    @Bean(destroyMethod = "close")
    public HikariDataSource dataSource(@Value("${jdbc.url}") String url,
                                       @Value("${jdbc.username}") String username,
                                       @Value("${jdbc.password}") String password,
                                       @Value("${jdbc.pool.max_size}") int poolMaxSize,
                                       @Value("${jdbc.pool.min_idle}") int poolMinIdle) {

        LOGGER.debug("PostgreSQL configuration: server [{}]", url);

        HikariConfig config = new HikariConfig();
        config.setPoolName("miningcore");
        config.setConnectionTestQuery("SELECT 1");
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName("org.postgresql.Driver");
        config.setMaximumPoolSize(poolMaxSize);
        config.setMinimumIdle(poolMinIdle);
        config.setLeakDetectionThreshold(10 * 60 * 1000);

        return new HikariDataSource(config);
    }

    @Bean
    @Primary
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(HikariDataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean
    public JdbcTemplate jdbcTemplate(HikariDataSource dataSource) {
        JdbcTemplate template = new JdbcTemplate();
        template.setDataSource(dataSource);
        return template;
    }

    @Bean(name = "transactionManager")
    public PlatformTransactionManager txManager(HikariDataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public TransactionTemplate transactionTemplate(PlatformTransactionManager txManager) {
        return new TransactionTemplate(txManager);
    }
}
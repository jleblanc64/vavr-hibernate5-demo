package com.demo.spring;

import com.demo.implem.MetaListImpl;
import com.demo.implem.MetaOptionImpl;
import com.demo.override.*;
import com.zaxxer.hikari.HikariDataSource;
import io.github.jleblanc64.libcustom.LibCustom;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {
    @Value("${spring.datasource.url}")
    String url;

    @Value("${spring.datasource.username}")
    String username;

    @Value("${spring.datasource.password}")
    String password;

    @Bean
    public DataSource getDataSource() {
        // meta
        var metaOption = new MetaOptionImpl();
        var metaList = new MetaListImpl();

        // override
        OverrideHibernateList.override(metaList);
        OverrideSpringList.override(metaList);

        OverrideHibernateOpt.override(metaOption);
        OverrideSpringOpt.override(metaOption);
        OverrideJackson.override(metaOption);

        LibCustom.load();

        // Hikari
        var ds = new HikariDataSource();
        ds.setJdbcUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);

        // Flyway migration
        var config = Flyway.configure().dataSource(url, username, password);
        config.load().migrate();

        return ds;
    }
}

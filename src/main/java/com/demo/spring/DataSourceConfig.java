package com.demo.spring;

import com.demo.override.OverrideHibernate;
import com.demo.override.meta.MetaListF;
import com.demo.override.meta.MetaOptionF;
import com.zaxxer.hikari.HikariDataSource;
import io.github.jleblanc64.libcustom.LibCustom;
import org.flywaydb.core.Flyway;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.Entity;
import javax.sql.DataSource;

import static io.github.jleblanc64.libcustom.functional.ListF.f;

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
        // list models
        var models = f(new Reflections("com.demo.model").getTypesAnnotatedWith(Entity.class));
        if (models.isEmpty())
            throw new RuntimeException("models cannot be empty");

        // meta
        var metaOption = new MetaOptionF();
        var metaList = new MetaListF();

        // override
        OverrideHibernate.override(models, metaOption, metaList);
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

package com.demo.spring;

import com.demo.implem.MetaListImpl;
import com.demo.implem.MetaOptionImpl;
import com.zaxxer.hikari.HikariDataSource;
import io.github.jleblanc64.libcustom.LibCustom;
import io.github.jleblanc64.libcustom.custom.hibernate.VavrHibernate5;
import io.github.jleblanc64.libcustom.custom.jackson.VavrJackson;
import io.github.jleblanc64.libcustom.custom.spring.VavrSpring;
import lombok.SneakyThrows;
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

    @SneakyThrows
    @Bean
    public DataSource getDataSource() {
        // meta
        var metaOption = new MetaOptionImpl();
        var metaList = new MetaListImpl();

        // override
        VavrHibernate5.override(metaList);
        VavrSpring.override(metaList);

        VavrHibernate5.override(metaOption);
        VavrSpring.override(metaOption);
        VavrJackson.override(metaOption, metaList);

        // accept text/plain content-type as json
        var httpHeadersClass = Class.forName("org.springframework.http.HttpHeaders");
        var mediaTypeClass = Class.forName("org.springframework.http.MediaType");
        LibCustom.modifyReturn(httpHeadersClass, "getContentType", argsR -> {
            var mediaType = argsR.returned;
            if (mediaType != null && mediaType.toString().toLowerCase().startsWith("text/plain"))
                return mediaTypeClass.getMethod("parseMediaType", String.class).invoke(null, "application/json");

            return mediaType;
        });

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

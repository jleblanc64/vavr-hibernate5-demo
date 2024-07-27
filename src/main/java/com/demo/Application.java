package com.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class Application {
    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(Application.class);

        String host = "database-1.cjrp4imxd7sw.eu-central-1.rds.amazonaws.com";
        String port = "3306";

        String username = "admin";
        String password = "Password123";

        String url = "jdbc:mysql://" + host + ":" + port + "/";
        String dbName = "myDB";

        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            try (Statement stmt = conn.createStatement()) {
                String sql = "CREATE DATABASE IF NOT EXISTS " + dbName;
                stmt.executeUpdate(sql);
            }
        }

        url += dbName;

        Map<String, Object> props = new HashMap<>();
        props.put("spring.datasource.url", url);
        props.put("spring.datasource.username", username);
        props.put("spring.datasource.password", password);
        props.put("spring.jpa.properties.hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
        props.put("spring.jpa.hibernate.ddl-auto", "update");
        app.setDefaultProperties(props);

        app.run(args);
    }
}

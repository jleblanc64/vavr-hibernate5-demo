package com.example.easynotes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.testcontainers.containers.MySQLContainer;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@EnableJpaAuditing
public class EasyNotesApplication {
    public static void main(String[] args) {
        MySQLContainer<?> mysql = new MySQLContainer<>("mysql:5.5").withDatabaseName("notes_app").withPassword("test").withPassword("test");
        mysql.start();

        SpringApplication app = new SpringApplication(EasyNotesApplication.class);

        Map<String, Object> props = new HashMap<>();
        props.put("spring.datasource.url", mysql.getJdbcUrl());
        props.put("spring.datasource.username", "test");
        props.put("spring.datasource.password", "test");
        app.setDefaultProperties(props);

        app.run(args);
    }
}

package com.example.easynotes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.testcontainers.containers.MySQLContainer;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Properties;

@SpringBootApplication
@EnableJpaAuditing
public class EasyNotesApplication {

    static String propertyFile = "/home/charles/Desktop/spring.properties";

    @Bean
    public static PropertySourcesPlaceholderConfigurer properties() {
        PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
        propertySourcesPlaceholderConfigurer.setLocation(new FileSystemResource(propertyFile));
        return propertySourcesPlaceholderConfigurer;
    }

    public static void main(String[] args) throws Exception {
        MySQLContainer<?> mysql = new MySQLContainer<>("mysql:5.5").withDatabaseName("notes_app").withPassword("test").withPassword("test");
        mysql.start();

        try (OutputStream output = new FileOutputStream(propertyFile)) {
            Properties prop = new Properties();
            prop.setProperty("spring.datasource.url", mysql.getJdbcUrl());
            prop.setProperty("spring.datasource.username", "test");
            prop.setProperty("spring.datasource.password", "test");

            // save properties to project root folder
            prop.store(output, null);
        }

        SpringApplication app = new SpringApplication(EasyNotesApplication.class);

//        Map<String, Object> props = new HashMap<>();
//        props.put("spring.datasource.url", mysql.getJdbcUrl());
//        app.setDefaultProperties(props);

        app.run(args);
    }
}

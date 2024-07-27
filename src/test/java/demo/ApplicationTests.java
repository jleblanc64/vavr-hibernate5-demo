package demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.*;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.*;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationTests {

    @LocalServerPort
    private int port;

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:5.5").withDatabaseName("database").withPassword("test").withPassword("test");

    @DynamicPropertySource
    static void mysqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.datasource.username", mysql::getUsername);
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void contextLoads() throws JsonProcessingException {
        String url = "http://localhost:" + port + "/customers";

        // POST customer
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>("{\"name\":\"a\"}", headers);
        String resp = restTemplate.postForObject(url, request, String.class);

        // extract ID from created customer
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(resp);
        long id = root.path("id").longValue();

        // GET by ID
        resp = restTemplate.getForObject(url + "/" + id, String.class);
        root = objectMapper.readTree(resp);
        String name = root.path("name").textValue();
        assertThat(name).isEqualTo("a");

        // LIST
        resp = restTemplate.getForObject(url, String.class);
        root = objectMapper.readTree(resp);
        assertThat(root.size()).isEqualTo(1);

        name = root.path(0).path("name").textValue();
        assertThat(name).isEqualTo("a");

        // DELETE
        restTemplate.delete(url + "/" + id, request, String.class);

        // LIST
        resp = restTemplate.getForObject(url, String.class);
        root = objectMapper.readTree(resp);
        assertThat(root.size()).isEqualTo(0);

        // GET by ID should respond 404 NOT FOUND
        int httpCode = restTemplate.getForEntity(url + "/" + id, String.class).getStatusCodeValue();
        assertThat(httpCode).isEqualTo(404);
    }
}

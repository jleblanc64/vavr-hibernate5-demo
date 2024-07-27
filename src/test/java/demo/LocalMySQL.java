package demo;

import org.testcontainers.containers.MySQLContainer;

public class LocalMySQL {

    public static void main(String[] args) throws Exception {
        MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0.23");
        mysql.start();

        System.out.println(mysql.getJdbcUrl());

        Thread.sleep(100000000);
    }
}

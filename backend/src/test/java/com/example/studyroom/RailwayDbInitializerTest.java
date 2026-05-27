package com.example.studyroom;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import java.sql.Connection;

public class RailwayDbInitializerTest {

    @Test
    public void testInitializeRailwayDatabase() {
        String url = "jdbc:mysql://zephyr.proxy.rlwy.net:19607/railway?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true";
        String user = "root";
        String password = "yHNprwmyYMMqXIIYJkohPITXYULrPHsw";
        String sqlFilePath = "c:/Users/40138/Desktop/移动计算/backend/src/main/resources/init.sql";

        System.out.println("====== Connecting to Railway MySQL via JDBC ======");
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(password);

        try (Connection conn = dataSource.getConnection()) {
            System.out.println("====== Connected successfully! Running init.sql... ======");
            ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
            populator.addScript(new FileSystemResource(sqlFilePath));
            populator.populate(conn);
            System.out.println("====== Railway Database Initialized Successfully! ======");
        } catch (Exception e) {
            System.err.println("====== Failed to initialize Railway database ======");
            e.printStackTrace();
        }
    }
}

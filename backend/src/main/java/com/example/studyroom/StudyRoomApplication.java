package com.example.studyroom;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

@SpringBootApplication
@EnableScheduling
public class StudyRoomApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudyRoomApplication.class, args);
    }

    @Bean
    public CommandLineRunner initDatabase(DataSource dataSource) {
        return args -> {
            try (Connection conn = dataSource.getConnection()) {
                DatabaseMetaData metaData = conn.getMetaData();
                // 检查是否存在 users 表，如果已存在，说明数据库已初始化过，直接跳过
                try (ResultSet rs = metaData.getTables(conn.getCatalog(), null, "users", null)) {
                    if (rs.next()) {
                        System.out.println("====== DATABASE ALREADY INITIALIZED (users table exists). SKIPPING INIT.SQL ======");
                        return;
                    }
                }
                
                System.out.println("====== START RUNNING INIT.SQL ======");
                ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
                populator.addScript(new ClassPathResource("init.sql"));
                populator.populate(conn);
                System.out.println("====== DATABASE INITIALIZATION SUCCESS ======");
            } catch (Exception e) {
                System.err.println("====== DATABASE INITIALIZATION ERROR: " + e.getMessage() + " ======");
                e.printStackTrace();
            }
        };
    }
}

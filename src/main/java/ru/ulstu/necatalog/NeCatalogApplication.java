package ru.ulstu.necatalog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan("ru.ulstu")
@EnableJpaRepositories("ru.ulstu")
@EntityScan("ru.ulstu")
public class NeCatalogApplication {

    public static void main(String[] args) {
        SpringApplication.run(NeCatalogApplication.class, args);
    }

}

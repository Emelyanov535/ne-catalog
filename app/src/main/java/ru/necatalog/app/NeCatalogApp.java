package ru.necatalog.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan("ru.necatalog")
@EnableJpaRepositories("ru.necatalog")
@SpringBootApplication(scanBasePackages = {"ru.necatalog"})
public class NeCatalogApp {

    public static void main(String[] args) {
        SpringApplication.run(NeCatalogApp.class, args);
    }

}

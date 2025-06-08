package ru.necatalog.analogfinder.service;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.necatalog.analogfinder.dto.AnalogResult;

@Sql("classpath:/search.sql")
@DataJpaTest
@Testcontainers
@ExtendWith(SpringExtension.class)
@EnableJpaRepositories(basePackages = "ru.necatalog.persistence")
@EntityScan(basePackages = "ru.necatalog.persistence")
@ContextConfiguration(classes = AnalogFinderService.class)
public class AnalogFinderServiceIntegrationTests {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.liquibase.change-log", () -> "classpath:/db/changelog/master.yml");
    }

    @Autowired
    private AnalogFinderService analogFinderService;

    @Test
    void findAnalogsTest() {
        List<AnalogResult> analogs =  analogFinderService.findAnalogs(
            "https://www.ozon.ru/product/msi-alpha-17-c7vg-046cn-igrovoy-noutbuk-17-3-amd-ryzen-9-7940hx-ram-32-gb-ssd-nvidia-geforce-rtx-1826945055/",
            List.of("CPU_NAME", "FREQUENCY", "RESOLUTION", "RAM"),
            false);

        assertThat(analogs).hasSize(1);
    }

    @Test
    void getAttributeGroupsTest() {
        Map<String, List<String>> attributeGroups = analogFinderService.getAttributeGroups(
            "https://www.ozon.ru/product/msi-alpha-17-c7vg-046cn-igrovoy-noutbuk-17-3-amd-ryzen-9-7940hx-ram-32-gb-ssd-nvidia-geforce-rtx-1826945055/");

        assertThat(attributeGroups).hasSize(6);
    }


}

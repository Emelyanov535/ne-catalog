package ru.necatalog.search.service;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.BeforeEach;
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
import ru.necatalog.persistence.enumeration.Category;
import ru.necatalog.search.dto.FilterData;
import ru.necatalog.search.dto.SearchResults;

@Sql("classpath:/search.sql")
@DataJpaTest
@Testcontainers
@ExtendWith(SpringExtension.class)
@EnableJpaRepositories(basePackages = "ru.necatalog.persistence")
@EntityScan(basePackages = "ru.necatalog.persistence")
@ContextConfiguration(classes = SearchService.class)
public class SearchServiceIntegrationTests {

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
    private SearchService searchService;

    @BeforeEach
    void setUp() {
        searchService.init();
    }

    @Test
    void searchTest() {
        SearchResults results = searchService.search(
            Category.LAPTOP,
            "msi ram 16",
            "price",
            "desc",
            0,
            10,
            Map.of(),
            1000,
            500000);

        assertThat(results.getResults().size()).isEqualTo(2);
        assertThat(results.getMaxPage()).isEqualTo(1);
    }

    @Test
    void getAvailableFiltersTest() {
        FilterData results = searchService.getAvailableFilters(
            Category.LAPTOP);

        assertThat(results.getPriceStart()).isEqualTo(new BigDecimal(50000).setScale(2));
        assertThat(results.getPriceEnd()).isEqualTo(new BigDecimal(100000).setScale(2));
        assertThat(results.getFilters().size()).isEqualTo(24);
    }

}

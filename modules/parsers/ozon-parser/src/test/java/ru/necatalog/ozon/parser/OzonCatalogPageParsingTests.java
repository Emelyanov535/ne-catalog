package ru.necatalog.ozon.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.necatalog.ozon.parser.parsing.OzonCatalogPageParsingService;
import ru.necatalog.ozon.parser.parsing.OzonPageFetcher;
import ru.necatalog.ozon.parser.parsing.creator.OzonCategoryPageDataCreator;
import ru.necatalog.ozon.parser.parsing.dto.catalog.OzonProductEntityCreator;
import ru.necatalog.ozon.parser.parsing.enumeration.OzonCategory;
import ru.necatalog.ozon.parser.service.OzonProductService;
import ru.necatalog.persistence.entity.ProductEntity;

@ExtendWith(MockitoExtension.class)
public class OzonCatalogPageParsingTests {

    @Mock
    private OzonPageFetcher pageFetcher;

    private OzonCategoryPageDataCreator categoryPageDataCreator;

    @Spy
    private OzonProductEntityCreator productEntityCreator;

    @Spy
    private ObjectMapper objectMapper;

    @Mock
    private OzonProductService  productService;

    @InjectMocks
    private OzonCatalogPageParsingService catalogPageParsingService;

    @BeforeEach
    public void setUp() {
        categoryPageDataCreator = new OzonCategoryPageDataCreator(objectMapper);
        catalogPageParsingService = new OzonCatalogPageParsingService(
            pageFetcher,
            categoryPageDataCreator,
            productEntityCreator,
            objectMapper,
            productService
        );
        catalogPageParsingService.init();
    }

    @Test
    void catalogParsingTest_laptop() throws Exception {
        // given
        when(pageFetcher.fetchPageJson(any(), anyInt()))
            .thenReturn(readResourceFile("catalog.html"));

        // when
        catalogPageParsingService.parse(OzonCategory.LAPTOP);

        // then
        ArgumentCaptor<List<ProductEntity>> captor = ArgumentCaptor.forClass(List.class);
        verify(productService).save(captor.capture());
        List<ProductEntity> products = captor.getValue();

        assertThat(products).hasSize(12);
    }

    private String readResourceFile(String fileName) throws IOException {
        InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);
        if (is == null) {
            throw new IllegalArgumentException("Ресурс не найден: " + fileName);
        }
        try (var reader = new BufferedReader(
            new InputStreamReader(is, StandardCharsets.UTF_8))) {
            return reader
                .lines()
                .reduce("", (acc, line) -> acc + line + "\n");
        }
    }

}

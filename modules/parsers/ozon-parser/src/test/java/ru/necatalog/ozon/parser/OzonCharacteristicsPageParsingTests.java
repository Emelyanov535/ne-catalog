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
import ru.necatalog.ozon.parser.parsing.OzonCharacteristicsPageParsingService;
import ru.necatalog.ozon.parser.parsing.OzonPageFetcher;
import ru.necatalog.ozon.parser.parsing.creator.OzonCharacteristicsPageDataCreator;
import ru.necatalog.ozon.parser.parsing.processor.attribute.LaptopAttributeProcessor;
import ru.necatalog.ozon.parser.parsing.processor.definder.LaptopAttributeDefinder;
import ru.necatalog.ozon.parser.service.dto.ParseOzonCharacteristicPayload;
import ru.necatalog.persistence.entity.AttributeEntity;
import ru.necatalog.persistence.entity.ProductAttributeEntity;
import ru.necatalog.persistence.enumeration.Category;
import ru.necatalog.persistence.repository.AttributeRepository;
import ru.necatalog.persistence.repository.ProductAttributeRepository;

@ExtendWith(MockitoExtension.class)
public class OzonCharacteristicsPageParsingTests {

    private static final List<AttributeEntity> attributes = List.of(
        new AttributeEntity(70L,"CPU_NAME","LAPTOP_PROCESSOR"),
        new AttributeEntity(71L,"BRAND_NAME","LAPTOP_PROCESSOR"),
        new AttributeEntity(72L,"MODEL_NAME","LAPTOP_PROCESSOR"),
        new AttributeEntity(73L,"FREQUENCY","LAPTOP_PROCESSOR"),
        new AttributeEntity(74L,"NUM_OF_CORES","LAPTOP_PROCESSOR"),
        new AttributeEntity(75L,"NUM_OF_THREADS","LAPTOP_PROCESSOR"),
        new AttributeEntity(76L,"DIAGONAL","LAPTOP_DISPLAY"),
        new AttributeEntity(13671L,"VENDOR_CODE","LAPTOP_INFO"),
        new AttributeEntity(13672L,"PART_NUMBER","LAPTOP_INFO"),
        new AttributeEntity(77L,"TECHNOLOGY_MATRIX","LAPTOP_DISPLAY"),
        new AttributeEntity(78L,"SCREEN_COVER","LAPTOP_DISPLAY"),
        new AttributeEntity(79L,"RESOLUTION","LAPTOP_DISPLAY"),
        new AttributeEntity(80L,"MAX_FREQUENCY_SCREEN","LAPTOP_DISPLAY"),
        new AttributeEntity(81L,"SENSOR_SCREEN","LAPTOP_DISPLAY"),
        new AttributeEntity(82L,"GPU_NAME","LAPTOP_GPU"),
        new AttributeEntity(83L,"GPU_TYPE","LAPTOP_GPU"),
        new AttributeEntity(84L,"GPU_BRAND","LAPTOP_GPU"),
        new AttributeEntity(85L,"VIDEO_MEMORY","LAPTOP_GPU"),
        new AttributeEntity(92L,"RAM","LAPTOP_RAM"),
        new AttributeEntity(93L,"RAM_TYPE","LAPTOP_RAM"),
        new AttributeEntity(94L,"UPGRADE_CAPABILITY","LAPTOP_RAM"),
        new AttributeEntity(225L,"VOLUME_SSD","LAPTOP_STORAGE"),
        new AttributeEntity(226L,"VOLUME_HDD","LAPTOP_STORAGE"),
        new AttributeEntity(227L,"TYPE","LAPTOP_STORAGE"),
        new AttributeEntity(228L,"NUM_SSD","LAPTOP_STORAGE"),
        new AttributeEntity(229L,"NUM_HDD","LAPTOP_STORAGE"),
        new AttributeEntity(230L,"SSD_FORM_FACTOR","LAPTOP_STORAGE"),
        new AttributeEntity(231L,"HDD_FORM_FACTOR","LAPTOP_STORAGE"));

    @Mock
    private OzonPageFetcher pageFetcher;

    @Mock
    private ProductAttributeRepository productAttributeRepository;

    @Mock
    private AttributeRepository attributeRepository;

    @Spy
    LaptopAttributeDefinder laptopAttributeDefinder;

    @Spy
    private ObjectMapper objectMapper;

    @InjectMocks
    private OzonCharacteristicsPageParsingService characteristicsPageParsingService;

    @BeforeEach
    public void setUp() {
        var laptopAttributeProcessor = new LaptopAttributeProcessor(laptopAttributeDefinder, attributeRepository);
        when(attributeRepository.findAllByGroupContains(any())).thenReturn(attributes);
        laptopAttributeProcessor.init();
        var characteristicsPageDataCreator = new OzonCharacteristicsPageDataCreator(objectMapper);
        characteristicsPageParsingService = new OzonCharacteristicsPageParsingService(
            productAttributeRepository,
            List.of(laptopAttributeProcessor),
            pageFetcher,
            characteristicsPageDataCreator,
            objectMapper
        );
        characteristicsPageParsingService.init();
    }

    @Test
    void characteristicsParsingTest_laptop() throws Exception {
        ParseOzonCharacteristicPayload payload = new ParseOzonCharacteristicPayload(
            "producturl",
            Category.LAPTOP);

        // given
        when(pageFetcher.fetchPageJson(any(), anyInt()))
            .thenReturn(readResourceFile("characteristics.html"));

        // when
        characteristicsPageParsingService.processAttributePage(payload);

        // then
        ArgumentCaptor<List<ProductAttributeEntity>> captor = ArgumentCaptor.forClass(List.class);
        verify(productAttributeRepository).saveAll(captor.capture());
        List<ProductAttributeEntity> attributes = captor.getValue();

        assertThat(attributes).hasSize(19);
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

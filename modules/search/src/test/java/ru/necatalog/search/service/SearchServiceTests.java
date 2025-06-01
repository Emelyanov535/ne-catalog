package ru.necatalog.search.service;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.necatalog.persistence.dto.PriceFilterDto;
import ru.necatalog.persistence.dto.ProductAttributeFilter;
import ru.necatalog.persistence.entity.AttributeEntity;
import ru.necatalog.persistence.enumeration.Category;
import ru.necatalog.persistence.repository.AttributeRepository;
import ru.necatalog.persistence.repository.ProductAttributeRepository;
import ru.necatalog.persistence.repository.ProductRepository;
import ru.necatalog.search.dto.FilterData;

@ExtendWith(MockitoExtension.class)
public class SearchServiceTests {

    private static final List<AttributeEntity> attributes = List.of(
        new AttributeEntity(70L, "CPU_NAME", "LAPTOP_PROCESSOR"),
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
    private AttributeRepository attributeRepository;

    @Mock
    private ProductAttributeRepository productAttributeRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private EntityManager entityManager;

    private SearchService searchService;

    @BeforeEach
    public void setUp() {
        when(attributeRepository.findAll()).thenReturn(attributes);
        searchService = new SearchService(
            attributeRepository,
            productAttributeRepository,
            productRepository,
            entityManager
        );
        searchService.init();
    }

    @Test
    void getAvailableFiltersTest_success() {
        when(productRepository.findPriceFilterData(Category.LAPTOP.name()))
            .thenReturn(new PriceFilterDto(BigDecimal.valueOf(100), BigDecimal.TEN));
        when(productAttributeRepository.findDistinctById_attributeIdIn(any()))
            .thenReturn(List.of(
                new ProductAttributeFilter(76L, "15", null),
                new ProductAttributeFilter(77L, "IPS", null)));

        FilterData filterData = searchService.getAvailableFilters(Category.LAPTOP);

        assertThat(filterData.getPriceStart()).isEqualTo(BigDecimal.TEN);
        assertThat(filterData.getPriceEnd()).isEqualTo(BigDecimal.valueOf(100));
        assertThat(filterData.getFilters())
            .containsEntry("DIAGONAL", List.of("15"))
            .containsEntry("TECHNOLOGY_MATRIX", List.of("IPS"));
    }

    @Test
    void getAvailableFiltersTest_emptyFilters() {
        when(productRepository.findPriceFilterData(Category.LAPTOP.name()))
            .thenReturn(new PriceFilterDto(BigDecimal.valueOf(100), BigDecimal.TEN));
        when(productAttributeRepository.findDistinctById_attributeIdIn(any()))
            .thenReturn(List.of());

        FilterData filterData = searchService.getAvailableFilters(Category.LAPTOP);

        assertThat(filterData.getPriceStart()).isEqualTo(BigDecimal.TEN);
        assertThat(filterData.getPriceEnd()).isEqualTo(BigDecimal.valueOf(100));
        assertThat(filterData.getFilters()).isEmpty();
    }

    @Test
    void getAvailableFiltersTest_EmptyPrice() {
        when(productRepository.findPriceFilterData(Category.LAPTOP.name()))
            .thenReturn(null);

        assertThatThrownBy(() -> searchService.getAvailableFilters(Category.LAPTOP))
            .isInstanceOf(EntityNotFoundException.class);
    }

}

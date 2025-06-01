package ru.necatalog.analogfinder.service;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.necatalog.persistence.entity.AttributeEntity;
import ru.necatalog.persistence.enumeration.Category;
import ru.necatalog.persistence.repository.AttributeRepository;
import ru.necatalog.persistence.repository.ProductPriceRepository;
import ru.necatalog.persistence.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
public class AnalogFinderServiceTests {

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
        new AttributeEntity(80L,"MAX_FREQUENCY_SCREEN","SMARTPHONE_DISPLAY"),
        new AttributeEntity(81L,"SENSOR_SCREEN","SMARTPHONE_DISPLAY"),
        new AttributeEntity(82L,"GPU_NAME","SMARTPHONE_GPU"),
        new AttributeEntity(83L,"GPU_TYPE","SMARTPHONE_GPU"),
        new AttributeEntity(84L,"GPU_BRAND","SMARTPHONE_GPU"),
        new AttributeEntity(85L,"VIDEO_MEMORY","SMARTPHONE_GPU"),
        new AttributeEntity(92L,"RAM","SMARTPHONE_RAM"),
        new AttributeEntity(93L,"RAM_TYPE","SMARTPHONE_RAM"),
        new AttributeEntity(94L,"UPGRADE_CAPABILITY","SMARTPHONE_RAM"),
        new AttributeEntity(225L,"VOLUME_SSD","SMARTPHONE_STORAGE"),
        new AttributeEntity(226L,"VOLUME_HDD","SMARTPHONE_STORAGE"),
        new AttributeEntity(227L,"TYPE","SMARTPHONE_STORAGE"),
        new AttributeEntity(228L,"NUM_SSD","SMARTPHONE_STORAGE"),
        new AttributeEntity(229L,"NUM_HDD","SMARTPHONE_STORAGE"),
        new AttributeEntity(230L,"SSD_FORM_FACTOR","SMARTPHONE_STORAGE"),
        new AttributeEntity(231L,"HDD_FORM_FACTOR","SMARTPHONE_STORAGE"));

    @Mock
    private AttributeRepository attributeRepository;

    @Mock
    private EntityManager entityManager;

    @Mock
    private ProductRepository productRepository;

    @Mock
    ProductPriceRepository productPriceRepository;

    @InjectMocks
    private AnalogFinderService analogFinderService;

    @Test
    void getAttributeGroupsTest_success() {
        when(productRepository.getProductCategory("http://test"))
            .thenReturn(Category.LAPTOP);
        when(attributeRepository.findAll())
            .thenReturn(attributes);
        Map<String, List<String>> response = analogFinderService.getAttributeGroups("http://test");
        assertThat(response.size()).isEqualTo(3);
    }

    @Test
    void getAttributeGroupsTest_categoryNotFound() {
        when(productRepository.getProductCategory("http://test"))
            .thenReturn(null);
        assertThatThrownBy(() -> analogFinderService.getAttributeGroups("http://test"))
            .isInstanceOf(EntityNotFoundException.class);
    }

}

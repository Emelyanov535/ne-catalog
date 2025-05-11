package ru.necatalog.ozon.parser.parsing.processor.definder;

import java.util.Optional;

import org.springframework.stereotype.Service;
import ru.necatalog.ozon.parser.parsing.processor.AttributeDefinder;
import ru.necatalog.persistence.enumeration.AttributeGroup;
import ru.necatalog.persistence.enumeration.attribute.laptop.LaptopDisplayAttribute;
import ru.necatalog.persistence.enumeration.attribute.laptop.LaptopGpuAttribute;
import ru.necatalog.persistence.enumeration.attribute.laptop.LaptopProcessorAttribute;
import ru.necatalog.persistence.enumeration.attribute.laptop.LaptopRamAttribute;
import ru.necatalog.persistence.enumeration.attribute.laptop.LaptopStorageAttribute;

@Service
public class LaptopAttributeDefinder implements AttributeDefinder {

    @Override
    public Optional<AttributeGroup> define(String title, String key) {
        return switch (title) {
            case "Процессор" -> defineCpuAttribute(key);
            case "Оперативная память" -> defineRamAttribute(key);
            case "Экран" -> defineDisplayAttribute(key);
            case "Видеокарта" -> defineGpuAttribute(key);
            case "Накопители данных" -> defineStorageAttribute(key);
            default -> Optional.empty();
        };
    }

    private Optional<AttributeGroup> defineCpuAttribute(String key) {
        return switch (key) {
            case "CPUName" -> Optional.of(LaptopProcessorAttribute.CPU_NAME);
            case "CPUModel" -> Optional.of(LaptopProcessorAttribute.MODEL_NAME);
            case "CPUBrandShort" -> Optional.of(LaptopProcessorAttribute.BRAND_NAME);
            case "FrequencyStr" -> Optional.of(LaptopProcessorAttribute.FREQUENCY);
            case "CPUCores" -> Optional.of(LaptopProcessorAttribute.NUM_OF_CORES);
            default -> Optional.empty();
        };
    }

    private Optional<AttributeGroup> defineRamAttribute(String key) {
        return switch (key) {
            case "RAM" -> Optional.of(LaptopRamAttribute.RAM);
            case "RAMType" -> Optional.of(LaptopRamAttribute.RAM_TYPE);
            case "UpgradeCapability" -> Optional.of(LaptopRamAttribute.UPGRADE_CAPABILITY);
            default -> Optional.empty();
        };
    }

    private Optional<AttributeGroup> defineDisplayAttribute(String key) {
        return switch (key) {
            case "RSDiagonalStr" -> Optional.of(LaptopDisplayAttribute.DIAGONAL);
            case "TechnologyMatrixNote" -> Optional.of(LaptopDisplayAttribute.TECHNOLOGY_MATRIX);
            case "ScreenCover" -> Optional.of(LaptopDisplayAttribute.SCREEN_COVER);
            case "Resolution" -> Optional.of(LaptopDisplayAttribute.RESOLUTION);
            case "MaxFrequencyScreen" -> Optional.of(LaptopDisplayAttribute.MAX_FREQUENCY_SCREEN);
            case "SensorScreen" -> Optional.of(LaptopDisplayAttribute.SENSOR_SCREEN);
            default -> Optional.empty();
        };
    }

    private Optional<AttributeGroup> defineGpuAttribute(String key) {
        return switch (key) {
            case "GPUName" -> Optional.of(LaptopGpuAttribute.GPU_NAME);
            case "VideoMemory" -> Optional.of(LaptopGpuAttribute.VIDEO_MEMORY);
            case "GPUType" -> Optional.of(LaptopGpuAttribute.GPU_TYPE);
            case "GPUBrandsShort" -> Optional.of(LaptopGpuAttribute.GPU_BRAND);
            default -> Optional.empty();
        };
    }

    private Optional<AttributeGroup> defineStorageAttribute(String key) {
        return switch (key) {
            case "VolumeSSDStr" -> Optional.of(LaptopStorageAttribute.VOLUME_SSD);
            case "VolumeHDDStr" -> Optional.of(LaptopStorageAttribute.VOLUME_HDD);
            case "HDType" -> Optional.of(LaptopStorageAttribute.TYPE);
            case "NumSSD" -> Optional.of(LaptopStorageAttribute.NUM_SSD);
            case "SSDSize" -> Optional.of(LaptopStorageAttribute.SSD_FORM_FACTOR);
            case "NumHDD" -> Optional.of(LaptopStorageAttribute.NUM_HDD);
            case "HDDSize" -> Optional.of(LaptopStorageAttribute.HDD_FORM_FACTOR);
            default -> Optional.empty();
        };
    }

}

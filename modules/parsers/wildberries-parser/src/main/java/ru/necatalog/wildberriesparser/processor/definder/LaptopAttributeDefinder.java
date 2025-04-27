package ru.necatalog.wildberriesparser.processor.definder;

import org.springframework.stereotype.Service;
import ru.necatalog.persistence.enumeration.AttributeGroup;
import ru.necatalog.persistence.enumeration.attribute.laptop.LaptopDisplayAttribute;
import ru.necatalog.persistence.enumeration.attribute.laptop.LaptopGpuAttribute;
import ru.necatalog.persistence.enumeration.attribute.laptop.LaptopProcessorAttribute;
import ru.necatalog.persistence.enumeration.attribute.laptop.LaptopRamAttribute;
import ru.necatalog.persistence.enumeration.attribute.laptop.LaptopStorageAttribute;
import ru.necatalog.wildberriesparser.processor.AttributeDefinder;

import java.util.Optional;

@Service("wildberriesLaptopAttributeDefinder")
public class LaptopAttributeDefinder implements AttributeDefinder {
	@Override
	public Optional<AttributeGroup> define(String title, String key) {
		return switch (title) {
			case "Процессор" -> defineCpuAttribute(key);
			case "Память" -> defineRamAttribute(key);
			case "Экран" -> defineDisplayAttribute(key);
			case "Видеокарта" -> defineGpuAttribute(key);
			case "Накопители данных" -> defineStorageAttribute(key);
			default -> Optional.empty();
		};
	}

	private Optional<AttributeGroup> defineCpuAttribute(String key) {
		return switch (key) {
			case "Процессор" -> Optional.of(LaptopProcessorAttribute.CPU_NAME);
			case "Линейка процессоров" -> Optional.of(LaptopProcessorAttribute.MODEL_NAME);
			case "Тактовая частота процессора" -> Optional.of(LaptopProcessorAttribute.FREQUENCY);
			case "Количество ядер процессора" -> Optional.of(LaptopProcessorAttribute.NUM_OF_CORES);
			default -> Optional.empty();
		};
	}

	private Optional<AttributeGroup> defineRamAttribute(String key) {
		return switch (key) {
			case "Объем оперативной памяти (Гб)" -> Optional.of(LaptopRamAttribute.RAM);
			case "Тип оперативной памяти" -> Optional.of(LaptopRamAttribute.RAM_TYPE);
			default -> Optional.empty();
		};
	}

	private Optional<AttributeGroup> defineDisplayAttribute(String key) {
		return switch (key) {
			case "Диагональ экрана" -> Optional.of(LaptopDisplayAttribute.DIAGONAL);
			case "Тип матрицы" -> Optional.of(LaptopDisplayAttribute.TECHNOLOGY_MATRIX);
			case "Поверхность экрана" -> Optional.of(LaptopDisplayAttribute.SCREEN_COVER);
			case "Разрешение экрана" -> Optional.of(LaptopDisplayAttribute.RESOLUTION);
			case "Частота обновления" -> Optional.of(LaptopDisplayAttribute.MAX_FREQUENCY_SCREEN);
			default -> Optional.empty();
		};
	}

	private Optional<AttributeGroup> defineGpuAttribute(String key) {
		return switch (key) {
			case "Тип видеокарты" -> Optional.of(LaptopGpuAttribute.GPU_TYPE);
			default -> Optional.empty();
		};
	}

	private Optional<AttributeGroup> defineStorageAttribute(String key) {
		return switch (key) {
			case "Объем накопителя SSD" -> Optional.of(LaptopStorageAttribute.VOLUME_SSD);
			case "Объем накопителя HDD" -> Optional.of(LaptopStorageAttribute.VOLUME_HDD);
			case "Тип накопителя" -> Optional.of(LaptopStorageAttribute.TYPE);
			default -> Optional.empty();
		};
	}
}

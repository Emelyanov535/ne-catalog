package ru.necatalog.ozon.parser.parsing.dto.characteristics;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CharacteristicsHolder {

    private List<Characteristic> characteristics;

}
package ru.necatalog.ozon.parser.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.necatalog.persistence.enumeration.Category;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParseOzonCharacteristicPayload {

    private String productUrl;

    private Category category;

}

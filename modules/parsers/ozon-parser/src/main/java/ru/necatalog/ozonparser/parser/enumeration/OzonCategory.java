package ru.necatalog.ozonparser.parser.enumeration;

import lombok.Getter;
import ru.necatalog.persistence.enumeration.Category;

public enum OzonCategory {

    LAPTOP ("/noutbuki-15692/?brandcertified=t", Category.LAPTOP);

    //SMARTPHONE ("/smartfony-15502/?brandcertified=t", Category.SMARTPHONE);

    private static final String BASE_CATEGORY_URL = "https://www.ozon.ru/category";

    private final String categoryUrl;

    @Getter
    private final Category mappedCategory;

    OzonCategory(String categoryUrl,
                 Category mappedCategory) {
        this.categoryUrl = categoryUrl;
        this.mappedCategory = mappedCategory;
    }

    public String getCategoryUrl() {
        return BASE_CATEGORY_URL + categoryUrl;
    }

}

package ru.necatalog.ozon.parser.parsing.dto.characteristics;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Characteristic {

    private String title;

    @JsonAlias("short")
    private List<Short> attributes;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Short {

        private String key;

        private String name;

        private List<Values> values;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Values {

            private String key;

            private String text;

        }

    }

}

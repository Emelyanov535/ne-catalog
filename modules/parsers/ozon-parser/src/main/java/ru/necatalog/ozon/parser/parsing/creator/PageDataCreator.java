package ru.necatalog.ozon.parser.parsing.creator;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.openqa.selenium.NotFoundException;

public interface PageDataCreator<T> {

    T create(String jsonInHtml) throws JsonProcessingException;

    default String getJson(String jsonInHtml) {
        Element jsonBlock = Jsoup.parse(jsonInHtml).select("pre").first();
        if (jsonBlock == null) {
            throw new NotFoundException("Не нашли json в пришедем html");
        }
        return jsonBlock.text().replace("'", "");
    }

}

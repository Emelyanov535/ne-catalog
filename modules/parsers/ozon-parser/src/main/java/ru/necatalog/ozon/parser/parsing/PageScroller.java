package ru.necatalog.ozon.parser.parsing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

@Slf4j
@RequiredArgsConstructor
public class PageScroller {

    private static final String SCROLL_TO_PAGE_HEIGHT = "window.scrollTo(0, document.body.scrollHeight);";

    public void scrollPage(WebDriver driver) {
        ((JavascriptExecutor) driver).executeScript(SCROLL_TO_PAGE_HEIGHT);
    }

}

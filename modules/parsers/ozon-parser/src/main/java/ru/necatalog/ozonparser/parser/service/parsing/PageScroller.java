package ru.necatalog.ozonparser.parser.service.parsing;

import java.util.concurrent.atomic.AtomicLong;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

@Slf4j
@RequiredArgsConstructor
public class PageScroller {

    private static final String ALL_CONTENT_PAGE_HEIGHT = "return document.body.scrollHeight";

    private static final String SCROLL_TO_PAGE_HEIGHT = "window.scrollTo(0, document.body.scrollHeight);";

    public void scrollToEndOfPage(WebDriver driver) throws InterruptedException {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        AtomicLong lastHeight = new AtomicLong((long) js.executeScript(ALL_CONTENT_PAGE_HEIGHT));
        int attemptsLimit = 100;
        log.info("Начинаем пролистывать страницу до конца");
        while (true) {
            js.executeScript(SCROLL_TO_PAGE_HEIGHT);

            long newHeight = (long) js.executeScript(ALL_CONTENT_PAGE_HEIGHT);

            try {
                var nextPageButtons = driver.findElements(By.cssSelector("div[data-widget='megaPaginator'] > div")).get(1)
                    .findElement(By.cssSelector(":scope > div > div > div"))
                    .findElements(By.tagName("a"));

                if (nextPageButtons != null && newHeight > lastHeight.get()) {
                    log.info("ЗАКОНЧИЛИ СКРОЛЛИТЬ");
                    break;
                }
            } catch (Exception ignored) {}


            if (newHeight > lastHeight.get()) {
                attemptsLimit = 100;
                lastHeight.set(newHeight);
            } else {
                attemptsLimit--;
                Thread.sleep(1000);
                if (attemptsLimit == 0) {
                    break;
                }
            }
        }
    }

}

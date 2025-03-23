package ru.necatalog.ozonparser.parser.service.parsing;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.N;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

@Slf4j
@RequiredArgsConstructor
public class PageScroller {

    private static final String ALL_CONTENT_PAGE_HEIGHT = "return document.body.scrollHeight";

    private static final String SCROLL_TO_PAGE_HEIGHT = "window.scrollTo(0, document.body.scrollHeight);";

    public void scrollToEndOfPage(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        AtomicLong lastHeight = new AtomicLong((long) js.executeScript(ALL_CONTENT_PAGE_HEIGHT));
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        //log.info("Начинаем пролистывать страницу до конца");
        while (true) {
            js.executeScript(SCROLL_TO_PAGE_HEIGHT);
            AtomicLong finalLastHeight = lastHeight;
            try {
                boolean heightChanged = wait.until((ExpectedCondition<Boolean>) d -> {
                    Long newHeight = (Long) js.executeScript(ALL_CONTENT_PAGE_HEIGHT);
                    if (Long.valueOf(newHeight).equals(finalLastHeight.get())) {}
                    return newHeight > finalLastHeight.get();
                });
                if (heightChanged) {
                    lastHeight = new AtomicLong((long) js.executeScript("return document.body.scrollHeight"));
                }
            } catch (TimeoutException e) {
                break;
            }
        }
    }

}

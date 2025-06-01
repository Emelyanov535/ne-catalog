package ru.necatalog.ozon.parser.config;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import com.valensas.undetected.chrome.driver.ChromeDriverBuilder;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.github.bonigarcia.wdm.managers.ChromeDriverManager;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
@ConditionalOnBean(OzonParserConfig.class)
@RequiredArgsConstructor
public class WebDriverConfig {

    private static final AtomicInteger COUNTER = new AtomicInteger();

    //@Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public WebDriver webDriverHeadless(ChromeOptions options) {
        WebDriverManager manager = new ChromeDriverManager();
        manager.cachePath("chromedrivers/" + COUNTER.getAndIncrement());
        manager.setup();
        return new ChromeDriverBuilder().build(options, manager.getDownloadedDriverPath());
    }

    //@Bean
    public ChromeOptions chromeOptions() {
        var options = new ChromeOptions();
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/135.0.7049.114 Safari/537.36");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-infobars");
        options.addArguments("--enable-javascript");

        return options;
    }

}

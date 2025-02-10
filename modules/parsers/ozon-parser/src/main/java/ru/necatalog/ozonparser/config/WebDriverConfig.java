package ru.necatalog.ozonparser.config;

import java.util.HashMap;
import java.util.Map;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class WebDriverConfig {

    @Bean
    @ConditionalOnProperty(prefix = "ozon_parser", name = "mode", havingValue = "visible")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public WebDriver webDriverVisible() {
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.managed_default_content_settings.images", 2);
        prefs.put("profile.managed_default_content_settings.geolocation", 2);

        var options = new ChromeOptions();
        options.setExperimentalOption("prefs", prefs);
        WebDriverManager.chromedriver().setup();
        return new ChromeDriver(options);
    }

    @Bean
    @ConditionalOnProperty(prefix = "ozon_parser", name = "mode", havingValue = "headless")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public WebDriver webDriverHeadless(ChromeOptions options) {
        WebDriverManager.chromedriver().setup();
        return new ChromeDriver(options);
    }

    @Bean
    @ConditionalOnProperty(prefix = "ozon_parser", name = "mode", havingValue = "headless")
    public ChromeOptions chromeOptions() {
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.managed_default_content_settings.images", 2);
        prefs.put("profile.managed_default_content_settings.stylesheets", 2);

        var options = new ChromeOptions();
        options.setExperimentalOption("prefs", prefs);
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.121 Safari/537.36");
        //options.addArguments("--window-size=1920,2000");
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        return options;
    }

}

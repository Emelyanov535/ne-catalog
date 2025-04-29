package ru.necatalog.ozonparser.config;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.valensas.undetected.chrome.driver.ChromeDriverBuilder;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.github.bonigarcia.wdm.managers.ChromeDriverManager;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
@ConditionalOnBean(OzonParserConfig.class)
@RequiredArgsConstructor
public class WebDriverConfig {

    @Bean
    @ConditionalOnProperty(prefix = "ozon-parser", name = "mode", havingValue = "visible")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public WebDriver webDriverVisible() {
        Map<String, Object> prefs = new HashMap<>();
        //prefs.put("profile.managed_default_content_settings.images", 2);
        //prefs.put("profile.managed_default_content_settings.geolocation", 2);

        var options = new ChromeOptions();
        options.setExperimentalOption("prefs", prefs);
        WebDriverManager.chromedriver().setup();
        return new ChromeDriver(options);
    }

    @Bean
    @ConditionalOnProperty(prefix = "ozon-parser", name = "mode", havingValue = "headless")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public WebDriver webDriverHeadless(ChromeOptions options) {
        WebDriverManager manager = new ChromeDriverManager();
        manager.setup();
        String temp = manager.getDownloadedDriverPath();
        return new ChromeDriverBuilder().build(options, temp);
    }

    @Bean
    @ConditionalOnProperty(prefix = "ozon-parser", name = "mode", havingValue = "headless")
    public ChromeOptions chromeOptions() {
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.managed_default_content_settings.images", 2);
        prefs.put("profile.managed_default_content_settings.stylesheets", 2);

        var options = new ChromeOptions();
        //options.setExperimentalOption("excludeSwitches", List.of("enable-automation"));
        //options.setExperimentalOption("useAutomationExtension", false);
        options.setExperimentalOption("prefs", prefs);
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/135.0.7049.114 Safari/537.36");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--start-maximized");
        //options.addArguments("--headless=new");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-infobars");
        options.addArguments("--enable-javascript");

        return options;
    }

}

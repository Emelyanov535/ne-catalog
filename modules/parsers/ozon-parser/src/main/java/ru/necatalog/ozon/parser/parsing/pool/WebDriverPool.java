package ru.necatalog.ozon.parser.parsing.pool;

import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import com.valensas.undetected.chrome.driver.ChromeDriverBuilder;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.github.bonigarcia.wdm.managers.ChromeDriverManager;
import jakarta.annotation.PreDestroy;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.ObjectFactory;
import ru.necatalog.ozon.parser.config.properties.OzonParserProperties;

@Slf4j
public class WebDriverPool {

    private final Queue<WebDriver> availableDrivers = new ConcurrentLinkedQueue<>();

    private final Queue<WebDriver> busyDrivers = new ConcurrentLinkedQueue<>();

    private final OzonParserProperties properties;

    private final ObjectFactory<WebDriver> webDriverFactory;

    public WebDriverPool(ObjectFactory<WebDriver> webDriverFactory,
                         OzonParserProperties ozonConfigProperties) {
        this.properties = ozonConfigProperties;
        this.webDriverFactory = webDriverFactory;
        int poolSize = ozonConfigProperties.getMaxThreads();

        for (int i = 0; i < poolSize + 1; i++) {
            availableDrivers.add(createNewDriver());
        }
    }

    private WebDriver createNewDriver() {
        return webDriverFactory.getObject();
    }

    public WebDriver borrowDriver() {
        WebDriver driver = availableDrivers.poll();
        if (driver != null) {
            ((JavascriptExecutor) driver).executeScript(
                "Object.defineProperty(navigator, 'webdriver', {get: () => undefined})"
            );
            busyDrivers.add(driver);
            driver.manage().timeouts().pageLoadTimeout(Duration.of(10, ChronoUnit.SECONDS));
            driver.switchTo().newWindow(WindowType.TAB);
            return driver;
        }
        throw new NoSuchElementException("No available driver found");
    }

    public void returnDriver(WebDriver driver) {
        busyDrivers.remove(driver);
        ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
        for (int i = 0; i < tabs.size();) {
            if (tabs.size() == 1) {
                break;
            }
            driver.switchTo().window(tabs.get(i));
            driver.close();
            tabs.remove(i);
        }
        driver.switchTo().window(tabs.getFirst());
        availableDrivers.add(driver);
    }

    @PreDestroy
    public void shutdownPool() {
        for (WebDriver driver : availableDrivers) {
            driver.quit();
        }

        for (WebDriver driver : busyDrivers) {
            driver.quit();
        }
        availableDrivers.clear();
        busyDrivers.clear();
    }

    public WebDriver recreate(WebDriver driver) {
        driver.quit();
        availableDrivers.remove(driver);
        busyDrivers.remove(driver);
        driver = createNewDriver();
        availableDrivers.add(driver);

        return borrowDriver();
    }

}

package ru.necatalog.ozon.parser.parsing.pool;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.valensas.undetected.chrome.driver.ChromeDriverBuilder;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.github.bonigarcia.wdm.managers.ChromeDriverManager;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.ObjectFactory;
import ru.necatalog.ozon.parser.config.properties.OzonParserProperties;
import ru.necatalog.ozon.parser.parsing.dto.KeyValue;

@Slf4j
public class WebDriverPool {

    private final Queue<KeyValue<Integer, WebDriver>> availableDrivers = new ConcurrentLinkedQueue<>();

    private final Queue<KeyValue<Integer, WebDriver>> busyDrivers = new ConcurrentLinkedQueue<>();

    private final ObjectFactory<WebDriver> webDriverFactory;

    private static final ChromeOptions chromeOptions = new ChromeOptions();

    public WebDriverPool(ObjectFactory<WebDriver> webDriverFactory,
                         OzonParserProperties ozonConfigProperties) {
        this.webDriverFactory = webDriverFactory;
        int poolSize = ozonConfigProperties.getMaxThreads();

        for (int i = 0; i < poolSize + 1; i++) {
            availableDrivers.add(new KeyValue<>(i, createNewDriver(i)));
        }

        chromeOptions();
    }

    private WebDriver createNewDriver(Integer i) {
        return webDriverHeadless(i);
    }

    public WebDriver webDriverHeadless(Integer i) {
        WebDriverManager manager = new ChromeDriverManager();
        manager.cachePath("chromedrivers/" + i);
        manager.setup();
        WebDriver driver = new ChromeDriverBuilder().build(chromeOptions(), manager.getDownloadedDriverPath());
        driver.switchTo().newWindow(WindowType.TAB);
        ((JavascriptExecutor) driver).executeScript(
            "Object.defineProperty(navigator, 'webdriver', {get: () => undefined})"
        );
        return driver;
    }

    public ChromeOptions chromeOptions() {
        return new ChromeOptions().addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
            "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/135.0.7049.114 Safari/537.36")
            .addArguments("--disable-blink-features=AutomationControlled")
            .addArguments("--disable-gpu")
            .addArguments("--no-sandbox")
            .addArguments("--disable-dev-shm-usage")
            .addArguments("--remote-allow-origins=*")
            .addArguments("--disable-infobars")
            .addArguments("--enable-javascript");
    }

    public synchronized KeyValue<Integer, WebDriver> borrowDriver() {
        KeyValue<Integer, WebDriver> driver = availableDrivers.poll();
        if (driver != null) {
            busyDrivers.add(driver);
            driver.getValue().manage().timeouts().pageLoadTimeout(Duration.of(10, ChronoUnit.SECONDS));
            return driver;
        }
        throw new NoSuchElementException("No available driver found");
    }

    public synchronized void returnDriver(KeyValue<Integer, WebDriver> driver) {
        try {
            busyDrivers.remove(driver);
            /*driver.getValue().close();
            driver.setValue(webDriverHeadless(driver.getKey()));*/
            availableDrivers.add(driver);
        } catch (Exception ex) {
            log.info(ex.getMessage());
        }
        /*ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
        for (int i = 0; i < tabs.size();) {
            if (tabs.size() == 1) {
                break;
            }
            driver.switchTo().window(tabs.get(i));
            driver.close();
            tabs.remove(i);
        }*/
    }

    @PreDestroy
    public void shutdownPool() {
        for (KeyValue<Integer, WebDriver> driver : availableDrivers) {
            driver.getValue().quit();
        }

        for (KeyValue<Integer, WebDriver> driver : busyDrivers) {
            driver.getValue().quit();
        }
        availableDrivers.clear();
        busyDrivers.clear();
    }

    public synchronized KeyValue<Integer, WebDriver> recreate(KeyValue<Integer, WebDriver> driver) {
        availableDrivers.remove(driver);
        busyDrivers.remove(driver);
        driver.getValue().quit();
        driver.setValue(webDriverHeadless(driver.getKey()));
        availableDrivers.add(driver);

        return borrowDriver();
    }

}

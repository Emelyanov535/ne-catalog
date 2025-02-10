package ru.necatalog.ozonparser.parser.pool;

import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.ObjectFactory;
import ru.necatalog.ozonparser.config.properties.OzonParserConfigProperties;

@Slf4j
public class WebDriverPool {

    private final Queue<WebDriver> availableDrivers = new ConcurrentLinkedQueue<>();

    private final Queue<WebDriver> busyDrivers = new ConcurrentLinkedQueue<>();

    private final ObjectFactory<WebDriver> webDriverFactory;

    private final OzonParserConfigProperties ozonConfigProperties;

    public WebDriverPool(ObjectFactory<WebDriver> webDriverFactory,
                         OzonParserConfigProperties ozonConfigProperties) {
        this.webDriverFactory = webDriverFactory;
        this.ozonConfigProperties = ozonConfigProperties;
        int poolSize = ozonConfigProperties.getMaxThreads();

        for (int i = 0; i < poolSize; i++) {
            availableDrivers.add(createNewDriver());
        }
    }

    private WebDriver createNewDriver() {
        return webDriverFactory.getObject();
    }

    public WebDriver borrowDriver() {
        WebDriver driver = availableDrivers.poll();
        if (driver != null) {
            busyDrivers.add(driver);
            return driver;
        }
        throw new NoSuchElementException("No available driver found");
    }

    public void returnDriver(WebDriver driver) {
        busyDrivers.remove(driver);
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

}

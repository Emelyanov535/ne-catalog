package ru.necatalog.wildberriesparser.util;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v135.network.Network;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Component
public class CaptchaTokenProvider {
	private volatile String captchaId;
	private final ReentrantLock lock = new ReentrantLock();
	private final AtomicInteger failedRequestsCounter = new AtomicInteger(0);
	private static final int FAILED_REQUESTS_THRESHOLD = 20;

	@SneakyThrows
	public String getCurrentCaptchaId() {
		if (captchaId == null) {
			refreshCaptchaId();
		}
		return captchaId;
	}

	public void requestFailed() {
		int currentFailures = failedRequestsCounter.incrementAndGet();
		if (currentFailures >= FAILED_REQUESTS_THRESHOLD) {
			refreshCaptchaId();
			failedRequestsCounter.set(0);
		}
	}

	@SneakyThrows
	public void refreshCaptchaId() {
		if (lock.tryLock()) {
			try {
				log.info("Refreshing captcha token...");
				this.captchaId = fetchNewCaptchaId();
				log.info("New captcha token received: {}", captchaId);
			} finally {
				lock.unlock();
			}
		}
	}

	private String fetchNewCaptchaId() {
		ChromeDriver driver = new ChromeDriver();
		try {
			DevTools devTools = driver.getDevTools();
			devTools.createSession();
			devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));

			final String[] captchaId = new String[1];

			devTools.addListener(Network.requestWillBeSent(), request -> {
				if (request.getRequest().getHeaders().containsKey("x-captcha-id")) {
					captchaId[0] = (String) request.getRequest().getHeaders().get("x-captcha-id");
				}
			});

			driver.get("https://www.wildberries.ru/");
			Thread.sleep(10000);

			if (captchaId[0] == null) {
				throw new RuntimeException("Failed to get captchaId from headers");
			}

			return captchaId[0];
		} catch (Exception e) {
			log.error("Error while fetching captchaId", e);
			throw new RuntimeException("Failed to fetch captchaId", e);
		} finally {
			driver.quit();
		}
	}
}
package ru.necatalog.wildberriesparser.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class DynamicProxyInterceptor implements ClientHttpRequestInterceptor {

	private final int proxyPort;

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
		boolean isIpCheck = request.getURI().toString().contains("checkip.amazonaws.com");

		if (!isIpCheck) {
			Map.Entry<String, String> randomUserAgent = UserAgentProvider.getRandomUserAgent();
			request.getHeaders().set("sec-ch-ua-platform", randomUserAgent.getKey());
			request.getHeaders().set("User-Agent", randomUserAgent.getValue());

			log.info("Используемый User-Agent ({}) : {}", randomUserAgent.getKey(), randomUserAgent.getValue());
			log.info("Using SOCKS proxy on port: {}", proxyPort);
		}

		return execution.execute(request, body);
	}
}


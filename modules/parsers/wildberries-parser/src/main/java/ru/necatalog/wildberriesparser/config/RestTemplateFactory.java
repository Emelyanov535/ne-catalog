package ru.necatalog.wildberriesparser.config;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;
import java.util.Random;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Slf4j
@AllArgsConstructor
public class RestTemplateFactory {

    private static final List<Integer> TOR_PORTS = List.of(
            9050, 9052, 9053, 9054, 9055,
            9056, 9057, 9058, 9059, 9060,
            9061, 9062, 9063, 9064, 9065,
            9066, 9067, 9068, 9069, 9070,
            9071, 9072, 9073, 9074, 9075);
    private static final Random RANDOM = new Random();

    public static RestTemplate createRestTemplateWithDynamicProxy() {
        int selectedPort = TOR_PORTS.get(RANDOM.nextInt(TOR_PORTS.size()));

        Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1", selectedPort));
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setProxy(proxy);

        RestTemplate restTemplate = new RestTemplate(factory);
        restTemplate.getInterceptors().add(new DynamicProxyInterceptor(selectedPort));

        log.info("Created RestTemplate with proxy on port: {}", selectedPort);

        return restTemplate;
    }
}

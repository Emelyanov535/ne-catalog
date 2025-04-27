package ru.necatalog.wildberriesparser.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class UserAgentProvider {


    private static final Map<String, String> userAgents = Map.of(
            "windows", "Mozilla/5.0 (Windows; U; Windows NT 6.2; x64) AppleWebKit/537.32 (KHTML, like Gecko) Chrome/50.0.1219.254 Safari/534",
            "firefox", "Mozilla/5.0 (Linux x86_64; en-US) Gecko/20100101 Firefox/73.8",
            "linux", "Mozilla/5.0 (Linux; Linux i586 x86_64; en-US) Gecko/20100101 Firefox/60.4",
            "edge", "Mozilla/5.0 (Windows NT 10.2;; en-US) AppleWebKit/533.50 (KHTML, like Gecko) Chrome/47.0.3124.309 Safari/533.3 Edge/15.18428",
            "mobile", "Mozilla/5.0 (iPad; CPU iPad OS 10_2_0 like Mac OS X) AppleWebKit/535.19 (KHTML, like Gecko)  Chrome/54.0.2284.207 Mobile Safari/535.3",
            "explorer", "Mozilla/5.0 (compatible; MSIE 7.0; Windows; Windows NT 6.3; WOW64; en-US Trident/4.0)",
            "iphone", "Mozilla/5.0 (iPhone; CPU iPhone OS 11_9_3; like Mac OS X) AppleWebKit/602.4 (KHTML, like Gecko)  Chrome/51.0.1430.160 Mobile Safari/537.6",
            "android", "Mozilla/5.0 (Linux; U; Android 6.0; HTC One M8 Pro Build/MRA58K) AppleWebKit/601.6 (KHTML, like Gecko)  Chrome/51.0.3751.375 Mobile Safari/603.2"
    );

    public static Map.Entry<String, String> getRandomUserAgent() {
        List<Map.Entry<String, String>> entries = new ArrayList<>(userAgents.entrySet());
        return entries.get(new Random().nextInt(entries.size()));
    }


}

package ru.necatalog.wildberriesparser.service.client;

import java.util.Map;

public interface Client {
    Map<String, Object> scrapPage(int page, String shard, String query);
}

package ru.ulstu.parsingservice.ozon_parser.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Profile("postgres_stat")
public class PartitionService {

    private final JdbcTemplate postgresDataSource;

    public boolean checkPartitionExists(String partitionName) {
        String query = "SELECT to_regclass('public." + partitionName + "')";
        String result = postgresDataSource.queryForObject(query, String.class);
        return result != null;
    }

    public void createPartition(String partitionName, String startDate, String endDate) {
        String createPartitionSQL = "CREATE TABLE IF NOT EXISTS " + partitionName +
            " PARTITION OF price_history FOR VALUES FROM ('" + startDate + "') TO ('" + endDate + "')";
        postgresDataSource.execute(createPartitionSQL);
    }

}

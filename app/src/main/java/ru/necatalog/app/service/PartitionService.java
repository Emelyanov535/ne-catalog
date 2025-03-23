package ru.necatalog.app.service;

import lombok.RequiredArgsConstructor;
import org.hibernate.StaleStateException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PartitionService {

    private final JdbcTemplate postgresJdbcTemplate;

    public boolean checkPartitionExists(String partitionName) {
        String query = "SELECT to_regclass('public." + partitionName + "')";
        String result = postgresJdbcTemplate.queryForObject(query, String.class);
        return result != null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Retryable(maxAttempts = 5, backoff = @Backoff(value = 100L, multiplier = 2),
        include = {StaleStateException.class, ObjectOptimisticLockingFailureException.class})
    public void createPartition(String partitionName, String startDate, String endDate) {
        String createPartitionSQL = "CREATE TABLE IF NOT EXISTS " + partitionName +
            " PARTITION OF price_history FOR VALUES FROM ('" + startDate + "') TO ('" + endDate + "')";
        postgresJdbcTemplate.execute(createPartitionSQL);
    }

}

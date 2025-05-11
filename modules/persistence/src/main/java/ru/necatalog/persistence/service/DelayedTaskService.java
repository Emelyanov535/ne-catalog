package ru.necatalog.persistence.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.config.DelayedTask;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.necatalog.persistence.entity.DelayedTaskEntity;
import ru.necatalog.persistence.enumeration.DelayedTaskStatus;
import ru.necatalog.persistence.enumeration.DelayedTaskType;
import ru.necatalog.persistence.repository.DelayedTaskRepository;

@Service
@RequiredArgsConstructor
public class DelayedTaskService {

    private final DelayedTaskRepository delayedTaskRepository;

    @Transactional
    public void update(DelayedTaskEntity delayedTask) {
        delayedTaskRepository.save(delayedTask);
    }

    @Transactional(readOnly = true)
    public List<DelayedTaskEntity> findByType(DelayedTaskType type) {
      return delayedTaskRepository.findByTypeEquals(type);
    }

    @Transactional
    public void delete(DelayedTaskEntity task) {
        delayedTaskRepository.delete(task);
    }

}

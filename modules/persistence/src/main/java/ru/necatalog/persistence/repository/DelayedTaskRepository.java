package ru.necatalog.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.necatalog.persistence.entity.DelayedTaskEntity;
import ru.necatalog.persistence.enumeration.DelayedTaskType;

@Repository
public interface DelayedTaskRepository extends JpaRepository<DelayedTaskEntity, Long> {

    List<DelayedTaskEntity> findByTypeEquals(DelayedTaskType type);

}

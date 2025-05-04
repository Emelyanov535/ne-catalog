package ru.necatalog.persistence.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.necatalog.persistence.entity.AttributeEntity;

@Repository
public interface AttributeRepository extends JpaRepository<AttributeEntity, Long> {

    List<AttributeEntity> findAllByGroup(String name);

    List<AttributeEntity> findAllByGroupContains(String category);

    List<AttributeEntity> findAllByGroupIn(Collection<String> groups);

    List<AttributeEntity> findAllByNameIn(Collection<String> names);
}

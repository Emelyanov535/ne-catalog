package ru.necatalog.persistence.entity;

import java.sql.Types;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;
import ru.necatalog.persistence.enumeration.DelayedTaskStatus;
import ru.necatalog.persistence.enumeration.DelayedTaskType;

@Getter
@Setter
@Entity
@Table(name = "delayed_task")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DelayedTaskEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private DelayedTaskStatus status;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private DelayedTaskType type;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String payload;

    private LocalDateTime createdAt;

}

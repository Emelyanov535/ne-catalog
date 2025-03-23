package ru.necatalog.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.necatalog.persistence.enumeration.AttributeGroup;

@Entity
@Getter
@Table(name = "attribute")
@NoArgsConstructor
public class AttributeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "\"group\"", nullable = false)
    private String group;

    public AttributeEntity(AttributeGroup attributeGroup) {
        this.name = attributeGroup.toString();
        this.group = attributeGroup.getGroupName();
    }

}

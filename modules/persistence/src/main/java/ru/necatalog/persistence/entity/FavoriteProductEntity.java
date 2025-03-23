package ru.necatalog.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "favorite_products")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FavoriteProductEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private UserEntity user;

	@ManyToOne
	@JoinColumn(name = "product_url", nullable = false)
	private ProductEntity product;
}


package ru.necatalog.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "price_change_message")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PriceChangeMessage {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "product_url", nullable = false)
	private ProductEntity product;

	@Column(name = "old_price", nullable = false, precision = 10, scale = 2)
	private BigDecimal oldPrice;

	@Column(name = "new_price", nullable = false, precision = 10, scale = 2)
	private BigDecimal newPrice;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "processed")
	private boolean processed;
}

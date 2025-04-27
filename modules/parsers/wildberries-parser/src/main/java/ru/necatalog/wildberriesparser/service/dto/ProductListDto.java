package ru.necatalog.wildberriesparser.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductListDto {

	private Data data;

	@lombok.Data
	@NoArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Data {
		private int total;
		private List<ProductInfoDto> products;

		@lombok.Data
		@NoArgsConstructor
		@JsonIgnoreProperties(ignoreUnknown = true)
		public static class ProductInfoDto {
			private Long id;
			private String brand;
			private String name;
			private List<Sizes> sizes;

			@lombok.Data
			@NoArgsConstructor
			@JsonIgnoreProperties(ignoreUnknown = true)
			public static class Sizes {
				private Price price;

				@lombok.Data
				@NoArgsConstructor
				@JsonIgnoreProperties(ignoreUnknown = true)
				public static class Price {
					private Integer basic;
					private Integer product;
					private Integer total;
				}
			}
		}
	}
}

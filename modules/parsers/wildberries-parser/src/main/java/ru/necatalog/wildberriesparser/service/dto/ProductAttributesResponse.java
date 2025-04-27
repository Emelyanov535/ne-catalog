package ru.necatalog.wildberriesparser.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductAttributesResponse {
	@JsonProperty("grouped_options")
	private List<GroupedOption> groupedOptions;

	@Data
	@NoArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class GroupedOption {
		@JsonProperty("group_name")
		private String groupName;
		private List<Option> options;

		@Data
		@NoArgsConstructor
		@JsonIgnoreProperties(ignoreUnknown = true)
		public static class Option {
			private String name;
			private String value;
		}
	}
}


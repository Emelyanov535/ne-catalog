package ru.necatalog.analytics.service.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForecastRequest {
	private OriginalTimeSeries originalTimeSeries;
	private int countForecast;
	private String methodClassName;

	@Getter
	@Setter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class OriginalTimeSeries {
		private List<ValueDto> values;
		private int length;
	}
}





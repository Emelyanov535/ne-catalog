package ru.necatalog.analytics.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

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
		private List<Value> values;
	}

	@Getter
	@Setter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Value {
		private String date;
		private BigDecimal value;
	}
}





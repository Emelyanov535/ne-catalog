package ru.necatalog.analytics.service.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ForecastResponse {
	private TimeSeries timeSeries;
	private TimeSeries testForecast;
	private List<ParamValue> paramValues;

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class TimeSeries {
		private List<ValueDto> values;
		private String name;
		private int length;
		private boolean empty;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ParamValue {
		private Parameter parameter;
		private double value;
		private int intValue;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Parameter {
		private String name;
		private double minValue;
		private double maxValue;
		private double optimizationStep;
	}
}



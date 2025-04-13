package ru.necatalog.analytics.web.dto;

import lombok.Builder;
import lombok.Data;
import ru.necatalog.analytics.service.dto.ValueDto;

import java.util.List;

@Data
@Builder
public class ForecastWithHistoricalAndPredData {
	private List<ValueDto> historical;
	private List<ValueDto> prediction;
}

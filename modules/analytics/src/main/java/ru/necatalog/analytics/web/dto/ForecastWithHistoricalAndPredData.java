package ru.necatalog.analytics.web.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import ru.necatalog.analytics.service.dto.ValueDto;

@Data
@Builder
public class ForecastWithHistoricalAndPredData {
	private List<ValueDto> historical;
	private List<ValueDto> prediction;
}

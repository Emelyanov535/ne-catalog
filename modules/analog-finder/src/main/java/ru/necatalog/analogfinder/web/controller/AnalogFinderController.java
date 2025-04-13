package ru.necatalog.analogfinder.web.controller;

import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.necatalog.analogfinder.service.AnalogFinderService;
import ru.necatalog.analogfinder.web.api.AnalogFinderApi;

@RestController
@RequiredArgsConstructor
public class AnalogFinderController implements AnalogFinderApi {

    private final AnalogFinderService analogFinderService;

    @Override
    public Map<String, List<Double>> findAnalogs(String productUrl,
                                           List<String> attributeGroups) {
        return analogFinderService.findAnalogs(productUrl, attributeGroups);
    }

}

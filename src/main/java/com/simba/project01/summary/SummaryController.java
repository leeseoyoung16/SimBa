package com.simba.project01.summary;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SummaryController
{
    private final SummaryService summaryService;

    @PostMapping(path = "/_debug/summary", consumes="text/plain", produces="text/plain")
    public String debugSummarize(@RequestBody String text) {
        return summaryService.summarize(text);
    }
}

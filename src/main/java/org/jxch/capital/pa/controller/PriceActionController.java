package org.jxch.capital.pa.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.jxch.capital.pa.core.HistK;
import org.jxch.capital.pa.core.PAOutput;
import org.jxch.capital.pa.core.PriceActionService;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "pa")
public class PriceActionController {
    private final PriceActionService priceActionService;

    @ResponseBody
    @RequestMapping(path = "analyzeDefault", method = {RequestMethod.GET, RequestMethod.POST})
    public List<PAOutput> analyze(@RequestBody @NonNull PAAnalyzeParams params) {
        if (params.hasParams()) {
            return priceActionService.analyze(params.getHistKs(), params.getParams());
        } else {
            return priceActionService.analyze(params.getHistKs());
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PAAnalyzeParams {
        private List<HistK> histKs = null;
        private Map<String, Object> params = null;

        public boolean hasParams() {
            return Objects.nonNull(params);
        }
    }

}

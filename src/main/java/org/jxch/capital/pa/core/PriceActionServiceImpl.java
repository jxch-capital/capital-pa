package org.jxch.capital.pa.core;

import com.alibaba.fastjson2.JSON;
import org.jxch.capital.pa.support.SpringContextHolder;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PriceActionServiceImpl implements PriceActionService {

    public List<PriceAction> getAllPriceAction() {
        Map<String, PriceAction> beansOfType = SpringContextHolder.APP_CONTEXT.getBeansOfType(PriceAction.class);
        return beansOfType.values().stream().sorted(Comparator.comparingInt(PriceAction::getOrder)).toList();
    }

    @Override
    public List<PAOutput> analyze(List<HistK> histKs) {
        List<PriceAction> priceActions = getAllPriceAction().stream().filter(priceAction ->
                priceAction.support(histKs, priceAction.getDefaultPAParams())).toList();

        Map<PriceAction, PAOutput> outputMap = priceActions.stream()
                .map(priceAction -> new AbstractMap.SimpleEntry<>(priceAction, priceAction.output(histKs, priceAction.getDefaultPAParams())))
                .filter(entry -> entry.getValue().notEmpty())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return removeDependOutput(outputMap);
    }

    @Override
    public List<PAOutput> analyze(@NonNull List<HistK> histKs, @NonNull Map<String, Object> params) {
        HashMap<PriceAction, PAOutput> outputMap = new HashMap<>();
        Set<String> paNames = params.keySet();
        for (PriceAction priceAction : getAllPriceAction()) {
            PAParams paParams;
            if (paNames.contains(priceAction.getPAName())) {
                paParams = JSON.parseObject(JSON.toJSONString(params.get(priceAction.getPAName())),
                        priceAction.getDefaultPAParams().getClass());
            } else {
                paParams = priceAction.getDefaultPAParams();
            }

            if (priceAction.support(histKs, paParams)) {
                PAOutput output = priceAction.output(histKs, paParams);
                if (output.notEmpty()) {
                    outputMap.put(priceAction, output);
                }
            }
        }

        return removeDependOutput(outputMap);
    }

    private List<PAOutput> removeDependOutput(@NonNull Map<PriceAction, PAOutput> outputMap) {
        List<PriceAction> dependPAs = outputMap.keySet().stream().map(PriceAction::depend).flatMap(List::stream).toList();
        for (PriceAction priceAction : dependPAs) {
            outputMap.remove(priceAction);
        }

        return outputMap.values().stream().toList();
    }

}

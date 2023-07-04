package org.jxch.capital.pa.core;

import com.alibaba.fastjson2.JSON;
import org.jxch.capital.pa.support.SpringContextHolder;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PriceActionServiceImpl implements PriceActionService {

    public List<PriceAction> getAllPriceAction() {
        Map<String, PriceAction> beansOfType = SpringContextHolder.APP_CONTEXT.getBeansOfType(PriceAction.class);
        return beansOfType.values().stream().sorted(Comparator.comparingInt(PriceAction::getOrder)).toList();
    }

    @Override
    public List<PAOutput> analyze(List<HistK> histKs) {
        return getAllPriceAction().stream().filter(priceAction -> priceAction.support(histKs, priceAction.getDefaultPAParams()))
                .map(priceAction -> priceAction.output(histKs, priceAction.getDefaultPAParams()))
                .filter(PAOutput::notEmpty)
                .toList();
    }

    @Override
    public List<PAOutput> analyze(@NonNull List<HistK> histKs, @NonNull Map<String, Object> params) {
        List<PAOutput> outputs = new ArrayList<>();
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
                    outputs.add(output);
                }
            }
        }

        return outputs;
    }
}

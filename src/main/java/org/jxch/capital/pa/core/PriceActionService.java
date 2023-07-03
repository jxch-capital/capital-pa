package org.jxch.capital.pa.core;

import java.util.List;
import java.util.Map;

public interface PriceActionService {

    List<PAOutput> analyze(List<HistK> histKs);

    List<PAOutput> analyze(List<HistK> histKs, Map<String, Object> params);
}

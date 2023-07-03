package org.jxch.capital.pa.core.index;

import org.jxch.capital.pa.core.HistK;

import java.util.List;

public interface Index<Params extends IndexParams, Output extends IndexOutput> {
    boolean support(List<HistK> histKs, Params params);
    Output calculate(List<HistK> histKs, Params params);
}

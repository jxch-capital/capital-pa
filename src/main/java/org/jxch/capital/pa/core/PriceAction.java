package org.jxch.capital.pa.core;

import org.springframework.core.Ordered;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public interface PriceAction extends Ordered {

    boolean support(List<HistK> histKs, PAParams params);

    PAOutput output(List<HistK> histKs, PAParams params);

    @Override
    default int getOrder() {
        return 0;
    }

    PAParams getDefaultPAParams();

    default String getPAName() {
        return StringUtils.uncapitalize(getClass().getSimpleName());
    }

    default List<PriceAction> depend() {
        return new ArrayList<>();
    }

}

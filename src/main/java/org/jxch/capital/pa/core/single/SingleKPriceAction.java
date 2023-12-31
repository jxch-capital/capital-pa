package org.jxch.capital.pa.core.single;

import org.jxch.capital.pa.core.PriceAction;

public interface SingleKPriceAction extends PriceAction {

    @Override
    default int getOrder() {
        return PriceAction.super.getOrder() - 1;
    }

}

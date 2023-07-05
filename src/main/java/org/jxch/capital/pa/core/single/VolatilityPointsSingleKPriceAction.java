package org.jxch.capital.pa.core.single;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jxch.capital.pa.core.HistK;
import org.jxch.capital.pa.core.PAOutput;
import org.jxch.capital.pa.core.PAParams;
import org.springframework.lang.NonNull;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.IntStream;

public abstract class VolatilityPointsSingleKPriceAction implements SingleKPriceAction {

    protected abstract boolean isVolatility(@NonNull HistK last1, @NonNull HistK last2, @NonNull HistK last3);

    @Override
    public boolean support(@NonNull List<HistK> histKs, PAParams params) {
        if (params instanceof VolatilityPointsPAParams) {
            int len = histKs.size();
            return len > 3 && this.isVolatility(histKs.get(len - 1), histKs.get(len - 2), histKs.get(len - 3));
        }
        return false;
    }

    @Override
    public PAOutput output(@NonNull List<HistK> histKs, PAParams params) {
        List<HistK> points = IntStream.range(3, histKs.size())
                .filter(i -> isVolatility(histKs.get(i), histKs.get(i - 1), histKs.get(i - 2)))
                .mapToObj(i -> histKs.get(i - 1))
                .toList();

        PAOutput paOutput =getDefaultPAOutput();
        if (points.isEmpty()) {
            return paOutput.emptyPAOutput();
        }

        VolatilityPointsPAParams paParams = (VolatilityPointsPAParams) params;
        int size = Math.min(paParams.getPeriod() + 2, points.size());
        if (size == 1) {
            return paOutput;
        }

        return paOutput.setDetailed(getDetailed(points, size, paParams))
                .setSummary(MessageFormat.format("参考最近{0}个波动点", size - 2));
    }

    @Override
    public PAParams getDefaultPAParams() {
        return new VolatilityPointsPAParams();
    }

    protected abstract PAOutput getDefaultPAOutput();

    protected abstract String getDetailed(List<HistK> points, int size, VolatilityPointsPAParams params);

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VolatilityPointsPAParams implements PAParams {
        private int period = 8;
        private double threshold = 0.001;
    }

}

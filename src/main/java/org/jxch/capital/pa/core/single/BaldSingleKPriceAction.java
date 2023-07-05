package org.jxch.capital.pa.core.single;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jxch.capital.pa.core.HistK;
import org.jxch.capital.pa.core.PAOutput;
import org.jxch.capital.pa.core.PAParams;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class BaldSingleKPriceAction implements SingleKPriceAction {
    @Override
    public boolean support(@NonNull List<HistK> histKs, PAParams params) {
        if (histKs.size() > 1 && params instanceof BaldPAParams paParams) {
            HistK lasted = Objects.requireNonNull(CollectionUtils.lastElement(histKs));
            return Math.abs(lasted.getHigh() - lasted.getOpen()) / lasted.getHigh() < paParams.getThreshold()
                    || Math.abs(lasted.getHigh() - lasted.getClose()) / lasted.getHigh() < paParams.getThreshold()
                    || Math.abs(lasted.getLow() - lasted.getClose()) / lasted.getLow() < paParams.getThreshold()
                    || Math.abs(lasted.getLow() - lasted.getOpen()) / lasted.getLow() < paParams.getThreshold();
        }
        return false;
    }

    @Override
    public PAOutput output(List<HistK> histKs, PAParams params) {
        return new BaldPAOutput();
    }

    @Override
    public PAParams getDefaultPAParams() {
        return new BaldPAParams();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BaldPAParams implements PAParams {
        private double threshold = 0.001;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class BaldPAOutput extends PAOutput {
        public BaldPAOutput() {
            setTitle("光K线")
                    .setSummary("一端或两端没有影线的K线")
                    .setDetailed("""
                            只有当其形成于强趋势之中，才是一个建仓形态。
                            比如，在强劲上升趋势中，如果出现一根没有上影线或下影线的多头趋势K线，那么它就是一个买入形态。
                            """);
        }
    }

}

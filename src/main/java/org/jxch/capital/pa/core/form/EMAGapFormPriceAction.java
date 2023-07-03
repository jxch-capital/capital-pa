package org.jxch.capital.pa.core.form;

import lombok.*;
import org.jxch.capital.pa.core.HistK;
import org.jxch.capital.pa.core.PAOutput;
import org.jxch.capital.pa.core.PAParams;
import org.jxch.capital.pa.core.index.EMAIndex;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class EMAGapFormPriceAction implements FormPriceAction {
    private final EMAIndex emaIndex;

    @Override
    public boolean support(List<HistK> histKs, PAParams params) {
        if (params instanceof MAGapFormPAParams maGapFormPAParams) {
            EMAIndex.EMAIndexParams emaIndexParams = toEMAIndexParams(maGapFormPAParams);
            return histKs.size() > maGapFormPAParams.period + emaIndexParams.getPeriod() &&
                    emaIndex.support(histKs, emaIndexParams);
        }
        return false;
    }


    @Override
    public PAOutput output(List<HistK> histKs, PAParams params) {
        MAGapFormPAParams maGapFormPAParams = ((MAGapFormPAParams) params);
        EMAIndex.EMAIndexParams emaIndexParams = toEMAIndexParams(maGapFormPAParams);
        EMAIndex.EMAIndexOutput emaIndexOutput = emaIndex.calculate(histKs, emaIndexParams);

        double[] outReal = emaIndexOutput.getOutReal();

        if (IntStream.range(0, maGapFormPAParams.period).allMatch(i -> histKs.get(histKs.size() - i).getLow() > outReal[outReal.length - i])) {
            String msg = MessageFormat.format("最低价连续{0}日在EMA{1}之上，注意下跌回测均线时出现均线缺口形态，预计回测前高", maGapFormPAParams.period, emaIndexParams.getPeriod());
            return new MAGapFormPAOutput(msg);
        } else if (IntStream.range(0, maGapFormPAParams.period).allMatch(i -> histKs.get(histKs.size() - i).getHigh() < outReal[outReal.length - i])) {
            String msg = MessageFormat.format("最高价连续{0}日在EMA{1}之下，注意上涨回测均线时出现均线缺口形态，预计回踩前底", maGapFormPAParams.period, emaIndexParams.getPeriod());
            return new MAGapFormPAOutput(msg);
        }

        return new MAGapFormPAOutput().emptyPAOutput();
    }

    @Override
    public PAParams getDefaultPAParams() {
        return new MAGapFormPAParams();
    }

    protected EMAIndex.EMAIndexParams toEMAIndexParams(@NonNull MAGapFormPAParams maGapFormPAParams) {
        return new EMAIndex.EMAIndexParams(HistK::getClose, maGapFormPAParams.period);
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MAGapFormPAParams implements PAParams {
        private int period = 20;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MAGapFormPAOutput implements PAOutput {
        private String msg = null;

        @Override
        public boolean isNotEmpty() {
            return StringUtils.hasText(msg);
        }

        @Override
        public PAOutput emptyPAOutput() {
            msg = null;
            return this;
        }
    }

}

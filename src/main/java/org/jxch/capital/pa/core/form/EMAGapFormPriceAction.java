package org.jxch.capital.pa.core.form;

import lombok.*;
import org.jxch.capital.pa.core.HistK;
import org.jxch.capital.pa.core.PAOutput;
import org.jxch.capital.pa.core.PAParams;
import org.jxch.capital.pa.core.index.EMAIndex;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class EMAGapFormPriceAction implements FormPriceAction {
    private final EMAIndex emaIndex;
    private final static String DETAILED = """
            注意回测均线时出现均线缺口形态，预计回测前极值。反转时可能是通道线过靶后的高潮耗竭反转，注意反转形态。
            如果趋势强劲，那么回测时可能并未触碰到均线（如果已经发生了回调，可以忽视这条提示），但第一反弹目标仍是前高。
            """;

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

        if (IntStream.range(1, maGapFormPAParams.period + 1).allMatch(i -> histKs.get(histKs.size() - i).getLow() > outReal[outReal.length - i])) {
            String msg = MessageFormat.format("最低价连续{0}日在EMA{1}之上，" + DETAILED, maGapFormPAParams.period, emaIndexParams.getPeriod());
            return new MAGapFormPAOutput().setMsg(msg);
        } else if (IntStream.range(1, maGapFormPAParams.period + 1).allMatch(i -> histKs.get(histKs.size() - i).getHigh() < outReal[outReal.length - i])) {
            String msg = MessageFormat.format("最高价连续{0}日在EMA{1}之下，" + DETAILED, maGapFormPAParams.period, emaIndexParams.getPeriod());
            return new MAGapFormPAOutput().setMsg(msg);
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
    @EqualsAndHashCode(callSuper = true)
    public static class MAGapFormPAOutput extends PAOutput {
        public MAGapFormPAOutput() {
            setTitle("均线缺口形态");
        }
    }

}

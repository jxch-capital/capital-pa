package org.jxch.capital.pa.core.index;


import com.alibaba.fastjson2.JSON;
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jxch.capital.pa.core.HistK;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.function.ToDoubleFunction;

@Component
public class EMAIndex implements Index<EMAIndex.EMAIndexParams, EMAIndex.EMAIndexOutput> {

    @Override
    public boolean support(@NonNull List<HistK> histKs, @NonNull EMAIndexParams params) {
        return histKs.size() > params.period;
    }

    public EMAIndexOutput calculate(@NonNull List<HistK> histKs, @NonNull EMAIndexParams params) {
        double[] price = histKs.stream().mapToDouble(params.getHistKPrice).toArray();

        int startIdx = 0;
        int endIdx = price.length - 1;

        MInteger outBegIdx = new MInteger();
        MInteger outNBElement = new MInteger();
        double[] outReal = new double[price.length - params.period + 1];

        Core lib = new Core();
        RetCode retCode = lib.ema(startIdx, endIdx, price, params.period, outBegIdx, outNBElement, outReal);

        if (retCode == RetCode.Success) {
            int num = outBegIdx.value;
            double[] newPrice = new double[num + outReal.length];
            Arrays.fill(newPrice, 0, num, params.fill);
            System.arraycopy(outReal, 0, newPrice, num, outReal.length);

            return EMAIndexOutput.builder()
                    .outBegIdx(outBegIdx.value)
                    .outNum(outNBElement.value)
                    .outReal(outReal)
                    .fillOutReal(newPrice)
                    .build();
        } else {
            throw new IllegalArgumentException("计算失败, params:" + JSON.toJSONString(params) +
                    ". arr:" + JSON.toJSONString(histKs));
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EMAIndexParams implements IndexParams {
        private ToDoubleFunction<HistK> getHistKPrice;
        private int period;
        private int fill = -1;

        public EMAIndexParams(ToDoubleFunction<HistK> getHistKPrice, int period) {
            this.getHistKPrice = getHistKPrice;
            this.period = period;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EMAIndexOutput implements IndexOutput {
        private int outBegIdx;
        private int outNum;
        private double[] outReal;
        private double[] fillOutReal;
    }

}

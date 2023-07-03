package org.jxch.capital.pa.core.form;

import lombok.*;
import org.jxch.capital.pa.core.HistK;
import org.jxch.capital.pa.core.PAOutput;
import org.jxch.capital.pa.core.PAParams;
import org.jxch.capital.pa.core.index.EMAIndex;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MAGapFormPriceAction implements FormPriceAction {
    private final EMAIndex emaIndex;

    @Override
    public boolean support(List<HistK> histKs, PAParams params) {
        if (params instanceof MAGapFormPAParams maGapFormPAParams) {
            return emaIndex.support(histKs, toEMAIndexParams(maGapFormPAParams));
        }
        return false;
    }


    @Override
    public PAOutput output(List<HistK> histKs, PAParams params) {
        EMAIndex.EMAIndexOutput emaIndexOutput = emaIndex.calculate(histKs, toEMAIndexParams((MAGapFormPAParams) params));


        return null;
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

    }

}

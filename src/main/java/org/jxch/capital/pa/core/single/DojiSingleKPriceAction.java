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
public class DojiSingleKPriceAction implements SingleKPriceAction {

    @Override
    public boolean support(@NonNull List<HistK> histKs, PAParams params) {
        if (histKs.size() > 1 && params instanceof DojiPAParams paParams) {
            HistK lasted = Objects.requireNonNull(CollectionUtils.lastElement(histKs));
            return Math.abs(lasted.getOpen() - lasted.getClose()) / (lasted.getHigh() - lasted.getLow()) < paParams.getThreshold();
        }
        return false;
    }

    @Override
    public PAOutput output(List<HistK> histKs, PAParams params) {
        return new DojiPAOutput();
    }

    @Override
    public PAParams getDefaultPAParams() {
        return new DojiPAParams();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DojiPAParams implements PAParams {
        private double threshold = 0.1;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class DojiPAOutput extends PAOutput {
        public DojiPAOutput() {
            setTitle("十字星")
                    .setSummary("实体几乎不存在")
                    .setDetailed("""
                            十字星意味着单K线交易区间，意味着不确定，不是好的信号K线。
                            只有在交易区间顶底或高潮K线末端才能形成有效的反转信号
                            """);
        }
    }
}

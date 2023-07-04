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

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
public class VolatilityLowsSingleKPriceAction implements SingleKPriceAction {

    private boolean isVolatilityLow(@NonNull HistK last1, @NonNull HistK last2, @NonNull HistK last3) {
        return last1.getLow() >= last2.getLow() && last2.getLow() <= last3.getLow();
    }

    @Override
    public boolean support(@NonNull List<HistK> histKs, PAParams params) {
        if (params instanceof VolatilityLowsPAParams) {
            int len = histKs.size();
            return len > 3 && this.isVolatilityLow(histKs.get(len - 1), histKs.get(len - 2), histKs.get(len - 3));
        }
        return false;
    }

    @Override
    public PAOutput output(@NonNull List<HistK> histKs, PAParams params) {
        List<HistK> lowPoints = IntStream.range(3, histKs.size())
                .filter(i -> isVolatilityLow(histKs.get(i), histKs.get(i - 1), histKs.get(i - 2)))
                .mapToObj(i -> histKs.get(i - 1))
                .toList();

        VolatilityLowsPAOutput paOutput = new VolatilityLowsPAOutput();
        if (lowPoints.isEmpty()) {
            return paOutput.emptyPAOutput();
        }

        VolatilityLowsPAParams paParams = (VolatilityLowsPAParams) params;
        int size = Math.min(paParams.getPeriod() + 2, lowPoints.size());
        if (size == 1) {
            return paOutput;
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Double lastLow = Objects.requireNonNull(CollectionUtils.lastElement(lowPoints)).getLow();
        String detailed = IntStream.range(2, size)
                .mapToObj(i -> {
                    HistK point = lowPoints.get(lowPoints.size() - i);
                    Double low = point.getLow();

                    String msg = "";
                    if (Math.abs(lastLow - low) / lastLow < paParams.getThreshold()) {
                        msg = MessageFormat.format("与{0}形成双底. ", formatter.format(point.getDate()));
                    }

                    if (lastLow > low) {
                        msg += MessageFormat.format("相对于{0}形成低点抬升. ", formatter.format(point.getDate()));
                    } else if (lastLow < low) {
                        msg += MessageFormat.format("相对于{0}形成低点下降. ", formatter.format(point.getDate()));
                    }

                    return msg;
                }).map(Object::toString)
                .collect(Collectors.joining("\n"));

        return paOutput.setDetailed(detailed).setSummary(MessageFormat.format("参考最近{0}个波动低点", size - 2));
    }

    @Override
    public PAParams getDefaultPAParams() {
        return new VolatilityLowsPAParams();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VolatilityLowsPAParams implements PAParams {
        private int period = 8;
        private double threshold = 0.0001;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class VolatilityLowsPAOutput extends PAOutput {
        public VolatilityLowsPAOutput() {
            setTitle("波动低点");
        }
    }

}

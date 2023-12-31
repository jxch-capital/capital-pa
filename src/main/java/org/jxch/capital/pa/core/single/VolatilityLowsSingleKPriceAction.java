package org.jxch.capital.pa.core.single;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.jxch.capital.pa.core.HistK;
import org.jxch.capital.pa.core.PAOutput;
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
public class VolatilityLowsSingleKPriceAction extends VolatilityPointsSingleKPriceAction {

    @Override
    protected boolean isVolatility(@NonNull HistK last1, @NonNull HistK last2, @NonNull HistK last3) {
        return last1.getLow() >= last2.getLow() && last2.getLow() <= last3.getLow();
    }

    @Override
    protected String getDetailed(List<HistK> points, int size, VolatilityPointsPAParams params) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Double lastLow = Objects.requireNonNull(CollectionUtils.lastElement(points)).getLow();
        return IntStream.range(2, size)
                .mapToObj(i -> {
                    HistK point = points.get(points.size() - i);
                    Double low = point.getLow();

                    String msg = "";
                    if (Math.abs(lastLow - low) / lastLow < params.getThreshold()) {
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
    }

    @Override
    protected PAOutput getDefaultPAOutput() {
        return new VolatilityLowsPAOutput();
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class VolatilityLowsPAOutput extends PAOutput {
        public VolatilityLowsPAOutput() {
            setTitle("波动低点");
        }
    }

}

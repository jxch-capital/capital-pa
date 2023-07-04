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
public class VolatilityHighsSingleKPriceAction extends VolatilityPointsSingleKPriceAction {

    @Override
    protected boolean isVolatility(@NonNull HistK last1, @NonNull HistK last2, @NonNull HistK last3) {
        return last1.getHigh() <= last2.getHigh() && last2.getHigh() >= last3.getHigh();
    }

    @Override
    protected PAOutput getDefaultPAOutput() {
        return new VolatilityHighsPAOutput();
    }

    @Override
    protected String getDetailed(List<HistK> points, int size, VolatilityPointsPAParams params) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Double lastHigh = Objects.requireNonNull(CollectionUtils.lastElement(points)).getHigh();

        return IntStream.range(2, size)
                .mapToObj(i -> {
                    HistK point = points.get(points.size() - i);
                    Double high = point.getHigh();

                    String msg = "";
                    if (Math.abs(lastHigh - high) / lastHigh < params.getThreshold()) {
                        msg = MessageFormat.format("与{0}形成双顶. ", formatter.format(point.getDate()));
                    }

                    if (lastHigh > high) {
                        msg += MessageFormat.format("相对于{0}形成高点抬升. ", formatter.format(point.getDate()));
                    } else if (lastHigh < high) {
                        msg += MessageFormat.format("相对于{0}形成高点下降. ", formatter.format(point.getDate()));
                    }

                    return msg;
                }).map(Object::toString)
                .collect(Collectors.joining("\n"));
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class VolatilityHighsPAOutput extends PAOutput {
        public VolatilityHighsPAOutput() {
            setTitle("波动高点");
        }
    }

}

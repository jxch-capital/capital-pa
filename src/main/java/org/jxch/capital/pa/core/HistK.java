package org.jxch.capital.pa.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class HistK {
    private Date date;
    private Double open;
    private Double close;
    private Double high;
    private Double low;
    private Double volume;
}

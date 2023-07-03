package org.jxch.capital.pa.core;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Duration;
import java.util.Arrays;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum Interval {
    M1(Duration.ofMinutes(1)),
    M5(Duration.ofMinutes(5)),
    M15(Duration.ofMinutes(15)),
    M30(Duration.ofMinutes(30)),
    H1(Duration.ofHours(1)),
    H2(Duration.ofHours(2)),
    H4(Duration.ofHours(4)),
    D1(Duration.ofDays(1)),
    D5(Duration.ofDays(5)),
    D20(Duration.ofDays(20)),
    ;

    private final Duration duration;

    public static Interval valueOf(Duration duration) {
        return Objects.requireNonNull(Arrays.stream(Interval.values())
                .filter(interval -> interval.duration.equals(duration))
                .findAny()
                .orElse(null));
    }

}

package org.jxch.capital.pa.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.util.StringUtils;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class PAOutput {
    protected String detailed = null;
    protected String summary = null;
    protected String title = null;

    public boolean notEmpty() {
        return StringUtils.hasText(title);
    }

    public PAOutput emptyPAOutput() {
        return setTitle(null);
    }

}

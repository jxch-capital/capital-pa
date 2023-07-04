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
    protected String msg = null;
    protected String title = null;

    public boolean notEmpty() {
        return StringUtils.hasText(msg);
    }

    public PAOutput emptyPAOutput() {
        return setMsg(null);
    }

}

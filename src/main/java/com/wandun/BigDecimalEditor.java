package com.wandun;

import org.apache.commons.lang.StringUtils;

import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;

public class BigDecimalEditor extends PropertyEditorSupport {
    @Override
    public String getAsText() {
        Object value = getValue();
        return value != null ? value.toString() : StringUtils.EMPTY;
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (StringUtils.isBlank(text)) {
            setValue(BigDecimal.ZERO);
        } else {
            setValue(text);
        }
    }
}

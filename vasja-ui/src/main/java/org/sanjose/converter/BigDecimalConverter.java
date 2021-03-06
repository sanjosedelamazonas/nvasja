package org.sanjose.converter;

import com.vaadin.data.util.converter.StringToBigDecimalConverter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * A converter that adds/removes the euro sign and formats currencies with two
 * decimal places.
 */
public class BigDecimalConverter extends StringToBigDecimalConverter {

    private int scale = 2;

    public BigDecimalConverter() {
    }

    public BigDecimalConverter(int scale) {
        this.scale = scale;
    }

    @Override
    public BigDecimal convertToModel(String value,
            Class<? extends BigDecimal> targetType, Locale locale)
            throws ConversionException {
        //value = value.replaceAll("[€\\s]", "").trim();
        if ("".equals(value)) {
            value = "0";
        }
        return super.convertToModel(value, targetType, locale);
    }

    @Override
    protected NumberFormat getFormat(Locale locale) {
        // Always display currency with two decimals
        NumberFormat format = super.getFormat(locale);
        if (format instanceof DecimalFormat) {
            ((DecimalFormat) format).setMaximumFractionDigits(scale);
            ((DecimalFormat) format).setMinimumFractionDigits(scale);
        }
        return format;
    }

    @Override
    public String convertToPresentation(BigDecimal value,
            Class<? extends String> targetType, Locale locale)
            throws ConversionException {
        return super.convertToPresentation(value, targetType, locale);
    }
}

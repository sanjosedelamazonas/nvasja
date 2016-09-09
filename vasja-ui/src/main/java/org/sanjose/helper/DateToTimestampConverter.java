package org.sanjose.helper;

import com.vaadin.data.util.converter.Converter;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Locale;

/**
 * SORCER class
 * User: prubach
 * Date: 08.09.16
 */
public class DateToTimestampConverter implements Converter<Date, Timestamp> {

        private static final long serialVersionUID = 1L;
        public static final DateToTimestampConverter INSTANCE = new DateToTimestampConverter();

        @Override
        public Timestamp convertToModel(Date value,
                                        Class<? extends Timestamp> targetType, Locale locale)
                throws Converter.ConversionException {
            return value == null ? null : new Timestamp(value.getTime());
        }

        @Override
        public Date convertToPresentation(Timestamp value,
                                          Class<? extends Date> targetType, Locale locale)
                throws ConversionException {
            return value;
        }

        @Override
        public Class<Timestamp> getModelType() {
            return Timestamp.class;
        }

        @Override
        public Class<Date> getPresentationType() {
            return Date.class;
        }

        private Object readResolve() {
            return INSTANCE; // preserves singleton property
        }

    }

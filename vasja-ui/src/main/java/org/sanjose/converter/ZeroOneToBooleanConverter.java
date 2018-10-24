package org.sanjose.converter;

import com.vaadin.v7.data.util.converter.Converter;

import java.util.Locale;

public class ZeroOneToBooleanConverter implements Converter<Boolean, Character> {

    @Override
    public Character convertToModel(Boolean s, Class<? extends Character> aClass, Locale locale) throws ConversionException {
        if (s != null && s) return '1';
        else return '0';
    }

    @Override
    public Boolean convertToPresentation(Character value, Class<? extends Boolean> targetType, Locale locale) throws ConversionException {
        if (value == null || value.equals('0')) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public Class<Character> getModelType() {
        return Character.class;
    }

    @Override
    public Class<Boolean> getPresentationType() {
        return Boolean.class;
    }
}
package org.sanjose.converter;

import com.vaadin.v7.data.util.converter.Converter;

import java.util.Locale;

public class StringToCharacterConverter implements Converter<String, Character> {

	@Override
	public Character convertToModel(String s, Class<? extends Character> aClass, Locale locale) throws ConversionException {
		return s.charAt(0);
	}

	@Override
	public String convertToPresentation(Character value,
			Class<? extends String> targetType, Locale locale)
					throws ConversionException {
		return value.toString();
	}

	@Override
	public Class<Character> getModelType() {
		return Character.class;
	}

	@Override
	public Class<String> getPresentationType() {
		return String.class;
	}
}
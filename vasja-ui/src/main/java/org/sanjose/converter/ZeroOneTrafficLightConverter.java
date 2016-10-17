package org.sanjose.converter;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.server.FontAwesome;

import java.util.Locale;

public class ZeroOneTrafficLightConverter implements Converter<String, Character> {

	@Override
	public Character convertToModel(String s, Class<? extends Character> aClass, Locale locale) throws ConversionException {
		if (s.contains("#f54993")) return '0';
		else return '1';
	}

	@Override
	public String convertToPresentation(Character value,
			Class<? extends String> targetType, Locale locale)
					throws ConversionException {
		String color;
		if (value == null || value.equals('0')) {
			color = "#f54993";
		} else {
			color = "#2dd085";			
		}
		return FontAwesome.CIRCLE.getHtml().replace("style=\"",
				"style=\"color: " + color + ";");
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
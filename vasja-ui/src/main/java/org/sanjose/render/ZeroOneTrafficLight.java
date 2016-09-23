package org.sanjose.render;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.server.FontAwesome;

import java.util.Locale;

public class ZeroOneTrafficLight implements Converter<String, Character> {

	@Override
	public Character convertToModel(String s, Class<? extends Character> aClass, Locale locale) throws ConversionException {
		return s.charAt(0);
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
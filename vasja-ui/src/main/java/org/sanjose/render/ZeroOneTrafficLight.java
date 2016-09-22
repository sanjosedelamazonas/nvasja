package org.sanjose.render;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.server.FontAwesome;

import java.util.Locale;

public class ZeroOneTrafficLight implements Converter<String, String> {

	@Override
	public String convertToModel(String s, Class<? extends String> aClass, Locale locale) throws ConversionException {
		return s;
	}

	@Override
	public String convertToPresentation(String value,
			Class<? extends String> targetType, Locale locale)
					throws ConversionException {
		String color;
		if (value == null || "0".equals(value)) {
			color = "#f54993";
		} else {
			color = "#2dd085";			
		}
		return FontAwesome.CIRCLE.getHtml().replace("style=\"",
				"style=\"color: " + color + ";");
	}

	@Override
	public Class<String> getModelType() {
		return String.class;
	}

	@Override
	public Class<String> getPresentationType() {
		return String.class;
	}
}
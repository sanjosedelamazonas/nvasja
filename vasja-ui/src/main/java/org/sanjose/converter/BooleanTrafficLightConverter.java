package org.sanjose.converter;

import com.vaadin.v7.data.util.converter.StringToBooleanConverter;
import com.vaadin.server.FontAwesome;

import java.util.Locale;

public class BooleanTrafficLightConverter extends StringToBooleanConverter {

	@Override
	public String convertToPresentation(Boolean value,
			Class<? extends String> targetType, Locale locale)
					throws com.vaadin.v7.data.util.converter.Converter.ConversionException {
		String color;
		if (value == null) return "";
		if (value == null || !value) {
			color = "#f54993";
		} else {
			color = "#2dd085";			
		}
		return FontAwesome.CIRCLE.getHtml().replace("style=\"",
				"style=\"color: " + color + ";");
	}
}
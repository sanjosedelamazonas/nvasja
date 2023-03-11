package org.sanjose.converter;

import com.vaadin.data.util.converter.Converter;

import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class TipoDestinoConverter implements
		Converter<String, Character> {

	private Map<Character, String> valMap = new TreeMap<>();

	public TipoDestinoConverter() {
		valMap.put('0',"Proveedor");
		valMap.put('1',"Empleado");
		valMap.put('2',"Cliente");
		valMap.put('3',"Tercero");
	}

	@Override
	public Character convertToModel(String s, Class<? extends Character> aClass, Locale locale) throws ConversionException {
		throw new UnsupportedOperationException(
				"Can only convert from tipo destino to string");
	}

	@Override
	public String convertToPresentation(Character character, Class<? extends String> aClass, Locale locale) throws ConversionException {
		return valMap.get(character);
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
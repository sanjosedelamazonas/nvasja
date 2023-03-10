package org.sanjose.converter;

import com.vaadin.data.util.converter.Converter;
import org.sanjose.model.ScpCargocuarta;
import org.sanjose.repo.ScpCargocuartaRep;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CargoCuartaConverter implements
		Converter<String, String> {

	private Map<String, String> cargos = new HashMap<>();

	public CargoCuartaConverter(ScpCargocuartaRep cargocuartaRep) {
		for (ScpCargocuarta tipo : cargocuartaRep.findAll()) {
			cargos.put(tipo.getCodCargo(), tipo.getTxtDescripcion());
		}
	}

	@Override
	public String convertToModel(String s, Class<? extends String> aClass, Locale locale) throws ConversionException {
		throw new UnsupportedOperationException(
				"Can only convert from tipo documento to string");
	}

	@Override
	public String convertToPresentation(String value,
			Class<? extends String> targetType, Locale locale)
					throws ConversionException {
		return cargos.get(value);
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
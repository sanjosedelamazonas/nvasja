package org.sanjose.converter;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.StringToBooleanConverter;
import com.vaadin.server.FontAwesome;
import org.sanjose.model.ScpTipodocumento;
import org.sanjose.repo.ScpTipodocumentoRep;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TipoDocumentoConverter implements
		Converter<String, String> {

	private Map<String, String> tipoDocs = new HashMap<>();

	public TipoDocumentoConverter(ScpTipodocumentoRep tipodocumentoRep) {
		for (ScpTipodocumento tipo : tipodocumentoRep.findAll()) {
			tipoDocs.put(tipo.getCodTipodocumento(), tipo.getTxtDescripcion());
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
		return tipoDocs.get(value);
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
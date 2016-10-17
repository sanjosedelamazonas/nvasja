package org.sanjose.converter;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.ui.Grid;
import org.sanjose.model.VsjBancocabecera;
import org.sanjose.util.GenUtil;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class MesCobradoToBooleanConverter implements Converter<Boolean, String> {

    private VsjBancocabecera cabecera;
    private Grid grid;

    public MesCobradoToBooleanConverter(VsjBancocabecera cabecera) {
        this.cabecera = cabecera;
    }

    public MesCobradoToBooleanConverter(Grid grid) {
        this.grid = grid;
    }

    @Override
    public String convertToModel(Boolean s, Class<? extends String> aClass, Locale locale) throws ConversionException {
        if (cabecera == null && grid == null)
            throw new ConversionException("Error converting. Both cabecera and grid are NULLs!");
        String mesCobrado = null;
        if (cabecera != null) mesCobrado = cabecera.getCodMescobrado();
        else mesCobrado = ((VsjBancocabecera) grid.getEditedItemId()).getCodMescobrado();

        if (s != null && s && GenUtil.strNullOrEmpty(mesCobrado)) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM");
            return sdf.format(System.currentTimeMillis());
        } else if ((s == null || !s) && !GenUtil.strNullOrEmpty(mesCobrado)) {
            return null;
        } else {
            return mesCobrado;
        }
    }

    @Override
    public Boolean convertToPresentation(String value, Class<? extends Boolean> targetType, Locale locale) throws ConversionException {
        return !GenUtil.strNullOrEmpty(value);
    }

    @Override
    public Class<String> getModelType() {
        return String.class;
    }

    @Override
    public Class<Boolean> getPresentationType() {
        return Boolean.class;
    }
}
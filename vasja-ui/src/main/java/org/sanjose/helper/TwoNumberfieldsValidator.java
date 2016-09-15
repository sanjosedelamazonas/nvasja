package org.sanjose.helper;

import com.vaadin.data.Validator;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.ui.AbstractField;
import tm.kod.widgets.numberfield.NumberField;


/**
 * SORCER class
 * User: prubach
 * Date: 09.09.16
 */
public class TwoNumberfieldsValidator implements Validator {

    private static final Logger log = LoggerFactory.getLogger(TwoNumberfieldsValidator.class);

    final AbstractField field;

    final String message;

    final boolean isPermitEmpty;

    public TwoNumberfieldsValidator(NumberField field, boolean isPermitEmpty, String message) {
        if (message!=null) message = "los dos no pueden ser rellenados en mismo tiempo";
        this.field = field;
        this.message = message;
        this.isPermitEmpty = isPermitEmpty;
    }

    public boolean isZero(Object value) {
        if (value==null) return false;
        return "0.00".equals(value.toString()) || "0,00".equals(value.toString()) || "0".equals(value.toString());
    }

    @Override
    public void validate(Object value) throws InvalidValueException {
        if (!isPermitEmpty && ((GenUtil.objNullOrEmpty(value)
                && GenUtil.objNullOrEmpty(field.getValue())))) {
            field.markAsDirty();
            throw new InvalidValueException("Uno de los dos tiene que ser rellenado");
        }

        if (!isPermitEmpty && isZero(value) && isZero(field.getValue())) {
            field.markAsDirty();
            throw new InvalidValueException("Los dos no pueden tener zeros");
        }


   }
}

package org.sanjose.validator;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.v7.data.Validator;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.v7.ui.AbstractField;
import org.sanjose.util.GenUtil;
import tm.kod.widgets.numberfield.NumberField;


/**
 * VASJA class
 * User: prubach
 * Date: 09.09.16
 */
public class TwoNumberfieldsValidator implements Validator {

    private static final Logger log = LoggerFactory.getLogger(TwoNumberfieldsValidator.class);

    private final AbstractComponent field;

    private final String message;

    private final boolean isPermitEmpty;

    public TwoNumberfieldsValidator(NumberField field, boolean isPermitEmpty, String message) {
        if (message!=null) message = "los dos no pueden ser rellenados en mismo tiempo";
        this.field = field;
        this.message = message;
        this.isPermitEmpty = isPermitEmpty;
    }

    private boolean isZero(Object value) {
        return value != null && ("0.00".equals(value.toString()) || "0,00".equals(value.toString()) || "0".equals(value.toString()));
    }

    @Override
    public void validate(Object value) throws InvalidValueException {
        if (!isPermitEmpty && ((GenUtil.objNullOrEmpty(value)
                && GenUtil.objNullOrEmpty(field.getData())))) {
            field.markAsDirty();
            throw new InvalidValueException("Uno de los dos tiene que estar rellenado");
        }

        if (!isPermitEmpty && isZero(value) && isZero(field.getData())) {
            field.markAsDirty();
            throw new InvalidValueException("Los dos no pueden tener zeros");
        }


   }
}

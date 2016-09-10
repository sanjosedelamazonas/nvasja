package org.sanjose.helper;

import com.vaadin.data.Validator;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.ui.AbstractSelect;
import org.sanjose.views.CajaGridView;


/**
 * SORCER class
 * User: prubach
 * Date: 09.09.16
 */
public class TwoCombosValidator implements Validator {

    private static final Logger log = LoggerFactory.getLogger(TwoCombosValidator.class);

    final AbstractSelect field;

    final String message;

    final boolean isPermitEmpty;

    public TwoCombosValidator(AbstractSelect field, boolean isPermitEmpty, String message) {
        if (message!=null) message = "los dos no pueden ser rellenados en mismo tiempo";
        this.field = field;
        this.message = message;
        this.isPermitEmpty = isPermitEmpty;
    }

    @Override
    public void validate(Object value) throws InvalidValueException {
        //log.info("validating: " + value);
        field.markAsDirty();
        if (!isPermitEmpty && ((GenUtil.objNullOrEmpty(value)
                && GenUtil.objNullOrEmpty(field.getValue()))))
            throw new InvalidValueException("Uno de los dos tiene que ser rellenado");
        if (!GenUtil.objNullOrEmpty(value)
                && !GenUtil.objNullOrEmpty(field.getValue())) {
             field.select(null);
            //throw new InvalidValueException(message);
        }

    }
}

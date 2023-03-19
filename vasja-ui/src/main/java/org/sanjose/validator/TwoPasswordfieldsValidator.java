package org.sanjose.validator;

import com.vaadin.data.Validator;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.PasswordField;
import org.sanjose.util.GenUtil;


/**
 * VASJA class
 * User: prubach
 */
public class TwoPasswordfieldsValidator implements Validator {

    private static final Logger log = LoggerFactory.getLogger(TwoPasswordfieldsValidator.class);

    private final AbstractField field;

    private final String message;

    private final boolean isPermitEmpty;

    public TwoPasswordfieldsValidator(PasswordField field,  boolean isPermitEmpty, String message) {
        if (message!=null) message = "Los dos claves tienen que ser identicos";
        this.field = field;
        this.message = message;
        this.isPermitEmpty = isPermitEmpty;
    }

    @Override
    public void validate(Object value) throws InvalidValueException {
        if (!GenUtil.objNullOrEmpty(value) || !GenUtil.objNullOrEmpty(field.getValue())) {
            if (((String)value).compareTo(((String)field.getValue()))!=0) {
                field.markAsDirty();
                throw new InvalidValueException(message);
            }
        }
   }
}

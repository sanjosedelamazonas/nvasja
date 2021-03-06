package org.sanjose.validator;

import com.vaadin.data.validator.BeanValidator;
import org.sanjose.util.ConfigurationUtil;

public class LocalizedBeanValidator extends BeanValidator
{
    public LocalizedBeanValidator(Class<?> beanClass, String propertyName) {
        super(beanClass, propertyName);
        setLocale(ConfigurationUtil.LOCALE);
    }
}

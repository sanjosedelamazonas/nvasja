package org.sanjose.render;

import com.vaadin.ui.renderers.NumberRenderer;
import elemental.json.JsonValue;

import java.util.Locale;

/**
 * VASJA class
 * User: prubach
 * Date: 15.09.16
 */
public class EmptyZeroNumberRendrer extends NumberRenderer {

    public EmptyZeroNumberRendrer(String formatString, Locale locale) throws IllegalArgumentException {
        super(formatString, locale, "");
    }

    @Override
    public JsonValue encode(Number value) {
        JsonValue js = super.encode(value);
        if (value==null || value.doubleValue()==0)
            return this.encode("", String.class);
        else
            return js;
    }
}

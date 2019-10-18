package org.sanjose.render;

import com.vaadin.ui.renderers.DateRenderer;
import elemental.json.JsonValue;
import org.sanjose.util.GenUtil;

import java.util.Date;

public class DateNotNullRenderer extends DateRenderer {

    public DateNotNullRenderer(String formatString) throws IllegalArgumentException {
        super(formatString);
    }

    public JsonValue encode(Date value) {
        if (value!=null && value.getTime()==GenUtil.getBegin20thCent().getTime())
            value = null;
        return super.encode(value);
    }
}

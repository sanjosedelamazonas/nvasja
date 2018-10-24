/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.sanjose.client;

import com.google.web.bindery.event.shared.HandlerRegistration;
import com.vaadin.v7.client.connectors.ClickableRendererConnector;
import com.vaadin.v7.client.renderers.ClickableRenderer;
import com.vaadin.shared.ui.Connect;
import elemental.json.JsonObject;
import org.sanjose.renderer.FlgCobradoCheckboxRenderer;


@Connect(FlgCobradoCheckboxRenderer.class)
public class CssCheckboxRendererConnector extends ClickableRendererConnector<Boolean> {
    @Override
    public org.sanjose.client.renderer.CssCheckboxRenderer getRenderer() {
        if (!isEnabled()) System.out.println("Not enabled CssCheckBoxRenderer");
        return (org.sanjose.client.renderer.CssCheckboxRenderer) super.getRenderer();
    }

    @Override
    protected HandlerRegistration addClickHandler(ClickableRenderer.RendererClickHandler<JsonObject> handler) {
        return getRenderer().addClickHandler(handler);
    }
}

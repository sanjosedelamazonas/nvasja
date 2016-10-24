package org.sanjose.renderer;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.TransactionalPropertyWrapper;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.ClickableRenderer;

/*
* Copyright Paweł Rubach 2016
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

public class FlgCobradoCheckboxRenderer extends ClickableRenderer<Boolean> {

    public FlgCobradoCheckboxRenderer() {
        super(Boolean.class, null);
        addClickListener(new RendererClickListener() {
            @Override
            public void click(RendererClickEvent event) {
                Grid grid = getParentGrid();

                event.getColumn().getEditorField().setPropertyDataSource(
                        new TransactionalPropertyWrapper(
                                grid.getContainerDataSource().getContainerProperty(event.getItemId(), event.getPropertyId())));
                if (event.getColumn().isEditable() && grid.isEditorEnabled()) {
                    try {
                        Object itemId = event.getItemId();
                        Object propertyId = event.getPropertyId();
                        Container.Indexed containerDataSource = grid.getContainerDataSource();
                        Property itemProperty = containerDataSource.getItem(itemId).getItemProperty(propertyId);
                        boolean currentValue = (Boolean) itemProperty.getValue();
                        itemProperty.setValue(!currentValue);
                        grid.editItem(itemId);
                        grid.saveEditor();
                        grid.cancelEditor();
                    } catch (FieldGroup.CommitException e) {
                        Grid.CommitErrorEvent errorEvent = new Grid.CommitErrorEvent(grid, e);
                        grid.getEditorErrorHandler().commitError(errorEvent);
                    }
                }
            }
        });
    }
}

package org.sanjose.views;

import com.vaadin.data.fieldgroup.FieldGroup.CommitEvent;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitHandler;
import com.vaadin.data.util.BeanItem;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.server.Page;
import org.sanjose.MainUI;
import org.sanjose.util.ConfigurationUtil;
import org.sanjose.model.VsjPropiedad;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class provides an interface for the logical operations between the CRUD
 * view, its parts like the product editor form and the data source, including
 * fetching and saving products.
 *
 * Having this separate from the view makes it easier to test various parts of
 * the system separately, and to e.g. provide alternative views for the same
 * data.
 */
public class PropiedadLogic implements Serializable {


	private static final Logger log = LoggerFactory.getLogger(PropiedadLogic.class);

    private final PropiedadView view;

    public PropiedadLogic(PropiedadView propiedadView) {
        view = propiedadView;
    }

    public void init() {
        view.btnNuevaPropiedad.addClickListener(e -> newPropiedad());
        view.btnEliminar.addClickListener(e -> deletePropiedad());
        // register save listener
        view.gridPropiedad.getEditorFieldGroup().addCommitHandler(new CommitHandler() {
            @Override
            public void preCommit(CommitEvent commitEvent) throws CommitException {
            }
            @Override
            public void postCommit(CommitEvent commitEvent) throws CommitException {
                // You can persist your data here
                Object item = view.gridPropiedad.getContainerDataSource().getItem(view.gridPropiedad.getEditedItemId());
                if (item!=null) 
                	view.repo.save((VsjPropiedad)((BeanItem)item).getBean());
                ConfigurationUtil.resetConfiguration();
            }
        });
               
    }

    public void enter(String productId) {
    }

    private void newPropiedad() {
        view.clearSelection();
        VsjPropiedad vcb = new VsjPropiedad();
        view.gridPropiedad.getContainerDataSource().addItem(vcb);
    }


    private void deletePropiedad() {
        List<VsjPropiedad> rows = view.getSelectedRow().stream().filter(vsj -> vsj instanceof VsjPropiedad).map(vsj -> (VsjPropiedad) vsj).collect(Collectors.toList());
        view.clearSelection();
        for (VsjPropiedad vsj : rows) {
        	view.removeRow(vsj);
        }
        ConfigurationUtil.resetConfiguration();
    }
}

package org.sanjose.views.rendicion;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.*;
import org.sanjose.converter.DateToTimestampConverter;
import org.sanjose.model.ScpRendiciondetalle;
import org.sanjose.model.ScpRendiciondetallePK;
import org.sanjose.render.DateNotNullRenderer;
import org.sanjose.util.ConfigurationUtil;
import org.sanjose.util.DataFilterUtil;
import org.sanjose.util.GenUtil;
import org.sanjose.util.ViewUtil;
import org.sanjose.validator.LocalizedBeanValidator;
import org.sanjose.validator.NotNullNotBoundValidator;
import org.sanjose.views.sys.DestinoView;
import org.sanjose.views.sys.SubWindowing;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ImportView extends ImportUI implements SubWindowing {

    public static final String VIEW_NAME = "Import rendicion detalles";
    public String getWindowTitle() {
        return VIEW_NAME;
    }
    private static final Logger log = LoggerFactory.getLogger(DestinoView.class);
    private RendicionItemLogic rendicionItemLogic;
    private RendicionImport ri;
    private Window subWindow;

    private List<BeanItem<ScpRendiciondetalle>> beanItems = new ArrayList<>();
    private List<ImportedDetalleLineView> rows = new ArrayList<>();
    private List<FieldGroup> fieldGroups = new ArrayList<>();

    public ImportView(RendicionItemLogic ril) {
        this.rendicionItemLogic = ril;
        RendicionUploader ru = new RendicionUploader();
        this.btnUpload.setReceiver(ru);
        this.btnUpload.setButtonCaption("Subir");
        this.btnUpload.addSucceededListener(ru);
        this.btnUpload.addFailedListener(e -> {
            Notification.show("Problema al importar al servidor: " + e.getReason().getLocalizedMessage(), Notification.TYPE_ERROR_MESSAGE);
        });
        getBtnAnular().addClickListener(e -> this.subWindow.close());
        getBtnImportar().addClickListener(e -> guardarImportar());
    }


    public void onUpload(Upload.SucceededEvent e, File file) {
        try {
            getLayItem().removeAllComponents();
            String f = e.getFilename();
            log.info(f);
            log.info(e.getMIMEType());
            ri = new RendicionImport(file, rendicionItemLogic.moneda);
            List<ScpRendiciondetalle> importedDets = ri.getRendDetalles();
            int i = rendicionItemLogic.view.getContainer().size()+1;
            for (ScpRendiciondetalle det : importedDets) {
                ScpRendiciondetallePK id = new ScpRendiciondetallePK();
                id.setCodRendicioncabecera(this.rendicionItemLogic.rendicioncabecera!=null ? this.rendicionItemLogic.rendicioncabecera.getCodRendicioncabecera() : -1);
                id.setNumNroitem(i);
                det.setId(id);
                ImportedDetalleLineView row = new ImportedDetalleLineView();
                bindRow(det, row);
                getLayItem().addComponent(row);
                String h = new Integer(25*i).toString();
                getLayItem().setHeight(h);
                h = new Integer(25*i+80).toString();
                this.setHeight(h);
                i++;
            }


            //rendicionItemLogic.addImportedDetalles();
        } catch (IOException ie) {
            Notification.show("Problema al importar el archivo subido: " + ie.getLocalizedMessage());
        }
    }

    public void bindRow(ScpRendiciondetalle item, ImportedDetalleLineView row) {

        BeanItem beanItem = new BeanItem<>(item);
        FieldGroup fieldGroup = new FieldGroup(beanItem);
        fieldGroup.setItemDataSource(beanItem);
        fieldGroup.bind(row.getTxtGlosaItem(), "txtGlosaitem");

        String montoField = "numDebe" + GenUtil.getDescMoneda(rendicionItemLogic.moneda);

        fieldGroup.bind(row.getNumMonto(), montoField);
        row.getNumItem().setValue(item.getId().getNumNroitem().toString());
        row.getNumItem().setEnabled(false);

        Date fechaComprobpago = (Date)beanItem.getItemProperty("fecComprobantepago").getValue();
        if (fechaComprobpago!=null && fechaComprobpago.getTime()==GenUtil.getBegin20thCent().getTime())
            row.getFecFechaDoc().setValue(null);

        DateField pdf = row.getFecFechaDoc();
        pdf.setConverter(DateToTimestampConverter.INSTANCE);
        pdf.setResolution(Resolution.DAY);
        fieldGroup.bind(pdf, "fecComprobantepago");
        //pdf.setRenderer(new DateNotNullRenderer(ConfigurationUtil.get("DEFAULT_DATE_RENDERER_FORMAT")));

        // Auxiliar
        DataFilterUtil.bindComboBox(row.getSelDestino(), "codDestino", rendicionItemLogic.view.getService().getDestinoRepo().findByIndTipodestinoNot('3'), item.getCodDestino(),
                "txtNombredestino");
        //row.getSelDestino().setInvalidAllowed(true);
        //row.getSelDestino().setNewItemsAllowed(true);
        //row.getSelDestino().setImmediate(false);
        fieldGroup.bind(row.getSelDestino(), "codDestino");

        row.getTxtGlosaItem().setMaxLength(70);

        // Rubro inst
        DataFilterUtil.bindComboBox(row.getSelRubroInst(), "id.codCtaespecial",
                rendicionItemLogic.view.getService().getPlanEspRepo().findByFlgMovimientoAndId_TxtAnoproceso('N', GenUtil.getCurYear()),
                "Rubro instit", "txtDescctaespecial");
        fieldGroup.bind(row.getSelRubroInst(), "codCtaespecial");
        row.getSelRubroInst().setNullSelectionAllowed(false);
        row.getSelRubroInst().setInvalidAllowed(false);
        row.getSelRubroInst().setImmediate(true);
        row.getSelRubroInst().setDescription(item.getCodCtaespecial());


        // Rubro Proy
        DataFilterUtil.bindComboBox(row.getSelPartidaP(), "id.codCtaproyecto",
                rendicionItemLogic.view.getService().getPlanproyectoRepo().findByFlgMovimientoAndId_TxtAnoproceso("N", GenUtil.getCurYear()),
                "Partida pptal", "txtDescctaproyecto");
        fieldGroup.bind(row.getSelPartidaP(), "codCtaproyecto");
        row.getSelPartidaP().setInvalidAllowed(true);

        ViewUtil.setFieldsNullRepresentation(fieldGroup);
        ViewUtil.setDefaultsForNumberField(row.getNumMonto());
        row.getNumMonto().addStyleName("v-align-right");

        row.getSelDestino().addValidator(new NotNullNotBoundValidator(null));
        row.getSelRubroInst().addValidator(new NotNullNotBoundValidator(null));
        row.getSelPartidaP().addValidator(new NotNullNotBoundValidator(null));

        fieldGroups.add(fieldGroup);
        rows.add(row);
        beanItems.add(beanItem);
    }

    private void guardarImportar() {
        try {
            for (FieldGroup fg : fieldGroups) {
                fg.commit();
            }
        } catch (FieldGroup.CommitException ce) {
            Notification.show("Por favor rellena los datos necessarios!", Notification.Type.ERROR_MESSAGE);
            return;
        }
        List<ScpRendiciondetalle> updatedDetalles = new ArrayList<>();
        beanItems.forEach(b -> updatedDetalles.add(b.getBean()));
        rendicionItemLogic.addImportedDetalles(updatedDetalles);
        subWindow.close();
    }



    class RendicionUploader implements Upload.Receiver, Upload.SucceededListener {
        public File file;

        public OutputStream receiveUpload(String filename,
                                          String mimeType) {
            // Create upload stream
            FileOutputStream fos = null; // Stream to write to
            try {
                // Open the file for writing.
                String tmpDir = System.getProperty("java.io.tmpdir");
                file = new File(tmpDir + "/" + System.currentTimeMillis() + "_" + filename);
                fos = new FileOutputStream(file);
            } catch (final java.io.FileNotFoundException e) {
                new Notification("Could not open file<br/>",
                        e.getMessage(),
                        Notification.Type.ERROR_MESSAGE)
                        .show(Page.getCurrent());
                return null;
            }
            return fos; // Return the output stream to write to
        }

        public void uploadSucceeded(Upload.SucceededEvent e) {
            onUpload(e, file);
        }
    };






    @Override
    public Window getSubWindow() {
        return subWindow;
    }

    @Override
    public void setSubWindow(Window subWindow) {
        this.subWindow = subWindow;
    }

    public Upload getBtnUpload() {
        return btnUpload;
    }

    public VerticalLayout getLayItem() {
        return layItem;
    }

    public Button getBtnAnular() {
        return btnAnular;
    }

    public Button getBtnImportar() {
        return btnImportar;
    }

}

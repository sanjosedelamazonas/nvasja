package org.sanjose.views.rendicion;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import com.vaadin.server.FileDownloader;
import com.vaadin.server.Sizeable;
import com.vaadin.server.StreamResource;
import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.shared.ui.window.WindowMode;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import org.sanjose.helper.XlsExporter;
import org.sanjose.model.ScpRendicioncabecera;
import org.sanjose.model.ScpRendiciondetalle;
import org.sanjose.util.GenUtil;
import org.sanjose.views.sys.PersistanceService;

public class RendicionExportXLS extends XlsExporter {

    private ScpRendicioncabecera cabecera;

    private PersistanceService service;

    public RendicionExportXLS(ScpRendicioncabecera cabecera, PersistanceService service) {
        super(cabecera.getTxtGlosa());
        this.cabecera = cabecera;
        this.service = service;
        exportRendicion();
        //sheet.setColumnWidth(0, 6000);
        //sheet.setColumnWidth(1, 4000);
    }

    public void exportRendicion() {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        writeTitleLine(Arrays.asList(new String[]{ cabecera.getTxtGlosa()}), 0);

        writeDataLine(Arrays.asList(new String[]{ "Fecha de rendicion: ", sdf.format(cabecera.getFecComprobante())}), 1);
        writeDataLine(Arrays.asList(new String[]{ "Destino", cabecera.getCodDestino()}), 2);
        writeDataLine(Arrays.asList(new String[]{ "Moneda", GenUtil.getSymMoneda(cabecera.getCodTipomoneda())}), 3);

        writeHeaderLine(Arrays.asList(new String[]{ "Nro", "Descripcion", "Ingreso", "Egreso"}), 5);

        List<ScpRendiciondetalle> detalles = service.getRendiciondetalleRep().findById_CodRendicioncabecera(cabecera.getCodRendicioncabecera());
        writeDataLines(ScpRendiciondetalle.class,
                Arrays.asList(new String[]{"id.numNroitem", "txtGlosaitem", "numDebesol", "numHabersol"}),
                detalles,
                1);

        openExported();
    }


    public void openExported() {
        StreamResource.StreamSource source = (StreamResource.StreamSource) () -> {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try {
                getWorkbook().write(outputStream);
                getWorkbook().close();
            } catch (IOException ex) {
                log.error("Error exporting to XLS");

            }
            return new ByteArrayInputStream(outputStream.toByteArray());
        };
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        StreamResource resource = new StreamResource(source, "RendicionExport_"
                        + cabecera.getCodComprobante() + "_"
                        + df.format(new Date(System.currentTimeMillis()))
                        + ".xlsx");
        //resource.setMIMEType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8");
        //resource.setMIMEType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        resource.setMIMEType("application/xls");

        log.info("Resource: " + resource.getFilename() + " "
                + resource.getMIMEType());


        FileDownloader fileDownloader = new FileDownloader(resource);
        //UI.getCurrent().

        //)fileDownloader.
        Embedded emb = new Embedded();
        emb.setSizeFull();
        emb.setType(Embedded.TYPE_BROWSER);
        emb.setSource(resource);

        Window repWindow = new Window();
        repWindow.setWindowMode(WindowMode.NORMAL);
        repWindow.setWidth(700, Sizeable.Unit.PIXELS);
        repWindow.setHeight(600, Sizeable.Unit.PIXELS);
        repWindow.setPositionX(200);
        repWindow.setPositionY(50);
        repWindow.setModal(false);
        repWindow.setContent(emb);
        repWindow.setDraggable(true);
        UI.getCurrent().addWindow(repWindow);
        //UI.getCurrent().addWindow(fileDownloader);
        //JavaScript.getCurrent().execute("window.onload = function() { window.print(); } ");
    }
}

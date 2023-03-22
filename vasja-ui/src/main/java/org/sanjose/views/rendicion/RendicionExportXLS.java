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
import org.sanjose.model.ScpDestino;
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
        log.info("running export " + cabecera.getCodRendicioncabecera());
        writeTitleLine(Arrays.asList(new String[]{ " ", cabecera.getTxtGlosa(), " ", " "}), 0);
        ScpDestino destino = service.getDestinoRepo().findByCodDestino(cabecera.getCodDestino());
        writeDataLine(Arrays.asList(new String[]{ "Fecha de rendicion: ", sdf.format(cabecera.getFecComprobante()), "", ""}), 1);
        writeDataLine(Arrays.asList(new String[]{ "Destino", destino.getCodDestino() + " " + destino.getTxtNombredestino(), "", ""}), 2);
        writeDataLine(Arrays.asList(new String[]{ "Moneda", GenUtil.getSymMoneda(GenUtil.getLitMoneda(cabecera.getCodTipomoneda())), "", ""}), 3);

        writeHeaderLine(Arrays.asList(new String[]{ "Nro", "Descripcion", "Ingreso", "Egreso"}), 5);

        List<ScpRendiciondetalle> detalles = service.getRendiciondetalleRep().findById_CodRendicioncabecera(cabecera.getCodRendicioncabecera());
        detalles.sort(new Comparator<ScpRendiciondetalle>() {
            @Override
            public int compare(ScpRendiciondetalle o1, ScpRendiciondetalle o2) {
                return o1.getId().getNumNroitem().compareTo(o2.getId().getNumNroitem());
            }
        });
        writeDataLines(ScpRendiciondetalle.class,
                Arrays.asList(new String[]{"id.numNroitem", "txtGlosaitem", "numHaber" + GenUtil.getDescMoneda(cabecera.getCodTipomoneda()), "numDebe" + GenUtil.getDescMoneda(cabecera.getCodTipomoneda())}),
                detalles,
                6);
        //writeDataLines(ScpRendiciondetalle.class,


                //openExported();
    }

    public ByteArrayOutputStream getExported() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            getWorkbook().write(outputStream);
            getWorkbook().close();
        } catch (IOException ex) {
            log.error("Error exporting to XLS");

        }
        return outputStream;
    }


    public StreamResource openExported() {
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
        resource.setMIMEType("application/xls");
        return resource;
    }
}

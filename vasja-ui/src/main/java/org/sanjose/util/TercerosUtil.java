package org.sanjose.util;

import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Notification;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.type.WhenNoDataTypeEnum;
import net.sf.jasperreports.engine.util.JRLoader;
import org.sanjose.MainUI;
import org.sanjose.bean.VsjOperaciontercero;
import org.sanjose.helper.EmailAttachment;
import org.sanjose.helper.ReportHelper;
import org.sanjose.model.ScpBancodetalle;
import org.sanjose.model.ScpCajabanco;
import org.sanjose.model.ScpComprobantedetalle;
import org.sanjose.model.ScpDestino;
import org.sanjose.repo.ScpDestinoRep;
import org.sanjose.views.sys.PersistanceService;

import java.io.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

public class TercerosUtil {

    private static final Logger log = LoggerFactory.getLogger(ReportHelper.class.getName());

    public static List<VsjOperaciontercero> getAll(Date fechaDesde, 
                                                   Date fechaHasta, 
                                                   List<String> codigosTerc,
                                                   String curCodTercero,
                                                   PersistanceService service,
                                                   boolean isReporte) {

        return TercerosUtil.getFrom(
                service.getScpComprobantedetalleRep().
                        findByFecComprobanteBetweenAndCodTerceroIsInAndCodCtacontableStartingWithOrderByFecComprobanteAscId_CodComprobanteAsc(fechaDesde, fechaHasta, codigosTerc, "4"),
                service.getScpComprobantedetalleRep().
                        findByFecComprobanteBetweenAndCodTerceroIsInAndCodCtacontableStartingWithOrderByFecComprobanteAscId_CodComprobanteAsc(fechaDesde, fechaHasta, codigosTerc, "1"),
                service.getCajabancoRep().findByFecFechaBetweenAndCodTerceroIsInAndFlgEnviadoOrderByFecFechaAscCodCajabancoAsc(fechaDesde, fechaHasta, codigosTerc, '0'),
                service.getBancodetalleRep().findByFecFechaBetweenAndCodTerceroIsInAndVsjBancocabecera_FlgEnviadoOrderByFecFechaAscId_CodBancocabeceraAsc(fechaDesde, fechaHasta, codigosTerc, '0'),
                service.getDestinoRepo(),
                TercerosUtil.getAllSaldoPorFecha(fechaDesde, curCodTercero),
                fechaDesde,
                isReporte
        );
    }


    public static Map<Character, List<VsjOperaciontercero>> getAllForReport(Date fechaDesde,
                                                   Date fechaHasta,
                                                   List<String> codigosTerc,
                                                   String curCodTercero,
                                                   PersistanceService service) {

        List<VsjOperaciontercero> all = TercerosUtil.getFrom(
                service.getScpComprobantedetalleRep().
                        findByFecComprobanteBetweenAndCodTerceroIsInAndCodCtacontableStartingWithOrderByFecComprobanteAscId_CodComprobanteAsc(fechaDesde, fechaHasta, codigosTerc, "4"),
                service.getScpComprobantedetalleRep().
                        findByFecComprobanteBetweenAndCodTerceroIsInAndCodCtacontableStartingWithOrderByFecComprobanteAscId_CodComprobanteAsc(fechaDesde, fechaHasta, codigosTerc, "1"),
                service.getCajabancoRep().findByFecFechaBetweenAndCodTerceroIsInAndFlgEnviadoOrderByFecFechaAscCodCajabancoAsc(fechaDesde, fechaHasta, codigosTerc, '0'),
                service.getBancodetalleRep().findByFecFechaBetweenAndCodTerceroIsInAndVsjBancocabecera_FlgEnviadoOrderByFecFechaAscId_CodBancocabeceraAsc(fechaDesde, fechaHasta, codigosTerc, '0'),
                service.getDestinoRepo(),
                TercerosUtil.getAllSaldoPorFecha(fechaDesde, curCodTercero),
                fechaDesde,
                true
        );
        Map<Character, List<VsjOperaciontercero>> allops = new HashMap<>();
        allops.put(GenUtil.PEN, new ArrayList<>());
        allops.put(GenUtil.USD, new ArrayList<>());
        allops.put(GenUtil.EUR, new ArrayList<>());
        all.forEach( op -> {
            allops.get(op.getCodTipomoneda()).add(op);
        });
//        for (Character moneda : GenUtil.getMonedasAsCharacter()) {
//            List<VsjOperaciontercero> opers = (allops.get(moneda));
////            switch (moneda) {
////                case '0':
////                    opers.get(0).getNumSaldosol()
////
////            }
//            if (opers.size()==1) {
//                allops.put(moneda, new ArrayList<>());
//            }
//        }
        return allops;
    }


    public static ProcUtil.Saldos getAllSaldoPorFecha(Date fecha, String curCodTercero) {
        return MainUI.get().getProcUtil().getSaldos(fecha, null, curCodTercero);
    }


    public static List<VsjOperaciontercero> getFrom(
            List<ScpComprobantedetalle> comprobantedetalleList4,
            List<ScpComprobantedetalle> comprobantedetalleList1,
            List<ScpCajabanco> cajabancoList,
            List<ScpBancodetalle> bancodetalleList,
            ScpDestinoRep destinoRep,
            ProcUtil.Saldos saldosIniciales,
            Date fechaDesde,
            boolean isReporte) {

        List<VsjOperaciontercero> terc = new ArrayList<>();
        BigDecimal sumSaldosol = saldosIniciales.getSaldoPEN();
        BigDecimal sumSaldodolar = saldosIniciales.getSaldoUSD();
        BigDecimal sumSaldomo = saldosIniciales.getSaldoEUR();

        if (isReporte) {
            terc.add(new VsjOperaciontercero(
                    null,
                    "",
                    new Timestamp(fechaDesde.getTime()),
                                "",
                    "",
                    "Saldo inicial SOLES",
                    "",
                    "",
                    "",
                    '0',
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    sumSaldosol,
                    null,
                    null,
                    "",
                    true
            ));

            terc.add(new VsjOperaciontercero(
                    null,
                    "",
                    new Timestamp(fechaDesde.getTime()),
                    "",
                    "000000",
                    "Saldo inicial DOLARES",
                    "",
                    "",
                    "",
                    '1',
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    sumSaldodolar,
                    null,
                    "",
                    true
            ));

            terc.add(new VsjOperaciontercero(
                    null,
                    "",
                    new Timestamp(fechaDesde.getTime()),
                    "",
                    "000000",
                    "Saldo inicial EUROS",
                    "",
                    "",
                    "",
                    '2',
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    sumSaldomo,
                    "",
                    true
            ));
        }

        HashMap<String, String> contraCtaList = new HashMap<>();
        for (ScpComprobantedetalle det : comprobantedetalleList1) {
            contraCtaList.put(det.getId().getCodComprobante(), det.getCodCtacontable());
        }
        long id = 1;
        // Operaciones enviadas en contabilidad
        for (ScpComprobantedetalle det : comprobantedetalleList4) {
            String nombreDestino = "";
            if (det.getCodDestino()!=null) {
                ScpDestino dest = destinoRep.findByCodDestino(det.getCodDestino());
                if (dest!=null)
                    nombreDestino = dest.getTxtNombredestino();
            }
            String contraCta = contraCtaList.get(det.getId().getCodComprobante());

            terc.add(new VsjOperaciontercero(
                    id++,
                    det.getCodTercero(),
                    det.getFecComprobante(),
                    det.getId().getCodOrigen() + " " + det.getId().getCodComprobante(),
                    det.getId().getCodComprobante(),
                    det.getTxtGlosaitem(),
                    det.getCodDestino(),
                    nombreDestino,
                    det.getCodCtacontable(),
                    det.getCodTipomoneda(),
                    det.getNumDebedolar(),
                    det.getNumDebemc(),
                    det.getNumDebemo(),
                    det.getNumDebesol(),
                    det.getNumHaberdolar(),
                    det.getNumHabermc(),
                    det.getNumHabermo(),
                    det.getNumHabersol(),
                    contraCta!=null ? contraCta : "",
                    true
                    ));
        }

        // Cajabanco no enviadas
        for (ScpCajabanco det : cajabancoList) {
            String nombreDestino = "";
            if (det.getCodDestino()!=null) {
                ScpDestino dest = destinoRep.findByCodDestino(det.getCodDestino());
                if (dest!=null)
                    nombreDestino = dest.getTxtNombredestino();
            }
            //String contraCta = contraCtaList.get(det.getId().getCodComprobante());

            terc.add(new VsjOperaciontercero(
                    id++,
                    det.getCodTercero(),
                    det.getFecFecha(),
                    "01 " + det.getTxtCorrelativo(),
                    det.getTxtCorrelativo(),
                    det.getTxtGlosaitem(),
                    det.getCodDestino(),
                    nombreDestino,
                    det.getCodContracta(),
                    det.getCodTipomoneda(),
                    det.getNumHaberdolar(),
                    new BigDecimal(0),
                    det.getNumHabermo(),
                    det.getNumHabersol(),
                    det.getNumDebedolar(),
                    new BigDecimal(0),
                    det.getNumDebemo(),
                    det.getNumDebesol(),
                    //TODO: is that the Contra Cta?
                    det.getCodCtacontable(),
                    false
            ));
        }

        //Bancos
        for (ScpBancodetalle det : bancodetalleList) {
            String nombreDestino = "";
            if (det.getCodDestino()!=null) {
                ScpDestino dest = destinoRep.findByCodDestino(det.getCodDestino());
                if (dest!=null)
                    nombreDestino = dest.getTxtNombredestino();
            }
            //String contraCta = contraCtaList.get(det.getId().getCodComprobante());

            terc.add(new VsjOperaciontercero(
                    id++,
                    det.getCodTercero(),
                    det.getFecFecha(),
                    "02 " + det.getTxtCorrelativo(),
                    det.getTxtCorrelativo(),
                    det.getTxtGlosaitem(),
                    det.getCodDestino(),
                    nombreDestino,
                    det.getCodContracta(),
                    det.getCodTipomoneda(),
                    det.getNumHaberdolar(),
                    new BigDecimal(0),
                    det.getNumHabermo(),
                    det.getNumHabersol(),
                    det.getNumDebedolar(),
                    new BigDecimal(0),
                    det.getNumDebemo(),
                    det.getNumDebesol(),
                    //TODO: is that the Contra Cta?
                    det.getCodCtacontable(),
                    false
            ));
        }

        Collections.sort(terc);
        long id_sol = 1;
        long id_dol = 1;
        long id_mo = 1;

        for (VsjOperaciontercero trc : terc) {
            if (trc.getCodTipomoneda()=='0') {
                if (trc.getNumDebesol()!=null && trc.getNumHabersol()!=null)
                    sumSaldosol = sumSaldosol.subtract(trc.getNumDebesol()).add(trc.getNumHabersol());
                trc.setId(id_sol++);
            }
            else if (trc.getCodTipomoneda()=='1') {
                if (trc.getNumDebedolar()!=null && trc.getNumHaberdolar()!=null)
                    sumSaldodolar = sumSaldodolar.subtract(trc.getNumDebedolar()).add(trc.getNumHaberdolar());
                trc.setId(id_dol++);
            }
            else {
                if (trc.getNumDebemo()!=null && trc.getNumHabermo()!=null)
                    sumSaldomo = sumSaldomo.subtract(trc.getNumDebemo()).add(trc.getNumHabermo());
                trc.setId(id_mo++);
            }
            trc.setNumSaldosol(sumSaldosol);
            trc.setNumSaldodolar(sumSaldodolar);
            trc.setNumSaldomo(sumSaldomo);
        }
        return terc;
    }


    public static EmailAttachment generateTerceroOperacionesReport(final Date fechaDesde, final Date fechaHasta,
                                      String codTercero, PersistanceService service, boolean isShow) throws JRException {

        ScpDestino dest = service.getDestinoRepo().findByCodDestino(codTercero);

        Map<Character, List<VsjOperaciontercero>> allOpsTerc =  TercerosUtil.getAllForReport(
                fechaDesde,
                fechaHasta,
                Arrays.asList(new String[]{codTercero}),
                codTercero,
                service);

        log.debug("Generating Tercero Operaciones: ");
        HashMap paramMap = new HashMap();
        SimpleDateFormat sdf = new SimpleDateFormat(ConfigurationUtil.get("DEFAULT_DATE_FORMAT"));

        for (Character moneda : GenUtil.getMonedasAsCharacter()) {
            JRBeanCollectionDataSource operCollection = new JRBeanCollectionDataSource(allOpsTerc.get(moneda));
            paramMap.put("OPERACIONES_" + GenUtil.getDescMoneda(moneda), !allOpsTerc.get(moneda).isEmpty() ? operCollection : null);
        }

        paramMap.put("REPORT_LOCALE", ConfigurationUtil.getLocale());
        paramMap.put("FECHA_MIN", sdf.format(fechaDesde));
        paramMap.put("FECHA_MAX", sdf.format(fechaHasta));
        paramMap.put("COD_TERCERO", codTercero);
        paramMap.put("TXT_NOMBRE", dest!=null ? dest.getTxtNombredestino() : "");

        InputStream input = ReportHelper.loadReport("ReporteTerceroOperaciones");

        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(input);
        /*compiling jrxml with help of JasperReport class*/
        //JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

        /* Using jasperReport object to generate PDF */
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String filename= "Diario_" + codTercero + "_"
                        + df.format(new Date()) + ".pdf";
        if (isShow) {
            StreamResource.StreamSource source = (StreamResource.StreamSource) () ->
                    generateJasperReport(jasperReport, paramMap);
            ReportHelper.showReportInSubWindow(source, filename, null, "pdf");
            return null;
        } else {
             return new EmailAttachment(filename, JasperRunManager.runReportToPdf(jasperReport,
                    paramMap, new JREmptyDataSource()));

//            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, paramMap, new JREmptyDataSource());
//            OutputStream output = new FileOutputStream(new File(filename));
//            JasperExportManager.exportReportToPdfStream(jasperPrint, output);
        }
//        JRPdfExporter exporter = new JRPdfExporter();
//        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));

//        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(
//                response.getOutputStream()));
//        response.setHeader("Content-Disposition", "attachment;filename=jasperfile.docx");
//        response.setContentType("application/octet-stream");
//        exporter.exportReport();
    }

    public static ByteArrayInputStream generateJasperReport(JasperReport report, HashMap paramMap) {
        byte[] b = null;
        try {
            if (report != null) {
                report.setWhenNoDataType(WhenNoDataTypeEnum.ALL_SECTIONS_NO_DETAIL);
                b = JasperRunManager.runReportToPdf(report,
                        paramMap, new JREmptyDataSource());
            } else {
                Notification.show(
                        "There is no report file: "  + report.getName());
            }
        } catch (JRException ex) {
            log.error(ex.getMessage());
            ex.printStackTrace();
        }
        return new ByteArrayInputStream(b != null ? b : new byte[0]);
    }

}

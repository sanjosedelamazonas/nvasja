package org.sanjose.util;

import org.sanjose.bean.VsjOperaciontercero;
import org.sanjose.model.ScpBancodetalle;
import org.sanjose.model.ScpCajabanco;
import org.sanjose.model.ScpComprobantedetalle;
import org.sanjose.model.ScpDestino;
import org.sanjose.repo.ScpDestinoRep;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class TercerosUtil {


    public static List<VsjOperaciontercero> getFrom(
            List<ScpComprobantedetalle> comprobantedetalleList4,
            List<ScpComprobantedetalle> comprobantedetalleList1,
            List<ScpCajabanco> cajabancoList,
            List<ScpBancodetalle> bancodetalleList,
            ScpDestinoRep destinoRep,
            ProcUtil.Saldos saldosIniciales) {
        List<VsjOperaciontercero> terc = new ArrayList<>();
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
        BigDecimal sumSaldosol = saldosIniciales.getSaldoPEN();
        BigDecimal sumSaldodolar = saldosIniciales.getSaldoUSD();
        BigDecimal sumSaldomo = saldosIniciales.getSaldoEUR();
        long id_sol = 1;
        long id_dol = 1;
        long id_mo = 1;

        for (VsjOperaciontercero trc : terc) {
            if (trc.getCodTipomoneda()=='0') {
                sumSaldosol = sumSaldosol.subtract(trc.getNumDebesol()).add(trc.getNumHabersol());
                trc.setId(id_sol++);
            }
            else if (trc.getCodTipomoneda()=='1') {
                sumSaldodolar = sumSaldodolar.subtract(trc.getNumDebedolar()).add(trc.getNumHaberdolar());
                trc.setId(id_dol++);
            }
            else {
                sumSaldomo = sumSaldomo.subtract(trc.getNumDebemo()).add(trc.getNumHabermo());
                trc.setId(id_mo++);
            }
            trc.setNumSaldosol(sumSaldosol);
            trc.setNumSaldodolar(sumSaldodolar);
            trc.setNumSaldomo(sumSaldomo);
        }
        return terc;
    }
}

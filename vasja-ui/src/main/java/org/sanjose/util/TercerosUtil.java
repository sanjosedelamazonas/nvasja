package org.sanjose.util;

import org.sanjose.bean.VsjOperaciontercero;
import org.sanjose.model.ScpComprobantedetalle;
import org.sanjose.model.ScpDestino;
import org.sanjose.repo.ScpComprobantecabeceraRep;
import org.sanjose.repo.ScpComprobantedetalleRep;
import org.sanjose.repo.ScpDestinoRep;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TercerosUtil {


    public static List<VsjOperaciontercero> getFrom(
            List<ScpComprobantedetalle> comprobantedetalleList4,
            List<ScpComprobantedetalle> comprobantedetalleList1,
            ScpDestinoRep destinoRep,
            ProcUtil.Saldos saldosIniciales) {
        List<VsjOperaciontercero> terc = new ArrayList<>();
        HashMap<String, String> contraCtaList = new HashMap<>();
        for (ScpComprobantedetalle det : comprobantedetalleList1) {
            contraCtaList.put(det.getId().getCodComprobante(), det.getCodCtacontable());
        }
        long id = 1;
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
        BigDecimal sumSaldosol = saldosIniciales.getSaldoPEN();
        BigDecimal sumSaldodolar = saldosIniciales.getSaldoUSD();
        BigDecimal sumSaldomo = saldosIniciales.getSaldoEUR();
        for (VsjOperaciontercero trc : terc) {
            if (trc.getCodTipomoneda()=='0')
                sumSaldosol  = sumSaldosol.subtract(trc.getNumDebesol()).add(trc.getNumHabersol());
            else if (trc.getCodTipomoneda()=='1')
                sumSaldodolar  = sumSaldodolar.subtract(trc.getNumDebedolar()).add(trc.getNumHaberdolar());
            else
                sumSaldomo  = sumSaldomo.subtract(trc.getNumDebemo()).add(trc.getNumHabermo());
            trc.setNumSaldosol(sumSaldosol);
            trc.setNumSaldodolar(sumSaldodolar);
            trc.setNumSaldomo(sumSaldomo);
        }
        return terc;
    }
}

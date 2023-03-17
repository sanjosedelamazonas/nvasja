package org.sanjose.util;

import org.sanjose.bean.VsjOperaciontercero;
import org.sanjose.model.ScpComprobantedetalle;
import org.sanjose.model.ScpDestino;
import org.sanjose.repo.ScpDestinoRep;

import java.util.ArrayList;
import java.util.List;

public class TercerosUtil {


    public static List<VsjOperaciontercero> getFrom(List<ScpComprobantedetalle> comprobantedetalleList, ScpDestinoRep destinoRep) {
        List<VsjOperaciontercero> terc = new ArrayList<>();
        long id = 0;
        for (ScpComprobantedetalle det : comprobantedetalleList) {

            String nombreDestino = "";
            if (det.getCodDestino()!=null) {
                ScpDestino dest = destinoRep.findByCodDestino(det.getCodDestino());
                if (dest!=null)
                    nombreDestino = dest.getTxtNombredestino();
            }

            terc.add(new VsjOperaciontercero(
                    id++,
                    det.getCodTercero(),
                    det.getFecComprobante(),
                    det.getId().getNumNroitem() + "-" + det.getId().getCodComprobante(),
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
                    ""
                    ));
        }
        return terc;
    }
}

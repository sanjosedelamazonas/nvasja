package org.sanjose.views.rendicion;

import org.sanjose.model.ScpRendicioncabecera;
import org.sanjose.util.ProcUtil;
import org.sanjose.views.ItemsRefreshing;
import org.sanjose.views.sys.TipoCambioLogic;
import org.sanjose.views.sys.TipoCambioView;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.sanjose.util.ProcUtil.existeTipoDeCambio;

public class RendicionTipoCambiosLogic {

    private Set<ScpRendicioncabecera> rends;

    private Map<ScpRendicioncabecera, Boolean> rendicionsFaltaTipoCambioDecision = new HashMap<>();

    private RendicionService service;

    private TipoCambioView tcTview;

    private ItemsRefreshing<ScpRendicioncabecera> itemsRefreshing;

    private ProcUtil procUtil;

    public RendicionTipoCambiosLogic(Set<ScpRendicioncabecera> rends, RendicionService service, ProcUtil procUtil, ItemsRefreshing itemsRefreshing) {
        this.rends = rends;
        this.service = service;
        this.procUtil = procUtil;
        this.itemsRefreshing = itemsRefreshing;
        check();
    }

    public void check() {
        boolean isDone = true;
        for (ScpRendicioncabecera cab : rends) {
            if ((!rendicionsFaltaTipoCambioDecision.containsKey(cab)) &&
            (!existeTipoDeCambio(cab.getFecComprobante(), cab.getCodTipomoneda(), service.getTipocambioRep()))) {
                isDone = false;
                tcTview = TipoCambioLogic.openTipocambio(cab.getFecComprobante(), service.getTipocambioRep());
                tcTview.getBtnIgnorar().addClickListener( e -> ignore(cab) );
                tcTview.getBtnGuardar().addClickListener( e -> added(cab) );
                tcTview.getBtnAnular().addClickListener( e -> tcTview.close() );
                break;
            }
        }
        if (isDone) done();
    }

    public void ignore(ScpRendicioncabecera cab) {
        rendicionsFaltaTipoCambioDecision.put(cab, false);
        tcTview.close();
        check();
    }

    public void added(ScpRendicioncabecera cab) {
        tcTview.viewLogic.saveTipoCambio();
        rendicionsFaltaTipoCambioDecision.put(cab, true);
        tcTview.close();
        check();
    }

    public void done() {
        procUtil.enviarContabilidadRendicionConTipoCambio(rends, this.service, this.itemsRefreshing);
    }

    public Map<ScpRendicioncabecera, Boolean> get() {
        return rendicionsFaltaTipoCambioDecision;
    }
}

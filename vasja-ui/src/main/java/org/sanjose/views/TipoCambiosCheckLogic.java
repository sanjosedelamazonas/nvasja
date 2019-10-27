package org.sanjose.views;

import org.sanjose.util.ProcUtil;
import org.sanjose.views.sys.PersistanceService;
import org.sanjose.views.sys.TipoCambioLogic;
import org.sanjose.views.sys.TipoCambioView;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.sanjose.util.ProcUtil.existeTipoDeCambio;

public abstract class TipoCambiosCheckLogic<T> {

    protected Set<T> rends;

    private Map<T, Boolean> rendicionsFaltaTipoCambioDecision = new HashMap<>();

    protected PersistanceService service;

    private TipoCambioView tcTview;

    protected ItemsRefreshing<T> itemsRefreshing;

    protected ProcUtil procUtil;

    public TipoCambiosCheckLogic(Set<T> rends, PersistanceService service, ProcUtil procUtil, ItemsRefreshing itemsRefreshing) {
        this.rends = rends;
        this.service = service;
        this.procUtil = procUtil;
        this.itemsRefreshing = itemsRefreshing;
        check();
    }

    protected abstract Date getFecha(T cab);

    protected abstract Character getCodTipomoneda(T cab);

    protected abstract void done();

    public void check() {
        boolean isDone = true;
        for (T cab : rends) {
            if ((!rendicionsFaltaTipoCambioDecision.containsKey(cab)) &&
            (!existeTipoDeCambio(getFecha(cab), getCodTipomoneda(cab), service.getTipocambioRep()))) {
                isDone = false;
                tcTview = TipoCambioLogic.openTipocambio(getFecha(cab), service.getTipocambioRep());
                tcTview.getBtnIgnorar().addClickListener( e -> ignore(cab) );
                tcTview.getBtnGuardar().addClickListener( e -> added(cab) );
                tcTview.getBtnAnular().addClickListener( e -> tcTview.close() );
                break;
            }
        }
        if (isDone) done();
    }

    public void ignore(T cab) {
        rendicionsFaltaTipoCambioDecision.put(cab, false);
        tcTview.close();
        check();
    }

    public void added(T cab) {
        tcTview.viewLogic.saveTipoCambio();
        rendicionsFaltaTipoCambioDecision.put(cab, true);
        tcTview.close();
        check();
    }



    public Map<T, Boolean> get() {
        return rendicionsFaltaTipoCambioDecision;
    }
}

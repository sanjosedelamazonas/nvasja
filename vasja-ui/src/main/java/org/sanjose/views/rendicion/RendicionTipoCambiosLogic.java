package org.sanjose.views.rendicion;

import org.sanjose.model.ScpRendicioncabecera;
import org.sanjose.util.ProcUtil;
import org.sanjose.views.ItemsRefreshing;
import org.sanjose.views.TipoCambiosCheckLogic;
import org.sanjose.views.sys.PersistanceService;

import java.util.Date;
import java.util.Set;

public class RendicionTipoCambiosLogic extends TipoCambiosCheckLogic<ScpRendicioncabecera> {

    public RendicionTipoCambiosLogic(Set<ScpRendicioncabecera> rends, PersistanceService service, ProcUtil procUtil, ItemsRefreshing itemsRefreshing) {
        super(rends, service, procUtil, itemsRefreshing);
    }

    @Override
    protected Date getFecha(ScpRendicioncabecera cab) {
        return cab.getFecComprobante();
    }

    @Override
    protected Character getCodTipomoneda(ScpRendicioncabecera cab) {
        return cab.getCodTipomoneda();
    }

    public void done() {
        procUtil.enviarContabilidadRendicionConTipoCambio(rends, this.service, this.itemsRefreshing);
    }
}

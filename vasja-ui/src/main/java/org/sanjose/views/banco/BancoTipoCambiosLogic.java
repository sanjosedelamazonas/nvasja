package org.sanjose.views.banco;

import org.sanjose.model.ScpBancocabecera;
import org.sanjose.util.ProcUtil;
import org.sanjose.views.ItemsRefreshing;
import org.sanjose.views.TipoCambiosCheckLogic;
import org.sanjose.views.sys.PersistanceService;

import java.util.Date;
import java.util.Set;

public class BancoTipoCambiosLogic extends TipoCambiosCheckLogic<ScpBancocabecera> {

    public BancoTipoCambiosLogic(Set<ScpBancocabecera> rends, PersistanceService service, ProcUtil procUtil, ItemsRefreshing itemsRefreshing) {
        super(rends, service, procUtil, itemsRefreshing);
    }

    @Override
    protected Date getFecha(ScpBancocabecera cab) {
        return cab.getFecFecha();
    }

    @Override
    protected Character getCodTipomoneda(ScpBancocabecera cab) {
        return cab.getCodTipomoneda();
    }

    public void done() {
        procUtil.enviarContabilidadBancoConTipoCambio(this.rends, this.service, this.itemsRefreshing);
    }
}

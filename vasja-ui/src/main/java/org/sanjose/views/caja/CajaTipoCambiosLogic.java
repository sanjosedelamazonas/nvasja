package org.sanjose.views.caja;

import org.sanjose.model.ScpBancocabecera;
import org.sanjose.model.ScpCajabanco;
import org.sanjose.util.ProcUtil;
import org.sanjose.views.ItemsRefreshing;
import org.sanjose.views.TipoCambiosCheckLogic;
import org.sanjose.views.sys.PersistanceService;

import java.util.Date;
import java.util.Set;

public class CajaTipoCambiosLogic extends TipoCambiosCheckLogic<ScpCajabanco> {

    public CajaTipoCambiosLogic(Set<ScpCajabanco> rends, PersistanceService service, ProcUtil procUtil, ItemsRefreshing itemsRefreshing) {
        super(rends, service, procUtil, itemsRefreshing);
    }

    @Override
    protected Date getFecha(ScpCajabanco cab) {
        return cab.getFecFecha();
    }

    @Override
    protected Character getCodTipomoneda(ScpCajabanco cab) {
        return cab.getCodTipomoneda();
    }

    public void done() {
        procUtil.enviarContabilidadCajaConTipoCambio(this.rends, this.service, this.itemsRefreshing);
    }
}

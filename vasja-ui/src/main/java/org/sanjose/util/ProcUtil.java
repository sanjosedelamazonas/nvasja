package org.sanjose.util;

import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.ui.Notification;
import org.sanjose.authentication.CurrentUser;
import org.sanjose.model.ScpTipocambio;
import org.sanjose.model.VsjBancocabecera;
import org.sanjose.model.VsjCajabanco;
import org.sanjose.views.banco.BancoService;
import org.sanjose.views.caja.ComprobanteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.StoredProcedureQuery;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.sanjose.util.GenUtil.PEN;

/**
 * VASJA class
 * User: prubach
 * Date: 12.09.16
 */
@Service
public class ProcUtil {

    private static final Logger log = LoggerFactory.getLogger(ProcUtil.class);

    private EntityManager em;

    @Autowired
    public ProcUtil(EntityManager em) {
        this.em = em;
    }

    @Transactional
    public Saldos getSaldos(Date fecha, String codProyecto, String codTercero) {
        StoredProcedureQuery getSaldoAlDiaQuery = em.createNamedStoredProcedureQuery("getSaldoAlDia");
        if (codProyecto!=null) {
            getSaldoAlDiaQuery.setParameter(1, "1");
            getSaldoAlDiaQuery.setParameter(3, codProyecto);
        } else if (codTercero!=null) {
            getSaldoAlDiaQuery.setParameter(1, "2");
            getSaldoAlDiaQuery.setParameter(3, codTercero);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        //log.info("Getting date: " + sdf.format(fecha));
        getSaldoAlDiaQuery.setParameter(2, sdf.format(fecha));
        getSaldoAlDiaQuery.execute();
        BigDecimal pen = (BigDecimal) getSaldoAlDiaQuery.getOutputParameterValue(4);
        pen = pen.setScale(2,BigDecimal.ROUND_HALF_EVEN);
        BigDecimal usd = (BigDecimal) getSaldoAlDiaQuery.getOutputParameterValue(5);
        usd = usd.setScale(2,BigDecimal.ROUND_HALF_EVEN);
        BigDecimal eur = (BigDecimal) getSaldoAlDiaQuery.getOutputParameterValue(6);
        eur = eur.setScale(2,BigDecimal.ROUND_HALF_EVEN);
        //em.clear();
        em.close();
        return new Saldos(pen, usd, eur);
    }

    // moneda { 0, 1 }
    @Transactional
    public BigDecimal getSaldoCaja(Date fecha, String codCtacaja, Character moneda) {
        StoredProcedureQuery getSaldoAlDiaCajaQuery = em.createNamedStoredProcedureQuery("getSaldoAlDiaCaja");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //log.info("Getting saldo caja: " +codCtacaja + " " + moneda + " : " + sdf.format(fecha));
        getSaldoAlDiaCajaQuery.setParameter(1, sdf.format(fecha));
        getSaldoAlDiaCajaQuery.setParameter(2, codCtacaja);
        getSaldoAlDiaCajaQuery.setParameter(3, moneda.toString());
        getSaldoAlDiaCajaQuery.execute();
        BigDecimal res = (BigDecimal) getSaldoAlDiaCajaQuery.getOutputParameterValue(4);
        res = res.setScale(2,BigDecimal.ROUND_HALF_EVEN);
        em.close();
        return res;
    }

    // moneda { 0, 1, 2 }
    @Transactional
    public BigDecimal getSaldoBanco(Date fecha, String codCtacaja, Character moneda) {
        StoredProcedureQuery getSaldoAlDiaCajaQuery = em.createNamedStoredProcedureQuery("getSaldoAlDiaBanco");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-dd-MM HH:mm:ss");
        getSaldoAlDiaCajaQuery.setParameter(1, sdf.format(fecha));
        getSaldoAlDiaCajaQuery.setParameter(2, codCtacaja);
        getSaldoAlDiaCajaQuery.setParameter(3, moneda.toString());
        getSaldoAlDiaCajaQuery.execute();
        BigDecimal res = (BigDecimal) getSaldoAlDiaCajaQuery.getOutputParameterValue(4);
        res = res.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        em.close();
        return res;
    }

    @Transactional
    public String enviarContabilidad(VsjCajabanco vcb) {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery("getEnviarContabilidad");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        query.setParameter(1, vcb.getCodCajabanco());
        query.setParameter(2, CurrentUser.get());
        query.setParameter(3, sdf.format(vcb.getFecFecha()));
        query.setParameter(4, vcb.getCodTipomoneda());
        if (PEN.equals(vcb.getCodTipomoneda())) {
            query.setParameter(5, vcb.getNumDebesol());
            query.setParameter(6, vcb.getNumHabersol());
        } else {
            query.setParameter(5, vcb.getNumDebedolar());
            query.setParameter(6, vcb.getNumHaberdolar());
        }
        if (!GenUtil.strNullOrEmpty(vcb.getCodProyecto())) {
            query.setParameter(7, vcb.getCodProyecto());
        } else {
            query.setParameter(7, vcb.getCodTercero());
        }
        query.execute();
        return (String)query.getOutputParameterValue(8);
    }

    public void enviarContabilidad(Collection<Object> vcbs, ComprobanteService service) {
        VsjCajabanco vcb = null;
        try {
            List<VsjCajabanco> vsjCajabancoList = new ArrayList<>();
            for (Object objVcb : vcbs) {
                vcb = (VsjCajabanco) objVcb;
                if (vcb.isEnviado()) {
                    continue;
                }
                vsjCajabancoList.add(vcb);
                // Check TipoDeCambio
                log.info("Check tipoDeCambio: " + vcb);
                List<ScpTipocambio> tipocambios = service.getTipocambioRep().findById_FecFechacambio(
                        GenUtil.getBeginningOfDay(vcb.getFecFecha()));
                if (tipocambios.isEmpty()) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    Notification.show("Falta tipo de cambio para el dia: " + sdf.format(vcb.getFecFecha()), Notification.Type.WARNING_MESSAGE);
                    return;
                }
            }
            for (VsjCajabanco vcbS : vsjCajabancoList) {
                vcb = vcbS;
                log.info("Enviando: " + vcb);
                String result = enviarContabilidad(vcb);
                log.info("Resultado: " + result);
                Notification.show("Operacion: " + vcb.getCodCajabanco(), result, Notification.Type.TRAY_NOTIFICATION);
            }
            if (vcbs.size() != vsjCajabancoList.size()) {
                Notification.show("!Attention!", "!Algunas operaciones eran omitidas por ya ser enviadas!", Notification.Type.TRAY_NOTIFICATION);
            }
        } catch (PersistenceException pe) {
            Notification.show("Problema al enviar a contabilidad operacion: " + (vcb != null ? vcb.getCodCajabanco() : 0)
                            + "\n\n" + pe.getMessage() +
                            (pe.getCause() != null ? "\n" + pe.getCause().getMessage() : "")
                            + (pe.getCause() != null && pe.getCause().getCause() != null ? "\n" + pe.getCause().getCause().getMessage() : "")
                    , Notification.Type.ERROR_MESSAGE);
        }
    }

    @Transactional
    public String enviarContabilidadBanco(VsjBancocabecera vcb) {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery("getEnviarContabilidadBanco");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        query.setParameter(1, vcb.getCodBancocabecera());
        query.setParameter(2, CurrentUser.get());
        query.setParameter(3, sdf.format(vcb.getFecFecha()));
        query.setParameter(4, vcb.getCodTipomoneda());
        query.execute();
        return (String) query.getOutputParameterValue(5);
    }

    public void enviarContabilidadBanco(Collection<Object> vcbs, BancoService service) {
        VsjBancocabecera vcb = null;
        try {
            List<VsjBancocabecera> vsjBancocabeceras = new ArrayList<>();
            for (Object objVcb : vcbs) {
                vcb = (VsjBancocabecera) objVcb;
                if (vcb.isEnviado()) {
                    continue;
                }
                vsjBancocabeceras.add(vcb);
                // Check TipoDeCambio
                log.info("Check tipoDeCambio: " + vcb);
                List<ScpTipocambio> tipocambios = service.getScpTipocambioRep().findById_FecFechacambio(
                        GenUtil.getBeginningOfDay(vcb.getFecFecha()));
                if (tipocambios.isEmpty()) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    Notification.show("Falta tipo de cambio para el dia: " + sdf.format(vcb.getFecFecha()), Notification.Type.WARNING_MESSAGE);
                    return;
                }
            }
            for (VsjBancocabecera vcbS : vsjBancocabeceras) {
                vcb = vcbS;
                log.info("Enviando: " + vcb);
                String result = enviarContabilidadBanco(vcb);
                log.info("Resultado: " + result);
                Notification.show("Operacion: " + vcb.getCodBancocabecera(), result, Notification.Type.TRAY_NOTIFICATION);
            }
            if (vcbs.size() != vsjBancocabeceras.size()) {
                Notification.show("!Attention!", "!Algunas operaciones eran omitidas por ya ser enviadas!", Notification.Type.TRAY_NOTIFICATION);
            }
        } catch (PersistenceException pe) {
            Notification.show("Problema al enviar a contabilidad operacion: " + (vcb != null ? vcb.getCodBancocabecera() : 0)
                            + "\n\n" + pe.getMessage() +
                            (pe.getCause() != null ? "\n" + pe.getCause().getMessage() : "")
                            + (pe.getCause() != null && pe.getCause().getCause() != null ? "\n" + pe.getCause().getCause().getMessage() : "")
                    , Notification.Type.ERROR_MESSAGE);
        }
    }


    public class Saldos {

        private BigDecimal saldoPEN;

        private BigDecimal saldoUSD;

        private BigDecimal saldoEUR;

        public Saldos(BigDecimal saldoPEN, BigDecimal saldoUSD, BigDecimal saldoEUR) {
            this.saldoPEN = saldoPEN;
            this.saldoUSD = saldoUSD;
            this.saldoEUR = saldoEUR;
        }

        public BigDecimal getSaldoPEN() {
            return saldoPEN;
        }

        public void setSaldoPEN(BigDecimal saldoPEN) {
            this.saldoPEN = saldoPEN;
        }

        public BigDecimal getSaldoUSD() {
            return saldoUSD;
        }

        public void setSaldoUSD(BigDecimal saldoUSD) {
            this.saldoUSD = saldoUSD;
        }

        public BigDecimal getSaldoEUR() {
            return saldoEUR;
        }

        public void setSaldoEUR(BigDecimal saldoEUR) {
            this.saldoEUR = saldoEUR;
        }

        @Override
        public String toString() {
            return "Saldos{" +
                    "saldoPEN=" + saldoPEN +
                    ", saldoUSD=" + saldoUSD +
                    ", saldoEUR=" + saldoEUR +
                    '}';
        }
    }
}

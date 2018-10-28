package org.sanjose.util;

import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.ui.Notification;
import de.steinwedel.messagebox.MessageBox;
import org.sanjose.authentication.CurrentUser;
import org.sanjose.model.ScpCajabanco;
import org.sanjose.model.ScpTipocambio;
import org.sanjose.model.ScpBancocabecera;
import org.sanjose.repo.ScpTipocambioRep;
import org.sanjose.views.ItemsRefreshing;
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
import java.util.*;

import static org.sanjose.util.GenUtil.EUR;
import static org.sanjose.util.GenUtil.PEN;
import static org.sanjose.util.GenUtil.USD;

/**
 * VASJA class
 * User: prubach
 * Date: 12.09.16
 */
@Service
public class ProcUtil {

    private static final Logger log = LoggerFactory.getLogger(ProcUtil.class);

    private EntityManager em;

    private ScpBancocabecera curBancoCabecera = null;

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

    // moneda { 0, 1, 2 }
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
    public BigDecimal getSaldoBanco(Date fecha, String codCtabanco, Character moneda) {
        StoredProcedureQuery getSaldoAlDiaCajaQuery = em.createNamedStoredProcedureQuery("getSaldoAlDiaBanco");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //log.info("running getSaldoBanco for: " + sdf.format(fecha) + " " + codCtabanco + " " + moneda);
        getSaldoAlDiaCajaQuery.setParameter(1, sdf.format(fecha));
        getSaldoAlDiaCajaQuery.setParameter(2, codCtabanco);
        getSaldoAlDiaCajaQuery.setParameter(3, moneda.toString());
        getSaldoAlDiaCajaQuery.execute();
        BigDecimal res = (BigDecimal) getSaldoAlDiaCajaQuery.getOutputParameterValue(4);
        res = res.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        em.close();
        return res;
    }

    @Transactional(readOnly = false)
    public void doEnviarContabilidad(Set<ScpCajabanco> vcbs) throws EnviarContabilidadException {
        ScpCajabanco scp = null;
        try {
            for (ScpCajabanco vcb : vcbs) {
                scp = vcb;
                StoredProcedureQuery query = em.createNamedStoredProcedureQuery("getEnviarContabilidad");
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                query.setParameter(1, vcb.getCodCajabanco());
                query.setParameter(2, CurrentUser.get());
                query.setParameter(3, sdf.format(vcb.getFecFecha()));
                query.setParameter(4, vcb.getCodTipomoneda());
                if (PEN.equals(vcb.getCodTipomoneda())) {
                    query.setParameter(5, vcb.getNumDebesol());
                    query.setParameter(6, vcb.getNumHabersol());
                } else if (USD.equals(vcb.getCodTipomoneda())) {
                    query.setParameter(5, vcb.getNumDebedolar());
                    query.setParameter(6, vcb.getNumHaberdolar());
                } else {
                    query.setParameter(5, vcb.getNumDebemo());
                    query.setParameter(6, vcb.getNumHabermo());
                }
                if (!GenUtil.strNullOrEmpty(vcb.getCodProyecto())) {
                    query.setParameter(7, vcb.getCodProyecto());
                } else {
                    query.setParameter(7, vcb.getCodTercero());
                }
                String result = (String) query.getOutputParameterValue(8);
                query.execute();
                Notification.show("Operacion: " + vcb.getCodCajabanco(), result, Notification.Type.TRAY_NOTIFICATION);
            }
        } catch (Exception pe) {
            throw new EnviarContabilidadException("Problema al enviar a contabilidad operacion: " + (scp != null ? scp.getCodCajabanco() : 0)
                    + "\n\n" + pe.getMessage() +
                    (pe.getCause() != null ? "\n" + pe.getCause().getMessage() : "")
                    + (pe.getCause() != null && pe.getCause().getCause() != null ? "\n" + pe.getCause().getCause().getMessage() : ""), scp);
        }
    }

/*
    public void enviarContabilidad(Collection<Object> vcbs, ComprobanteService service) {
        StringBuffer sb = new StringBuffer();
        vcbs.forEach(scpCajabanco -> {
            sb.append("\n").append(((ScpCajabanco) scpCajabanco).getTxtCorrelativo()).append(" fecha: ").append(((ScpCajabanco) scpCajabanco).getFecFecha());
        });
        MessageBox
                .createQuestion()
                .withCaption("Falta tipo de cambio")
                .withMessage("Falta tipo de cambio para operaciones: " + sb.toString() + "\n?Continuar o ignorar esta operacion?\n")
                .withYesButton(() -> {})
                .withNoButton(() -> {})
                .open();
    }

*/
    //@Transactional(readOnly = false)

    private boolean existeTipoDeCambio(Date fecha, Character moneda, ScpTipocambioRep tipocambioRep) {
        List<ScpTipocambio> tipocambios = tipocambioRep.findById_FecFechacambio(
                GenUtil.getBeginningOfDay(fecha));
        BigDecimal tcval = new BigDecimal(0);
        if (!tipocambios.isEmpty()) {
            ScpTipocambio tipocambio = tipocambios.get(0);
            tcval = EUR.equals(moneda) ? tipocambio.getNumTcveuro() : tipocambio.getNumTcvdolar();
        }
        // Falta Tipo de cambio
        return !(tcval.compareTo(new BigDecimal(0))==0);
    }

    public void enviarContabilidad(Collection<Object> vcbs, ComprobanteService service, ItemsRefreshing<ScpCajabanco> itemsRefreshing) {
        Set<ScpCajabanco> cajabancosAEnviar = new HashSet<>();
        try{
            Set<ScpCajabanco> cajaBancosFaltaTipoCambio = new HashSet<>();
            for (Object objVcb : vcbs) {
                ScpCajabanco vcb = (ScpCajabanco) objVcb;
                if (vcb.isEnviado()) {
                    Notification.show("!Attention!", "!Omitiendo operacion " + vcb.getTxtCorrelativo() + " - ya esta enviada!", Notification.Type.TRAY_NOTIFICATION);
                    continue;
                }
                cajabancosAEnviar.add(vcb);
                // Falta Tipo de cambio?
                if (!existeTipoDeCambio(vcb.getFecFecha(), vcb.getCodTipomoneda(), service.getTipocambioRep())) {
                    cajaBancosFaltaTipoCambio.add(vcb);
                }
            }
            if (!cajaBancosFaltaTipoCambio.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                StringBuffer sb = new StringBuffer();
                cajaBancosFaltaTipoCambio.forEach(scpCajabanco -> {
                    sb.append("\n").append(scpCajabanco.getTxtCorrelativo()).append(" fecha: ").append(sdf.format(scpCajabanco.getFecFecha()));});
                MessageBox
                        .createQuestion()
                        .withCaption("Falta tipo de cambio")
                        .withMessage("Falta tipo de cambio para operaciones: " + sb.toString() +"\n?Continuar o ignorar esta operacion?\n")
                        .withYesButton(() -> {
                            try {
                                doEnviarContabilidad(cajabancosAEnviar);
                                itemsRefreshing.refreshItems(cajabancosAEnviar);
                            } catch (EnviarContabilidadException envexc) {
                                MessageBox
                                        .createError()
                                        .withCaption("Problema al Enviar a contabilidad")
                                        .withMessage(envexc.getMessage())
                                        .withOkButton()
                                        .open();
                            }
                        })
                        .withNoButton()
                        .open();
            } else {
                doEnviarContabilidad(cajabancosAEnviar);
            }
        } catch (EnviarContabilidadException envexc) {
            MessageBox
                    .createError()
                    .withCaption("Problema al Enviar a contabilidad")
                    .withMessage(envexc.getMessage())
                    .withOkButton()
                    .open();

        }
        itemsRefreshing.refreshItems(cajabancosAEnviar);
    }


    public void enviarContabilidadBanco(Collection<Object> vcbs, BancoService service, ItemsRefreshing<ScpBancocabecera> itemsRefreshing) {
        try{
            Set<ScpBancocabecera> bancosAEnviar = new HashSet<>();
            Set<ScpBancocabecera> bancosFaltaTipoCambio = new HashSet<>();
            for (Object objVcb : vcbs) {
                curBancoCabecera = (ScpBancocabecera) objVcb;
                if (curBancoCabecera.isEnviado()) {
                    Notification.show("!Attention!", "!Omitiendo operacion " + curBancoCabecera.getTxtCorrelativo() + " - ya esta enviada!", Notification.Type.TRAY_NOTIFICATION);
                    continue;
                }
                bancosAEnviar.add(curBancoCabecera);
                // Falta Tipo de cambio?
                if (!existeTipoDeCambio(curBancoCabecera.getFecFecha(), curBancoCabecera.getCodTipomoneda(), service.getScpTipocambioRep())) {
                    bancosFaltaTipoCambio.add(curBancoCabecera);
                }
            }
            if (!bancosFaltaTipoCambio.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                StringBuffer sb = new StringBuffer();
                bancosFaltaTipoCambio.forEach(scpBanco -> {
                    sb.append("\n").append(scpBanco.getTxtCorrelativo()).append(" fecha: ").append(sdf.format(scpBanco.getFecFecha()));});
                MessageBox
                        .createQuestion()
                        .withCaption("Falta tipo de cambio")
                        .withMessage("Falta tipo de cambio para operaciones: " + sb.toString() +"\n?Continuar o ignorar esta operacion?\n")
                        .withYesButton(() -> {
                            try {
                                itemsRefreshing.refreshItems(enviarContabilidadBancoInTransaction(bancosAEnviar, service));
                            } catch (EnviarContabilidadException envexc) {
                                MessageBox
                                        .createError()
                                        .withCaption("Problema al Enviar a contabilidad")
                                        .withMessage(envexc.getMessage())
                                        .withOkButton()
                                        .open();
                            }
                        })
                        .withNoButton()
                        .open();
            } else {
                itemsRefreshing.refreshItems(enviarContabilidadBancoInTransaction(bancosAEnviar, service));
            }
        } catch (EnviarContabilidadException envexc) {
            MessageBox
                    .createError()
                    .withCaption("Problema al Enviar a contabilidad")
                    .withMessage(envexc.getMessage())
                    .withOkButton()
                    .open();
        }
    }


    @Transactional(readOnly = false)
    public String doEnviarContabilidadBanco(ScpBancocabecera vcb) throws EnviarContabilidadException {
        try {
            StoredProcedureQuery query = em.createNamedStoredProcedureQuery("getEnviarBanco");
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            query.setParameter(1, vcb.getCodBancocabecera());
            query.setParameter(2, CurrentUser.get());
            query.setParameter(3, sdf.format(vcb.getFecFecha()));
            query.setParameter(4, vcb.getCodTipomoneda());
            String result = (String) query.getOutputParameterValue(5);
            query.execute();
            return result;
        } catch (Exception pe) {
            throw new EnviarContabilidadException("Problema al enviar a contabilidad operacion: " + (vcb != null ? vcb.getCodBancocabecera() : 0)
                    + "\n\n" + pe.getMessage() +
                    (pe.getCause() != null ? "\n" + pe.getCause().getMessage() : "")
                    + (pe.getCause() != null && pe.getCause().getCause() != null ? "\n" + pe.getCause().getCause().getMessage() : ""), null);
        }
    }

    @Transactional(readOnly = false)
    public Set<ScpBancocabecera> enviarContabilidadBancoInTransaction(Set<ScpBancocabecera> vsjBancocabeceras, BancoService service) throws EnviarContabilidadException {
        Set<ScpBancocabecera> vsjBancocabecerasEnviados = new HashSet<>();
        for (ScpBancocabecera vcbS : vsjBancocabeceras) {
            curBancoCabecera = vcbS;
            log.info("Enviando: " + curBancoCabecera);
            String result = doEnviarContabilidadBanco(curBancoCabecera);
            curBancoCabecera = service.getBancocabeceraRep().findByCodBancocabecera(curBancoCabecera.getCodBancocabecera());
            service.getBancocabeceraRep().save(curBancoCabecera);
            if (result.contains("correctamente"))
                vsjBancocabecerasEnviados.add(curBancoCabecera);
            log.info("Resultado: " + result);
            Notification.show("Operacion: " + curBancoCabecera.getCodBancocabecera(), result, Notification.Type.TRAY_NOTIFICATION);
        }
        return vsjBancocabecerasEnviados;
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

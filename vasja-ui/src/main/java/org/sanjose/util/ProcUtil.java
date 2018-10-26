package org.sanjose.util;

import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.ui.Notification;
import org.sanjose.authentication.CurrentUser;
import org.sanjose.model.ScpCajabanco;
import org.sanjose.model.ScpTipocambio;
import org.sanjose.model.ScpBancocabecera;
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

    private boolean isContinueEnviar = true;

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
    public String enviarContabilidad(ScpCajabanco vcb) {
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
        query.execute();
        return (String)query.getOutputParameterValue(8);
    }

    @Transactional(readOnly = false)
    public void enviarContabilidad(Collection<Object> vcbs, ComprobanteService service) {
        ScpCajabanco vcb = null;
        try {
            List<ScpCajabanco> scpCajabancoList = new ArrayList<>();
            for (Object objVcb : vcbs) {
                vcb = (ScpCajabanco) objVcb;
                if (vcb.isEnviado()) {
                    continue;
                }
                scpCajabancoList.add(vcb);
                // Check TipoDeCambio
                log.info("Check tipoDeCambio: " + vcb);
                List<ScpTipocambio> tipocambios = service.getTipocambioRep().findById_FecFechacambio(
                        GenUtil.getBeginningOfDay(vcb.getFecFecha()));
                BigDecimal tcval = new BigDecimal(0);
                if (!tipocambios.isEmpty()) {
                    ScpTipocambio tipocambio = tipocambios.get(0);
                    tcval = EUR.equals(vcb.getCodTipomoneda()) ? tipocambio.getNumTcveuro() : tipocambio.getNumTcvdolar();
                }
                System.out.println("got tcval: " + tcval + " " + tcval.compareTo(new BigDecimal(0)));
                if (tcval.compareTo(new BigDecimal(0))==0) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    Notification.show("Falta tipo de cambio para el dia: " + sdf.format(vcb.getFecFecha()), Notification.Type.WARNING_MESSAGE);
                    final ProcUtil pu = this;
                    final ScpCajabanco tmpVcb = vcb;
/*
                    MessageBox
                            .createQuestion()
                            .withCaption("Falta tipo de cambio")
                            .withMessage("Falta tipo de cambio para el dia: " + sdf.format(vcb.getFecFecha()) +"\n?Continuar?\n")
                            .withYesButton(() -> {
                                pu.setContinueEnviar(true);
                                System.out.println("Continue");
                            })
                            .withNoButton(() -> {
                                pu.setContinueEnviar(false);
                                System.out.println("No continue");
                            })
                            .open();
*/
                }
                if (!isContinueEnviar) return;
            }
            for (ScpCajabanco vcbS : scpCajabancoList) {
                vcb = vcbS;
                log.info("Enviando: " + vcb);
                String result = enviarContabilidad(vcb);
                log.info("Resultado: " + result);
                Notification.show("Operacion: " + vcb.getCodCajabanco(), result, Notification.Type.TRAY_NOTIFICATION);
            }
            if (vcbs.size() != scpCajabancoList.size()) {
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

    @Transactional(readOnly = false)
    public String enviarContabilidadBanco(ScpBancocabecera vcb) {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery("getEnviarBanco");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        query.setParameter(1, vcb.getCodBancocabecera());
        query.setParameter(2, CurrentUser.get());
        query.setParameter(3, sdf.format(vcb.getFecFecha()));
        query.setParameter(4, vcb.getCodTipomoneda());
        boolean res = query.execute();
        if (res)
            return "La operacion ha sido enviada a contabilidad correctamente";
        else
            return "Problema al enviar la operacion a contabilidad";
    }

    @Transactional(readOnly = false)
    public List<ScpBancocabecera> enviarContabilidadBancoInTransaction(Collection<Object> vcbs, BancoService service) {
        List<ScpBancocabecera> vsjBancocabeceras = new ArrayList<>();
        List<ScpBancocabecera> vsjBancocabecerasEnviados = new ArrayList<>();
        for (Object objVcb : vcbs) {
            curBancoCabecera = (ScpBancocabecera) objVcb;
            if (curBancoCabecera.isEnviado()) {
                continue;
            }
            vsjBancocabeceras.add(curBancoCabecera);
            // Check TipoDeCambio
            log.info("Check tipoDeCambio: " + curBancoCabecera);
            List<ScpTipocambio> tipocambios = service.getScpTipocambioRep().findById_FecFechacambio(
                    GenUtil.getBeginningOfDay(curBancoCabecera.getFecFecha()));
            if (tipocambios.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Notification.show("Falta tipo de cambio para el dia: " + sdf.format(curBancoCabecera.getFecFecha()), Notification.Type.WARNING_MESSAGE);
                return vsjBancocabecerasEnviados;
            }
        }
        for (ScpBancocabecera vcbS : vsjBancocabeceras) {
            curBancoCabecera = vcbS;
            log.info("Enviando: " + curBancoCabecera);
            String result = enviarContabilidadBanco(curBancoCabecera);
            curBancoCabecera = service.getBancocabeceraRep().findByCodBancocabecera(curBancoCabecera.getCodBancocabecera());
            service.getBancocabeceraRep().save(curBancoCabecera);
            if (result.contains("correctamente"))
                vsjBancocabecerasEnviados.add(curBancoCabecera);
            log.info("Resultado: " + result);
            Notification.show("Operacion: " + curBancoCabecera.getCodBancocabecera(), result, Notification.Type.TRAY_NOTIFICATION);
        }
        if (vcbs.size() != vsjBancocabeceras.size()) {
            Notification.show("!Attention!", "!Algunas operaciones eran omitidas por ya ser enviadas!", Notification.Type.TRAY_NOTIFICATION);
        }
        return vsjBancocabecerasEnviados;
    }

    public List<ScpBancocabecera> enviarContabilidadBanco(Collection<Object> vcbs, BancoService service) {
        try {
            return enviarContabilidadBancoInTransaction(vcbs, service);
        } catch (PersistenceException pe) {
            Notification.show("Problema al enviar a contabilidad operacion: " + (curBancoCabecera != null ? curBancoCabecera.getCodBancocabecera() : 0)
                            + "\n\n" + pe.getMessage() +
                            (pe.getCause() != null ? "\n" + pe.getCause().getMessage() : "")
                            + (pe.getCause() != null && pe.getCause().getCause() != null ? "\n" + pe.getCause().getCause().getMessage() : "")
                    , Notification.Type.ERROR_MESSAGE);
            pe.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void setContinueEnviar(boolean is) {
        isContinueEnviar = is;
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

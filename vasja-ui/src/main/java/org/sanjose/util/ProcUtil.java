package org.sanjose.util;

import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.ui.Notification;
import de.steinwedel.messagebox.MessageBox;
import org.sanjose.MainUI;
import org.sanjose.authentication.CurrentUser;
import org.sanjose.model.*;
import org.sanjose.repo.ScpTipocambioRep;
import org.sanjose.views.ItemsRefreshing;
import org.sanjose.views.banco.BancoTipoCambiosLogic;
import org.sanjose.views.caja.CajaTipoCambiosLogic;
import org.sanjose.views.rendicion.RendicionManejoViewing;
import org.sanjose.views.sys.PersistanceService;
import org.sanjose.views.rendicion.RendicionTipoCambiosLogic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.StoredProcedureQuery;
import java.math.BigDecimal;
import java.sql.Timestamp;
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
    
    private ScpRendicioncabecera curRendicionCabecera = null;

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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
    public SaldosBanco getSaldoBanco(Date fecha, String codCtabanco, Character moneda) {
        StoredProcedureQuery getSaldoAlDiaCajaQuery = em.createNamedStoredProcedureQuery("getSaldoAlDiaBanco");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //log.info("running getSaldoBanco for: " + sdf.format(fecha) + " " + codCtabanco + " " + moneda);
        getSaldoAlDiaCajaQuery.setParameter(1, sdf.format(fecha));
        getSaldoAlDiaCajaQuery.setParameter(2, codCtabanco);
        getSaldoAlDiaCajaQuery.setParameter(3, moneda.toString());
        getSaldoAlDiaCajaQuery.execute();
        BigDecimal saldoLibro = (BigDecimal) getSaldoAlDiaCajaQuery.getOutputParameterValue(4);
        BigDecimal saldoBanco = (BigDecimal) getSaldoAlDiaCajaQuery.getOutputParameterValue(5);
        SaldosBanco sb = new SaldosBanco();
        sb.setSegLibro(saldoLibro.setScale(2, BigDecimal.ROUND_HALF_EVEN));
        if (saldoBanco!=null) sb.setSegBanco(saldoBanco.setScale(2, BigDecimal.ROUND_HALF_EVEN));
        em.close();
        return sb;
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
    public void enviarContabilidad(Collection<Object> vcbs, PersistanceService service) {
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

    public static boolean existeTipoDeCambio(Date fecha, Character moneda, ScpTipocambioRep tipocambioRep) {
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

    public void enviarContabilidad(Collection<Object> vcbs, PersistanceService service, ItemsRefreshing<ScpCajabanco> itemsRefreshing) {
        Set<ScpCajabanco> cajabancosAEnviar = new HashSet<>();
        Map<ScpCajabanco, String> cajaBancosFaltaTipoCambio = new HashMap<>();
        for (Object objVcb : vcbs) {
            ScpCajabanco vcb = (ScpCajabanco) objVcb;
            if (vcb.isEnviado()) {
                Notification.show("!Atencion!", "!Omitiendo operacion " + vcb.getTxtCorrelativo() + " - ya esta enviada!", Notification.Type.TRAY_NOTIFICATION);
                continue;
            }
            cajabancosAEnviar.add(vcb);
            // Falta Tipo de cambio?
            if (!existeTipoDeCambio(vcb.getFecFecha(), vcb.getCodTipomoneda(), service.getTipocambioRep())) {
                try {
                    TipoCambio.checkTipoCambio(vcb.getFecFecha(), service.getTipocambioRep());
                } catch (TipoCambio.TipoCambioNoExiste | TipoCambio.TipoCambioNoSePuedeBajar e) {
                    cajaBancosFaltaTipoCambio.put(vcb, e.getMessage());
                }
            }
        }
        if (!cajaBancosFaltaTipoCambio.isEmpty()) {
            new CajaTipoCambiosLogic(cajabancosAEnviar, service, this, itemsRefreshing);
        } else {
            enviarContabilidadCajaConTipoCambio(cajabancosAEnviar, service, itemsRefreshing);
        }
    }

    public void enviarContabilidadCajaConTipoCambio(Set<ScpCajabanco> cajabancos, PersistanceService service, ItemsRefreshing<ScpCajabanco> itemsRefreshing) {
        try {
            doEnviarContabilidad(cajabancos);
            itemsRefreshing.refreshItems(cajabancos);
        } catch (EnviarContabilidadException envexc) {
            MessageBox
                    .createError()
                    .withCaption("Problema al Enviar a contabilidad")
                    .withMessage(envexc.getMessage())
                    .withOkButton()
                    .open();
        }
    }


    public void enviarContabilidadBanco(Collection<Object> vcbs, PersistanceService service, ItemsRefreshing<ScpBancocabecera> itemsRefreshing) {
        Set<ScpBancocabecera> bancosAEnviar = new HashSet<>();
        Map<ScpBancocabecera, String> bancosFaltaTipoCambio = new HashMap<>();
        for (Object objVcb : vcbs) {
            curBancoCabecera = (ScpBancocabecera) objVcb;
            if (curBancoCabecera.isEnviado()) {
                Notification.show("!Attention!", "!Omitiendo operacion " + curBancoCabecera.getTxtCorrelativo() + " - ya esta enviada!", Notification.Type.TRAY_NOTIFICATION);
                continue;
            }
            // Falta Tipo de cambio?
            if (!existeTipoDeCambio(curBancoCabecera.getFecFecha(), curBancoCabecera.getCodTipomoneda(), service.getTipocambioRep())) {
                try {
                    TipoCambio.checkTipoCambio(curBancoCabecera.getFecFecha(), service.getTipocambioRep());
                } catch (TipoCambio.TipoCambioNoExiste | TipoCambio.TipoCambioNoSePuedeBajar e) {
                    bancosFaltaTipoCambio.put(curBancoCabecera, e.getMessage());
                }
            }
            bancosAEnviar.add(curBancoCabecera);
        }
        if (!bancosFaltaTipoCambio.isEmpty()) {
            new BancoTipoCambiosLogic(bancosAEnviar, service, this, itemsRefreshing);
        } else {
            enviarContabilidadBancoConTipoCambio(bancosAEnviar, service, itemsRefreshing);
        }
    }



    public void enviarContabilidadBancoConTipoCambio(Set<ScpBancocabecera> rendicionsAEnviar, PersistanceService service, ItemsRefreshing<ScpBancocabecera> itemsRefreshing) {
        try {
            itemsRefreshing.refreshItems(enviarContabilidadBancoInTransaction(rendicionsAEnviar, service));
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
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            StoredProcedureQuery query = em.createNamedStoredProcedureQuery("getEnviarBanco");
            query.setParameter(1, vcb.getCodBancocabecera());
            query.setParameter(2, CurrentUser.get());
            query.setParameter(3, sdf.format(vcb.getFecFecha()));
            query.setParameter(4, vcb.getCodTipomoneda());
            String result = (String) query.getOutputParameterValue(5);
            query.execute();
            return result;
        } catch (Exception pe) {
            log.warn("Problem running:\n" +
                    "EXEC [dbo].[usp_scp_vsj_enviarAContabilidadBanco] " + vcb.getCodBancocabecera() + ", '"
                    + CurrentUser.get() + "', '" + sdf.format(vcb.getFecFecha()) + "', " + vcb.getCodTipomoneda());
            throw new EnviarContabilidadException("Problema al enviar a contabilidad operacion: " + (vcb != null ? vcb.getCodBancocabecera() : 0)
                    + "\n\n" + pe.getMessage() +
                    (pe.getCause() != null ? "\n" + pe.getCause().getMessage() : "")
                    + (pe.getCause() != null && pe.getCause().getCause() != null ? "\n" + pe.getCause().getCause().getMessage() : ""), null);
        }
    }

    @Transactional(readOnly = false)
    public Set<ScpBancocabecera> enviarContabilidadBancoInTransaction(Set<ScpBancocabecera> vsjBancocabeceras, PersistanceService service) throws EnviarContabilidadException {
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

    public class SaldosBanco {

        private BigDecimal segLibro;
        private BigDecimal segBanco;

        public BigDecimal getSegLibro() {
            return segLibro;
        }

        public void setSegLibro(BigDecimal segLibro) {
            this.segLibro = segLibro;
        }

        public BigDecimal getSegBanco() {
            return segBanco;
        }

        public void setSegBanco(BigDecimal segBanco) {
            this.segBanco = segBanco;
        }

        @Override
        public String toString() {
            return "SaldosBanco{" +
                    "segLibro=" + segLibro +
                    ", segBanco=" + segBanco +
                    '}';
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

        public BigDecimal getSaldoUSD() {
            return saldoUSD;
        }

        public BigDecimal getSaldoEUR() {
            return saldoEUR;
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

    public void checkDescuadradoAndEnviaContab(ScpRendicioncabecera rendicioncabecera, boolean isEnviar, PersistanceService service, RendicionManejoViewing manView, ItemsRefreshing<ScpRendicioncabecera> itemsRefreshing) {
        List<ScpRendicioncabecera> cabecerasParaEnviar = new ArrayList<>();

        if (manView!=null) {
            Collection<Object> cabs = manView.getSelectedRows();
            cabs.forEach(e -> cabecerasParaEnviar.add((ScpRendicioncabecera) e));
        }
        if (cabecerasParaEnviar.isEmpty() && rendicioncabecera!=null) {
            cabecerasParaEnviar.add(rendicioncabecera);
        }

        List<String> desuadrados = new ArrayList<>();
        for (Object objVcb : cabecerasParaEnviar) {
            ScpRendicioncabecera rendcab = (ScpRendicioncabecera) objVcb;
            if (service.checkIfRendicionDescuadrado(rendcab)) {
                desuadrados.add(rendcab.getCodComprobante());
            }
        }

        if (isEnviar && !desuadrados.isEmpty()) {
            MessageBox
                    .createQuestion()
                    .withCaption("!Atencion!")
                    .withMessage("?Estas rendiciones son descuadradas, quieres enviarlas de todas maneras?\n"+ Arrays.toString(desuadrados.toArray()) +"")
                    .withYesButton(() -> enviarContabilidad(cabecerasParaEnviar, isEnviar, service, manView, itemsRefreshing))
                    .withNoButton()
                    .open();
        } else {
            enviarContabilidad(cabecerasParaEnviar, isEnviar, service, manView, itemsRefreshing);
        }
    }



    public void enviarContabilidad(List<ScpRendicioncabecera> cabecerasParaEnviar, boolean isEnviar, PersistanceService service, RendicionManejoViewing manView, ItemsRefreshing<ScpRendicioncabecera> itemsRefreshing) {
        Collection<ScpRendicioncabecera> cabecerasParaRefresh = new ArrayList<>();
        cabecerasParaEnviar.forEach(e -> cabecerasParaRefresh.add(e));
        if (isEnviar) {
            Set<ScpRendicioncabecera> cabecerasEnviados = new HashSet<>();
            List<String> cabecerasIdsEnviados = new ArrayList<>();
            // Check if already sent and ask if only marcar...
            for (Object objVcb : cabecerasParaEnviar) {
                ScpRendicioncabecera rendcab = (ScpRendicioncabecera) objVcb;
                if (!rendcab.isEnviado() && service.checkIfAlreadyEnviado(rendcab)) {
                    cabecerasEnviados.add(rendcab);
                    cabecerasIdsEnviados.add(rendcab.getCodRendicioncabecera().toString());
                }
            }
            for (ScpRendicioncabecera rendcab : cabecerasEnviados) {
                cabecerasParaEnviar.remove(rendcab);
            }
            if (cabecerasEnviados.isEmpty()) {
                MainUI.get().getProcUtil().enviarContabilidadRendicion(cabecerasParaEnviar, service, (manView!=null ? manView.getViewLogic() : itemsRefreshing));
                if (manView!=null) {
                    manView.getGrid().deselectAll();
                    manView.clearSelection();
                }

            } else {
                MessageBox
                        .createQuestion()
                        .withCaption("!Atencion!")
                        .withMessage("?Estas operaciones ya fueron enviadas ("+ Arrays.toString(cabecerasIdsEnviados.toArray()) +"), quiere solo marcar los como enviadas?")
                        .withYesButton(() -> doMarcarEnviados(cabecerasParaEnviar, cabecerasEnviados, service, manView, itemsRefreshing))
                        .withNoButton()
                        .open();
            }
            //MainUI.get().getProcUtil().enviarContabilidadRendicion(cabecerasParaEnviar, manView.getService(), this);
        } else {
            for (Object objVcb : cabecerasParaEnviar) {
                ScpRendicioncabecera scpRendicioncabecera = (ScpRendicioncabecera) objVcb;
                if (!scpRendicioncabecera.isEnviado()) {
                    Notification.show("!Atencion!", "!Omitiendo operacion " + scpRendicioncabecera.getCodRendicioncabecera() + " - no esta enviada!", Notification.Type.TRAY_NOTIFICATION);
                    continue;
                }
                if (manView!=null)
                    manView.getGrid().deselect(scpRendicioncabecera);
                scpRendicioncabecera.setFlgEnviado('0');
                scpRendicioncabecera.setFecFactualiza(new Timestamp(System.currentTimeMillis()));
                scpRendicioncabecera.setCodUactualiza(CurrentUser.get());
                service.getRendicioncabeceraRep().save(scpRendicioncabecera);
            }
            if (manView!=null)
                manView.getViewLogic().refreshItems(cabecerasParaRefresh);
        }
    }

    public void doMarcarEnviados(List<ScpRendicioncabecera> cabecerasParaEnviar , Set<ScpRendicioncabecera> cabecerasEnviados, PersistanceService service, RendicionManejoViewing manView, ItemsRefreshing<ScpRendicioncabecera> itemsRefreshing) {
        for (ScpRendicioncabecera cabecera : cabecerasEnviados) {
            cabecera.setFlgEnviado('1');
            cabecera.setFecFactualiza(new Timestamp(System.currentTimeMillis()));
            cabecera.setCodUactualiza(CurrentUser.get());
            service.getRendicioncabeceraRep().save(cabecera);
        }
        if (manView!=null)
            manView.getGrid().deselectAll();
        //this.refreshItems(cabecerasEnviados);
        if (!cabecerasParaEnviar.isEmpty())
            MainUI.get().getProcUtil().enviarContabilidadRendicion(cabecerasParaEnviar, service, (manView!=null ? manView.getViewLogic() : itemsRefreshing));
        if (manView!=null) {
            manView.getGrid().deselectAll();
            manView.clearSelection();
            manView.getViewLogic().refreshItems(cabecerasEnviados);
        }
        else
            itemsRefreshing.refreshItems(cabecerasEnviados);
    }


    public void enviarContabilidadRendicion(List<ScpRendicioncabecera> vcbs, PersistanceService service, ItemsRefreshing<ScpRendicioncabecera> itemsRefreshing) {
        Set<ScpRendicioncabecera> rendicionsAEnviar = new HashSet<>();
        Map<ScpRendicioncabecera, String> rendicionsFaltaTipoCambio = new HashMap<>();
        for (ScpRendicioncabecera objVcb : vcbs) {
            curRendicionCabecera = objVcb;
            if (curRendicionCabecera.isEnviado()) {
                Notification.show("!Attention!", "!Omitiendo rendicion " + curRendicionCabecera.getCodComprobante() + " - ya esta enviada!", Notification.Type.TRAY_NOTIFICATION);
                continue;
            }
            if (GenUtil.strNullOrEmpty(curRendicionCabecera.getTxtGlosa())) {
                Notification.show("!Attention!", "!Omitiendo rendicion " + curRendicionCabecera.getCodComprobante() + " - falta glosa de cabecera!", Notification.Type.TRAY_NOTIFICATION);
                continue;
            }
            boolean isValidated = true;
            for (ScpRendiciondetalle det : service.getRendiciondetalleRep().findById_CodRendicioncabecera(curRendicionCabecera.getCodRendicioncabecera())) {
                if (GenUtil.strNullOrEmpty(det.getCodCtacontable()) || GenUtil.strNullOrEmpty(det.getCodProyecto()) || GenUtil.strNullOrEmpty(det.getTxtGlosaitem())) {
                    MessageBox
                            .createError()
                            .withCaption("Problema al Enviar a contabilidad")
                            .withMessage("!Omitiendo rendicion " + curRendicionCabecera.getCodComprobante() + " - falta glosa, proyecto o cuenta contable en item numero: " + det.getId().getNumNroitem() + " !")
                            .withOkButton()
                            .open();
                    isValidated = false;
                    break;
                }
                if ((det.getCodCtacontable().startsWith("6") || det.getCodCtacontable().startsWith("3"))
                    && (GenUtil.strNullOrEmpty(det.getCodCtaespecial()) || GenUtil.strNullOrEmpty(det.getCodContraparte()))) {
                    MessageBox
                            .createError()
                            .withCaption("Problema al Enviar a contabilidad")
                            .withMessage("!Omitiendo rendicion " + curRendicionCabecera.getCodComprobante() + " - falta rubro institucional o lugar de gasto en item numero: " + det.getId().getNumNroitem() + " !")
                            .withOkButton()
                            .open();
                    isValidated = false;
                    break;
                }
                if ((det.getCodCtacontable().startsWith("46"))
                        && (GenUtil.strNullOrEmpty(det.getCodCtaespecial()) || !GenUtil.strNullOrEmpty(det.getCodContraparte()))) {
                    MessageBox
                            .createError()
                            .withCaption("Problema al Enviar a contabilidad")
                            .withMessage("!Omitiendo rendicion " + curRendicionCabecera.getCodComprobante() + " - falta rubro institucional o lugar de gasto esta rellenado pero no deberia estar en item numero: " + det.getId().getNumNroitem() + " !")
                            .withOkButton()
                            .open();
                    isValidated = false;
                    break;
                }
            }
            if (!isValidated) continue;
            // Falta Tipo de cambio?
            if (!existeTipoDeCambio(curRendicionCabecera.getFecComprobante(), curRendicionCabecera.getCodTipomoneda(), service.getTipocambioRep())) {
                try {
                    TipoCambio.checkTipoCambio(curRendicionCabecera.getFecComprobante(), service.getTipocambioRep());
                } catch (TipoCambio.TipoCambioNoExiste | TipoCambio.TipoCambioNoSePuedeBajar e) {
                    //Tipo
                    rendicionsFaltaTipoCambio.put(curRendicionCabecera, e.getMessage());
                }
            }
            rendicionsAEnviar.add(curRendicionCabecera);
        }
        if (!rendicionsFaltaTipoCambio.isEmpty()) {
            new RendicionTipoCambiosLogic(rendicionsAEnviar, service, this, itemsRefreshing);
        } else {
            enviarContabilidadRendicionConTipoCambio(rendicionsAEnviar, service, itemsRefreshing);
        }
    }

    public void enviarContabilidadRendicionConTipoCambio(Set<ScpRendicioncabecera> rendicionsAEnviar, PersistanceService service, ItemsRefreshing<ScpRendicioncabecera> itemsRefreshing) {
        try {
            Set<ScpRendicioncabecera> enviadas = enviarContabilidadRendicionInTransaction(rendicionsAEnviar, service);
            if (itemsRefreshing!=null)
                itemsRefreshing.refreshItems(enviadas);
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
    public String doEnviarContabilidadRendicion(ScpRendicioncabecera vcb) throws EnviarContabilidadException {
        try {
            log.debug("Ready to run stored procedure to enviar: " + vcb.getCodRendicioncabecera() + " " + vcb.getCodComprobante());
            StoredProcedureQuery query = em.createNamedStoredProcedureQuery("getEnviarRendicion");
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            query.setParameter(1, vcb.getCodRendicioncabecera());
            query.setParameter(2, CurrentUser.get());
            query.setParameter(3, sdf.format(vcb.getFecComprobante()));
            query.setParameter(4, vcb.getCodTipomoneda());
            String result = (String) query.getOutputParameterValue(5);
            query.execute();
            return result;
        } catch (Exception pe) {
            throw new EnviarContabilidadException("Problema al enviar a contabilidad rendicion: " + (vcb != null ? vcb.getCodRendicioncabecera() : 0)
                    + "\n\n" + pe.getMessage() +
                    (pe.getCause() != null ? "\n" + pe.getCause().getMessage() : "")
                    + (pe.getCause() != null && pe.getCause().getCause() != null ? "\n" + pe.getCause().getCause().getMessage() : ""), null);
        }
    }

    @Transactional(readOnly = false)
    public Set<ScpRendicioncabecera> enviarContabilidadRendicionInTransaction(Set<ScpRendicioncabecera> vsjRendicioncabeceras, PersistanceService service) throws EnviarContabilidadException {
        Set<ScpRendicioncabecera> vsjRendicioncabecerasEnviados = new HashSet<>();
        for (ScpRendicioncabecera vcbS : vsjRendicioncabeceras) {
            curRendicionCabecera = vcbS;
            log.info("Enviando: " + curRendicionCabecera);
            String result = doEnviarContabilidadRendicion(curRendicionCabecera);
            curRendicionCabecera = service.getRendicioncabeceraRep().findByCodRendicioncabecera(curRendicionCabecera.getCodRendicioncabecera());
            service.getRendicioncabeceraRep().save(curRendicionCabecera);
            if (result.contains("correctamente"))
                vsjRendicioncabecerasEnviados.add(curRendicionCabecera);
            log.info("Resultado: " + result);
            Notification.show("Operacion: " + curRendicionCabecera.getCodRendicioncabecera(), result, Notification.Type.TRAY_NOTIFICATION);
        }
        return vsjRendicioncabecerasEnviados;
    }


    public String checkIfcanBeDeleted(String codDestino, PersistanceService service) {
        List<ScpCajabanco> comprobantes = service.getCajabancoRep().findByCodDestinoOrCodDestinoitem(codDestino, codDestino);
        List<ScpBancocabecera> bancoscabeceras = service.getBancocabeceraRep().findByCodDestino(codDestino);
        List<ScpBancodetalle> bancositems = service.getBancodetalleRep().findByCodDestinoOrCodDestinoitem(codDestino, codDestino);
        List<ScpRendicioncabecera> rendicionescab = service.getRendicioncabeceraRep().findByCodDestino(codDestino);
        List<ScpRendiciondetalle> rendicionitems = service.getRendiciondetalleRep().findByCodDestino(codDestino);

        StringBuilder sb = new StringBuilder();
        for (ScpCajabanco vcb : comprobantes) {
            sb.append("\n").append("Caja: ").append(vcb.getTxtCorrelativo()).append(" ").append(vcb.getFecFecha()).append(" ").append(vcb.getTxtGlosaitem());
        }
        for (ScpBancodetalle bancodet : bancositems) {
            ScpBancocabecera cab = bancodet.getScpBancocabecera();
            if (!bancoscabeceras.contains(cab))
                bancoscabeceras.add(cab);

        }
        for (ScpRendiciondetalle renddet : rendicionitems) {
            ScpRendicioncabecera cab = renddet.getScpRendicioncabecera();
            if (!rendicionescab.contains(cab))
                rendicionescab.add(cab);

        }

        for (ScpCajabanco vcb : comprobantes) {
            sb.append("\n").append("Caja: ").append(vcb.getTxtCorrelativo()).append(" ").append(vcb.getFecFecha()).append(" ").append(vcb.getTxtGlosaitem());
        }
        for (ScpBancocabecera vcb : bancoscabeceras) {
            sb.append("\n").append("Banco: ").append(vcb.getTxtCorrelativo()).append(" ").append(vcb.getFecFecha()).append(" ").append(vcb.getTxtGlosa());
        }
        for (ScpRendicioncabecera vcb : rendicionescab) {
            sb.append("\n").append("Rendicion: ").append(vcb.getCodComprobante()).append(" ").append(vcb.getFecComprobante()).append(" ").append(vcb.getTxtGlosa());
        }
        return sb.toString();
    }


    //@Transactional(readOnly = false)
    //@Transactional
    public int replaceDestino(String codDestinoToReplace, String codDestinoNew, PersistanceService service) {

        List<ScpCajabanco> comprobantes = service.getCajabancoRep().findByCodDestinoOrCodDestinoitem(codDestinoToReplace, codDestinoToReplace);
        List<ScpBancocabecera> bancoscabeceras = service.getBancocabeceraRep().findByCodDestino(codDestinoToReplace);
        List<ScpRendicioncabecera> rendicionescab = service.getRendicioncabeceraRep().findByCodDestino(codDestinoToReplace);
        List<ScpRendiciondetalle> rendicionitems = service.getRendiciondetalleRep().findByCodDestino(codDestinoToReplace);

        int cambios = 0;

        for (ScpCajabanco vcb : comprobantes) {
            boolean cambiado = false;
            if (vcb.getCodDestino().equalsIgnoreCase(codDestinoToReplace)) {
                vcb.setCodDestino(codDestinoNew);
                cambiado = true;
                cambios++;
            }
            if (vcb.getCodDestinoitem().equalsIgnoreCase(codDestinoToReplace)) {
                vcb.setCodDestinoitem(codDestinoNew);
                cambiado = true;
                cambios++;
            }
            if (cambiado)
                service.getCajabancoRep().save(vcb);
        }
        
        for (ScpBancocabecera vcb : bancoscabeceras) {
            boolean cambiadoCab = false;
            if (vcb.getCodDestino().equalsIgnoreCase(codDestinoToReplace)) {
                vcb.setCodDestino(codDestinoNew);
                cambiadoCab = true;
                cambios++;
            }
            for (ScpBancodetalle det : vcb.getScpBancodetalles()) {
                boolean cambiado = false;
                if (det.getCodDestino().equalsIgnoreCase(codDestinoToReplace)) {
                    det.setCodDestino(codDestinoNew);
                    cambiado = true;
                    //cambiadoCab = true;
                    cambios++;
                }
                if (det.getCodDestinoitem().equalsIgnoreCase(codDestinoToReplace)) {
                    det.setCodDestinoitem(codDestinoNew);
                    cambiado = true;
                    //cambiadoCab = true;
                    cambios++;
                }
                if (cambiado)
                    service.getBancodetalleRep().save(det);
            }
            if (cambiadoCab)
                service.getBancocabeceraRep().save(vcb);
        }


        List<ScpBancodetalle> bancositems = service.getBancodetalleRep().findByCodDestinoOrCodDestinoitem(codDestinoToReplace, codDestinoToReplace);
        for (ScpBancodetalle det : bancositems) {
            boolean cambiado = false;
            if (det.getCodDestino().equalsIgnoreCase(codDestinoToReplace)) {
                det.setCodDestino(codDestinoNew);
                cambiado = true;
                //cambiadoCab = true;
                cambios++;
            }
            if (det.getCodDestinoitem().equalsIgnoreCase(codDestinoToReplace)) {
                det.setCodDestinoitem(codDestinoNew);
                cambiado = true;
                //cambiadoCab = true;
                cambios++;
            }
            if (cambiado)
                service.getBancodetalleRep().save(det);
        }


        for (ScpRendicioncabecera vcb : rendicionescab) {
            if (vcb.getCodDestino().equalsIgnoreCase(codDestinoToReplace)) {
                vcb.setCodDestino(codDestinoNew);
                service.getRendicioncabeceraRep().save(vcb);
                cambios++;
            }
        }

        for (ScpRendiciondetalle vcb : rendicionitems) {
            if (vcb.getCodDestino().equalsIgnoreCase(codDestinoToReplace)) {
                vcb.setCodDestino(codDestinoNew);
                service.getRendiciondetalleRep().save(vcb);
                cambios++;
            }
        }
        return cambios;
    }
}

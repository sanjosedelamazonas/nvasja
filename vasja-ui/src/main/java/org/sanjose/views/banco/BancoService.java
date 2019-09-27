package org.sanjose.views.banco;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import org.sanjose.converter.MesCobradoToBooleanConverter;
import org.sanjose.model.*;
import org.sanjose.repo.*;
import org.sanjose.util.ConfigurationUtil;
import org.sanjose.util.GenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.sanjose.util.GenUtil.PEN;
import static org.sanjose.util.GenUtil.USD;

/**
 * Created by pol on 06.10.16.
 */
@Service
@Transactional
public class BancoService {

    private final VsjConfiguractacajabancoRep configuractacajabancoRepo;
    private final ScpPlancontableRep planRepo;
    private final ScpPlanespecialRep planEspRepo;
    private final ScpProyectoRep proyectoRepo;
    private final ScpDestinoRep destinoRepo;
    private final ScpComprobantepagoRep comprobantepagoRepo;
    private final ScpFinancieraRep financieraRepo;
    private final ScpPlanproyectoRep planproyectoRepo;
    private final Scp_ProyectoPorFinancieraRep proyectoPorFinancieraRepo;
    private final Scp_ContraparteRep contraparteRepo;
    private final VsjConfiguracioncajaRep configuracioncajaRepo;
    private final ScpCargocuartaRep cargocuartaRepo;
    private final ScpTipodocumentoRep tipodocumentoRepo;
    private final EntityManager em;
    private final ScpBancocabeceraRep bancocabeceraRep;
    private final ScpBancodetalleRep bancodetalleRep;
    private final Logger log = LoggerFactory.getLogger(BancoService.class);
    private ScpTipocambioRep scpTipocambioRep;
    private ScpComprobantedetalleRep scpComprobantedetalleRep;

    @Autowired
    public BancoService(ScpBancocabeceraRep bancocabeceraRep, ScpBancodetalleRep bancodetalleRep,
                        VsjConfiguractacajabancoRep configuractacajabancoRepo, ScpPlancontableRep planRepo,
                        ScpPlanespecialRep planEspRepo, ScpProyectoRep proyectoRepo, ScpDestinoRep destinoRepo,
                        ScpComprobantepagoRep comprobantepagoRepo, ScpFinancieraRep financieraRepo,
                        ScpPlanproyectoRep planproyectoRepo, Scp_ProyectoPorFinancieraRep proyectoPorFinancieraRepo,
                        Scp_ContraparteRep contraparteRepo, VsjConfiguracioncajaRep configuracioncajaRepo,
                        ScpCargocuartaRep cargocuartaRepo, ScpTipodocumentoRep tipodocumentoRepo,
                        ScpComprobantedetalleRep scpComprobantedetalleRep, ScpTipocambioRep scpTipocambioRep, EntityManager em) {
        this.bancocabeceraRep = bancocabeceraRep;
        this.bancodetalleRep = bancodetalleRep;
        this.configuractacajabancoRepo = configuractacajabancoRepo;
        this.planRepo = planRepo;
        this.planEspRepo = planEspRepo;
        this.proyectoRepo = proyectoRepo;
        this.destinoRepo = destinoRepo;
        this.comprobantepagoRepo = comprobantepagoRepo;
        this.financieraRepo = financieraRepo;
        this.planproyectoRepo = planproyectoRepo;
        this.proyectoPorFinancieraRepo = proyectoPorFinancieraRepo;
        this.contraparteRepo = contraparteRepo;
        this.configuracioncajaRepo = configuracioncajaRepo;
        this.cargocuartaRepo = cargocuartaRepo;
        this.tipodocumentoRepo = tipodocumentoRepo;
        this.scpComprobantedetalleRep = scpComprobantedetalleRep;
        this.scpTipocambioRep = scpTipocambioRep;
        this.em = em;
    }

    @Transactional(readOnly = false)
    public ScpBancodetalle saveBancoOperacion(ScpBancocabecera cabecera, ScpBancodetalle bancoItem, Character moneda) throws FieldGroup.CommitException {
        cabecera.setCodTipomoneda(moneda);
        cabecera = cabecera.prepareToSave();
        cabecera = bancocabeceraRep.save(cabecera);
        if (GenUtil.strNullOrEmpty(cabecera.getTxtCorrelativo())) {
            cabecera.setTxtCorrelativo(GenUtil.getTxtCorrelativo(cabecera.getCodBancocabecera()));
            cabecera = bancocabeceraRep.save(cabecera);
        }
        bancoItem.setCodCtacontable(cabecera.getCodCtacontable());
        VsjConfiguractacajabanco codTipoMov = configuractacajabancoRepo.findById(bancoItem.getCodTipomov());
        if (codTipoMov==null) {
            throw new FieldGroup.CommitException("No se puede encontrar el Codigo Tipo Gasto - por favor verifica la configuracion de Caja y Bancos");
        }
        bancoItem.setCodTipogasto(codTipoMov.getCodTipocuenta());
        bancoItem.setCodTipomoneda(moneda);
        bancoItem = bancoItem.prepareToSave();
        bancoItem.setFecFecha(cabecera.getFecFecha());
        bancoItem.setTxtCheque(cabecera.getTxtCheque());
        bancoItem.setScpBancocabecera(cabecera);
        if (bancoItem.getId() == null) {
            ScpBancodetallePK id = new ScpBancodetallePK();
            id.setCodBancocabecera(cabecera.getCodBancocabecera());
            id.setNumItem(bancodetalleRep.findById_CodBancocabecera(cabecera.getCodBancocabecera()).size() + 1);
            bancoItem.setId(id);
        }

        bancoItem.setScpBancocabecera(cabecera);
        if (GenUtil.strNullOrEmpty(bancoItem.getTxtCorrelativo())) {
            bancoItem.setTxtCorrelativo(cabecera.getTxtCorrelativo());
        }
        bancoItem = bancodetalleRep.save(bancoItem);
        BigDecimal saldoHabersol = new BigDecimal(0);
        BigDecimal saldoHaberdolar = new BigDecimal(0);
        BigDecimal saldoHabermo = new BigDecimal(0);
        BigDecimal saldoDebesol = new BigDecimal(0);
        BigDecimal saldoDebedolar = new BigDecimal(0);
        BigDecimal saldoDebemo = new BigDecimal(0);
        for (ScpBancodetalle it : bancodetalleRep
                .findById_CodBancocabecera(cabecera.getCodBancocabecera())) {
            // Check if moneda changed - if so update in other items
            if (!it.getCodTipomoneda().equals(moneda)) {
                // Change moneda for old items
                BigDecimal oldDebe = getDebeAndReset(it);
                BigDecimal oldHaber = getHaberAndReset(it);
                it = setNewSaldos(it, oldDebe, oldHaber, moneda);
                it = bancodetalleRep.save(it);
            }
            
            saldoDebedolar = saldoDebedolar.add(it.getNumDebedolar());
            saldoDebemo = saldoDebemo.add(it.getNumDebemo());
            saldoDebesol = saldoDebesol.add(it.getNumDebesol());
            saldoHaberdolar = saldoHaberdolar.add(it.getNumHaberdolar());
            saldoHabermo = saldoHabermo.add(it.getNumHabermo());
            saldoHabersol = saldoHabersol.add(it.getNumHabersol());
        }
        cabecera.setNumDebesol(saldoDebesol);
        cabecera.setNumHabersol(saldoHabersol);
        cabecera.setNumDebedolar(saldoDebedolar);
        cabecera.setNumHaberdolar(saldoHaberdolar);
        cabecera.setNumDebemo(saldoDebemo);
        cabecera.setNumHabermo(saldoHabermo);
        cabecera = bancocabeceraRep.save(cabecera);
        bancoItem.setScpBancocabecera(cabecera);
        return bancoItem;
    }

    private ScpBancodetalle setNewSaldos(ScpBancodetalle bd, BigDecimal debe, BigDecimal haber, Character moneda) {
        if (moneda.equals(PEN)) {
            bd.setNumDebesol(debe);
            bd.setNumHabersol(haber);
        } else if (moneda.equals(USD)) {
            bd.setNumDebedolar(debe);
            bd.setNumHaberdolar(haber);
        } else {
            bd.setNumDebemo(debe);
            bd.setNumHabermo(haber);
        }
        bd.setCodTipomoneda(moneda);
        return bd;
    }
    
    private BigDecimal getDebeAndReset(ScpBancodetalle bd) {
        BigDecimal debe = new BigDecimal(0);
        if (bd.getCodTipomoneda().equals(PEN)) {
            debe = debe.add(bd.getNumDebesol());
            bd.setNumDebesol(new BigDecimal(0));
        } else if (bd.getCodTipomoneda().equals(USD)) {
            debe = debe.add(bd.getNumDebedolar());
            bd.setNumDebedolar(new BigDecimal(0));
        } else {
            debe = debe.add(bd.getNumDebemo());
            bd.setNumDebemo(new BigDecimal(0));
        }
        return debe;
    }

    private BigDecimal getHaberAndReset(ScpBancodetalle bd) {
        BigDecimal haber = new BigDecimal(0);
        if (bd.getCodTipomoneda().equals(PEN)) {
            haber = haber.add(bd.getNumHabersol());
            bd.setNumHabersol(new BigDecimal(0));
        } else if (bd.getCodTipomoneda().equals(USD)) {
            haber = haber.add(bd.getNumHaberdolar());
            bd.setNumHaberdolar(new BigDecimal(0));
        } else {
            haber = haber.add(bd.getNumHabermo());
            bd.setNumHabermo(new BigDecimal(0));
        }
        return haber;
    }
    
    @Transactional(readOnly = false)
    public void deleteBancoOperacion(ScpBancocabecera cabecera, ScpBancodetalle bancoItem) {
        int delNumItem = bancoItem.getId().getNumItem();
        bancodetalleRep.delete(bancoItem);
        for (ScpBancodetalle it : bancodetalleRep
                .findById_CodBancocabeceraAndId_NumItemGreaterThan(cabecera.getCodBancocabecera(), delNumItem)) {
            try {
                ScpBancodetalle newBancoDetalle = (ScpBancodetalle) it.clone();
                ScpBancodetallePK id = (ScpBancodetallePK) it.getId().clone();
                id.setNumItem(it.getId().getNumItem() - 1);
                newBancoDetalle.setId(id);
                bancodetalleRep.delete(it);
                bancodetalleRep.save(newBancoDetalle);
            } catch (CloneNotSupportedException cle) {
                cle.printStackTrace();
            }
        }
        BigDecimal saldoHabersol = new BigDecimal(0);
        BigDecimal saldoHaberdolar = new BigDecimal(0);
        BigDecimal saldoHabermo = new BigDecimal(0);
        BigDecimal saldoDebesol = new BigDecimal(0);
        BigDecimal saldoDebedolar = new BigDecimal(0);
        BigDecimal saldoDebemo = new BigDecimal(0);
        for (ScpBancodetalle it : bancodetalleRep
                .findById_CodBancocabecera(cabecera.getCodBancocabecera())) {
            saldoDebedolar = saldoDebedolar.add(it.getNumDebedolar());
            saldoDebemo = saldoDebemo.add(it.getNumDebemo());
            saldoDebesol = saldoDebesol.add(it.getNumDebesol());
            saldoHaberdolar = saldoHaberdolar.add(it.getNumHaberdolar());
            saldoHabermo = saldoHabermo.add(it.getNumHabermo());
            saldoHabersol = saldoHabersol.add(it.getNumHabersol());
        }
        cabecera.setNumDebesol(saldoDebesol);
        cabecera.setNumHabersol(saldoHabersol);
        cabecera.setNumDebedolar(saldoDebedolar);
        cabecera.setNumHaberdolar(saldoHaberdolar);
        cabecera.setNumDebemo(saldoDebemo);
        cabecera.setNumDebemo(saldoHabermo);
        bancocabeceraRep.save(cabecera);
    }

    @Transactional(readOnly = false)
    public void updateCobradoInCabecera(ScpBancocabecera bancocabecera) {
        if (bancocabecera.isEnviado()) {
            // UPDATE in contabilidad
            List<ScpComprobantedetalle> comprobantedetalles = scpComprobantedetalleRep.findById_TxtAnoprocesoAndId_CodMesAndId_CodOrigenAndId_CodComprobante(
                    bancocabecera.getTxtAnoproceso(), bancocabecera.getCodMes(), bancocabecera.getCodOrigenenlace(),
                    bancocabecera.getCodComprobanteenlace()); //, bancocabecera.getCodCtacontable());
            log.info("Will update comprodets: " + comprobantedetalles.size());
            for (ScpComprobantedetalle det : comprobantedetalles) {
                if (bancocabecera.getFlgCobrado() != null && bancocabecera.getFlgCobrado()) {
                    det.setFlgChequecobrado('1');
                    det.setCodMescobr(bancocabecera.getCodMescobrado());
                } else {
                    det.setFlgChequecobrado('0');
                    det.setCodMescobr("");
                }
                scpComprobantedetalleRep.save(det);
            }
        }
        bancocabeceraRep.save(bancocabecera);
    }

    @Transactional(readOnly = false)
    public void anularCheque(ScpBancocabecera vcb) throws FieldGroup.CommitException {
        vcb.setCodMescobrado(new MesCobradoToBooleanConverter(vcb)
                .convertToModel(vcb.getFlgCobrado(), String.class, ConfigurationUtil.LOCALE));
        updateCobradoInCabecera(vcb);
        vcb.setTxtGlosa("ANULADO");
        vcb.setCodDestino("00000000");
        vcb.setFlg_Anula('1');
        for (ScpBancodetalle det : bancodetalleRep.findById_CodBancocabecera(vcb.getCodBancocabecera())) {
            det.setFlg_Anula('1');
            saveBancoOperacion(vcb, det, vcb.getCodTipomoneda());
        }
    }

    @Transactional
    public List<ScpBancocabecera> findAll() {
        return bancocabeceraRep.findAll();
    }

    @Transactional
    public List<ScpBancocabecera> findByFecFechaBetween(Date from, Date to) {
        return bancocabeceraRep.findByFecFechaBetween(from, to);
    }

    public ScpBancocabeceraRep getBancocabeceraRep() {
        return bancocabeceraRep;
    }

    public ScpBancodetalleRep getBancodetalleRep() {
        return bancodetalleRep;
    }

    public VsjConfiguractacajabancoRep getConfiguractacajabancoRepo() {
        return configuractacajabancoRepo;
    }

    public ScpPlancontableRep getPlanRepo() {
        return planRepo;
    }

    public ScpPlanespecialRep getPlanEspRepo() {
        return planEspRepo;
    }

    public ScpProyectoRep getProyectoRepo() {
        return proyectoRepo;
    }

    public ScpDestinoRep getDestinoRepo() {
        return destinoRepo;
    }

    public ScpComprobantepagoRep getComprobantepagoRepo() {
        return comprobantepagoRepo;
    }

    public ScpFinancieraRep getFinancieraRepo() {
        return financieraRepo;
    }

    public ScpPlanproyectoRep getPlanproyectoRepo() {
        return planproyectoRepo;
    }

    public Scp_ProyectoPorFinancieraRep getProyectoPorFinancieraRepo() {
        return proyectoPorFinancieraRepo;
    }

    public Scp_ContraparteRep getContraparteRepo() {
        return contraparteRepo;
    }

    public VsjConfiguracioncajaRep getConfiguracioncajaRepo() {
        return configuracioncajaRepo;
    }

    public ScpCargocuartaRep getCargocuartaRepo() {
        return cargocuartaRepo;
    }

    public ScpTipodocumentoRep getTipodocumentoRepo() {
        return tipodocumentoRepo;
    }

    public ScpTipocambioRep getScpTipocambioRep() {
        return scpTipocambioRep;
    }

    public EntityManager getEm() {
        return em;
    }
}

package org.sanjose.views.banco;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import org.sanjose.model.ScpComprobantedetalle;
import org.sanjose.model.VsjBancocabecera;
import org.sanjose.model.VsjBancodetalle;
import org.sanjose.model.VsjBancodetallePK;
import org.sanjose.repo.*;
import org.sanjose.util.GenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;

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
    private final VsjBancocabeceraRep bancocabeceraRep;
    private final VsjBancodetalleRep bancodetalleRep;
    private final Logger log = LoggerFactory.getLogger(BancoService.class);
    private ScpTipocambioRep scpTipocambioRep;
    private ScpComprobantedetalleRep scpComprobantedetalleRep;

    @Autowired
    public BancoService(VsjBancocabeceraRep bancocabeceraRep, VsjBancodetalleRep bancodetalleRep,
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
    public VsjBancodetalle saveBancoOperacion(VsjBancocabecera cabecera, VsjBancodetalle bancoItem, Character moneda) throws FieldGroup.CommitException {
        cabecera.setCodTipomoneda(moneda);
        cabecera = cabecera.prepareToSave();
        cabecera = bancocabeceraRep.save(cabecera);
        if (GenUtil.strNullOrEmpty(cabecera.getTxtCorrelativo())) {
            cabecera.setTxtCorrelativo(GenUtil.getTxtCorrelativo(cabecera.getCodBancocabecera()));
            cabecera = bancocabeceraRep.save(cabecera);
        }

        bancoItem.setCodTipomoneda(moneda);
        bancoItem = bancoItem.prepareToSave();
        bancoItem.setTxtCheque(cabecera.getTxtCheque());
        bancoItem.setVsjBancocabecera(cabecera);
        if (bancoItem.getId() == null) {
            VsjBancodetallePK id = new VsjBancodetallePK();
            id.setCodBancocabecera(cabecera.getCodBancocabecera());
            id.setNumItem(bancodetalleRep.findById_CodBancocabecera(cabecera.getCodBancocabecera()).size() + 1);
            bancoItem.setId(id);
        }

        bancoItem.setVsjBancocabecera(cabecera);
        bancoItem = bancodetalleRep.save(bancoItem);
        if (GenUtil.strNullOrEmpty(bancoItem.getTxtCorrelativo())) {
            bancoItem.setTxtCorrelativo(GenUtil.getTxtCorrelativo(bancoItem.getId().getNumItem()));
            bancoItem = bancodetalleRep.save(bancoItem);
        }
        BigDecimal saldoHabersol = new BigDecimal(0);
        BigDecimal saldoHaberdolar = new BigDecimal(0);
        BigDecimal saldoHabermo = new BigDecimal(0);
        BigDecimal saldoDebesol = new BigDecimal(0);
        BigDecimal saldoDebedolar = new BigDecimal(0);
        BigDecimal saldoDebemo = new BigDecimal(0);
        for (VsjBancodetalle it : bancodetalleRep
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
        cabecera = bancocabeceraRep.save(cabecera);
        bancoItem.setVsjBancocabecera(cabecera);
        return bancoItem;
    }


    @Transactional(readOnly = false)
    public void deleteBancoOperacion(VsjBancocabecera cabecera, VsjBancodetalle bancoItem) {
        int delNumItem = bancoItem.getId().getNumItem();
        bancodetalleRep.delete(bancoItem);
        for (VsjBancodetalle it : bancodetalleRep
                .findById_CodBancocabeceraAndId_NumItemGreaterThan(cabecera.getCodBancocabecera(), delNumItem)) {
            try {
                VsjBancodetalle newBancoDetalle = (VsjBancodetalle) it.clone();
                VsjBancodetallePK id = (VsjBancodetallePK) it.getId().clone();
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
        for (VsjBancodetalle it : bancodetalleRep
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
    public void updateCobradoInCabecera(VsjBancocabecera bancocabecera) {
        if (bancocabecera.isEnviado()) {
            // UPDATE in contabilidad
            List<ScpComprobantedetalle> comprobantedetalles = scpComprobantedetalleRep.findById_TxtAnoprocesoAndId_CodMesAndId_CodOrigenAndId_CodComprobanteAndCodCtacontable(
                    bancocabecera.getTxtAnoproceso(), bancocabecera.getCodMes(), bancocabecera.getCodOrigenenlace(),
                    bancocabecera.getCodComprobanteenlace(), bancocabecera.getCodCtacontable());
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
    public void anularCheque(VsjBancocabecera vcb) throws FieldGroup.CommitException {
        vcb.setTxtGlosa("ANULADO");
        vcb.setCodDestino("00000000");
        vcb.setFlg_Anula('1');
        for (VsjBancodetalle det : bancodetalleRep.findById_CodBancocabecera(vcb.getCodBancocabecera())) {
            det.setFlg_Anula('1');
            saveBancoOperacion(vcb, det, vcb.getCodTipomoneda());
        }
    }



    @Transactional
    public List<VsjBancocabecera> findAll() {
        return bancocabeceraRep.findAll();
    }

    public VsjBancocabeceraRep getBancocabeceraRep() {
        return bancocabeceraRep;
    }

    public VsjBancodetalleRep getBancodetalleRep() {
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

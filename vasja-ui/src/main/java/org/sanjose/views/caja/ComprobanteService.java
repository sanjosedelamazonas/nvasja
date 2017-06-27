package org.sanjose.views.caja;

import org.sanjose.model.VsjCajabanco;
import org.sanjose.repo.*;
import org.sanjose.util.GenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pol on 06.10.16.
 */
@Service
@Transactional
public class ComprobanteService {

    private final VsjCajabancoRep cajabancoRep;
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
    private final ScpCategoriaproyectoRep scpCategoriaproyectoRep;
    private final ScpTipocambioRep tipocambioRep;

    @Autowired
    public ComprobanteService(VsjCajabancoRep cajabancoRep, VsjConfiguractacajabancoRep configuractacajabancoRepo,
                              ScpCategoriaproyectoRep scpCategoriaproyectoRep, ScpPlancontableRep planRepo,
                              ScpPlanespecialRep planEspRepo, ScpProyectoRep proyectoRepo, ScpDestinoRep destinoRepo,
                              ScpComprobantepagoRep comprobantepagoRepo, ScpFinancieraRep financieraRepo,
                              ScpPlanproyectoRep planproyectoRepo, Scp_ProyectoPorFinancieraRep proyectoPorFinancieraRepo,
                              Scp_ContraparteRep contraparteRepo, VsjConfiguracioncajaRep configuracioncajaRepo,
                              ScpCargocuartaRep cargocuartaRepo, ScpTipodocumentoRep tipodocumentoRepo, ScpTipocambioRep
                                      tipocambioRep, EntityManager em) {
        this.cajabancoRep = cajabancoRep;
        this.configuractacajabancoRepo = configuractacajabancoRepo;
        this.scpCategoriaproyectoRep = scpCategoriaproyectoRep;
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
        this.tipocambioRep = tipocambioRep;
        this.em = em;
    }

    @Transactional(readOnly = false)
    public VsjCajabanco save(VsjCajabanco cajabanco) {
        // You can persist your data here
        VsjCajabanco savedCajabanco = cajabancoRep.save(cajabanco);

        if (GenUtil.strNullOrEmpty(savedCajabanco.getTxtCorrelativo())) {
            savedCajabanco.setTxtCorrelativo(GenUtil.getTxtCorrelativo(savedCajabanco.getCodCajabanco()));
            savedCajabanco = cajabancoRep.save(savedCajabanco);
        }
        if (savedCajabanco.getTxtGlosaitem().equals("abc")) {
            throw new RuntimeException();
        }
        return savedCajabanco;
    }

    @Transactional(readOnly = false)
    public List<VsjCajabanco> saveVsjCajabancos(List<VsjCajabanco> cajabancos) {
        assert TransactionSynchronizationManager.isActualTransactionActive();
        List<VsjCajabanco> savedOperaciones = new ArrayList<>();

        String transCorrelativo = null;
        // Find at least one operation with transCorrelativo set
        for (VsjCajabanco oper : cajabancos) {
            if (!GenUtil.strNullOrEmpty(oper.getCodTranscorrelativo())) {
                transCorrelativo = oper.getCodTranscorrelativo();
                break;
            }
        }
        if (transCorrelativo == null) transCorrelativo = GenUtil.getUuid();
        for (VsjCajabanco oper : cajabancos) {
            if (GenUtil.strNullOrEmpty(oper.getCodTranscorrelativo()))
                oper.setCodTranscorrelativo(transCorrelativo);
        }
        for (VsjCajabanco oper : cajabancoRep.save(cajabancos)) {
            // Tested saving each element using entityManager directly but then an Exception is raised:
            // javax.persistence.TransactionRequiredException: No EntityManager with actual transaction available for
            // current thread - cannot reliably process 'merge' call
            //
//            VsjCajabanco savedCajabanco = em.merge(oper);
            if (GenUtil.strNullOrEmpty(oper.getTxtCorrelativo())) {
                oper.setTxtCorrelativo(GenUtil.getTxtCorrelativo(oper.getCodCajabanco()));
                // TEST transactionality - causes org.springframework.dao.DataIntegrityViolationException
                // because codMes is NOT NULL in the database
                if (oper.getTxtGlosaitem().equals("abc")) {
                    throw new RuntimeException("Test transactions");
                }
                //oper.setCodMes(null);
                oper = cajabancoRep.save(oper);
                //log.info("Saved cajabanco from transferencia: " + oper);
//                oper = em.merge(oper);
            }
            savedOperaciones.add(oper);
        }
        return savedOperaciones;
    }

    public VsjCajabancoRep getCajabancoRep() {
        return cajabancoRep;
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

    public ScpTipocambioRep getTipocambioRep() {
        return tipocambioRep;
    }

    public ScpCategoriaproyectoRep getScpCategoriaproyectoRep() {
        return scpCategoriaproyectoRep;
    }

    public EntityManager getEm() {
        return em;
    }
}

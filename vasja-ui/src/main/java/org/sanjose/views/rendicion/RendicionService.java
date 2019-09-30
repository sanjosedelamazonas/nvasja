package org.sanjose.views.rendicion;

import org.sanjose.model.ScpCajabanco;
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
public class RendicionService {

    private final ScpRendicioncabeceraRep rendicioncabeceraRep;
    private final ScpRendiciondetalleRep rendiciondetalleRep;
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
    public RendicionService(ScpRendicioncabeceraRep rendicioncabeceraRep, ScpRendiciondetalleRep rendiciondetalleRep, VsjConfiguractacajabancoRep configuractacajabancoRepo,
                            ScpCategoriaproyectoRep scpCategoriaproyectoRep, ScpPlancontableRep planRepo,
                            ScpPlanespecialRep planEspRepo, ScpProyectoRep proyectoRepo, ScpDestinoRep destinoRepo,
                            ScpComprobantepagoRep comprobantepagoRepo, ScpFinancieraRep financieraRepo,
                            ScpPlanproyectoRep planproyectoRepo, Scp_ProyectoPorFinancieraRep proyectoPorFinancieraRepo,
                            Scp_ContraparteRep contraparteRepo, VsjConfiguracioncajaRep configuracioncajaRepo,
                            ScpCargocuartaRep cargocuartaRepo, ScpTipodocumentoRep tipodocumentoRepo, ScpTipocambioRep
                                      tipocambioRep, EntityManager em) {
        this.rendicioncabeceraRep = rendicioncabeceraRep;
        this.rendiciondetalleRep = rendiciondetalleRep;
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

    public ScpRendicioncabeceraRep getRendicioncabeceraRep() {
        return rendicioncabeceraRep;
    }

    public ScpRendiciondetalleRep getRendiciondetalleRep() {
        return rendiciondetalleRep;
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

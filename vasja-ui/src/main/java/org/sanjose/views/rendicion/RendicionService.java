package org.sanjose.views.rendicion;

import com.vaadin.data.fieldgroup.FieldGroup;
import org.sanjose.model.*;
import org.sanjose.repo.*;
import org.sanjose.util.GenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
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

    
    @Transactional(readOnly = false)
    public ScpRendiciondetalle saveRendicionOperacion(ScpRendicioncabecera cabecera, ScpRendiciondetalle rendicionItem) throws FieldGroup.CommitException {
        //cabecera.setCodTipomoneda(moneda);
        cabecera.prepareToSave();
        System.out.println("saving: " + cabecera);
        cabecera = rendicioncabeceraRep.save(cabecera);
        if (GenUtil.strNullOrEmpty(cabecera.getCodComprobante())) {
            cabecera.setCodComprobante(GenUtil.getTxtCorrelativoLen(cabecera.getCodRendicioncabecera(), 6));
            cabecera = rendicioncabeceraRep.save(cabecera);
        }
        //cabecera = rendicioncabeceraRep.save(cabecera);
        if (rendicionItem!=null) {
            if (!GenUtil.objNullOrEmpty(rendicionItem.getCodTipomov()) && rendicionItem.getCodTipomov()>0) {
                VsjConfiguractacajabanco codTipoMov = configuractacajabancoRepo.findById(rendicionItem.getCodTipomov());
                if (codTipoMov == null) {
                    throw new FieldGroup.CommitException("No se puede encontrar el Codigo Tipo Gasto (" + rendicionItem.getCodTipomov() + ") - por favor verifica la configuracion de Caja y Rendicions");
                }
                //rendicionItem.setCodTipogasto(codTipoMov.getCodTipocuenta());
            }
            //rendicionItem.setCodTipomoneda(moneda);
            rendicionItem.prepareToSave();
            rendicionItem.setFecComprobante(cabecera.getFecComprobante());
            rendicionItem.setScpRendicioncabecera(cabecera);
            if (rendicionItem.getId() == null) {
                ScpRendiciondetallePK id = new ScpRendiciondetallePK();
                id.setCodRendicioncabecera(cabecera.getCodRendicioncabecera());
                id.setNumNroitem(rendiciondetalleRep.findById_CodRendicioncabecera(cabecera.getCodRendicioncabecera()).size() + 1);
                rendicionItem.setId(id);
            }

            rendicionItem.setScpRendicioncabecera(cabecera);
            if (GenUtil.strNullOrEmpty(rendicionItem.getCodComprobante())) {
                rendicionItem.setCodComprobante(cabecera.getCodComprobante());
            }
            rendicionItem = rendiciondetalleRep.save(rendicionItem);
        } else {
            rendicionItem = new ScpRendiciondetalle();
            rendicionItem.setCodTipomoneda(cabecera.getCodTipomoneda());
            ScpRendiciondetallePK id = new ScpRendiciondetallePK();
            id.setCodRendicioncabecera(cabecera.getCodRendicioncabecera());
            rendicionItem.setId(id);
            rendicionItem.setCodComprobante(cabecera.getCodComprobante());
        }
        rendicionItem.setScpRendicioncabecera(cabecera);
        return rendicionItem;
    }
}

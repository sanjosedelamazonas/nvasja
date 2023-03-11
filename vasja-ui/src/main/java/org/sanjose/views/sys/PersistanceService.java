package org.sanjose.views.sys;

import com.vaadin.data.fieldgroup.FieldGroup;
import org.sanjose.converter.MesCobradoToBooleanConverter;
import org.sanjose.model.*;
import org.sanjose.repo.*;
import org.sanjose.util.ConfigurationUtil;
import org.sanjose.util.GenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.sanjose.util.GenUtil.PEN;
import static org.sanjose.util.GenUtil.USD;

/**
 * Created by pol on 06.10.16.
 */
@Service
@Transactional
public class PersistanceService {

    private final ScpCajabancoRep cajabancoRep;
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
    private final MsgUsuarioRep msgUsuarioRep;
    private final ScpBancocabeceraRep bancocabeceraRep;
    private final ScpBancodetalleRep bancodetalleRep;
    private final ScpComprobantedetalleRep scpComprobantedetalleRep;
    private final ScpComprobantecabeceraRep scpComprobantecabeceraRep;
    private final ScpChequependienteRep scpChequependienteRep;
    private final VsjRendicionanticipioRep vsjRendicionanticipioRep;


    @Autowired
    public PersistanceService(ScpRendicioncabeceraRep rendicioncabeceraRep, ScpRendiciondetalleRep rendiciondetalleRep, VsjConfiguractacajabancoRep configuractacajabancoRepo,
                              ScpCategoriaproyectoRep scpCategoriaproyectoRep, ScpPlancontableRep planRepo,
                              ScpPlanespecialRep planEspRepo, ScpProyectoRep proyectoRepo, ScpDestinoRep destinoRepo,
                              ScpComprobantepagoRep comprobantepagoRepo, ScpFinancieraRep financieraRepo,
                              ScpPlanproyectoRep planproyectoRepo, Scp_ProyectoPorFinancieraRep proyectoPorFinancieraRepo,
                              Scp_ContraparteRep contraparteRepo, VsjConfiguracioncajaRep configuracioncajaRepo,
                              ScpCargocuartaRep cargocuartaRepo, ScpTipodocumentoRep tipodocumentoRepo, ScpTipocambioRep
                                      tipocambioRep, MsgUsuarioRep msgUsuarioRep, ScpCajabancoRep cajabancoRep,
                              ScpBancocabeceraRep bancocabeceraRep, ScpBancodetalleRep bancodetalleRep, ScpComprobantedetalleRep scpComprobantedetalleRep,
                              ScpComprobantecabeceraRep scpComprobantecabeceraRep,
                              ScpChequependienteRep scpChequependienteRep, VsjRendicionanticipioRep vsjRendicionanticipioRep, EntityManager em) {
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
        this.msgUsuarioRep = msgUsuarioRep;
        this.cajabancoRep = cajabancoRep;
        this.bancocabeceraRep = bancocabeceraRep;
        this.bancodetalleRep = bancodetalleRep;
        this.scpComprobantedetalleRep = scpComprobantedetalleRep;
        this.scpComprobantecabeceraRep = scpComprobantecabeceraRep;
        this.scpChequependienteRep = scpChequependienteRep;
        this.vsjRendicionanticipioRep = vsjRendicionanticipioRep;
        this.em = em;
    }

    // Caja

    @Transactional(readOnly = false)
    public ScpCajabanco save(ScpCajabanco cajabanco) {
        // You can persist your data here
        ScpCajabanco savedCajabanco = cajabancoRep.save(cajabanco);

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
    public List<ScpCajabanco> saveVsjCajabancos(List<ScpCajabanco> cajabancos) {
        assert TransactionSynchronizationManager.isActualTransactionActive();
        List<ScpCajabanco> savedOperaciones = new ArrayList<>();

        String transCorrelativo = null;
        // Find at least one operation with transCorrelativo set
        for (ScpCajabanco oper : cajabancos) {
            if (!GenUtil.strNullOrEmpty(oper.getCodTranscorrelativo())) {
                transCorrelativo = oper.getCodTranscorrelativo();
                break;
            }
        }
        if (transCorrelativo == null) transCorrelativo = GenUtil.getUuid();
        for (ScpCajabanco oper : cajabancos) {
            if (GenUtil.strNullOrEmpty(oper.getCodTranscorrelativo()))
                oper.setCodTranscorrelativo(transCorrelativo);
        }
        for (ScpCajabanco oper : cajabancoRep.save(cajabancos)) {
            // Tested saving each element using entityManager directly but then an Exception is raised:
            // javax.persistence.TransactionRequiredException: No EntityManager with actual transaction available for
            // current thread - cannot reliably process 'merge' call
            //
//            ScpCajabanco savedCajabanco = em.merge(oper);
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

    // Banco


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
        if (bancoItem.getCodTipomov()!=null && bancoItem.getCodTipomov() > 0 && codTipoMov == null) {
            throw new FieldGroup.CommitException("No se puede encontrar el Codigo Tipo Gasto - por favor verifica la configuracion de Caja y Bancos");
        }
        if (bancoItem.getCodTipomov() > 0) {
            bancoItem.setCodTipogasto(codTipoMov.getCodTipocuenta());
        }
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
    public void updateCobradoInCabecera(ScpBancocabecera cab) {
        if (cab.isEnviado()) {
            // UPDATE in contabilidad
            // First check in ChequePendiente if is not from previous year
            boolean foundInPendiente = false;
            List<ScpChequependiente> pendientes = scpChequependienteRep.
                    findById_CodCtacontableAndId_TxtChequeAndId_CodOrigenAndId_CodComprobanteAndFecComprobante(
                            cab.getCodCtacontable(), cab.getTxtCheque(), cab.getCodOrigenenlace(),
                            cab.getCodComprobanteenlace(), cab.getFecFecha());
            for (ScpChequependiente pendiente : pendientes) {
                foundInPendiente = true;
                if (cab.getFlgCobrado() != null && cab.getFlgCobrado()) {
                    pendiente.setFlgChequecobrado('1');
                    pendiente.setCodMescobrado(cab.getCodMescobrado());
                } else {
                    pendiente.setFlgChequecobrado('0');
                    pendiente.setCodMescobrado("");
                }
                scpChequependienteRep.save(pendiente);
            }
            // if found in pendiente then update only there
            if (foundInPendiente)
                return;
            // if however not found then change in CombrobanteDetalle
            List<ScpComprobantedetalle> comprobantedetalles = scpComprobantedetalleRep.
                    findById_TxtAnoprocesoAndId_CodMesAndId_CodOrigenAndId_CodComprobanteAndCodCtacontable(
                            cab.getTxtAnoproceso(), cab.getCodMes(), cab.getCodOrigenenlace(),
                            cab.getCodComprobanteenlace(), cab.getCodCtacontable());
            //log.info("Will update comprodets: " + comprobantedetalles.size());
            for (ScpComprobantedetalle det : comprobantedetalles) {
                if (cab.getFlgCobrado() != null && cab.getFlgCobrado()) {
                    det.setFlgChequecobrado('1');
                    det.setCodMescobr(cab.getCodMescobrado());
                } else {
                    det.setFlgChequecobrado('0');
                    det.setCodMescobr("");
                }
                scpComprobantedetalleRep.save(det);
            }
        }
        bancocabeceraRep.save(cab);
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



    // Rendicion
    
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
                //else if ((rendicionItem.getCodTipomov()>0)
                //rendicionItem.set(codTipoMov.getCodTipocuenta());
            }
            //rendicionItem.setCodTipomoneda(moneda);
            rendicionItem.prepareToSave();
            rendicionItem.setFecComprobante(cabecera.getFecComprobante());
            rendicionItem.setScpRendicioncabecera(cabecera);
            if (rendicionItem.getId() == null) {
                ScpRendiciondetallePK id = new ScpRendiciondetallePK();
                id = id.prepareToSave(rendicionItem);
                id.setCodRendicioncabecera(cabecera.getCodRendicioncabecera());
                id.setNumNroitem(rendiciondetalleRep.findById_CodRendicioncabecera(cabecera.getCodRendicioncabecera()).size() + 1);
                id.setCodFilial(cabecera.getCodFilial());
                id.setCodOrigen(cabecera.getCodOrigen());
                id.setCodComprobante(cabecera.getCodComprobante());
                rendicionItem.setId(id);
            }

            rendicionItem.setScpRendicioncabecera(cabecera);
            if (GenUtil.strNullOrEmpty(rendicionItem.getId().getCodComprobante())) {
                rendicionItem.getId().setCodComprobante(cabecera.getCodComprobante());
            }
            rendicionItem = rendiciondetalleRep.save(rendicionItem);
        } else {
            rendicionItem = new ScpRendiciondetalle();
            rendicionItem.setCodTipomoneda(cabecera.getCodTipomoneda());
            ScpRendiciondetallePK id = new ScpRendiciondetallePK();
            id = id.prepareToSave(rendicionItem);
            id.setCodRendicioncabecera(cabecera.getCodRendicioncabecera());
            rendicionItem.setId(id);
            rendicionItem.getId().setCodComprobante(cabecera.getCodComprobante());
        }
        rendicionItem.setScpRendicioncabecera(cabecera);
        return rendicionItem;
    }

    @Transactional(readOnly = false)
    public ScpRendicioncabecera saveRendicionCabecera(ScpRendicioncabecera cabecera) throws FieldGroup.CommitException {
        //cabecera.setCodTipomoneda(moneda);
        cabecera.prepareToSave();
        System.out.println("saving: " + cabecera);
        cabecera = rendicioncabeceraRep.save(cabecera);
        if (GenUtil.strNullOrEmpty(cabecera.getCodComprobante())) {
            cabecera.setCodComprobante(GenUtil.getTxtCorrelativoLen(cabecera.getCodRendicioncabecera(), 6));
            cabecera = rendicioncabeceraRep.save(cabecera);
        }
        //cabecera = rendicioncabeceraRep.save(cabecera);
        return cabecera;
    }


    @Transactional
    public void deleteRendicion(ScpRendicioncabecera cabecera) {
        cabecera = rendicioncabeceraRep.findByCodRendicioncabecera(cabecera.getCodRendicioncabecera());
        for (ScpRendiciondetalle det : cabecera.getScpRendiciondetalles()) {
            rendiciondetalleRep.delete(det);
        }
        rendicioncabeceraRep.delete(cabecera);
    }

    @Transactional(readOnly = false)
    public void deleteRendicionOperacion(ScpRendicioncabecera cabecera, ScpRendiciondetalle rendicionItem) {
        long delNumItem = rendicionItem.getId().getNumNroitem();
        rendiciondetalleRep.delete(rendicionItem);
        for (ScpRendiciondetalle it : rendiciondetalleRep
                .findById_CodRendicioncabeceraAndId_NumNroitemGreaterThan(cabecera.getCodRendicioncabecera(), delNumItem)) {
            try {
                ScpRendiciondetalle newRendicionDetalle = (ScpRendiciondetalle) it.clone();
                ScpRendiciondetallePK id = (ScpRendiciondetallePK) it.getId().clone();
                id.setNumNroitem(it.getId().getNumNroitem() - 1);
                newRendicionDetalle.setId(id);
                rendiciondetalleRep.delete(it);
                rendiciondetalleRep.save(newRendicionDetalle);
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
        for (ScpRendiciondetalle it : rendiciondetalleRep
                .findById_CodRendicioncabecera(cabecera.getCodRendicioncabecera())) {
            saldoDebedolar = saldoDebedolar.add(it.getNumDebedolar());
            saldoDebemo = saldoDebemo.add(it.getNumDebemo());
            saldoDebesol = saldoDebesol.add(it.getNumDebesol());
            saldoHaberdolar = saldoHaberdolar.add(it.getNumHaberdolar());
            saldoHabermo = saldoHabermo.add(it.getNumHabermo());
            saldoHabersol = saldoHabersol.add(it.getNumHabersol());
        }
        BigDecimal gastoTotal;
        switch (cabecera.getCodTipomoneda()) {
            case '0':
                gastoTotal = saldoDebesol.subtract(saldoHabersol);
                break;
            case '1':
                gastoTotal = saldoDebedolar.subtract(saldoHaberdolar);
                break;
            case '2':
                gastoTotal = saldoDebemo.subtract(saldoHabermo);
                break;
            default:
                throw new RuntimeException(cabecera.getCodTipomoneda() + " - Unknown currency - this shouldn't happen");
        }
        cabecera.setNumGastototal(gastoTotal);
        cabecera.setNumSaldopendiente(cabecera.getNumTotalanticipo().subtract(gastoTotal));
        rendicioncabeceraRep.save(cabecera);
    }

    public boolean checkIfAlreadyEnviado(ScpCajabanco it) {
        List<ScpComprobantecabecera> cabeceras = scpComprobantecabeceraRep.findById_TxtAnoprocesoAndId_CodFilialAndId_CodMesAndId_CodOrigenAndId_CodComprobante(it.getTxtAnoproceso(), "01", it.getCodMes(), "01", GenUtil.getCodComprobante(it.getCodCajabanco()));
        return !cabeceras.isEmpty();
    }

    public boolean checkIfAlreadyEnviado(ScpBancocabecera it) {
        List<ScpComprobantecabecera> cabeceras = scpComprobantecabeceraRep.findById_TxtAnoprocesoAndId_CodFilialAndId_CodMesAndId_CodOrigenAndId_CodComprobante(it.getTxtAnoproceso(), "01", it.getCodMes(), "02", GenUtil.getCodComprobante(it.getCodBancocabecera()));
        return !cabeceras.isEmpty();
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

    public MsgUsuarioRep getMsgUsuarioRep() {
        return msgUsuarioRep;
    }

    public ScpCajabancoRep getCajabancoRep() {
        return cajabancoRep;
    }

    public ScpBancocabeceraRep getBancocabeceraRep() {
        return bancocabeceraRep;
    }

    public ScpBancodetalleRep getBancodetalleRep() {
        return bancodetalleRep;
    }

    public ScpComprobantedetalleRep getScpComprobantedetalleRep() {
        return scpComprobantedetalleRep;
    }

    public ScpChequependienteRep getScpChequependienteRep() {
        return scpChequependienteRep;
    }

    public ScpComprobantecabeceraRep getScpComprobantecabeceraRep() {
        return scpComprobantecabeceraRep;
    }

    public VsjRendicionanticipioRep getVsjRendicionanticipioRep() {
        return vsjRendicionanticipioRep;
    }

    public EntityManager getEm() {
        return em;
    }
}

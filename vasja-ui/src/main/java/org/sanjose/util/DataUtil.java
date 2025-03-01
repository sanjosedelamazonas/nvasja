package org.sanjose.util;

import com.vaadin.ui.ComboBox;
import org.sanjose.MainUI;
import org.sanjose.authentication.Role;
import org.sanjose.bean.Caja;
import org.sanjose.model.*;
import org.sanjose.repo.ScpFinancieraRep;
import org.sanjose.repo.ScpPlancontableRep;
import org.sanjose.repo.Scp_ProyectoPorFinancieraRep;
import org.sanjose.views.sys.PersistanceService;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static org.sanjose.util.GenUtil.*;

/**
 * VASJA class
 * User: prubach
 * Date: 19.09.16
 */
public class DataUtil {

    public static void setupAndBindproyectoPorFinanciera(String codProyecto, ComboBox selFuente,
                                                         Scp_ProyectoPorFinancieraRep projectoPorFinRepo,
                                                         ScpFinancieraRep financieraRepo) {
        List<Scp_ProyectoPorFinanciera>
                proyectoPorFinancieraList = projectoPorFinRepo.findById_CodProyecto(codProyecto);

        // Filter financiera if exists in Proyecto Por Financiera
        List<ScpFinanciera> financieraList = financieraRepo.findAll();
        List<ScpFinanciera> financieraEfectList = new ArrayList<>();
        if (proyectoPorFinancieraList != null && !proyectoPorFinancieraList.isEmpty()) {
            List<String> codFinancieraList = proyectoPorFinancieraList.stream().map(proyectoPorFinanciera -> proyectoPorFinanciera.getId().getCodFinanciera()).collect(Collectors.toList());

            for (ScpFinanciera financiera : financieraList) {
                if (financiera.getCodFinanciera() != null &&
                        codFinancieraList.contains(financiera.getCodFinanciera())) {
                    financieraEfectList.add(financiera);
                }
            }
        } else {
            financieraEfectList = financieraList;
        }
        //DataFilterUtil.bindComboBox(selFuente, "codFinanciera", financieraEfectList,
        //        "Fuente", "txtDescfinanciera");
        DataFilterUtil.refreshComboBox(selFuente, financieraEfectList,
                "codFinanciera", "txtDescfinanciera", null);

        if (financieraEfectList.size() == 1)
            selFuente.select(financieraEfectList.get(0).getCodFinanciera());
    }

    public static List<ScpPlancontable> getCajas(Date ano, ScpPlancontableRep planRepo, Character moneda) {
        List<ScpPlancontable> allCajas = planRepo.
                findByFlgMovimientoAndId_TxtAnoprocesoAndIndTipomonedaAndId_CodCtacontableStartingWith(
                        'N', GenUtil.getYear(ano), getLitMoneda(moneda), "101");
        List<ScpPlancontable> cajas = new ArrayList<>();
        for (ScpPlancontable caja : allCajas) {
            moneda = GenUtil.getNumMoneda(caja.getIndTipomoneda());
            BigDecimal saldo = MainUI.get().getProcUtil().getSaldoCaja(
                    GenUtil.getEndOfDay(GenUtil.dateAddDays(ano,-1)),
                    caja.getId().getCodCtacontable()
                    , moneda);
            // If is closed and has a saldo of "0.00" we can omit it
            if (!caja.isNotClosedCuenta() && saldo.compareTo(new BigDecimal(0)) == 0)
                continue;
            // Filtro para Gilmer
            if (Role.isOnlyCaja() && !caja.getId().getCodCtacontable().startsWith(ConfigurationUtil.get("CAJA_INTERNA_CTA_PREFIX")))
                continue;
            //
            cajas.add(caja);
        }
        return cajas;
    }

    public static List<ScpPlancontable> getCajas(Date ano, ScpPlancontableRep planRepo) {
        return planRepo.
                findByFlgEstadocuentaAndFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableStartingWith(
                        '0', 'N', GenUtil.getYear(ano), "101");
    }

    public static List<ScpPlancontable> getTodasCajas(Date ano, ScpPlancontableRep planRepo) {
        List<ScpPlancontable> allCajas = planRepo.
                findByFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableStartingWith(
                        'N', GenUtil.getYear(ano), "101");
        List<ScpPlancontable> cajas = new ArrayList<>();
        for (ScpPlancontable caja : allCajas) {
            Character moneda = GenUtil.getNumMoneda(caja.getIndTipomoneda());
            BigDecimal saldo = MainUI.get().getProcUtil().getSaldoCaja(
                    GenUtil.getEndOfDay(GenUtil.dateAddDays(ano,-1)),
                    caja.getId().getCodCtacontable()
                    , moneda);
            // If is closed and has a saldo of "0.00" we can omit it
            if (!caja.isNotClosedCuenta() && saldo.compareTo(new BigDecimal(0)) == 0)
                continue;
            // Filtro para Gilmer
            if (Role.isOnlyCaja() && !caja.getId().getCodCtacontable().startsWith(ConfigurationUtil.get("CAJA_INTERNA_CTA_PREFIX")))
                continue;
            //
            cajas.add(caja);
        }
        return cajas;
    }

    public static List<Caja> getBancoCuentasList(ScpPlancontableRep planRepo, Date date, boolean isInicial) {
        List<Caja> cajas = new ArrayList<>();
        for (ScpPlancontable caja : getBancoCuentas(date, planRepo, null)) {
            Character moneda = GenUtil.getNumMoneda(caja.getIndTipomoneda());
            if (moneda.equals('9')) {
                // Problem - TipoMoneda not set
                System.out.println("ERROR: TipoMoneda not set for BancoCuenta: " + caja.getCodCta4() + " " + caja.getTxtDescctacontable());
            }
            BigDecimal saldo = MainUI.get().getProcUtil().getSaldoBanco(
                    (isInicial ? GenUtil.getEndOfDay(GenUtil.dateAddDays(date,-1)) : GenUtil.getEndOfDay(date)),
                    caja.getId().getCodCtacontable()
                    , moneda).getSegLibro();
            // If is closed and has a saldo of "0.00" we can omit it
            if (!caja.isNotClosedCuenta() && saldo.compareTo(new BigDecimal(0)) == 0)
                continue;
            // Filtro para Gilmer
            if (Role.isOnlyCaja() && !caja.getId().getCodCtacontable().startsWith(ConfigurationUtil.get("CAJA_INTERNA_CTA_PREFIX")))
                continue;
            //
            cajas.add(new Caja(caja.getId().getCodCtacontable(), caja.getTxtDescctacontable(),
                    (caja.getIndTipomoneda().equals(_PEN) ? saldo : new BigDecimal(0.00)),
                    (caja.getIndTipomoneda().equals(_USD) ? saldo : new BigDecimal(0.00)),
                    (caja.getIndTipomoneda().equals(_EUR) ? saldo : new BigDecimal(0.00))
            ));
        }
        return cajas;
    }
    
    public static ProcUtil.SaldosBanco getBancoCuentaSaldos(ScpPlancontable cuenta, Date date, boolean isInicial) {
        Character moneda = GenUtil.getNumMoneda(cuenta.getIndTipomoneda());
        if (moneda.equals('9')) {
            // Problem - TipoMoneda not set
            System.out.println("ERROR: TipoMoneda not set for BancoCuenta: " + cuenta.getCodCta4() + " " + cuenta.getTxtDescctacontable());
        }
        ProcUtil.SaldosBanco saldos = MainUI.get().getProcUtil().getSaldoBanco(
                (isInicial ? GenUtil.getEndOfDay(GenUtil.dateAddDays(date,-1)) : GenUtil.getEndOfDay(date)),
                cuenta.getId().getCodCtacontable()
                , moneda);
        // If is closed and has a saldo of "0.00" we can omit it
        if (!cuenta.isNotClosedCuenta() && saldos.getSegLibro().compareTo(new BigDecimal(0)) == 0)
            return null;
        return saldos;
    }

    //public static List<Caja> getCajasList(ScpPlancontableRep planRepo, Date date) {


    public static List<Caja> getCajasList(ScpPlancontableRep planRepo, Date date) {
        List<Caja> cajas = new ArrayList<>();
        for (ScpPlancontable caja : getTodasCajas(date, planRepo)) {
            Character moneda = GenUtil.getNumMoneda(caja.getIndTipomoneda());
            //TODO Why are we asking for previous day!!
            BigDecimal saldo = MainUI.get().getProcUtil().getSaldoCaja(
                    //GenUtil.getEndOfDay(GenUtil.dateAddDays(date,-1)),
                    date,
                    caja.getId().getCodCtacontable()
                    , moneda);
            // If is closed and has a saldo of "0.00" we can omit it
            if (!caja.isNotClosedCuenta() && saldo.compareTo(new BigDecimal(0))==0)
                continue;
            // Filtro para Gilmer
            if (Role.isOnlyCaja() && !caja.getId().getCodCtacontable().startsWith(ConfigurationUtil.get("CAJA_INTERNA_CTA_PREFIX")))
                continue;
            //

            cajas.add(new Caja(caja.getId().getCodCtacontable(), caja.getTxtDescctacontable(),
                    (caja.getIndTipomoneda().equals(_PEN) ? saldo : new BigDecimal(0.00)),
                    (caja.getIndTipomoneda().equals(_USD) ? saldo : new BigDecimal(0.00)),
                    (caja.getIndTipomoneda().equals(_EUR) ? saldo : new BigDecimal(0.00))
            ));
        }
        return cajas;
    }

    public static List<Caja> getCajasList(ScpPlancontableRep planRepo, Date dateInicial, Date dateFinal) {
        List<Caja> cajas = new ArrayList<>();
        Set<ScpPlancontable> cajasToProcess = new HashSet<>();
        Set<ScpPlancontablePK> cajaIds = new HashSet<>();
        for (ScpPlancontable caja : getTodasCajas(dateInicial, planRepo)) {
            cajaIds.add(caja.getId());
            cajasToProcess.add(caja);
        }
        for (ScpPlancontable caja : getTodasCajas(dateFinal, planRepo)) {
            if (!cajaIds.contains(caja.getId()))
                cajasToProcess.add(caja);
        }
        for (ScpPlancontable caja : cajasToProcess) {
            Character moneda = GenUtil.getNumMoneda(caja.getIndTipomoneda());
            BigDecimal saldoInicial = MainUI.get().getProcUtil().getSaldoCaja(
                    GenUtil.getEndOfDay(GenUtil.dateAddDays(dateInicial,-1)),
                    caja.getId().getCodCtacontable()
                    , moneda);
            BigDecimal saldoFinal = MainUI.get().getProcUtil().getSaldoCaja(
                    GenUtil.getEndOfDay(dateFinal),
                    caja.getId().getCodCtacontable()
                    , moneda);
            // If is closed and has a saldo of "0.00" we can omit it
            if (!caja.isNotClosedCuenta() && saldoInicial.compareTo(new BigDecimal(0))==0 &&
                    saldoFinal.compareTo(new BigDecimal(0))==0)
                continue;

            // Filtro para Gilmer
            if (Role.isOnlyCaja() && !caja.getId().getCodCtacontable().startsWith(ConfigurationUtil.get("CAJA_INTERNA_CTA_PREFIX")))
                continue;
            //
            
            cajas.add(new Caja(caja.getId().getCodCtacontable(), caja.getTxtDescctacontable(),
                    (caja.getIndTipomoneda().equals(_PEN) ? saldoInicial : new BigDecimal(0.00)),
                    (caja.getIndTipomoneda().equals(_USD) ? saldoInicial : new BigDecimal(0.00)),
                    (caja.getIndTipomoneda().equals(_PEN) ? saldoFinal : new BigDecimal(0.00)),
                    (caja.getIndTipomoneda().equals(_USD) ? saldoFinal : new BigDecimal(0.00)),
                    (caja.getIndTipomoneda().equals(_EUR) ? saldoInicial : new BigDecimal(0.00)),
                    (caja.getIndTipomoneda().equals(_EUR) ? saldoFinal : new BigDecimal(0.00))
                    ));
        }
        return cajas;
    }

    public static List<ScpPlancontable> getBancoCuentas(Date ano, ScpPlancontableRep planRepo) {
        return getBancoCuentas(ano, planRepo, null);
    }

    public static List<ScpPlancontable> getBancoCuentas(Date ano, ScpPlancontableRep planRepo, Character moneda) {
        if (moneda==null)
            return planRepo.findByFlgEstadocuentaLikeAndFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableLikeOrFlgEstadocuentaAndFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableLike(
                '0','N', GenUtil.getYear(ano), "104%",
                '0','N', GenUtil.getYear(ano), "106%");
        else
            return planRepo.findByIndTipomonedaAndFlgEstadocuentaLikeAndFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableLikeOrIndTipomonedaAndFlgEstadocuentaAndFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableLike(
                    GenUtil.getLitMoneda(moneda),'0','N', GenUtil.getYear(ano), "104%",
                    GenUtil.getLitMoneda(moneda), '0','N', GenUtil.getYear(ano), "106%");
    }

    public static Boolean isCobrado(ScpBancocabecera cab, PersistanceService service) {
        String codMescobrado = checkMesCobrado(cab, service);
        return isCobrado(codMescobrado);
    }

    public static Boolean isCobrado(String codMescobrado) {
        return codMescobrado!=null && !GenUtil.strNullOrEmpty(codMescobrado.trim());
    }

    public static String checkMesCobrado(ScpBancocabecera cab, PersistanceService service) {
        //ScpBancocabecera cab = detalle.getScpBancocabecera();
        if (!cab.isEnviado())
            return cab.getCodMescobrado();
        // If enviado a contabilidad
        // Check flag on ComprobanteDetalle
        List<ScpComprobantedetalle> comprobantedetalles = service.getScpComprobantedetalleRep()
                .findById_TxtAnoprocesoAndId_CodMesAndId_CodOrigenAndId_CodComprobanteAndCodCtacontable(
                cab.getTxtAnoproceso(), cab.getCodMes(), cab.getCodOrigenenlace(),
                cab.getCodComprobanteenlace(), cab.getCodCtacontable());
        for (ScpComprobantedetalle det : comprobantedetalles) {
            if (det.getFlgChequecobrado()=='1')
                return det.getCodMescobr();
            break;
        }
        // Now check if it is not in Chequependiente..
        List<ScpChequependiente> pendientes = service.getScpChequependienteRep().
                findById_CodCtacontableAndId_TxtChequeAndId_CodOrigenAndId_CodComprobanteAndFecComprobante(
                        cab.getCodCtacontable(), cab.getTxtCheque(), cab.getCodOrigenenlace(),
                        cab.getCodComprobanteenlace(), cab.getFecFecha());
        for (ScpChequependiente pendiente : pendientes) {
            if (pendiente.getFlgChequecobrado()=='1')
                return pendiente.getCodMescobrado();
            break;
        }
        return null;
    }

    public static List<ScpDestino> loadDestinos(PersistanceService service) {
        return loadDestinos(service, false);
    }

    public static List<ScpDestino> loadDestinos(PersistanceService service, boolean isTercero) {
        if (isTercero)
            return service.getDestinoRepo().findByIndTipodestinoAndActivoOrderByTxtNombre('3', true);
        else
            return service.getDestinoRepo().findByIndTipodestinoNotOrderByTxtNombre('3');
    }
}

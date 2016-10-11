package org.sanjose.util;

import org.sanjose.MainUI;
import org.sanjose.bean.Caja;
import org.sanjose.model.ScpPlancontable;
import org.sanjose.repo.ScpPlancontableRep;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * VASJA class
 * User: prubach
 * Date: 19.09.16
 */
public class DataUtil {


    public static List<ScpPlancontable> getCajas(Date ano, ScpPlancontableRep planRepo, boolean isPEN) {
        return planRepo.
                findByFlgEstadocuentaAndFlgMovimientoAndId_TxtAnoprocesoAndIndTipomonedaAndId_CodCtacontableStartingWith(
                        '0', 'N', GenUtil.getYear(ano), (isPEN ? 'N' : 'D') , "101");
    }

    public static List<ScpPlancontable> getCajas(Date ano, ScpPlancontableRep planRepo) {
        return planRepo.
                findByFlgEstadocuentaAndFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableStartingWith(
                        '0', 'N', GenUtil.getYear(ano), "101");
    }

    public static List<ScpPlancontable> getTodasCajas(Date ano, ScpPlancontableRep planRepo) {
        return planRepo.
                findByFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableStartingWith(
                        'N', GenUtil.getYear(ano), "101");
    }

    public static List<Caja> getCajasList(ScpPlancontableRep planRepo, Date date) {
        List<Caja> cajas = new ArrayList<>();
        for (ScpPlancontable caja : getTodasCajas(date, planRepo)) {
            Character moneda = caja.getIndTipomoneda().equals('N') ? '0' : '1';
            BigDecimal saldo = MainUI.get().getProcUtil().getSaldoCaja(
                    date,
                    caja.getId().getCodCtacontable()
                    , moneda);
            // If is closed and has a saldo of "0.00" we can omit it
            if (!caja.isNotClosedCuenta() && saldo.compareTo(new BigDecimal(0))==0)
                continue;

            cajas.add(new Caja(caja.getId().getCodCtacontable(), caja.getTxtDescctacontable(),
                    (caja.getIndTipomoneda().equals('N') ? saldo : new BigDecimal(0.00)),
                    (caja.getIndTipomoneda().equals('D') ? saldo : new BigDecimal(0.00))
            ));
        }
        return cajas;
    }

    public static List<Caja> getCajasList(ScpPlancontableRep planRepo, Date dateInicial, Date datefinal) {
        List<Caja> cajas = new ArrayList<>();
        List<ScpPlancontable> cajasInicial = getTodasCajas(dateInicial, planRepo);
        List<ScpPlancontable> cajasFinal = getTodasCajas(datefinal, planRepo);
        cajasInicial.addAll(cajasFinal);
        for (ScpPlancontable caja : cajasInicial) {
            Character moneda = caja.getIndTipomoneda().equals('N') ? '0' : '1';
            BigDecimal saldoInicial = MainUI.get().getProcUtil().getSaldoCaja(
                    GenUtil.getBeginningOfDay(dateInicial),
                    caja.getId().getCodCtacontable()
                    , moneda);
            BigDecimal saldoFinal = MainUI.get().getProcUtil().getSaldoCaja(
                    GenUtil.getEndOfDay(datefinal),
                    caja.getId().getCodCtacontable()
                    , moneda);
            // If is closed and has a saldo of "0.00" we can omit it
            if (!caja.isNotClosedCuenta() && saldoInicial.compareTo(new BigDecimal(0))==0 &&
                    saldoFinal.compareTo(new BigDecimal(0))==0)
                continue;

            cajas.add(new Caja(caja.getId().getCodCtacontable(), caja.getTxtDescctacontable(),
                    (caja.getIndTipomoneda().equals('N') ? saldoInicial : new BigDecimal(0.00)),
                    (caja.getIndTipomoneda().equals('D') ? saldoInicial : new BigDecimal(0.00)),
                    (caja.getIndTipomoneda().equals('N') ? saldoFinal : new BigDecimal(0.00)),
                    (caja.getIndTipomoneda().equals('D') ? saldoFinal : new BigDecimal(0.00))
            ));
        }
        return cajas;
    }

    public static List<ScpPlancontable> getBancoCuentas(Date ano, ScpPlancontableRep planRepo) {
        return planRepo.findByFlgEstadocuentaLikeAndFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableLikeOrFlgEstadocuentaAndFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableLike (
                '0', 'N', GenUtil.getYear(ano), "104%",
                '0', 'N', GenUtil.getYear(ano), "106%");
    }
}

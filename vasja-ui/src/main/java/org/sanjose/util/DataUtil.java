package org.sanjose.util;

import org.sanjose.MainUI;
import org.sanjose.bean.Caja;
import org.sanjose.model.ScpPlancontable;
import org.sanjose.repo.ScpPlancontableRep;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.sanjose.util.GenUtil.*;

/**
 * VASJA class
 * User: prubach
 * Date: 19.09.16
 */
public class DataUtil {


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
        return planRepo.
                findByFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableStartingWith(
                        'N', GenUtil.getYear(ano), "101");
    }


    public static List<Caja> getBancoCuentasList(ScpPlancontableRep planRepo, Date date) {
        List<Caja> cajas = new ArrayList<>();
        for (ScpPlancontable caja : getBancoCuentas(date, planRepo)) {
            Character moneda = GenUtil.getNumMoneda(caja.getIndTipomoneda());
            if (moneda.equals('9')) {
                // Problem - TipoMoneda not set
                System.out.println("ERROR: TipoMoneda not set for BancoCuenta: " + caja.getCodCta4() + " " + caja.getTxtDescctacontable());
            }
            BigDecimal saldo = MainUI.get().getProcUtil().getSaldoBanco(
                    GenUtil.getEndOfDay(GenUtil.dateAddDays(date,-1)),
                    caja.getId().getCodCtacontable()
                    , moneda);
            // If is closed and has a saldo of "0.00" we can omit it
            if (!caja.isNotClosedCuenta() && saldo.compareTo(new BigDecimal(0)) == 0)
                continue;

            cajas.add(new Caja(caja.getId().getCodCtacontable(), caja.getTxtDescctacontable(),
                    (caja.getIndTipomoneda().equals(_PEN) ? saldo : new BigDecimal(0.00)),
                    (caja.getIndTipomoneda().equals(_USD) ? saldo : new BigDecimal(0.00)),
                    (caja.getIndTipomoneda().equals(_EUR) ? saldo : new BigDecimal(0.00))
            ));
        }
        return cajas;
    }

    public static List<Caja> getCajasList(ScpPlancontableRep planRepo, Date date) {
        List<Caja> cajas = new ArrayList<>();
        for (ScpPlancontable caja : getTodasCajas(date, planRepo)) {
            Character moneda = GenUtil.getNumMoneda(caja.getIndTipomoneda());
            BigDecimal saldo = MainUI.get().getProcUtil().getSaldoCaja(
                    GenUtil.getEndOfDay(GenUtil.dateAddDays(date,-1)),
                    caja.getId().getCodCtacontable()
                    , moneda);
            // If is closed and has a saldo of "0.00" we can omit it
            if (!caja.isNotClosedCuenta() && saldo.compareTo(new BigDecimal(0))==0)
                continue;

            cajas.add(new Caja(caja.getId().getCodCtacontable(), caja.getTxtDescctacontable(),
                    (caja.getIndTipomoneda().equals(_PEN) ? saldo : new BigDecimal(0.00)),
                    (caja.getIndTipomoneda().equals(_USD) ? saldo : new BigDecimal(0.00)),
                    (caja.getIndTipomoneda().equals(_EUR) ? saldo : new BigDecimal(0.00))
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
            Character moneda = GenUtil.getNumMoneda(caja.getIndTipomoneda());
            BigDecimal saldoInicial = MainUI.get().getProcUtil().getSaldoCaja(
                    GenUtil.getEndOfDay(GenUtil.dateAddDays(dateInicial,-1)),
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
        return planRepo.findByFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableLikeOrFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableLike(
                'N', GenUtil.getYear(ano), "104%",
                'N', GenUtil.getYear(ano), "106%");
    }
}

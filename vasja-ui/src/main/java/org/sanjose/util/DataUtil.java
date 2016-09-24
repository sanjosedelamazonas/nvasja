package org.sanjose.util;

import com.vaadin.data.Validator;
import com.vaadin.data.fieldgroup.FieldGroup;
import org.sanjose.MainUI;
import org.sanjose.authentication.CurrentUser;
import org.sanjose.bean.Caja;
import org.sanjose.model.VsjItem;
import org.sanjose.model.ScpPlancontable;
import org.sanjose.model.VsjBancodetalle;
import org.sanjose.repo.ScpPlancontableRep;
import org.sanjose.model.VsjCajabanco;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.sanjose.util.GenUtil.PEN;
import static org.sanjose.util.GenUtil.USD;

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
           return getCajasList(MainUI.get().getEntityManager(), planRepo, date);
    }

    public static List<Caja> getCajasList(EntityManager em, ScpPlancontableRep planRepo, Date date) {
        List<Caja> cajas = new ArrayList<>();
        for (ScpPlancontable caja : getTodasCajas(date, planRepo)) {
            Character moneda = caja.getIndTipomoneda().equals('N') ? '0' : '1';
            BigDecimal saldo = new ProcUtil(em).getSaldoCaja(
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

    public static List<ScpPlancontable> getBancoCuentas(Date ano, ScpPlancontableRep planRepo) {
        return planRepo.findByFlgEstadocuentaAndFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableLikeOrFlgEstadocuentaAndFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableLike (
                '0', 'N', GenUtil.getYear(ano), "104%",
                '0', 'N', GenUtil.getYear(ano), "106%");
    }
}

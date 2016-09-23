package org.sanjose.util;

import com.vaadin.data.Validator;
import com.vaadin.data.fieldgroup.FieldGroup;
import org.sanjose.MainUI;
import org.sanjose.authentication.CurrentUser;
import org.sanjose.bean.Caja;
import org.sanjose.model.ScpPlancontable;
import org.sanjose.model.VsjBancodetalle;
import org.sanjose.repo.ScpPlancontableRep;
import org.sanjose.model.VsjCajabanco;
import org.sanjose.views.caja.ComprobanteView;

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

    public static VsjCajabanco prepareToSave(VsjCajabanco item) throws FieldGroup.CommitException {
        if (GenUtil.strNullOrEmpty(item.getCodProyecto()) && GenUtil.strNullOrEmpty(item.getCodTercero()))
            throw new Validator.InvalidValueException("Codigo Proyecto o Codigo Tercero debe ser rellenado");

        SimpleDateFormat sdf = new SimpleDateFormat("MM");
        item.setCodMes(sdf.format(item.getFecFecha()));
        sdf = new SimpleDateFormat("yyyy");
        item.setTxtAnoproceso(sdf.format(item.getFecFecha()));
        if (!GenUtil.strNullOrEmpty(item.getCodProyecto())) {
            item.setIndTipocuenta('0');
        } else {
            item.setIndTipocuenta('1');
        }
        if (item.getCodUregistro() == null) item.setCodUregistro(CurrentUser.get());
        if (item.getFecFregistro() == null) item.setFecFregistro(new Timestamp(System.currentTimeMillis()));
        item.setCodUactualiza(CurrentUser.get());
        item.setFecFactualiza(new Timestamp(System.currentTimeMillis()));

        // Verify moneda and fields
        if (PEN.equals(item.getCodTipomoneda())) {
            if (GenUtil.isNullOrZero(item.getNumHabersol()) && GenUtil.isNullOrZero(item.getNumDebesol()))
                throw new FieldGroup.CommitException("Selected SOL but values are zeros or nulls");
            if (!GenUtil.isNullOrZero(item.getNumHaberdolar()) || !GenUtil.isNullOrZero(item.getNumDebedolar()))
                throw new FieldGroup.CommitException("Selected SOL but values for Dolar are not zeros or nulls");
            item.setNumHaberdolar(new BigDecimal(0.00));
            item.setNumDebedolar(new BigDecimal(0.00));
        } else {
            if (GenUtil.isNullOrZero(item.getNumHaberdolar()) && GenUtil.isNullOrZero(item.getNumDebedolar()))
                throw new FieldGroup.CommitException("Selected USD but values are zeros or nulls");
            if (!GenUtil.isNullOrZero(item.getNumHabersol()) || !GenUtil.isNullOrZero(item.getNumDebesol()))
                throw new FieldGroup.CommitException("Selected USD but values for SOL are not zeros or nulls");
            item.setNumHabersol(new BigDecimal(0.00));
            item.setNumDebesol(new BigDecimal(0.00));
        }
        return item;
    }

    public static VsjBancodetalle prepareToSave(VsjBancodetalle item) throws FieldGroup.CommitException {
        if (GenUtil.strNullOrEmpty(item.getCodProyecto()) && GenUtil.strNullOrEmpty(item.getCodTercero()))
            throw new Validator.InvalidValueException("Codigo Proyecto o Codigo Tercero debe ser rellenado");

        SimpleDateFormat sdf = new SimpleDateFormat("MM");
        item.setCodMes(sdf.format(item.getFecFecha()));
        sdf = new SimpleDateFormat("yyyy");
        item.setTxtAnoproceso(sdf.format(item.getFecFecha()));
        if (!GenUtil.strNullOrEmpty(item.getCodProyecto())) {
            item.setIndTipocuenta('0');
        } else {
            item.setIndTipocuenta('1');
        }
        if (item.getCodUregistro() == null) item.setCodUregistro(CurrentUser.get());
        if (item.getFecFregistro() == null) item.setFecFregistro(new Timestamp(System.currentTimeMillis()));
        item.setCodUactualiza(CurrentUser.get());
        item.setFecFactualiza(new Timestamp(System.currentTimeMillis()));

        // Verify moneda and fields
        if (PEN.equals(item.getCodTipomoneda())) {
            if (GenUtil.isNullOrZero(item.getNumHabersol()) && GenUtil.isNullOrZero(item.getNumDebesol()))
                throw new FieldGroup.CommitException("Selected SOL but values are zeros or nulls");
            if (!GenUtil.isNullOrZero(item.getNumHaberdolar()) || !GenUtil.isNullOrZero(item.getNumDebedolar()))
                throw new FieldGroup.CommitException("Selected SOL but values for Dolar are not zeros or nulls");
            if (!GenUtil.isNullOrZero(item.getNumHabermo()) || !GenUtil.isNullOrZero(item.getNumDebemo()))
                throw new FieldGroup.CommitException("Selected SOL but values for EUR are not zeros or nulls");
            item.setNumHaberdolar(new BigDecimal(0.00));
            item.setNumDebedolar(new BigDecimal(0.00));
            item.setNumHabermo(new BigDecimal(0.00));
            item.setNumDebemo(new BigDecimal(0.00));
        } else if (USD.equals(item.getCodTipomoneda())) {
            if (GenUtil.isNullOrZero(item.getNumHaberdolar()) && GenUtil.isNullOrZero(item.getNumDebedolar()))
                throw new FieldGroup.CommitException("Selected USD but values are zeros or nulls");
            if (!GenUtil.isNullOrZero(item.getNumHabersol()) || !GenUtil.isNullOrZero(item.getNumDebesol()))
                throw new FieldGroup.CommitException("Selected USD but values for SOL are not zeros or nulls");
            if (!GenUtil.isNullOrZero(item.getNumHabermo()) || !GenUtil.isNullOrZero(item.getNumDebemo()))
                throw new FieldGroup.CommitException("Selected USD but values for EUR are not zeros or nulls");
            item.setNumHabersol(new BigDecimal(0.00));
            item.setNumDebesol(new BigDecimal(0.00));
            item.setNumHabermo(new BigDecimal(0.00));
            item.setNumDebemo(new BigDecimal(0.00));
        } else {
            if (GenUtil.isNullOrZero(item.getNumHabermo()) && GenUtil.isNullOrZero(item.getNumDebemo()))
                throw new FieldGroup.CommitException("Selected EUR but values are zeros or nulls");
            if (!GenUtil.isNullOrZero(item.getNumHabersol()) || !GenUtil.isNullOrZero(item.getNumDebesol()))
                throw new FieldGroup.CommitException("Selected EUR but values for SOL are not zeros or nulls");
            if (!GenUtil.isNullOrZero(item.getNumHaberdolar()) || !GenUtil.isNullOrZero(item.getNumDebedolar()))
                throw new FieldGroup.CommitException("Selected EUR but values for Dolar are not zeros or nulls");
            item.setNumHabersol(new BigDecimal(0.00));
            item.setNumDebesol(new BigDecimal(0.00));
            item.setNumHaberdolar(new BigDecimal(0.00));
            item.setNumDebedolar(new BigDecimal(0.00));
        }
        return item;
    }




}

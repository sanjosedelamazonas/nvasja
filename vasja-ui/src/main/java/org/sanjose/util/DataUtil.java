package org.sanjose.util;

import com.vaadin.data.Validator;
import com.vaadin.data.fieldgroup.FieldGroup;
import org.sanjose.authentication.CurrentUser;
import org.sanjose.model.ScpPlancontable;
import org.sanjose.repo.ScpPlancontableRep;
import org.sanjose.model.VsjCajabanco;
import org.sanjose.views.ComprobanteView;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * VASJA class
 * User: prubach
 * Date: 19.09.16
 */
public class DataUtil {


    public static List<ScpPlancontable> getCajas(ScpPlancontableRep planRepo, boolean isPEN) {
        return planRepo.
                findByFlgMovimientoAndId_TxtAnoprocesoAndIndTipomonedaAndId_CodCtacontableStartingWith(
                        "N", GenUtil.getCurYear(), (isPEN ? "N" : "D") , "101");
    }

    public static List<ScpPlancontable> getCajas(ScpPlancontableRep planRepo) {
        return planRepo.
                findByFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableStartingWith(
                        "N", GenUtil.getCurYear(), "101");
    }


    public static VsjCajabanco prepareToSave(VsjCajabanco item) throws FieldGroup.CommitException {
        if (GenUtil.strNullOrEmpty(item.getCodProyecto()) && GenUtil.strNullOrEmpty(item.getCodTercero()))
            throw new Validator.InvalidValueException("Codigo Proyecto o Codigo Tercero debe ser rellenado");

        SimpleDateFormat sdf = new SimpleDateFormat("MM");
        item.setCodMes(sdf.format(item.getFecFecha()));
        sdf = new SimpleDateFormat("yyyy");
        item.setTxtAnoproceso(sdf.format(item.getFecFecha()));
        if (!GenUtil.strNullOrEmpty(item.getCodProyecto())) {
            item.setIndTipocuenta("0");
        } else {
            item.setIndTipocuenta("1");
        }
        if (item.getCodUregistro() == null) item.setCodUregistro(CurrentUser.get());
        if (item.getFecFregistro() == null) item.setFecFregistro(new Timestamp(System.currentTimeMillis()));
        item.setCodUactualiza(CurrentUser.get());
        item.setFecFactualiza(new Timestamp(System.currentTimeMillis()));

        // Verify moneda and fields
        if (ComprobanteView.PEN.equals(item.getCodTipomoneda())) {
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




}
package org.sanjose.util;

import org.sanjose.model.ScpPlancontable;
import org.sanjose.model.ScpPlancontableRep;

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



}

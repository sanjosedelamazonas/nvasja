package org.sanjose.views.sys;

import org.sanjose.model.VsjPropiedad;
import org.sanjose.repo.VsjPropiedadRep;
import org.sanjose.util.ConfigurationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by pol on 06.10.16.
 */
@Service
@Transactional
public class PropiedadService {

    private VsjPropiedadRep propiedadRep;

    @Autowired
    public PropiedadService(VsjPropiedadRep propiedadRep) {
        this.propiedadRep = propiedadRep;
    }

    @Transactional(readOnly = false)
    public void savePropiedad(VsjPropiedad vsjPropiedad) {
        // You can persist your data here
        if (vsjPropiedad != null)
            propiedadRep.save(vsjPropiedad);
        if (vsjPropiedad.getValor().equals("A")) {
            throw new RuntimeException();
        }
        ConfigurationUtil.resetConfiguration();
    }

    public VsjPropiedadRep getPropiedadRep() {
        return propiedadRep;
    }
}

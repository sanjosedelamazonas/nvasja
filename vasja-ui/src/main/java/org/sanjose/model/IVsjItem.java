package org.sanjose.model;

import java.sql.Timestamp;

/**
 * VASJA class
 * User: prubach
 * Date: 23.09.16
 */
public interface IVsjItem {

    String getTxtAnoproceso();

    void setTxtAnoproceso(String txtAnoproceso);

    String getCodMes();

    void setCodMes(String codMes);

    Character getCodTipomoneda();

    void setCodTipomoneda(Character codTipomoneda);

    String getCodUactualiza();

    void setCodUactualiza(String codUactualiza);

    String getCodUregistro();

    void setCodUregistro(String codUregistro);

    Timestamp getFecFactualiza();

    void setFecFactualiza(Timestamp fecFactualiza);

    Timestamp getFecFecha();

    void setFecFecha(Timestamp fecFecha);

    Timestamp getFecFregistro();

    void setFecFregistro(Timestamp fecFregistro);

    Character getIndTipocuenta();

    void setIndTipocuenta(Character indTipocuenta);

    String getTxtCorrelativo();

    void setTxtCorrelativo(String txtCorrelativo);

}
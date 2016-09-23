package org.sanjose.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * VASJA class
 * User: prubach
 * Date: 23.09.16
 */
public interface IVsjBancoItem extends IVsjItem {

     BigDecimal getNumDebedolar() ;

     void setNumDebedolar(BigDecimal numDebedolar) ;

     BigDecimal getNumDebemo() ;

     void setNumDebemo(BigDecimal numDebemo) ;

     BigDecimal getNumDebesol() ;

     void setNumDebesol(BigDecimal numDebesol) ;

     BigDecimal getNumHaberdolar() ;

     void setNumHaberdolar(BigDecimal numHaberdolar) ;

     BigDecimal getNumHabermo() ;

     void setNumHabermo(BigDecimal numHabermo) ;

     BigDecimal getNumHabersol() ;

     void setNumHabersol(BigDecimal numHabersol);
}
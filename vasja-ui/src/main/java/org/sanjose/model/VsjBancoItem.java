package org.sanjose.model;

import com.vaadin.data.Validator;
import com.vaadin.data.fieldgroup.FieldGroup;
import org.sanjose.authentication.CurrentUser;
import org.sanjose.util.GenUtil;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import static org.sanjose.util.GenUtil.PEN;
import static org.sanjose.util.GenUtil.USD;

/**
 * VASJA class
 * User: prubach
 * Date: 23.09.16
 */
@MappedSuperclass
public abstract class VsjBancoItem extends VsjItem {

     @Override
     public VsjBancoItem  prepareToSave() throws FieldGroup.CommitException {
          VsjBancoItem item = (VsjBancoItem) super.prepareToSave();
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

     @Column(name="num_debedolar", columnDefinition="decimal(12,2)")
     private BigDecimal numDebedolar;

     @Column(name="num_debemo", columnDefinition="decimal(12,2)")
     private BigDecimal numDebemo;

     @Column(name="num_debesol", columnDefinition="decimal(12,2)")
     private BigDecimal numDebesol;

     @Column(name="num_haberdolar", columnDefinition="decimal(12,2)")
     private BigDecimal numHaberdolar;

     @Column(name="num_habermo", columnDefinition="decimal(12,2)")
     private BigDecimal numHabermo;

     @Column(name="num_habersol", columnDefinition="decimal(12,2)")
     private BigDecimal numHabersol;


     public BigDecimal getNumDebedolar() {
          return numDebedolar;
     }

     public void setNumDebedolar(BigDecimal numDebedolar) {
          this.numDebedolar = numDebedolar;
     }

     public BigDecimal getNumDebemo() {
          return numDebemo;
     }

     public void setNumDebemo(BigDecimal numDebemo) {
          this.numDebemo = numDebemo;
     }

     public BigDecimal getNumDebesol() {
          return numDebesol;
     }

     public void setNumDebesol(BigDecimal numDebesol) {
          this.numDebesol = numDebesol;
     }

     public BigDecimal getNumHaberdolar() {
          return numHaberdolar;
     }

     public void setNumHaberdolar(BigDecimal numHaberdolar) {
          this.numHaberdolar = numHaberdolar;
     }

     public BigDecimal getNumHabermo() {
          return numHabermo;
     }

     public void setNumHabermo(BigDecimal numHabermo) {
          this.numHabermo = numHabermo;
     }

     public BigDecimal getNumHabersol() {
          return numHabersol;
     }

     public void setNumHabersol(BigDecimal numHabersol) {
          this.numHabersol = numHabersol;
     }

     @Override
     public String toString() {
          return "VsjBancoItem{" + super.toString() + ", " +
                  "numDebedolar=" + numDebedolar +
                  ", numDebemo=" + numDebemo +
                  ", numDebesol=" + numDebesol +
                  ", numHaberdolar=" + numHaberdolar +
                  ", numHabermo=" + numHabermo +
                  ", numHabersol=" + numHabersol +
                  '}';
     }
}


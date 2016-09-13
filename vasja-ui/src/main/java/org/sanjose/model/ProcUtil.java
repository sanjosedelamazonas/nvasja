package org.sanjose.model;

import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import org.sanjose.views.CajaGridView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * SORCER class
 * User: prubach
 * Date: 12.09.16
 */
public class ProcUtil {


    private static final Logger log = LoggerFactory.getLogger(ProcUtil.class);

    @PersistenceContext
    EntityManager em;

    public ProcUtil(EntityManager em) {
        this.em = em;
    }

    public EntityManager getEntityManager() {
        return em;
    }

    @Autowired
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    public Saldos getSaldos(Date fecha, String codProyecto, String codTercero) {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery("getSaldoAlDia");
        if (codProyecto!=null) {
            query.setParameter(1, "1");
            query.setParameter(3, codProyecto);
        } else if (codTercero!=null) {
            query.setParameter(1, "2");
            query.setParameter(3, codTercero);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        //log.info("Getting date: " + sdf.format(fecha));
        query.setParameter(2, sdf.format(fecha));
        query.execute();
        BigDecimal pen = (BigDecimal) query.getOutputParameterValue(4);
        pen = pen.setScale(2,BigDecimal.ROUND_HALF_EVEN);
        BigDecimal usd = (BigDecimal) query.getOutputParameterValue(5);
        usd = usd.setScale(2,BigDecimal.ROUND_HALF_EVEN);
        BigDecimal eur = (BigDecimal) query.getOutputParameterValue(6);
        eur = eur.setScale(2,BigDecimal.ROUND_HALF_EVEN);
        return new Saldos(pen, usd, eur);
    }


    public class Saldos {

        private BigDecimal saldoPEN;

        private BigDecimal saldoUSD;

        private BigDecimal saldoEUR;

        public Saldos(BigDecimal saldoPEN, BigDecimal saldoUSD, BigDecimal saldoEUR) {
            this.saldoPEN = saldoPEN;
            this.saldoUSD = saldoUSD;
            this.saldoEUR = saldoEUR;
        }

        public BigDecimal getSaldoPEN() {
            return saldoPEN;
        }

        public void setSaldoPEN(BigDecimal saldoPEN) {
            this.saldoPEN = saldoPEN;
        }

        public BigDecimal getSaldoUSD() {
            return saldoUSD;
        }

        public void setSaldoUSD(BigDecimal saldoUSD) {
            this.saldoUSD = saldoUSD;
        }

        public BigDecimal getSaldoEUR() {
            return saldoEUR;
        }

        public void setSaldoEUR(BigDecimal saldoEUR) {
            this.saldoEUR = saldoEUR;
        }

        @Override
        public String toString() {
            return "Saldos{" +
                    "saldoPEN=" + saldoPEN +
                    ", saldoUSD=" + saldoUSD +
                    ", saldoEUR=" + saldoEUR +
                    '}';
        }
    }
}

package org.sanjose.model;

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
@NamedStoredProcedureQuery(
        name = "getSaldoAlDia",
        procedureName = "usp_scp_vsj_getSaldoAlDia",
        parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, type = String.class, name = "Tipo"),
                @StoredProcedureParameter(mode = ParameterMode.IN, type = String.class, name = "FechaFinal"),
                @StoredProcedureParameter(mode = ParameterMode.IN, type = String.class, name = "Codigo"),
                @StoredProcedureParameter(mode = ParameterMode.OUT, type = BigDecimal.class, name = "SaldoPEN"),
                @StoredProcedureParameter(mode = ParameterMode.OUT, type = BigDecimal.class, name = "SaldoUSD"),
                @StoredProcedureParameter(mode = ParameterMode.OUT, type = BigDecimal.class, name = "SaldoEUR")
        }
)
public class ProcUtil {

    @PersistenceContext
    EntityManager em;

    ProcUtil instance;

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
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery("usp_scp_vsj_getSaldoAlDia");
        if (codProyecto!=null) {
            query.setParameter("Tipo", 1);
            query.setParameter("Codigo", codProyecto);
        } else if (codTercero!=null) {
            query.setParameter("Tipo", 2);
            query.setParameter("Codigo", codTercero);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        query.setParameter("FechaFinal", sdf.format(fecha));
        query.execute();
        BigDecimal pen = (BigDecimal) query.getOutputParameterValue("SaldoPEN");
        BigDecimal usd = (BigDecimal) query.getOutputParameterValue("SaldoUSD");
        BigDecimal eur = (BigDecimal) query.getOutputParameterValue("SaldoEUR");
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

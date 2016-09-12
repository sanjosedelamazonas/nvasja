package org.sanjose.model;

import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureParameter;
import java.math.BigDecimal;

/**
 * Created by pol on 12.09.16.
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
public class SaldoAlDia {
}

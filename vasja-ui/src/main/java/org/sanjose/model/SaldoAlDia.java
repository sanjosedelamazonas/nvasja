package org.sanjose.model;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * VASJA class
 * User: prubach
 * Date: 12.09.16
 */


@Entity
@NamedStoredProcedureQuery(
        name = "getSaldoAlDia",
        procedureName = "usp_scp_vsj_getSaldoAlDia",
        parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, type = BigDecimal.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, type = BigDecimal.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, type = BigDecimal.class)
        }
)
public class SaldoAlDia {

        @Id
        @GeneratedValue(strategy=GenerationType.AUTO)
        private Long id;
}

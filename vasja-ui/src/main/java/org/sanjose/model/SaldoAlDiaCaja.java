package org.sanjose.model;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by pol on 12.09.16.
 */


@Entity
@NamedStoredProcedureQuery(
        name = "getSaldoAlDiaCaja",
        procedureName = "usp_scp_vsj_GetSaldoAlDiaCaja",
        parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, type = BigDecimal.class)
        }
)
public class SaldoAlDiaCaja {

        @Id
        @GeneratedValue(strategy=GenerationType.AUTO)
        private Long id;
}

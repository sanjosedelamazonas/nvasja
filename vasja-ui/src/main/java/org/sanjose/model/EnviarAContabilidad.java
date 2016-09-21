package org.sanjose.model;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by pol on 12.09.16.
 */


@Entity
@NamedStoredProcedureQuery(
        name = "getEnviarContabilidad",
        procedureName = "usp_scp_vsj_enviarAContabilidad",
        parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, type = Integer.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, type = BigDecimal.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, type = BigDecimal.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, type = String.class)
        }
)
public class EnviarAContabilidad {

        @Id
        @GeneratedValue(strategy=GenerationType.AUTO)
        private Long id;
}

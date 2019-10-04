package org.sanjose.repo;

import org.sanjose.model.ScpChequependiente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.util.List;

public interface ScpChequependienteRep extends JpaRepository<ScpChequependiente, Long> {

    List<ScpChequependiente> findById_CodCtacontableAndId_TxtChequeAndId_CodOrigenAndId_CodComprobanteAndFecComprobante
            (String contab, String cheque, String origen, String comprob, Timestamp fecComprob);

}

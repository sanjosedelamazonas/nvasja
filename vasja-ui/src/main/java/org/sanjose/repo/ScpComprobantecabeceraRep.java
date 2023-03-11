package org.sanjose.repo;

import org.sanjose.model.ScpComprobantecabecera;
import org.sanjose.model.ScpComprobantecabeceraPK;
import org.sanjose.model.ScpComprobantedetalle;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.Column;
import java.util.List;

public interface ScpComprobantecabeceraRep extends JpaRepository<ScpComprobantecabecera, Long> {

    List<ScpComprobantecabecera> findById_TxtAnoprocesoAndId_CodFilialAndId_CodMesAndId_CodOrigenAndId_CodComprobante(
            String s, String s2, String s3, String s4, String s5);
}

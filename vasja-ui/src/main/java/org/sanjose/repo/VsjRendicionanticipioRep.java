package org.sanjose.repo;

import org.sanjose.model.VsjConfiguracioncaja;
import org.sanjose.model.VsjRendicionanticipio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VsjRendicionanticipioRep extends JpaRepository<VsjRendicionanticipio, Long> {

    List<VsjRendicionanticipio> findByCodComprobante(String codComprobante);
}

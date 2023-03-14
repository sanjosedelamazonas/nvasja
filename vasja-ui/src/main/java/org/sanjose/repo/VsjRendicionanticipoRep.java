package org.sanjose.repo;

import org.sanjose.model.VsjConfiguracioncaja;
import org.sanjose.model.VsjRendicionanticipo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VsjRendicionanticipoRep extends JpaRepository<VsjRendicionanticipo, Long> {

    List<VsjRendicionanticipo> findByCodComprobante(String codComprobante);
}

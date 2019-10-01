package org.sanjose.repo;

import org.sanjose.model.ScpRendiciondetalle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScpRendiciondetalleRep extends JpaRepository<ScpRendiciondetalle, Long> {

    List<ScpRendiciondetalle> findByCodDestino(String s);

    List<ScpRendiciondetalle> findById_CodRendicioncabecera(Integer id);

    List<ScpRendiciondetalle> findById_CodRendicioncabeceraAndId_NumNroitemGreaterThan(Integer id, Integer numItem);
}

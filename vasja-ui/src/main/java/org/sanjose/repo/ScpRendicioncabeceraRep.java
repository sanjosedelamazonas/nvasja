package org.sanjose.repo;

import org.sanjose.model.ScpRendicioncabecera;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface ScpRendicioncabeceraRep extends JpaRepository<ScpRendicioncabecera, Long> {

    List<ScpRendicioncabecera> findByCodDestino(String s);

    ScpRendicioncabecera findByCodRendicioncabecera(Integer id);

    List<ScpRendicioncabecera> findByFecComprobanteBetween(Date from, Date to);

    List<ScpRendicioncabecera> findByCodUregistroAndFecComprobanteBetween(String codUregistro, Date from, Date to);

    //List<ScpRendicioncabecera> findByFecComprobanteBetweenAndCodCtacontable(Date from, Date to, String ctaCont);
}

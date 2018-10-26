package org.sanjose.repo;

import org.sanjose.model.ScpBancocabecera;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface ScpBancocabeceraRep extends JpaRepository<ScpBancocabecera, Long> {

    List<ScpBancocabecera> findByCodDestino(String s);

    ScpBancocabecera findByCodBancocabecera(Integer id);

    List<ScpBancocabecera> findByCodCtacontable(String cta);

    List<ScpBancocabecera> findByFecFechaBetween(Date from, Date to);

    List<ScpBancocabecera> findByFecFechaBetweenAndCodCtacontable(Date from, Date to, String ctaCont);
}

package org.sanjose.repo;

import org.sanjose.model.VsjBancocabecera;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface VsjBancocabeceraRep extends JpaRepository<VsjBancocabecera, Long> {

    List<VsjBancocabecera> findByCodDestino(String s);

    VsjBancocabecera findByCodBancocabecera(Integer id);

    List<VsjBancocabecera> findByCodCtacontable(String cta);

    List<VsjBancocabecera> findByFecFechaBetween(Date from, Date to);
}

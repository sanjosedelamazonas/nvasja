package org.sanjose.repo;

import org.sanjose.model.ScpBancodetalle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface ScpBancodetalleRep extends JpaRepository<ScpBancodetalle, Long> {

    List<ScpBancodetalle> findByCodDestinoOrCodDestinoitem(String s, String s2);

    List<ScpBancodetalle> findById_CodBancocabecera(Integer id);

    List<ScpBancodetalle> findById_CodBancocabeceraAndId_NumItemGreaterThan(Integer id, Integer numItem);

    List<ScpBancodetalle> findByFecFechaBetweenAndCodTerceroIsInAndVsjBancocabecera_FlgEnviadoOrderByFecFechaAscId_CodBancocabeceraAsc(Date from, Date to, List<String> s, Character c);
}

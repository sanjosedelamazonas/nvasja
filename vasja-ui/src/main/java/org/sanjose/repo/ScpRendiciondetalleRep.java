package org.sanjose.repo;

import org.sanjose.model.ScpRendiciondetalle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScpRendiciondetalleRep extends JpaRepository<ScpRendiciondetalle, Long> {

    List<ScpRendiciondetalle> findByCodDestino(String s);

    List<ScpRendiciondetalle> findByCodRendicioncabecera(Integer id);

    List<ScpRendiciondetalle> findByCodRendicioncabeceraAndId_NumNroitemGreaterThan(Integer id, Long numItem);

    List<ScpRendiciondetalle> findById_CodComprobanteAndId_CodOrigenAndId_CodMesAndId_TxtAnoprocesoAndId_CodFilial(String codComprob, String codOrigen, String codMes, String txtAno, String codFilial);

    List<ScpRendiciondetalle> findById_CodComprobanteAndId_CodOrigenAndId_CodMesAndId_TxtAnoprocesoAndId_CodFilialAndId_NumNroitemGreaterThan(String codComprob, String codOrigen, String codMes, String txtAno, String codFilial, Long numItem);

}

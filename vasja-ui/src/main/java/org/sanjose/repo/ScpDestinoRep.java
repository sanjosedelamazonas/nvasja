package org.sanjose.repo;

import org.sanjose.model.ScpDestino;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScpDestinoRep extends JpaRepository<ScpDestino, Long> {

	List<ScpDestino> findByIndTipodestino(Character s);

	List<ScpDestino> findByIndTipodestinoNot(Character s);

	ScpDestino findByCodDestino(String s);

	ScpDestino findByCodDestinoContaining(String s);

}

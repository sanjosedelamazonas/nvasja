package org.sanjose.repo;

import org.sanjose.model.ScpDestino;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScpDestinoRep extends JpaRepository<ScpDestino, Long> {

	List<ScpDestino> findByIndTipodestino(String s);

	List<ScpDestino> findByIndTipodestinoNot(String s);

	ScpDestino findByCodDestino(String s);

}
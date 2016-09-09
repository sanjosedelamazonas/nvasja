package org.sanjose.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScpDestinoRep extends JpaRepository<ScpDestino, Long> {

	List<ScpDestino> findByIndTipodestino(String s);

}

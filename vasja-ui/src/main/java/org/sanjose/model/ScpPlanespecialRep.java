package org.sanjose.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ScpPlanespecialRep extends JpaRepository<ScpPlanespecial, Long> {

	List<ScpPlanespecial> findByFlgMovimiento(String s);
	
	List<ScpPlanespecial> findByFlgMovimientoAndId_TxtAnoproceso(String mov, String ano);
}

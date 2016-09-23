package org.sanjose.repo;

import java.util.List;

import org.sanjose.model.ScpPlanespecial;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScpPlanespecialRep extends JpaRepository<ScpPlanespecial, Long> {

	List<ScpPlanespecial> findByFlgMovimiento(Character s);
	
	List<ScpPlanespecial> findByFlgMovimientoAndId_TxtAnoproceso(Character mov, String ano);
}

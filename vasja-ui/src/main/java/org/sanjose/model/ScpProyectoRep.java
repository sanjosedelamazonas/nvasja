package org.sanjose.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface ScpProyectoRep extends JpaRepository<ScpProyecto, Long> {

	List<ScpProyecto> findByFecFinalGreaterThan(Date fecFinal);
	
	//List<ScpPlanespecial> findByFlgMovimientoAndId_TxtAnoproceso(String mov, String ano);
}

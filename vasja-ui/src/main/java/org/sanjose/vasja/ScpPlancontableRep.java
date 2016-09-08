package org.sanjose.vasja;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ScpPlancontableRep extends JpaRepository<ScpPlancontable, Long> {

	List<ScpPlancontable> findByFlgMovimiento(String s);
	
	List<ScpPlancontable> findByFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableStartingWith(String mov, String ano, String codcta);
	
	List<ScpPlancontable> findByFlgMovimientoAndId_TxtAnoproceso(String mov, String ano);	
}

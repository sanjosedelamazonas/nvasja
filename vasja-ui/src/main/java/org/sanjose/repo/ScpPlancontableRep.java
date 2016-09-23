package org.sanjose.repo;

import java.util.List;

import org.sanjose.model.ScpPlancontable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScpPlancontableRep extends JpaRepository<ScpPlancontable, Long> {

	List<ScpPlancontable> findByFlgMovimiento(String s);
	
	List<ScpPlancontable> findByFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableStartingWith(
			Character mov, String ano, String codcta);

	List<ScpPlancontable> findByFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableNotLikeAndId_CodCtacontableNotLikeAndId_CodCtacontableNotLikeAndId_CodCtacontableNotLike(
			Character mov, String ano, String codcta1, String codcta2, String codcta3, String codcta4);

	List<ScpPlancontable> findByFlgMovimientoAndId_TxtAnoprocesoAndIndTipomonedaAndId_CodCtacontableStartingWith(
			Character mov, String ano, Character tipomoneda, String codcta);

	List<ScpPlancontable> findByFlgMovimientoAndId_TxtAnoproceso(String mov, String ano);	
}

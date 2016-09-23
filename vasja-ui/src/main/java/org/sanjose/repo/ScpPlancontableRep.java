package org.sanjose.repo;

import java.util.List;

import org.sanjose.model.ScpPlancontable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScpPlancontableRep extends JpaRepository<ScpPlancontable, Long> {

	List<ScpPlancontable> findByFlgMovimiento(String s);

	List<ScpPlancontable> findByFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableStartingWith(
			Character mov, String ano, String codcta);

	List<ScpPlancontable> findByFlgEstadocuentaAndFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableStartingWith(
			String activa, Character mov, String ano, String codcta);

	List<ScpPlancontable> findByFlgEstadocuentaAndFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableNotLikeAndId_CodCtacontableNotLikeAndId_CodCtacontableNotLikeAndId_CodCtacontableNotLike(
			String aciva, Character mov, String ano, String codcta1, String codcta2, String codcta3, String codcta4);

	List<ScpPlancontable> findByFlgEstadocuentaAndFlgMovimientoAndId_TxtAnoprocesoAndIndTipomonedaAndId_CodCtacontableStartingWith(
			String activa, Character mov, String ano, String tipomoneda, String codcta);

	List<ScpPlancontable> findByFlgMovimientoAndId_TxtAnoproceso(Character mov, String ano);
}

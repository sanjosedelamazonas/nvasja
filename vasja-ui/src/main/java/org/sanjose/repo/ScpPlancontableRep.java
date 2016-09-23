package org.sanjose.repo;

import java.util.List;

import org.sanjose.model.ScpPlancontable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScpPlancontableRep extends JpaRepository<ScpPlancontable, Long> {

	List<ScpPlancontable> findByFlgMovimiento(String s);
	
	List<ScpPlancontable> findByFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableStartingWith(
			String mov, String ano, String codcta);

	List<ScpPlancontable> findByFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableLikeOrId_CodCtacontableLike (
			String mov, String ano, String codcta, String codcta2);

	List<ScpPlancontable> findByFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableNotLikeAndId_CodCtacontableNotLikeAndId_CodCtacontableNotLikeAndId_CodCtacontableNotLike(
			String mov, String ano, String codcta1, String codcta2, String codcta3, String codcta4);

	List<ScpPlancontable> findByFlgMovimientoAndId_TxtAnoprocesoAndIndTipomonedaAndId_CodCtacontableStartingWith(
			String mov, String ano, String tipomoneda, String codcta);

	List<ScpPlancontable> findByFlgEstadocuentaAndFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableStartingWith(
			String activa, String mov, String ano, String codcta);

	List<ScpPlancontable> findByFlgEstadocuentaAndFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableNotLikeAndId_CodCtacontableNotLikeAndId_CodCtacontableNotLikeAndId_CodCtacontableNotLike(
			String aciva, String mov, String ano, String codcta1, String codcta2, String codcta3, String codcta4);

	List<ScpPlancontable> findByFlgEstadocuentaAndFlgMovimientoAndId_TxtAnoprocesoAndIndTipomonedaAndId_CodCtacontableStartingWith(
			String activa, String mov, String ano, String tipomoneda, String codcta);

	List<ScpPlancontable> findByFlgMovimientoAndId_TxtAnoproceso(String mov, String ano);

	ScpPlancontable findById_TxtAnoprocesoAndCodCtacontable(String s, String s2);
}

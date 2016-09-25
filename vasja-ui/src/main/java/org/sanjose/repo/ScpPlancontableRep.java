package org.sanjose.repo;

import java.util.List;

import org.sanjose.model.ScpPlancontable;
import org.sanjose.util.GenUtil;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScpPlancontableRep extends JpaRepository<ScpPlancontable, Long> {

	List<ScpPlancontable> findByFlgMovimiento(String s);

	List<ScpPlancontable> findByFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableStartingWith(
			Character mov, String ano, String codcta);

	List<ScpPlancontable> findByFlgEstadocuentaAndFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableLikeOrId_CodCtacontableLike (
			String activa, String mov, String ano, String codcta, String codcta2);
	List<ScpPlancontable> findByFlgEstadocuentaAndFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableStartingWith(
			Character activa, Character mov, String ano, String codcta);
	List<ScpPlancontable> findByFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableLikeOrId_CodCtacontableLike (
			String mov, String ano, String codcta, String codcta2);
    List<ScpPlancontable> findByFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableStartingWith(Character mov, String ano, String ctacon);

	List<ScpPlancontable> findByFlgEstadocuentaLikeAndFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableLikeOrFlgEstadocuentaAndFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableLike (
			Character activa, Character mov, String ano, String codcta,
            Character activa2, Character mov2, String ano2, String codcta2);

	List<ScpPlancontable> findByFlgEstadocuentaAndFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableStartingWith(
			String activa, String mov, String ano, String codcta);

	List<ScpPlancontable> findByFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableNotLikeAndId_CodCtacontableNotLikeAndId_CodCtacontableNotLikeAndId_CodCtacontableNotLike(
			String mov, String ano, String codcta1, String codcta2, String codcta3, String codcta4);

	List<ScpPlancontable> findByFlgEstadocuentaAndFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableNotLikeAndId_CodCtacontableNotLikeAndId_CodCtacontableNotLikeAndId_CodCtacontableNotLike(
			Character aciva, Character mov, String ano, String codcta1, String codcta2, String codcta3, String codcta4);

	List<ScpPlancontable> findByFlgEstadocuentaAndFlgMovimientoAndId_TxtAnoprocesoAndIndTipomonedaAndId_CodCtacontableStartingWith(
			Character activa, Character mov, String ano, Character tipomoneda, String codcta);

	List<ScpPlancontable> findByFlgMovimientoAndId_TxtAnoproceso(Character mov, String ano);

	ScpPlancontable findById_TxtAnoprocesoAndId_CodCtacontable(String s, String s2);
}

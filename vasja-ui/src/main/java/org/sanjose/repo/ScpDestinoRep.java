package org.sanjose.repo;

import org.sanjose.model.ScpDestino;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ScpDestinoRep extends JpaRepository<ScpDestino, Long> {

	List<ScpDestino> findByIndTipodestino(Character s);

	List<ScpDestino> findByIndTipodestinoAndActivo(Character s, Boolean isActive);

	List<ScpDestino> findByIndTipodestinoNot(Character s);

	List<ScpDestino> findByIndTipodestinoOrderByTxtNombre(Character s);

	List<ScpDestino> findByIndTipodestinoAndActivoOrderByTxtNombre(Character s, Boolean isActive);

	// All to send report terceros
	List<ScpDestino> findByIndTipodestinoAndActivoAndEnviarreporteAndTxtUsuarioNotLikeOrderByTxtNombre(Character s, Boolean isActive, Boolean enviar, String ss);

	List<ScpDestino> findByIndTipodestinoAndActivoAndEnviarreporteOrderByTxtNombre(Character s, Boolean isActive, Boolean enviar);

	List<ScpDestino> findByIndTipodestinoNotOrderByTxtNombre(Character s);

	ScpDestino findByCodDestino(String s);

	ScpDestino findByCodDestinoContaining(String s);

	List<ScpDestino> findByCodDestinoLikeOrderByCodDestinoDesc(String cd);

	List<ScpDestino> findByCodDestinoNotLike(String cd);

	List<ScpDestino> findByCodDestinoNotLikeOrderByTxtNombre(String cd);

	List<ScpDestino> findByTxtUsuario(String cd);

	List<ScpDestino> findByIndTipodestinoAndActivoAndTxtUsuarioLike(Character t, Boolean isActive, String usuario);

	List<ScpDestino> findByIndTipodestinoAndActivoAndTxtUsuarioIn(Character t, Boolean isActive, Collection<String> usuarios);
}

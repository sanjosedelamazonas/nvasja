package org.sanjose.repo;

import org.sanjose.model.MsgUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MsgUsuarioRep extends JpaRepository<MsgUsuario, Long> {

	MsgUsuario findByTxtUsuarioAndTxtPasswordAndFlgEstado(String s1, String s2, boolean est);

	MsgUsuario findByTxtUsuario(String username);

	MsgUsuario findByTxtUsuarioIgnoreCase(String username);

	MsgUsuario findByTxtCorreoIgnoreCase(String email);

	List<MsgUsuario> findByCodUsuarioLikeOrderByCodUsuarioDesc(String s);
}

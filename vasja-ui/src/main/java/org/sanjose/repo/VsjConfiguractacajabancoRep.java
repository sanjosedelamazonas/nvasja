package org.sanjose.repo;

import org.sanjose.model.VsjConfiguractacajabanco;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VsjConfiguractacajabancoRep extends JpaRepository<VsjConfiguractacajabanco, Long> {

    List<VsjConfiguractacajabanco> findByActivoAndParaCaja(Boolean activo, Boolean paraCaja);

    List<VsjConfiguractacajabanco> findByActivoAndParaBanco(Boolean activo, Boolean paraBanco);

    List<VsjConfiguractacajabanco> findByActivoAndParaCajaAndParaProyecto(Boolean activo, Boolean paraCaja, Boolean paraProyecto);

    List<VsjConfiguractacajabanco> findByActivoAndParaCajaAndParaTercero(Boolean activo, Boolean paraCaja, Boolean paraTercero);

    List<VsjConfiguractacajabanco> findByActivoAndParaBancoAndParaProyecto(Boolean activo, Boolean paraBanco, Boolean paraProyecto);

    List<VsjConfiguractacajabanco> findByActivoAndParaBancoAndParaTercero(Boolean activo, Boolean paraBanco, Boolean paraTercero);

    VsjConfiguractacajabanco findByCodTipocuenta(Integer codTipocuenta);
}

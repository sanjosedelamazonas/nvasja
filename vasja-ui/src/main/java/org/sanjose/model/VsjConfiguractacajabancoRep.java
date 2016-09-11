package org.sanjose.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VsjConfiguractacajabancoRep extends JpaRepository<VsjConfiguractacajabanco, Long> {

    List<VsjConfiguractacajabanco> findByActivoAndParaCaja(Boolean activo, Boolean paraCaja);

    List<VsjConfiguractacajabanco> findByActivoAndParaCajaAndParaProyecto(Boolean activo, Boolean paraCaja, Boolean paraProyecto);

    List<VsjConfiguractacajabanco> findByActivoAndParaCajaAndParaTercero(Boolean activo, Boolean paraCaja, Boolean paraTercero);

    VsjConfiguractacajabanco findByCodTipocuenta(Integer codTipocuenta);
}

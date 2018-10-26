USE SCP
GO

SELECT	
  scp_bancodetalle.cod_ctacontable AS cuenta_NUMERO,
  scp_plancontable.txt_descctacontable AS cuenta_DESC,
  fun_scp_vsj_GetSaldosAlDiaBanco.saldo AS cuenta_SALDO,
  scp_bancodetalle.cod_bancocabecera AS operacion_ID,
  scp_bancocabecera.txt_correlativo AS operacion_CORRELATIVO,
  scp_bancodetalle.fec_fecha AS operacion_FECHA,
  scp_bancocabecera.txt_cheque AS operacion_CHEQUE,
  scp_bancocabecera.flg_enviado AS operacion_ENVIADO,
  scp_bancodetalle.cod_proyecto AS operacion_PROYECTO,
  scp_bancodetalle.cod_tercero AS operacion_TERCERO,
  scp_bancodetalle.cod_financiera AS operacion_FINANCIERA,
  scp_bancodetalle.cod_ctaproyecto AS operacion_RUBRO_PROY,
  scp_bancodetalle.cod_contracta AS operacion_CTA_CONTABLE,
  scp_bancodetalle.cod_ctaespecial AS operacion_RUBRO_INST,
  scp_bancodetalle.cod_contraparte AS operacion_LUGAR_GASTO,
  scp_bancodetalle.txt_glosaitem AS operacion_DESC,
  scp_bancodetalle.num_debesol AS operacion_DEBESOL,
  scp_bancodetalle.num_habersol AS operacion_HABERSOL,
  scp_bancodetalle.num_debedolar AS operacion_DEBEDOLAR,
  scp_bancodetalle.num_haberdolar AS operacion_HABERDOLAR,
  scp_bancodetalle.num_debemo AS operacion_DEBEMO,
  scp_bancodetalle.num_habermo AS operacion_HABERMO,
  scp_bancodetalle.cod_tipomoneda AS banco_MONEDA,
  scp_destino.txt_nombredestino AS beneficiario_NOMBRE
FROM
	scp_bancodetalle
	INNER JOIN scp_bancocabecera ON scp_bancodetalle.cod_bancocabecera = scp_bancocabecera.cod_bancocabecera
	INNER JOIN scp_plancontable ON scp_bancodetalle.cod_ctacontable = scp_plancontable.cod_ctacontable
	INNER JOIN scp_destino ON scp_bancodetalle.cod_destino = scp_destino.cod_destino
	INNER JOIN [fun_scp_vsj_GetSaldosAlDiaBanco]('2016-09-01 00:00:00','0') ON scp_bancodetalle.cod_ctacontable = fun_scp_vsj_GetSaldosAlDiaBanco.cuenta
	-- INNER JOIN usp_scp_vsj_GetSaldoAlDiaBanco ON usp_scp_vsj_GetSaldoAlDiaBanco.cuenta = scp_bancodetalle.cod_ctacontable
--     `operacion` operacion INNER JOIN `cuenta` cuenta ON operacion.`cuenta_ID` = cuenta.`ID`
--     INNER JOIN `operacion` OPDET ON operacion.`operacionDETALLE_ID` = OPDET.`ID`
--     INNER JOIN `beneficiario` beneficiario ON operacion.`beneficiario_ID` = beneficiario.`ID`
--     INNER JOIN `banco` banco ON operacion.`banco_ID` = banco.`ID`
--     INNER JOIN `cuenta` cuentaDET ON OPDET.`cuenta_ID` = cuentaDET.`ID`
--     INNER JOIN `categoriacuenta` categoriacuenta ON cuentaDET.`categoriacuenta_ID` = categoriacuenta.`ID`
WHERE
     scp_bancodetalle.fec_fecha BETWEEN
     '2016-09-01 00:00:00'
--  $P{STR_FECHA_MIN} 
AND 
     '2016-10-01 00:00:00'
--	$P{STR_FECHA_MAX}
AND
	scp_bancodetalle.cod_tipomoneda = '0'
ORDER BY
     scp_bancodetalle.cod_ctacontable,
     scp_bancodetalle.cod_bancocabecera ASC
     --, scp_bancocabecera.cod_bancocabecera DESC



 USE SCP
 GO

 SELECT
   scp_bancodetalle.cod_ctacontable AS cuenta_NUMERO,
   scp_plancontable.txt_descctacontable AS cuenta_DESC,
   fun_scp_vsj_GetSaldosAlDiaBanco.saldo AS cuenta_SALDO,
   scp_bancodetalle.cod_bancocabecera AS operacion_ID,
   scp_bancocabecera.txt_correlativo AS operacion_CORRELATIVO,
   scp_bancodetalle.fec_fecha AS operacion_FECHA,
   scp_bancocabecera.txt_cheque AS operacion_CHEQUE,
   scp_bancocabecera.flg_enviado AS operacion_ENVIADO,
   scp_bancodetalle.cod_proyecto AS operacion_PROYECTO,
   scp_bancodetalle.cod_tercero AS operacion_TERCERO,
   scp_bancodetalle.cod_financiera AS operacion_FINANCIERA,
   scp_bancodetalle.cod_ctaproyecto AS operacion_RUBRO_PROY,
   scp_bancodetalle.cod_contracta AS operacion_CTA_CONTABLE,
   scp_bancodetalle.cod_ctaespecial AS operacion_RUBRO_INST,
   scp_bancodetalle.cod_contraparte AS operacion_LUGAR_GASTO,
   scp_bancodetalle.txt_glosaitem AS operacion_DESC,
   scp_bancodetalle.num_debesol AS operacion_DEBESOL,
   scp_bancodetalle.num_habersol AS operacion_HABERSOL,
   scp_bancodetalle.num_debedolar AS operacion_DEBEDOLAR,
   scp_bancodetalle.num_haberdolar AS operacion_HABERDOLAR,
   scp_bancodetalle.num_debemo AS operacion_DEBEMO,
   scp_bancodetalle.num_habermo AS operacion_HABERMO,
   scp_bancodetalle.cod_tipomoneda AS banco_MONEDA,
   scp_destino.txt_nombredestino AS beneficiario_NOMBRE
 FROM
    scp_bancodetalle
    INNER JOIN scp_bancocabecera ON scp_bancodetalle.cod_bancocabecera = scp_bancocabecera.cod_bancocabecera
    INNER JOIN scp_plancontable ON scp_bancodetalle.cod_ctacontable = scp_plancontable.cod_ctacontable
    INNER JOIN scp_destino ON scp_bancodetalle.cod_destino = scp_destino.cod_destino
    INNER JOIN [fun_scp_vsj_GetSaldosAlDiaBanco]($P{STR_FECHA_MIN},'0') ON scp_bancodetalle.cod_ctacontable = fun_scp_vsj_GetSaldosAlDiaBanco.cuenta
 WHERE
      scp_bancodetalle.fec_fecha BETWEEN
 --     '2016-09-01 00:00:00'
   $P{STR_FECHA_MIN}
 AND
 --     '2016-10-01 00:00:00'
    $P{STR_FECHA_MAX}
 AND
    scp_bancodetalle.cod_tipomoneda = '0'
 ORDER BY
      scp_bancodetalle.cod_ctacontable,
      scp_bancodetalle.cod_bancocabecera ASC
USE SCP
GO

SELECT	
  vsj_bancodetalle.cod_ctacontable AS cuenta_NUMERO,
  scp_plancontable.txt_descctacontable AS cuenta_DESC,
  fun_scp_vsj_GetSaldosAlDiaBanco.saldo AS cuenta_SALDO,
  vsj_bancodetalle.cod_bancocabecera AS operacion_ID,
  vsj_bancocabecera.txt_correlativo AS operacion_CORRELATIVO,
  vsj_bancodetalle.fec_fecha AS operacion_FECHA,
  vsj_bancocabecera.txt_cheque AS operacion_CHEQUE,
  vsj_bancocabecera.flg_enviado AS operacion_ENVIADO,
  vsj_bancodetalle.cod_proyecto AS operacion_PROYECTO,
  vsj_bancodetalle.cod_tercero AS operacion_TERCERO,
  vsj_bancodetalle.cod_financiera AS operacion_FINANCIERA,
  vsj_bancodetalle.cod_ctaproyecto AS operacion_RUBRO_PROY,
  vsj_bancodetalle.cod_contracta AS operacion_CTA_CONTABLE,
  vsj_bancodetalle.cod_ctaespecial AS operacion_RUBRO_INST,
  vsj_bancodetalle.cod_contraparte AS operacion_LUGAR_GASTO,
  vsj_bancodetalle.txt_glosaitem AS operacion_DESC,
  vsj_bancodetalle.num_debesol AS operacion_DEBESOL,
  vsj_bancodetalle.num_habersol AS operacion_HABERSOL,
  vsj_bancodetalle.num_debedolar AS operacion_DEBEDOLAR,
  vsj_bancodetalle.num_haberdolar AS operacion_HABERDOLAR, 
  vsj_bancodetalle.num_debemo AS operacion_DEBEMO,
  vsj_bancodetalle.num_habermo AS operacion_HABERMO,
  vsj_bancodetalle.cod_tipomoneda AS banco_MONEDA,
  scp_destino.txt_nombredestino AS beneficiario_NOMBRE
FROM
	vsj_bancodetalle 
	INNER JOIN vsj_bancocabecera ON vsj_bancodetalle.cod_bancocabecera = vsj_bancocabecera.cod_bancocabecera
	INNER JOIN scp_plancontable ON vsj_bancodetalle.cod_ctacontable = scp_plancontable.cod_ctacontable 
	INNER JOIN scp_destino ON vsj_bancodetalle.cod_destino = scp_destino.cod_destino
	INNER JOIN [fun_scp_vsj_GetSaldosAlDiaBanco]('2016-09-01 00:00:00','0') ON vsj_bancodetalle.cod_ctacontable = fun_scp_vsj_GetSaldosAlDiaBanco.cuenta
	-- INNER JOIN usp_scp_vsj_GetSaldoAlDiaBanco ON usp_scp_vsj_GetSaldoAlDiaBanco.cuenta = vsj_bancodetalle.cod_ctacontable
--     `operacion` operacion INNER JOIN `cuenta` cuenta ON operacion.`cuenta_ID` = cuenta.`ID`
--     INNER JOIN `operacion` OPDET ON operacion.`operacionDETALLE_ID` = OPDET.`ID`
--     INNER JOIN `beneficiario` beneficiario ON operacion.`beneficiario_ID` = beneficiario.`ID`
--     INNER JOIN `banco` banco ON operacion.`banco_ID` = banco.`ID`
--     INNER JOIN `cuenta` cuentaDET ON OPDET.`cuenta_ID` = cuentaDET.`ID`
--     INNER JOIN `categoriacuenta` categoriacuenta ON cuentaDET.`categoriacuenta_ID` = categoriacuenta.`ID`
WHERE
     vsj_bancodetalle.fec_fecha BETWEEN 
     '2016-09-01 00:00:00'
--  $P{STR_FECHA_MIN} 
AND 
     '2016-10-01 00:00:00'
--	$P{STR_FECHA_MAX}
AND
	vsj_bancodetalle.cod_tipomoneda = '0'
ORDER BY
     vsj_bancodetalle.cod_ctacontable,
     vsj_bancodetalle.cod_bancocabecera ASC
     --, vsj_bancocabecera.cod_bancocabecera DESC



 USE SCP
 GO

 SELECT
   vsj_bancodetalle.cod_ctacontable AS cuenta_NUMERO,
   scp_plancontable.txt_descctacontable AS cuenta_DESC,
   fun_scp_vsj_GetSaldosAlDiaBanco.saldo AS cuenta_SALDO,
   vsj_bancodetalle.cod_bancocabecera AS operacion_ID,
   vsj_bancocabecera.txt_correlativo AS operacion_CORRELATIVO,
   vsj_bancodetalle.fec_fecha AS operacion_FECHA,
   vsj_bancocabecera.txt_cheque AS operacion_CHEQUE,
   vsj_bancocabecera.flg_enviado AS operacion_ENVIADO,
   vsj_bancodetalle.cod_proyecto AS operacion_PROYECTO,
   vsj_bancodetalle.cod_tercero AS operacion_TERCERO,
   vsj_bancodetalle.cod_financiera AS operacion_FINANCIERA,
   vsj_bancodetalle.cod_ctaproyecto AS operacion_RUBRO_PROY,
   vsj_bancodetalle.cod_contracta AS operacion_CTA_CONTABLE,
   vsj_bancodetalle.cod_ctaespecial AS operacion_RUBRO_INST,
   vsj_bancodetalle.cod_contraparte AS operacion_LUGAR_GASTO,
   vsj_bancodetalle.txt_glosaitem AS operacion_DESC,
   vsj_bancodetalle.num_debesol AS operacion_DEBESOL,
   vsj_bancodetalle.num_habersol AS operacion_HABERSOL,
   vsj_bancodetalle.num_debedolar AS operacion_DEBEDOLAR,
   vsj_bancodetalle.num_haberdolar AS operacion_HABERDOLAR,
   vsj_bancodetalle.num_debemo AS operacion_DEBEMO,
   vsj_bancodetalle.num_habermo AS operacion_HABERMO,
   vsj_bancodetalle.cod_tipomoneda AS banco_MONEDA,
   scp_destino.txt_nombredestino AS beneficiario_NOMBRE
 FROM
    vsj_bancodetalle
    INNER JOIN vsj_bancocabecera ON vsj_bancodetalle.cod_bancocabecera = vsj_bancocabecera.cod_bancocabecera
    INNER JOIN scp_plancontable ON vsj_bancodetalle.cod_ctacontable = scp_plancontable.cod_ctacontable
    INNER JOIN scp_destino ON vsj_bancodetalle.cod_destino = scp_destino.cod_destino
    INNER JOIN [fun_scp_vsj_GetSaldosAlDiaBanco]($P{STR_FECHA_MIN},'0') ON vsj_bancodetalle.cod_ctacontable = fun_scp_vsj_GetSaldosAlDiaBanco.cuenta
 WHERE
      vsj_bancodetalle.fec_fecha BETWEEN
 --     '2016-09-01 00:00:00'
   $P{STR_FECHA_MIN}
 AND
 --     '2016-10-01 00:00:00'
    $P{STR_FECHA_MAX}
 AND
    vsj_bancodetalle.cod_tipomoneda = '0'
 ORDER BY
      vsj_bancodetalle.cod_ctacontable,
      vsj_bancodetalle.cod_bancocabecera ASC
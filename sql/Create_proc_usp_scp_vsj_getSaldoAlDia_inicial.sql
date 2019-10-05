/****** Object:  StoredProcedure [dbo].[usp_scp_vsj_getSaldoAlDia_contabilidad]    Script Date: 09/12/2016 10:05:54 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO
drop PROCEDURE if exists [dbo].[usp_scp_vsj_getSaldoAlDia_inicial]
go
CREATE PROCEDURE [dbo].[usp_scp_vsj_getSaldoAlDia_inicial]
(@Tipo int, -- 1 proyecto, 2 tercero
 @FechaInicial varchar(10),
 @FechaFinal varchar(19),
 @Codigo varchar(6),
 @SaldoPEN_inicial decimal(12,2) OUTPUT,
 @SaldoUSD_inicial decimal(12,2) OUTPUT,
 @SaldoEUR_inicial decimal(12,2) OUTPUT )

As
BEGIN

Set @SaldoPEN_inicial=0.00
Set @SaldoUSD_inicial=0.00
Set @SaldoEUR_inicial=0.00

if (@Tipo=1)
BEGIN
  --PEN
	Select @SaldoPEN_inicial=isnull((Sum(a.num_habersol)-Sum(a.num_debesol)),0)
	From scp_comprobantedetalle a
	Where
	(Substring(Ltrim(a.cod_ctacontable),1,1)<>'3' And Substring(Ltrim(a.cod_ctacontable),1,2)<>'10')
	And Ltrim(Rtrim(a.cod_proyecto)) = @Codigo and
	a.txt_anoproceso=SUBSTRING(@FechaFinal,0,5)
	and a.cod_origen='10'
	and cod_mes='00'
	and a.cod_tipomoneda=0
	Group By a.cod_tipomoneda,a.cod_proyecto

 --USD
	Select @SaldoUSD_inicial=isnull((Sum(a.num_haberdolar)-Sum(a.num_debedolar)),0)
	From scp_comprobantedetalle a
	Where
	(Substring(Ltrim(a.cod_ctacontable),1,1)<>'3' And Substring(Ltrim(a.cod_ctacontable),1,2)<>'10')
	And Ltrim(Rtrim(a.cod_proyecto)) = @Codigo and
	a.txt_anoproceso=SUBSTRING(@FechaFinal,0,5)
	and a.cod_origen='10'
	and cod_mes='00'
	and a.cod_tipomoneda=1
	Group By a.cod_tipomoneda,a.cod_proyecto

  --EUR
	Select @SaldoEUR_inicial=isnull((Sum(a.num_habermo)-Sum(a.num_debemo)),0)
	From scp_comprobantedetalle a
	Where
	(Substring(Ltrim(a.cod_ctacontable),1,1)<>'3' And Substring(Ltrim(a.cod_ctacontable),1,2)<>'10')
	And Ltrim(Rtrim(a.cod_proyecto)) = @Codigo and
	a.txt_anoproceso=SUBSTRING(@FechaFinal,0,5)
	and a.cod_origen='10'
	and cod_mes='00'
	and a.cod_tipomoneda=2
	Group By a.cod_tipomoneda,a.cod_proyecto
END
else if (@Tipo=2)
BEGIN
  --PEN
	Select @SaldoPEN_inicial=isnull((Sum(a.num_habersol)-Sum(a.num_debesol)),0)
	From scp_comprobantedetalle a
	Where
	(Substring(Ltrim(a.cod_ctacontable),1,1)<>'3' And Substring(Ltrim(a.cod_ctacontable),1,2)<>'10')
	AND Ltrim(Rtrim(a.cod_tercero)) = @Codigo
	and a.txt_anoproceso=SUBSTRING(@FechaFinal,0,5)
	and a.cod_origen='10'
	and cod_mes='00'
	and a.cod_tipomoneda=0
	Group By a.cod_tipomoneda,a.cod_tercero

 --USD
	Select @SaldoUSD_inicial=isnull((Sum(a.num_haberdolar)-Sum(a.num_debedolar)),0)
	From scp_comprobantedetalle a
	Where
	(Substring(Ltrim(a.cod_ctacontable),1,1)<>'3' And Substring(Ltrim(a.cod_ctacontable),1,2)<>'10')
	AND Ltrim(Rtrim(a.cod_tercero)) = @Codigo
	and a.txt_anoproceso=SUBSTRING(@FechaFinal,0,5)
	and a.cod_origen='10'
	and cod_mes='00'
	and a.cod_tipomoneda=1
	Group By a.cod_tipomoneda,a.cod_tercero

  --EUR
	Select @SaldoEUR_inicial=isnull((Sum(a.num_habermo)-Sum(a.num_debemo)),0)
	From scp_comprobantedetalle a
	Where
	(Substring(Ltrim(a.cod_ctacontable),1,1)<>'3' And Substring(Ltrim(a.cod_ctacontable),1,2)<>'10')
	AND Ltrim(Rtrim(a.cod_tercero)) = @Codigo
	and a.txt_anoproceso=SUBSTRING(@FechaFinal,0,5)
	and a.cod_origen='10'
	and cod_mes='00'
	and a.cod_tipomoneda=2
	Group By a.cod_tipomoneda,a.cod_tercero
END

--Print 'Inicial  PEN:'+CONVERT(char(14),@SaldoPEN_inicial ,121)
--+' USD:'+CONVERT(char(14),@SaldoUSD_inicial ,121)
--+' EUR:'+CONVERT(char(14),@SaldoEUR_inicial ,121)
END
/*

Exec usp_scp_vsj_getSaldoProyectoAlDia_contabilidad 1,'01/01/2016','09/09/2016','023017',0,0,0

*/
GO
;

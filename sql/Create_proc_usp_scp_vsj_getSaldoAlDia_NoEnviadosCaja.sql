
GO

/****** Object:  StoredProcedure [dbo].[usp_scp_vsj_getSaldoAlDia_NoEnviadosCaja]    Script Date: 09/12/2016 10:06:29 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO
drop PROCEDURE if exists [dbo].[usp_scp_vsj_getSaldoAlDia_NoEnviadosCaja]
go
CREATE PROCEDURE [dbo].[usp_scp_vsj_getSaldoAlDia_NoEnviadosCaja]
(@Tipo int, --1 Proyecto, 2 Tercero
 @FechaInicial char(10),
 @FechaFinal varchar(10),
 @Codigo varchar(6),
 @SaldoPEN_caja decimal(12,2) OUTPUT,
 @SaldoUSD_caja decimal(12,2) OUTPUT,
 @SaldoEUR_caja decimal(12,2) OUTPUT )

As
BEGIN
Set @SaldoPEN_caja=0.00
Set @SaldoUSD_caja=0.00
Set @SaldoEUR_caja=0.00



if (@Tipo=1) -- Proyecto
BEGIN
   --PEN
	Select @SaldoPEN_caja=isnull((Sum(a.num_habersol)-Sum(a.num_debesol)),0)
	From scp_cajabanco a
	Where (a.fec_fecha >= Convert(date, @FechaInicial, 103) And A.fec_fecha <= Convert(date, @FechaFinal, 120)) And
	Ltrim(Rtrim(a.cod_proyecto)) = @Codigo and
--	a.txt_anoproceso=SUBSTRING(@FechaFinal,7,4) and
	a.cod_tipomoneda=0 and
	a.flg_enviado=0

  --USD
	Select @SaldoUSD_caja=isnull((Sum(a.num_haberdolar)-Sum(a.num_debedolar)),0)
	From scp_cajabanco a
	Where (a.fec_fecha >= Convert(date, @FechaInicial, 103) And A.fec_fecha <= Convert(date, @FechaFinal, 120)) And
	Ltrim(Rtrim(a.cod_proyecto)) = @Codigo and
--	a.txt_anoproceso=SUBSTRING(@FechaFinal,7,4) and
	a.cod_tipomoneda=1 and
	a.flg_enviado=0

  --EUR
	Select @SaldoEUR_caja=isnull((Sum(a.num_habermo)-Sum(a.num_debemo)),0)
	From scp_cajabanco a
	Where (a.fec_fecha >= Convert(date, @FechaInicial, 103) And A.fec_fecha <= Convert(date, @FechaFinal, 120)) And
	Ltrim(Rtrim(a.cod_proyecto)) = @Codigo and
--	a.txt_anoproceso=SUBSTRING(@FechaFinal,7,4) and
	a.cod_tipomoneda=2 and
	a.flg_enviado=0
END

else if(@Tipo=2)-- TERCERO

BEGIN
  --PEN
	Select @SaldoPEN_caja=isnull((Sum(a.num_habersol)-Sum(a.num_debesol)),0)
	From scp_cajabanco a
	Where
	(a.fec_fecha >= Convert(date, @FechaInicial, 103) And A.fec_fecha <= Convert(date, @FechaFinal, 120)) And
	isnull(Ltrim(Rtrim(a.cod_tercero)),0) = @Codigo and
--	a.txt_anoproceso=SUBSTRING(@FechaFinal,7,4)
	 a.cod_tipomoneda=0
	and a.flg_enviado=0

  --USD
	Select @SaldoUSD_caja=isnull((Sum(a.num_haberdolar)-Sum(a.num_debedolar)),0)
	From scp_cajabanco a
	Where (a.fec_fecha >= Convert(date, @FechaInicial, 103) And A.fec_fecha <= Convert(date, @FechaFinal, 120)) And
	isnull(Ltrim(Rtrim(a.cod_tercero)),0) = @Codigo and
--	a.txt_anoproceso=SUBSTRING(@FechaFinal,7,4)
	 a.cod_tipomoneda=1
	and a.flg_enviado=0

  --EUR
	Select @SaldoEUR_caja=isnull((Sum(a.num_habermo)-Sum(a.num_debemo)),0)
	From scp_cajabanco a
	Where (a.fec_fecha >= Convert(date, @FechaInicial, 103) And A.fec_fecha <= Convert(date, @FechaFinal, 120)) And
	isnull(Ltrim(Rtrim(a.cod_tercero)),0) = @Codigo and
--	a.txt_anoproceso=SUBSTRING(@FechaFinal,7,4)
	a.cod_tipomoneda=2
	and a.flg_enviado=0
END
--Print 'Caja PEN:'+CONVERT(char(14),@SaldoPEN_caja ,121)+' USD:'+CONVERT(char(14),@SaldoUSD_caja ,121)+' EUR:'+CONVERT(char(14),@SaldoEUR_caja,121)

END
/*

Exec usp_scp_vsj_getSaldoProyectoAlDia_NoEnviadosCaja 2,'01/01/2016','09/09/2016','190410',0,0,0

*/
GO
;

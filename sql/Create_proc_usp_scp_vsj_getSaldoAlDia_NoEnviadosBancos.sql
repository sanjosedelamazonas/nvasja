
/****** Object:  StoredProcedure [dbo].[usp_scp_vsj_getSaldoAlDia_NoEnviadosBancos]    Script Date: 18/03/2023 21:40:34 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

ALTER PROCEDURE [dbo].[usp_scp_vsj_getSaldoAlDia_NoEnviadosBancos]
(@Tipo int, -- 0 proyecto, 1 tercero
 @FechaInicial char(10), -- @FechaInicial <=
 @FechaFinal varchar(10), -- < @FechaFinal+1
 @Codigo varchar(6),
 @SaldoPEN_banco decimal(12,2) OUTPUT,
 @SaldoUSD_banco decimal(12,2) OUTPUT,
 @SaldoEUR_banco decimal(12,2) OUTPUT )

As
BEGIN
Set @SaldoPEN_banco=0.00
Set @SaldoUSD_banco =0.00
Set @SaldoEUR_banco=0.00

if (@Tipo=1)
BEGIN
  --PEN
	Select @SaldoPEN_banco=isnull((Sum(b.num_habersol)-Sum(b.num_debesol)),0)
	From scp_bancocabecera A
	INNER JOIN scp_bancodetalle B ON A.txt_anoproceso = B.txt_anoproceso And
	A.cod_mes+A.ind_tipocuenta+A.txt_correlativo=B.cod_mes+B.ind_tipocuenta+B.txt_correlativo
	Where (a.fec_fecha >= Convert(date, @FechaInicial, 111) And A.fec_fecha < dateadd(day,1,Convert(date, @FechaFinal, 111)))
	And Ltrim(Rtrim(b.cod_proyecto)) = @Codigo And
--	a.txt_anoproceso=SUBSTRING(@FechaFinal,7,4) and
	A.flg_enviado=0 and
	A.cod_tipomoneda=0
	Group By B.cod_proyecto,A.cod_tipomoneda

  --USD
	Select @SaldoUSD_banco=isnull((Sum(b.num_haberdolar)-Sum(b.num_debedolar)),0)
	From scp_bancocabecera A
	INNER JOIN scp_bancodetalle B ON A.txt_anoproceso = B.txt_anoproceso And
	A.cod_mes+A.ind_tipocuenta+A.txt_correlativo=B.cod_mes+B.ind_tipocuenta+B.txt_correlativo
	Where (a.fec_fecha >= Convert(date, @FechaInicial, 111) And A.fec_fecha < dateadd(day,1,Convert(date, @FechaFinal, 111)))
	And Ltrim(Rtrim(b.cod_proyecto)) = @Codigo And
	--a.txt_anoproceso=SUBSTRING(@FechaFinal,7,4) and
	A.flg_enviado=0 and
	A.cod_tipomoneda=1
	Group By B.cod_proyecto,A.cod_tipomoneda

  --EUR
	Select @SaldoEUR_banco=isnull((Sum(b.num_habermo)-Sum(b.num_debemo)),0)
	From scp_bancocabecera A
	INNER JOIN scp_bancodetalle B ON A.txt_anoproceso = B.txt_anoproceso And
	A.cod_mes+A.ind_tipocuenta+A.txt_correlativo=B.cod_mes+B.ind_tipocuenta+B.txt_correlativo
	Where (a.fec_fecha >= Convert(date, @FechaInicial, 111) And A.fec_fecha <dateadd(day,1,Convert(date, @FechaFinal, 111)))
	And Ltrim(Rtrim(b.cod_proyecto)) = @Codigo And
	--a.txt_anoproceso=SUBSTRING(@FechaFinal,7,4) and
	A.flg_enviado=0 and
	A.cod_tipomoneda=2
	Group By B.cod_proyecto,A.cod_tipomoneda
END
else if (@Tipo=2) -- Tercero
BEGIN
  --PEN
	Select @SaldoPEN_banco=isnull((Sum(b.num_habersol)-Sum(b.num_debesol)),0)
	From scp_bancocabecera A
	INNER JOIN scp_bancodetalle B ON A.txt_anoproceso = B.txt_anoproceso And
	A.cod_mes+A.ind_tipocuenta+A.txt_correlativo=B.cod_mes+B.ind_tipocuenta+B.txt_correlativo
	Where (a.fec_fecha >= Convert(date, @FechaInicial, 111) And A.fec_fecha <dateadd(day,1,Convert(date, @FechaFinal, 111))) And
	Ltrim(Rtrim(b.cod_tercero)) = @Codigo And
--	a.txt_anoproceso=SUBSTRING(@FechaFinal,7,4) and
	A.flg_enviado=0 and
	A.cod_tipomoneda=0
	Group By B.cod_tercero,A.cod_tipomoneda

  --USD
	Select @SaldoUSD_banco=isnull((Sum(b.num_haberdolar)-Sum(b.num_debedolar)),0)
	From scp_bancocabecera A
	INNER JOIN scp_bancodetalle B ON A.txt_anoproceso = B.txt_anoproceso And
	A.cod_mes+A.ind_tipocuenta+A.txt_correlativo=B.cod_mes+B.ind_tipocuenta+B.txt_correlativo
	Where (a.fec_fecha >= Convert(date, @FechaInicial, 111) And A.fec_fecha <dateadd(day,1,Convert(date, @FechaFinal,111))) And
	Ltrim(Rtrim(b.cod_tercero)) = @Codigo And
--	a.txt_anoproceso=SUBSTRING(@FechaFinal,7,4) and
	A.flg_enviado=0 and
	A.cod_tipomoneda=1
	Group By B.cod_tercero,A.cod_tipomoneda

  --EUR
	Select @SaldoEUR_banco=isnull((Sum(b.num_habermo)-Sum(b.num_debemo)),0)
	From scp_bancocabecera A
	INNER JOIN scp_bancodetalle B ON A.txt_anoproceso = B.txt_anoproceso And
	A.cod_mes+A.ind_tipocuenta+A.txt_correlativo=B.cod_mes+B.ind_tipocuenta+B.txt_correlativo
	Where (a.fec_fecha >= Convert(date, @FechaInicial, 111) And A.fec_fecha <dateadd(day,1,Convert(date, @FechaFinal, 111))) And
	Ltrim(Rtrim(b.cod_tercero)) = @Codigo And
	--a.txt_anoproceso=SUBSTRING(@FechaFinal,7,4) and
	A.flg_enviado=0 and
	A.cod_tipomoneda=2
	Group By B.cod_tercero,A.cod_tipomoneda
END

--Print 'Bancos PEN: '+CONVERT(char(14),@SaldoPEN_banco ,121)
--+' USD: '+CONVERT(char(14),@SaldoUSD_banco ,121)
--+' EUR: '+CONVERT(char(14),@SaldoEUR_banco ,121)
END
/*

Exec usp_scp_vsj_getSaldoProyectoAlDia_NoEnviadosBancos 2,'01/01/2016','09/09/2016','190420',0,0,0

*/


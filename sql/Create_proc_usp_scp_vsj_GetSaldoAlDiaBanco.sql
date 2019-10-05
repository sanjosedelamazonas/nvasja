USE [SCP]
GO
/****** Object:  StoredProcedure [dbo].[usp_scp_vsj_GetSaldoAlDiaBanco]    Script Date: 03/10/2019 22:39:30 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
--drop PROCEDURE [dbo].[usp_scp_vsj_GetSaldoAlDiaBanco];
go

ALTER PROCEDURE [dbo].[usp_scp_vsj_GetSaldoAlDiaBanco]
	@Fecha varchar(19), -- Fecha para saldo formato yyyy-dd-mm hh:mi:ss(24h)
--	@FechaFinal varchar(19), -- Fecha para saldo formato yyyy-dd-mm hh:mi:ss(24h)
	@Cuenta varchar(7), -- Cuenta de banco por ejemplo '1040103'
	@Moneda varchar(1),  -- 0 PEN, 1 USD, 2 EUR
	@SaldoLibro decimal(12,2) OUTPUT,
	@SaldoBanco decimal(12,2) OUTPUT
AS

Declare @Ano varchar(4)
Declare @SaldoInicial decimal(12,2)
Declare @ChequesDelAnoAnt decimal(12,2)
Declare @ChequesCont decimal(12,2)
Declare @ChequesNoCobrados decimal(12,2)
Declare @SaldoNoEnviados decimal(12,2)
Declare @SaldoNoEnviadosNocobrados decimal(12,2)

BEGIN

Set @Ano=SUBSTRING(@Fecha,1,4)
Set @SaldoInicial=0.00
Set @ChequesDelAnoAnt=0.00
Set @ChequesCont=0.00
Set @ChequesNoCobrados=0.00
Set @SaldoNoEnviados=0.00
Set @SaldoNoEnviadosNocobrados=0.00
Set @SaldoBanco=0.00
Set @SaldoLibro=0.00

--select @FechaInicial=(SUBSTRING(@Fecha,1,4)+'-01-01 00:00:00')

if (@Moneda='0')
BEGIN
	Select @SaldoNoEnviados= isnull(Sum(A.num_debesol)-Sum(A.num_habersol),0)
	From scp_bancocabecera A
	Where a.txt_anoproceso=@Ano
	And A.cod_ctacontable=@Cuenta
	And a.cod_tipomoneda=@Moneda
	and a.flg_enviado='0'
	and a.fec_fecha <= Convert(datetime, @Fecha, 20)
	Group By A.cod_ctacontable, a.cod_tipomoneda;

	SELECT @SaldoLibro= isnull(Sum(A.num_debesol)-Sum(A.num_habersol),0)+@SaldoNoEnviados
    FROM [SCP].[dbo].[scp_comprobantedetalle] a
    where  a.txt_anoproceso=@Ano
	And a.cod_ctacontable=@Cuenta
	and a.cod_origen in ('02','10')
	and a.cod_mes not in ('13')
	and fec_comprobante<=Convert(datetime, @Fecha, 20);

	--Print 'Saldo libro: '+CONVERT(char(14),@SaldoLibro,14)

	select @ChequesDelAnoAnt= isnull(Sum(A.num_habersol),0)
	From scp_chequependiente a
	Where a.txt_anoproceso=@Ano
	And A.cod_ctacontable=@Cuenta
	and (a.flg_chequecobrado='0' or
	(a.flg_chequecobrado='1'and a.cod_mescobrado>'0'+CONVERT(varchar, month(@Fecha))))
	Group By A.cod_ctacontable

	--Print 'Cheques no cobrados del periodo anterior: '+CONVERT(char(14),@ChequesDelAnoAnt,14)

	SELECT @ChequesNoCobrados= isnull(Sum(A.num_debesol)-Sum(A.num_habersol),0)-@ChequesDelAnoAnt
	--   ,sum([num_debedolar]), sum([num_haberdolar])
    --  ,sum([num_debemo]), sum([num_habermo])
    FROM [SCP].[dbo].[scp_comprobantedetalle] a
    where  a.txt_anoproceso=@Ano
	And a.cod_ctacontable=@Cuenta
	and a.cod_origen='02'
	and (a.flg_chequecobrado='0' or (a.flg_chequecobrado='1' and a.cod_mescobr>'0'+CONVERT(varchar, month(@Fecha))))
	and a.fec_comprobante<=Convert(datetime, @Fecha, 20);

	--Print 'Cheques no cobrados seg contabilidad: '+CONVERT(char(14),@ChequesNoCobrados,14)

	Select @SaldoNoEnviadosNocobrados= isnull(Sum(A.num_debesol)-Sum(A.num_habersol),0)
	From scp_bancocabecera A
	Where a.txt_anoproceso=@Ano
	And A.cod_ctacontable=@Cuenta
	And a.cod_tipomoneda=@Moneda
	and a.flg_enviado='0'
	and (a.ind_cobrado='0' or (a.ind_cobrado='1' and a.cod_mescobrado>month(@Fecha)))
	and a.fec_fecha <= Convert(datetime, @Fecha, 20)
	Group By A.cod_ctacontable, a.cod_tipomoneda;

	--Print 'Cheques del periodo no enviados a cont no cobrados: '+CONVERT(char(14),@SaldoNoEnviadosNocobrados,14)
END

ELse if (@Moneda='1')
BEGIN
	Select @SaldoNoEnviados= isnull(Sum(A.num_debedolar)-Sum(A.num_haberdolar),0)
	From scp_bancocabecera A
	Where a.txt_anoproceso=@Ano
	And A.cod_ctacontable=@Cuenta
	And a.cod_tipomoneda=@Moneda
	and a.flg_enviado='0'
	and a.fec_fecha <= Convert(datetime, @Fecha, 20)
	Group By A.cod_ctacontable, a.cod_tipomoneda;

	SELECT @SaldoLibro= isnull(Sum(A.num_debedolar)-Sum(A.num_haberdolar),0)+@SaldoNoEnviados
    FROM [SCP].[dbo].[scp_comprobantedetalle] a
    where  a.txt_anoproceso=@Ano
	And a.cod_ctacontable=@Cuenta
	and a.cod_origen in ('02','10')
	and a.cod_mes not in ('13')
	and fec_comprobante<=Convert(datetime, @Fecha, 20);

	--Print 'Saldo libro: '+CONVERT(char(14),@SaldoLibro,14)

	select @ChequesDelAnoAnt= isnull(Sum(A.num_haberdolar),0)
	From scp_chequependiente a
	Where a.txt_anoproceso=@Ano
	And A.cod_ctacontable=@Cuenta
	and (a.flg_chequecobrado='0' or
	(a.flg_chequecobrado='1'and a.cod_mescobrado>'0'+CONVERT(varchar, month(@Fecha))))
	Group By A.cod_ctacontable

	--Print 'Cheques no cobrados del periodo anterior: '+CONVERT(char(14),@ChequesDelAnoAnt,14)

	SELECT @ChequesNoCobrados= isnull(Sum(A.num_debedolar)-Sum(A.num_haberdolar),0)-@ChequesDelAnoAnt
	--   ,sum([num_debedolar]), sum([num_haberdolar])
    --  ,sum([num_debemo]), sum([num_habermo])
    FROM [SCP].[dbo].[scp_comprobantedetalle] a
    where  a.txt_anoproceso=@Ano
	And a.cod_ctacontable=@Cuenta
	and a.cod_origen='02'
	and (a.flg_chequecobrado='0' or (a.flg_chequecobrado='1' and a.cod_mescobr>'0'+CONVERT(varchar, month(@Fecha))))
	and a.fec_comprobante<=Convert(datetime, @Fecha, 20);

	--Print 'Cheques no cobrados seg contabilidad: '+CONVERT(char(14),@ChequesNoCobrados,14)

	Select @SaldoNoEnviadosNocobrados= isnull(Sum(A.num_debedolar)-Sum(A.num_haberdolar),0)
	From scp_bancocabecera A
	Where a.txt_anoproceso=@Ano
	And A.cod_ctacontable=@Cuenta
	And a.cod_tipomoneda=@Moneda
	and a.flg_enviado='0'
	and (a.ind_cobrado='0' or (a.ind_cobrado='1' and a.cod_mescobrado>month(@Fecha)))
	and a.fec_fecha <= Convert(datetime, @Fecha, 20)
	Group By A.cod_ctacontable, a.cod_tipomoneda;

	--Print 'Cheques del periodo no enviados a cont no cobrados: '+CONVERT(char(14),@SaldoNoEnviadosNocobrados,14)
END
ELse if (@Moneda='2')
BEGIN
	Select @SaldoNoEnviados= isnull(Sum(A.num_debemo)-Sum(A.num_habermo),0)
	From scp_bancocabecera A
	Where a.txt_anoproceso=@Ano
	And A.cod_ctacontable=@Cuenta
	And a.cod_tipomoneda=@Moneda
	and a.flg_enviado='0'
	and a.fec_fecha <= Convert(datetime, @Fecha, 20)
	Group By A.cod_ctacontable, a.cod_tipomoneda;

	SELECT @SaldoLibro= isnull(Sum(A.num_debemo)-Sum(A.num_habermo),0)+@SaldoNoEnviados
    FROM [SCP].[dbo].[scp_comprobantedetalle] a
    where  a.txt_anoproceso=@Ano
	And a.cod_ctacontable=@Cuenta
	and a.cod_origen in ('02','10')
	and a.cod_mes not in ('13')
	and fec_comprobante<=Convert(datetime, @Fecha, 20);

	--Print 'Saldo libro: '+CONVERT(char(14),@SaldoLibro,14)

	select @ChequesDelAnoAnt= isnull(Sum(A.num_habermo),0)
	From scp_chequependiente a
	Where a.txt_anoproceso=@Ano
	And A.cod_ctacontable=@Cuenta
	and (a.flg_chequecobrado='0' or
	(a.flg_chequecobrado='1'and a.cod_mescobrado>'0'+CONVERT(varchar, month(@Fecha))))
	Group By A.cod_ctacontable

	--Print 'Cheques no cobrados del periodo anterior: '+CONVERT(char(14),@ChequesDelAnoAnt,14)

	SELECT @ChequesNoCobrados= isnull(Sum(A.num_debemo)-Sum(A.num_habermo),0)-@ChequesDelAnoAnt
	--   ,sum([num_debedolar]), sum([num_haberdolar])
    --  ,sum([num_debemo]), sum([num_habermo])
    FROM [SCP].[dbo].[scp_comprobantedetalle] a
    where  a.txt_anoproceso=@Ano
	And a.cod_ctacontable=@Cuenta
	and a.cod_origen='02'
	and (a.flg_chequecobrado='0' or (a.flg_chequecobrado='1' and a.cod_mescobr>'0'+CONVERT(varchar, month(@Fecha))))
	and a.fec_comprobante<=Convert(datetime, @Fecha, 20);

	--Print 'Cheques no cobrados seg contabilidad: '+CONVERT(char(14),@ChequesNoCobrados,14)

	Select @SaldoNoEnviadosNocobrados=isnull(Sum(A.num_debemo)-Sum(A.num_habermo),0)
	From scp_bancocabecera A
	Where a.txt_anoproceso=@Ano
	And A.cod_ctacontable=@Cuenta
	And a.cod_tipomoneda=@Moneda
	and a.flg_enviado='0'
	and (a.ind_cobrado='0' or (a.ind_cobrado='1' and a.cod_mescobrado>month(@Fecha)))
	and a.fec_fecha <= Convert(datetime, @Fecha, 20)
	Group By A.cod_ctacontable, a.cod_tipomoneda;

	--Print 'Cheques del periodo no enviados a cont no cobrados: '+CONVERT(char(14),@SaldoNoEnviadosNocobrados,14)
END
select @SaldoBanco=(@SaldoLibro-@ChequesNoCobrados+@SaldoNoEnviados-@SaldoNoEnviadosNocobrados)
--Print 'Saldo banco: '+CONVERT(char(14),@SaldoBanco,14)
END
-- Exec usp_scp_vsj_GetSaldoAlDiaBanco '2019-07-31 23:59:59','1060104','2',0,0

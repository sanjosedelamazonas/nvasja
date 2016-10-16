USE [SCP]
GO
/****** Object:  StoredProcedure [dbo].[usp_scp_vsj_GetSaldoAlDiaBanco]    Script Date: 10/14/2016 03:30:47 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
ALTER PROCEDURE [dbo].[usp_scp_vsj_GetSaldoAlDiaBanco]
	@Fecha varchar(19), -- Fecha para saldo formato yyyy-dd-mm hh:mi:ss(24h)
	@Cuenta varchar(7), -- Cuenta de caja por ejemplo '1011101'
	@Moneda varchar(1),  -- 0 PEN, 1 USD
	@Saldo decimal(12,2) OUTPUT

AS

Declare @Ano varchar(4)
Declare @FechaInicial char(10)
Declare @SaldoInicial decimal(12,2)
Declare @SaldoCaja decimal(12,2)

Set @Ano=SUBSTRING(@Fecha,1,4)
-- Set @FechaInicial='01/01/'+SUBSTRING(@Fecha,7,4)
Set @SaldoInicial=0.00
Set @SaldoCaja=0.00
Set @Saldo=0.00

select @FechaInicial=(SUBSTRING(@Fecha,1,4)+'-01-01 00:00:00')

if (@Moneda='0')
BEGIN
	select @SaldoInicial = Sum(A.num_habersol)-Sum(A.num_debesol) 
	From scp_comprobantedetalle a
	Where a.txt_anoproceso=@Ano
	And A.cod_ctacontable=@Cuenta And a.cod_tipomoneda=@Moneda
	and a.cod_mes='00'
	Group By A.cod_ctacontable, a.cod_tipomoneda, a.cod_mes
	
	Print 'Inicial: '+CONVERT(char(14),@SaldoInicial,14)

	Select @SaldoCaja = Sum(A.num_habersol)-Sum(A.num_debesol) 
	From scp_cajabanco A
	Where (A.fec_fecha >= Convert(datetime, @FechaInicial, 103) And A.fec_fecha <= Convert(datetime, @Fecha, 103)) 
	And A.cod_ctacontable=@Cuenta And a.cod_tipomoneda=@Moneda
	Group By A.cod_ctacontable, a.cod_tipomoneda
END
ELse if (@Moneda='1')
BEGIN
	select @SaldoInicial = Sum(A.num_haberdolar)-Sum(A.num_debedolar) 
	From scp_comprobantedetalle a
	Where a.txt_anoproceso=@Ano
	And A.cod_ctacontable=@Cuenta And a.cod_tipomoneda=@Moneda
	and a.cod_mes='00'
	Group By A.cod_ctacontable, a.cod_tipomoneda, a.cod_mes

	Print 'Inicial: '+CONVERT(char(14),@SaldoInicial,14)

	Select @SaldoCaja = Sum(A.num_haberdolar)-Sum(A.num_debedolar) 
	From scp_cajabanco A
	Where (A.fec_fecha >= Convert(datetime, @FechaInicial, 103) And A.fec_fecha <= Convert(datetime, @Fecha, 103)) 
	And A.cod_ctacontable=@Cuenta And a.cod_tipomoneda=@Moneda
	Group By A.cod_ctacontable, a.cod_tipomoneda
END

Print 'Caja : '+CONVERT(char(14),@SaldoCaja,14)

select @Saldo=-(@SaldoInicial+@SaldoCaja)

Print 'Saldo : '+CONVERT(char(14),@Saldo,14)

-- Exec usp_scp_vsj_GetSaldoAlDiaBanco '2016-08-31 23:59:59','1041101','2',0
USE [SCP]
GO
/****** Object:  StoredProcedure [dbo].[usp_scp_vsj_GetSaldoAlDiaCaja]    Script Date: 09/22/2016 23:08:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[usp_scp_vsj_GetSaldoAlDiaCaja]
	@Fecha varchar(19), -- Fecha para saldo formato yyyy-mm-dd hh:mi:ss(24h)
	@Cuenta varchar(7), -- Cuenta de caja por ejemplo '1011101'
	@Moneda varchar(1),  -- 0 PEN, 1 USD, 2 EUR
	@Saldo decimal(12,2) OUTPUT

AS

Declare @Ano varchar(4)
Declare @FechaInicial char(10)
Declare @SaldoInicial decimal(12,2)
Declare @SaldoCaja decimal(12,2)

Set @Ano=SUBSTRING(@Fecha,1,4)
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

--	Print 'Fechas '+@Ano+'fecha inicial '+@FechaInicial+'fecha input: '+@Fecha
--	Print 'Inicial: '+CONVERT(char(14),@SaldoInicial,14)

	Select @SaldoCaja = Sum(A.num_habersol)-Sum(A.num_debesol)
	From vsj_cajabanco A
	Where (A.fec_fecha >= Convert(datetime, @FechaInicial, 20) And A.fec_fecha < Convert(datetime, @Fecha, 20))
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

	--Print 'Fechas '+@Ano+'fecha inicial '+@FechaInicial+'fecha input: '+@Fecha
	--Print 'Inicial: '+CONVERT(char(14),@SaldoInicial,14)

	Select @SaldoCaja = Sum(A.num_haberdolar)-Sum(A.num_debedolar)
	From vsj_cajabanco A
	Where (A.fec_fecha >= Convert(datetime, @FechaInicial, 20) And A.fec_fecha < Convert(datetime, @Fecha, 20))
	And A.cod_ctacontable=@Cuenta And a.cod_tipomoneda=@Moneda
	Group By A.cod_ctacontable, a.cod_tipomoneda
END
ELse if (@Moneda='2')
BEGIN
	select @SaldoInicial = Sum(A.num_habermo)-Sum(A.num_debemo)
	From scp_comprobantedetalle a
	Where a.txt_anoproceso=@Ano
	And A.cod_ctacontable=@Cuenta And a.cod_tipomoneda=@Moneda
	and a.cod_mes='00'
	Group By A.cod_ctacontable, a.cod_tipomoneda, a.cod_mes

	--Print 'Fechas '+@Ano+'fecha inicial '+@FechaInicial+'fecha input: '+@Fecha
	--Print 'Inicial: '+CONVERT(char(14),@SaldoInicial,14)

	Select @SaldoCaja = Sum(A.num_habermo)-Sum(A.num_debemo)
	From vsj_cajabanco A
	Where (A.fec_fecha >= Convert(datetime, @FechaInicial, 20) And A.fec_fecha < Convert(datetime, @Fecha, 20))
	And A.cod_ctacontable=@Cuenta And a.cod_tipomoneda=@Moneda
	Group By A.cod_ctacontable, a.cod_tipomoneda
END

--Print 'Caja : '+CONVERT(char(14),@SaldoCaja,14)

select @Saldo=-(@SaldoInicial+@SaldoCaja)

--Print 'Saldo : '+CONVERT(char(14),@Saldo,14)

-- Exec usp_scp_vsj_GetSaldoAlDiaCaja '2016-08-31 23:59:59','1011101','0',0
-- Exec usp_scp_vsj_GetSaldoAlDiaCaja '2016-08-31 00:00:00','1011101','0',0
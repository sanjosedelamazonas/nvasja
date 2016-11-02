USE [SCP]
GO
/****** Object:  StoredProcedure [dbo].[usp_scp_vsj_GetSaldoAlDiaBanco]    Script Date: 10/14/2016 03:30:47 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
USE [SCP]
GO
/****** Object:  StoredProcedure [dbo].[usp_scp_vsj_GetSaldoAlDiaBanco]    Script Date: 10/30/2016 21:21:26 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
ALTER FUNCTION [dbo].[fun_scp_vsj_GetSaldosAlDiaBanco] (
	@Fecha varchar(19), -- Fecha para saldo formato yyyy-dd-mm hh:mi:ss(24h)
	@Moneda varchar(1)  -- 0 PEN, 1 USD, 2 EUR
	)
RETURNS @SaldosTable TABLE (
		item		int identity(1,1),
		cuenta		varchar(20),
        saldo		numeric(10,2)
)
AS
BEGIN
	Declare @Saldo decimal(12,2)
	Declare @Ano varchar(4)
	Declare @FechaInicial char(10)
	Declare @SaldoInicial decimal(12,2)
	Declare @SaldoCaja decimal(12,2)
	Declare @Cuenta varchar(7)
	Declare @MonedaLit varchar(1)

	Set @Ano=SUBSTRING(@Fecha,1,4)
	Set @SaldoInicial=0.00
	Set @SaldoCaja=0.00
	Set @Saldo=0.00

	select @FechaInicial=(SUBSTRING(@Fecha,1,4)+'-01-01 00:00:00')
	
	SELECT @MonedaLit = CASE @Moneda 
						 WHEN '0' then 'N'
						 WHEN '1' then 'D'
						 WHEN '2' then 'E'
						 ELSE 'N'
						END;
	DECLARE myCursor CURSOR FOR
		SELECT cod_ctacontable FROM scp_plancontable WHERE 
		flg_movimiento='N' 
		AND txt_anoproceso=@Ano 
		AND (cod_ctacontable LIKE '104%' OR cod_ctacontable LIKE '106%')
		AND ind_tipomoneda=@MonedaLit

	OPEN myCursor
	FETCH NEXT FROM myCursor INTO @Cuenta
	WHILE (@@FETCH_STATUS = 0) 
	BEGIN
		IF (@MonedaLit='N')
		BEGIN
			select @SaldoInicial = Sum(A.num_habersol)-Sum(A.num_debesol)
			From scp_comprobantedetalle a
			Where a.txt_anoproceso=@Ano
			And A.cod_ctacontable=@Cuenta And a.cod_tipomoneda=@Moneda
			and a.cod_mes='00'
			Group By A.cod_ctacontable, a.cod_tipomoneda, a.cod_mes

			Select @SaldoCaja = Sum(A.num_habersol)-Sum(A.num_debesol)
			From vsj_bancocabecera A
			Where (A.fec_fecha >= Convert(datetime, @FechaInicial, 20) And A.fec_fecha <= Convert(datetime, @Fecha, 20))
			And A.cod_ctacontable=@Cuenta And a.cod_tipomoneda=@Moneda
			Group By A.cod_ctacontable, a.cod_tipomoneda
		END
		ELSE IF (@MonedaLit='D')
		BEGIN
			select @SaldoInicial = Sum(A.num_haberdolar)-Sum(A.num_debedolar)
			From scp_comprobantedetalle a
			Where a.txt_anoproceso=@Ano
			And A.cod_ctacontable=@Cuenta And a.cod_tipomoneda=@Moneda
			and a.cod_mes='00'
			Group By A.cod_ctacontable, a.cod_tipomoneda, a.cod_mes		

			Select @SaldoCaja = Sum(A.num_haberdolar)-Sum(A.num_debedolar)
			From vsj_bancocabecera A
			Where (A.fec_fecha >= Convert(datetime, @FechaInicial, 20) And A.fec_fecha <= Convert(datetime, @Fecha, 20))
			And A.cod_ctacontable=@Cuenta And a.cod_tipomoneda=@Moneda
			Group By A.cod_ctacontable, a.cod_tipomoneda
		END
		ELSE IF (@MonedaLit='E')
		BEGIN
			select @SaldoInicial = Sum(A.num_habermo)-Sum(A.num_debemo)
			From scp_comprobantedetalle a
			Where a.txt_anoproceso=@Ano
			And A.cod_ctacontable=@Cuenta And a.cod_tipomoneda=@Moneda
			and a.cod_mes='00'
			Group By A.cod_ctacontable, a.cod_tipomoneda, a.cod_mes

			Select @SaldoCaja = Sum(A.num_habermo)-Sum(A.num_debemo)
			From vsj_bancocabecera A
			Where (A.fec_fecha >= Convert(datetime, @FechaInicial, 20) And A.fec_fecha <= Convert(datetime, @Fecha, 20))
			And A.cod_ctacontable=@Cuenta And a.cod_tipomoneda=@Moneda
			Group By A.cod_ctacontable, a.cod_tipomoneda
		END
		
		select @Saldo=-(@SaldoInicial+@SaldoCaja)
		
		INSERT INTO @SaldosTable (cuenta, saldo) VALUES (@Cuenta,@Saldo)
		FETCH NEXT FROM myCursor INTO @Cuenta		
	End
	CLOSE myCursor
	DEALLOCATE myCursor	
RETURN	
END
-- SELECT * FROM [fun_scp_vsj_GetSaldoAlDiaBanco]('2016-08-18 23:59:59','N')

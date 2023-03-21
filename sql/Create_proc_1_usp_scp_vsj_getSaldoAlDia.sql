/****** Object:  StoredProcedure [dbo].[usp_scp_vsj_getSaldoAlDia]    Script Date: 18/03/2023 20:56:07 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
ALTER PROCEDURE [dbo].[usp_scp_vsj_getSaldoAlDia]
	@Tipo char(1), -- 1 proyecto, 2 tercero
	@Fecha varchar(19),            -- Fecha para saldo formato yyyy-mm-dd hh:mi:ss
	--- si saldo @Fecha 00:00:00 regresa saldo al final del dia anterior
	--- cualquier otra hora regresa saldo al final del dia @FechaFinal
	@Codigo varchar(6),		-- codigo del proyecto o tercero
	@SaldoPEN decimal(12,2) OUTPUT,
	@SaldoUSD decimal(12,2) OUTPUT,
	@SaldoEUR decimal(12,2) OUTPUT
AS
Declare @FechaFinal varchar(10)

Declare @SaldoPEN_contabilidad decimal(12,2)
Declare @SaldoUSD_contabilidad decimal(12,2)
Declare @SaldoEUR_contabilidad decimal(12,2)

Declare @SaldoPEN_caja decimal(12,2)
Declare @SaldoUSD_caja decimal(12,2)
Declare @SaldoEUR_caja decimal(12,2)

Declare @SaldoPEN_banco decimal(12,2)
Declare @SaldoUSD_banco decimal(12,2)
Declare @SaldoEUR_banco decimal(12,2)

Declare @SaldoPEN_inicial decimal(12,2)
Declare @SaldoUSD_inicial decimal(12,2)
Declare @SaldoEUR_inicial decimal(12,2)

Declare @FechaInicial char(10)
Set @FechaInicial=SUBSTRING(@Fecha,0,5)+'/01/01'
Set @FechaFinal=SUBSTRING(@Fecha,0,11)

Print '@Fecha '+CONVERT(char(19),@Fecha,1)
Print '@FechaInicial '+CONVERT(char(10),@FechaInicial,10)
Print '@FechaFinal '+CONVERT(char(10),@FechaFinal,10)

Set @SaldoPEN = 0.00
Set @SaldoUSD = 0.00
Set @SaldoEUR = 0.00


IF (SUBSTRING(@Fecha,12,8)='00:00:00')
begin
 select @FechaFinal=FORMAT(dateadd(day,-1,Convert(date, @FechaFinal, 111)),'yyyy/MM/dd', 'en-US' )
 select @FechaInicial=SUBSTRING(@FechaFinal,0,5)+'/01/01'
 end


-- Saldo inicial de contabilidad
Exec usp_scp_vsj_getSaldoAlDia_inicial @Tipo,@FechaInicial,@FechaFinal,@Codigo,@SaldoPEN_inicial=@SaldoPEN_inicial OUTPUT, @SaldoUSD_inicial=@SaldoUSD_inicial OUTPUT,@SaldoEUR_inicial=@SaldoEUR_inicial OUTPUT

-- Saldo del proyecto o tercero segun contabilidad
Exec usp_scp_vsj_getSaldoAlDia_contabilidad @Tipo,@FechaInicial,@FechaFinal,@Codigo,@SaldoPEN_contabilidad=@SaldoPEN_contabilidad OUTPUT, @SaldoUSD_contabilidad=@SaldoUSD_contabilidad OUTPUT,@SaldoEUR_contabilidad=@SaldoEUR_contabilidad OUTPUT

-- Saldo de operaciones de caja no enviadas a contabilidad
Exec usp_scp_vsj_getSaldoAlDia_NoEnviadosCaja @Tipo,@FechaInicial,@FechaFinal,@Codigo,@SaldoPEN_caja=@SaldoPEN_caja OUTPUT, @SaldoUSD_caja=@SaldoUSD_caja OUTPUT,@SaldoEUR_caja=@SaldoEUR_caja OUTPUT

-- Saldo de operaciones de caja no enviadas a contabilidad
Exec usp_scp_vsj_getSaldoAlDia_NoEnviadosBancos @Tipo,@FechaInicial,@FechaFinal,@Codigo,@SaldoPEN_banco=@SaldoPEN_banco OUTPUT, @SaldoUSD_banco=@SaldoUSD_banco OUTPUT,@SaldoEUR_banco=@SaldoEUR_banco OUTPUT


Select @SaldoPEN=@SaldoPEN_inicial+@SaldoPEN_contabilidad-@SaldoPEN_caja-@SaldoPEN_banco
Select @SaldoUSD=@SaldoUSD_inicial+@SaldoUSD_contabilidad-@SaldoUSD_caja-@SaldoUSD_banco
Select @SaldoEUR=@SaldoEUR_inicial+@SaldoEUR_contabilidad-@SaldoEUR_caja-@SaldoEUR_banco

--Print 'Total PEN:'+CONVERT(char(14),@SaldoPEN,14)
--+' USD:'+CONVERT(char(14),@SaldoUSD,121)
--+' EUR:'+CONVERT(char(14),@SaldoEUR,121)

/*

Exec usp_scp_vsj_getSaldoAlDia 2,'2023/01/01 00:00:00','100310', 0,0,0

*/

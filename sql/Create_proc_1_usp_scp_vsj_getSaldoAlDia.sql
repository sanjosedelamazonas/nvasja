/****** Object:  StoredProcedure [dbo].[usp_scp_vsj_getSaldoAlDia]    Script Date: 09/12/2016 10:05:34 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO
DROP PROCEDURE [dbo].[usp_scp_vsj_getSaldoAlDia]
GO
CREATE PROCEDURE [dbo].[usp_scp_vsj_getSaldoAlDia]
	@Tipo char(1), -- 0 proyecto, 1 tercero
	@FechaFinal varchar(19),            -- Fecha para saldo formato yyyy-mm-dd hh:mi:ss
	@Codigo varchar(6),		-- codigo del proyecto o tercero
	@SaldoPEN decimal(12,2) OUTPUT,
	@SaldoUSD decimal(12,2) OUTPUT,
	@SaldoEUR decimal(12,2) OUTPUT
AS

Declare @SaldoPEN_contabilidad decimal(12,2)
Declare @SaldoUSD_contabilidad decimal(12,2)
Declare @SaldoEUR_contabilidad decimal(12,2)

Declare @SaldoPEN_caja decimal(12,2)
Declare @SaldoUSD_caja decimal(12,2)
Declare @SaldoEUR_caja decimal(12,2)

Declare @SaldoPEN_banco decimal(12,2)
Declare @SaldoUSD_banco decimal(12,2)
Declare @SaldoEUR_banco decimal(12,2)

Declare @FechaInicial char(10)
--Set @FechaInicial='01/01/'+SUBSTRING(@FechaFinal,7,4)
Set @FechaInicial='01/01/1900'

Set @SaldoPEN = 0.00
Set @SaldoUSD = 0.00
Set @SaldoEUR = 0.00


-- Saldo del proyecto o tercero segun contabilidad
Exec usp_scp_vsj_getSaldoAlDia_contabilidad @Tipo,@FechaInicial,@FechaFinal,@Codigo,@SaldoPEN_contabilidad=@SaldoPEN_contabilidad OUTPUT, @SaldoUSD_contabilidad=@SaldoUSD_contabilidad OUTPUT,@SaldoEUR_contabilidad=@SaldoEUR_contabilidad OUTPUT

-- Saldo de operaciones de caja no enviadas a contabilidad
Exec usp_scp_vsj_getSaldoAlDia_NoEnviadosCaja @Tipo,@FechaInicial,@FechaFinal,@Codigo,@SaldoPEN_caja=@SaldoPEN_caja OUTPUT, @SaldoUSD_caja=@SaldoUSD_caja OUTPUT,@SaldoEUR_caja=@SaldoEUR_caja OUTPUT

-- Saldo de operaciones de caja no enviadas a contabilidad
Exec usp_scp_vsj_getSaldoAlDia_NoEnviadosBancos @Tipo,@FechaInicial,@FechaFinal,@Codigo,@SaldoPEN_banco=@SaldoPEN_banco OUTPUT, @SaldoUSD_banco=@SaldoUSD_banco OUTPUT,@SaldoEUR_banco=@SaldoEUR_banco OUTPUT


Select @SaldoPEN=@SaldoPEN_contabilidad-@SaldoPEN_caja-@SaldoPEN_banco
Select @SaldoUSD=@SaldoUSD_contabilidad-@SaldoUSD_caja-@SaldoUSD_banco
Select @SaldoEUR=@SaldoEUR_contabilidad-@SaldoEUR_caja-@SaldoEUR_banco

Print 'Total PEN:'+CONVERT(char(14),@SaldoPEN,14)
+' USD:'+CONVERT(char(14),@SaldoUSD,121)
+' EUR:'+CONVERT(char(14),@SaldoEUR,121)

/* 

Exec usp_scp_vsj_getSaldoAlDia 2,'09/09/2016','100310', 0,0,0

*/
GO


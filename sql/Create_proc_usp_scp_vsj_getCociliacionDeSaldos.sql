/****** Object:  StoredProcedure [dbo].[usp_scp_vsj_getCociliacionDeSaldos]    Script Date: 09/12/2016 10:05:09 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[usp_scp_vsj_getCociliacionDeSaldos]
	@Tipo char(1), -- 0 proyecto, 1 tercero
	@FechaFinal varchar(10),            -- Fecha para saldo formato dd/mm/yyyy
	@Codigo varchar(6),		-- codigo del proyecto o tercero
	@SaldoPEN decimal(12,2) OUTPUT,	
	@SaldoUSD decimal(12,2) OUTPUT,
	@SaldoEUR decimal(12,2) OUTPUT
AS

Declare @SaldoPEN_contado decimal(12,2) 
Declare @SaldoUSD_contabilidad decimal(12,2) 	
Declare @SaldoEUR_contabilidad decimal(12,2) 	

Declare @SaldoPEN_caja decimal(12,2) 
Declare @SaldoUSD_caja decimal(12,2) 	
Declare @SaldoEUR_caja decimal(12,2) 

Declare @SaldoPEN_banco decimal(12,2) 
Declare @SaldoUSD_banco decimal(12,2) 	
Declare @SaldoEUR_banco decimal(12,2) 

Declare @FechaInicial char(10)
Set @FechaInicial='01/01/'+SUBSTRING(@FechaFinal,7,4)

Set @SaldoPEN = 0.00
Set @SaldoUSD = 0.00
Set @SaldoEUR = 0.00


-- Saldo del proyecto o tercero segun contabilidad
Exec usp_scp_vsj_getSaldoAlDia @Tipo,@FechaFinal,@Codigo,@SaldoPEN=@SaldoPEN OUTPUT, @SaldoUSD=@SaldoUSD OUTPUT,@SaldoEUR=@SaldoEUR OUTPUT

/****** Script for SelectTopNRows command from SSMS  ******/
SELECT txt_anoproceso,cod_proyecto,Substring(Ltrim(cod_ctacontable),1,3), cod_tipomoneda ,
SUM(num_habersol-num_debesol) PEN --,Sum(num_haberdolar-num_debedolar) USD, sum(num_habermo-num_debemo) EUR
  FROM [SCP].[dbo].[scp_comprobantedetalle]
  where txt_anoproceso='2015'
  and cod_proyecto='005013'
  and cod_mes<>'13'
  and cod_tipomoneda=0
  and  (Substring(Ltrim(cod_ctacontable),1,2) in ('12', '14','16','17','37','40','41','42','44','47')
  or Substring(Ltrim(cod_ctacontable),1,3) in('469'))
  group by txt_anoproceso,cod_proyecto,Substring(Ltrim(cod_ctacontable),1,3) ,cod_tipomoneda
  order by cod_proyecto,cod_tipomoneda, Substring(Ltrim(cod_ctacontable),1,3)

SELECT @SaldoPEN_contado=SUM(num_habersol-num_debesol) --,Sum(num_haberdolar-num_debedolar) USD, sum(num_habermo-num_debemo) EUR
  FROM [SCP].[dbo].[scp_comprobantedetalle]
  where txt_anoproceso='2015'
  and cod_proyecto='005013'
  and cod_mes<>'13'
  and cod_tipomoneda=0
  and  (Substring(Ltrim(cod_ctacontable),1,2) in ('12', '14','16','17','37','40','41','42','44','47')
  or Substring(Ltrim(cod_ctacontable),1,3) in('469'))
  group by txt_anoproceso,cod_proyecto,cod_tipomoneda
  order by cod_proyecto,cod_tipomoneda
  
  Print 'Saldo PEN cta 10'+  CONVERT(char(14),@SaldoPEN,14)
    
 select @SaldoPEN_contado-@SaldoPEN   
  
  SELECT txt_anoproceso,cod_proyecto,Substring(Ltrim(cod_ctacontable),1,3), cod_tipomoneda ,
SUM(num_habersol-num_debesol) PEN--,Sum(num_haberdolar-num_debedolar) USD, sum(num_habermo-num_debemo) EUR
  FROM [SCP].[dbo].[scp_comprobantedetalle]
  where txt_anoproceso='2015'
  and cod_proyecto='005013'
  and  Substring(Ltrim(cod_ctacontable),1,3) in('462','463')
  and cod_mes<>'13'
  and cod_tipomoneda=0
  group by txt_anoproceso,cod_proyecto,Substring(Ltrim(cod_ctacontable),1,3) ,cod_tipomoneda
  order by cod_proyecto,cod_tipomoneda, Substring(Ltrim(cod_ctacontable),1,3)

/* 

Exec usp_scp_vsj_getCociliacionDeSaldos 1,'31/12/2015','005013',0,0,0

*/
GO


/****** Object:  StoredProcedure [dbo].[usp_scp_vsj_getSaldoAlDia_contabilidad]    Script Date: 09/12/2016 10:05:54 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO


CREATE PROCEDURE [dbo].[usp_scp_vsj_getSaldoAlDia_contabilidad]
(@Tipo int, -- 1 proyecto, 2 tercero
 @FechaInicial varchar(10),
 @FechaFinal varchar(10),
 @Codigo varchar(6), 
 @SaldoPEN_contabilidad decimal(12,2) OUTPUT,	
 @SaldoUSD_contabilidad decimal(12,2) OUTPUT,
 @SaldoEUR_contabilidad decimal(12,2) OUTPUT )

As

Set @SaldoPEN_contabilidad=0.00
Set @SaldoUSD_contabilidad =0.00
Set @SaldoEUR_contabilidad=0.00

if (@Tipo=1)
BEGIN
  --PEN
	Select @SaldoPEN_contabilidad=isnull((Sum(a.num_habersol)-Sum(a.num_debesol)),0)
	From scp_comprobantedetalle a,
			(SELECT distinct [txt_anoproceso]+[cod_filial]+[cod_mes]+[cod_origen]+[cod_comprobante] codigo_comprobante, fec_comprobante
			FROM [SCP].[dbo].[scp_comprobantedetalle]
			where Substring(Ltrim(cod_ctacontable),1,3) in ('101','104','106')and cod_mes not in ('13') and
			(fec_comprobante >= Convert(date, @FechaInicial, 103) And fec_comprobante <= Convert(date, @FechaFinal, 103))) b
	Where 
	a.[txt_anoproceso]+a.[cod_filial]+a.[cod_mes]+a.[cod_origen]+a.[cod_comprobante]=b.codigo_comprobante and
	(Substring(Ltrim(a.cod_ctacontable),1,3)<>'101' And Substring(Ltrim(a.cod_ctacontable),1,3)<>'104' 
	And Substring(Ltrim(a.cod_ctacontable),1,3)<>'106') And 
	Ltrim(Rtrim(a.cod_proyecto)) = @Codigo and 
	a.txt_anoproceso=SUBSTRING(@FechaFinal,7,4) And 
	a.cod_tipomoneda=0
	Group By a.txt_anoproceso,a.cod_tipomoneda,a.cod_proyecto

 --USD
	Select @SaldoUSD_contabilidad=isnull((Sum(a.num_haberdolar)-Sum(a.num_debedolar)),0)
	From scp_comprobantedetalle a,
		(SELECT distinct [txt_anoproceso]+[cod_filial]+[cod_mes]+[cod_origen]+[cod_comprobante] codigo_comprobante
			FROM [SCP].[dbo].[scp_comprobantedetalle]
			where Substring(Ltrim(cod_ctacontable),1,3) in ('101','104','106')and cod_mes not in ('13') and
			(fec_comprobante >= Convert(date, @FechaInicial, 103) And fec_comprobante <= Convert(date, @FechaFinal, 103))) b
	Where 
	a.[txt_anoproceso]+a.[cod_filial]+a.[cod_mes]+a.[cod_origen]+a.[cod_comprobante]=b.codigo_comprobante and
	(Substring(Ltrim(a.cod_ctacontable),1,3)<>'101' And Substring(Ltrim(a.cod_ctacontable),1,3)<>'104' 
	And Substring(Ltrim(a.cod_ctacontable),1,3)<>'106') And 
	Ltrim(Rtrim(a.cod_proyecto)) = @Codigo and 
	a.txt_anoproceso=SUBSTRING(@FechaFinal,7,4) And 
	a.cod_tipomoneda=1
	Group By a.txt_anoproceso,a.cod_tipomoneda,a.cod_proyecto

  --EUR
	Select @SaldoEUR_contabilidad=isnull((Sum(a.num_habermo)-Sum(a.num_debemo)),0)
	From scp_comprobantedetalle a,
	(SELECT distinct [txt_anoproceso]+[cod_filial]+[cod_mes]+[cod_origen]+[cod_comprobante] codigo_comprobante
			FROM [SCP].[dbo].[scp_comprobantedetalle]
			where Substring(Ltrim(cod_ctacontable),1,3) in ('101','104','106')and cod_mes not in ('13') and
			(fec_comprobante >= Convert(date, @FechaInicial, 103) And fec_comprobante <= Convert(date, @FechaFinal, 103))) b 
	where 
	a.[txt_anoproceso]+a.[cod_filial]+a.[cod_mes]+a.[cod_origen]+a.[cod_comprobante]=b.codigo_comprobante and
	(Substring(Ltrim(a.cod_ctacontable),1,3)<>'101' And Substring(Ltrim(a.cod_ctacontable),1,3)<>'104' 
	And Substring(Ltrim(a.cod_ctacontable),1,3)<>'106') And 
	Ltrim(Rtrim(a.cod_proyecto)) = @Codigo and 
	a.txt_anoproceso=SUBSTRING(@FechaFinal,7,4) And 
	a.cod_mes<>'13' and 
	a.cod_tipomoneda=2
	Group By a.txt_anoproceso,a.cod_tipomoneda,a.cod_proyecto
END
else if (@Tipo=2)
BEGIN
  --PEN
	Select @SaldoPEN_contabilidad=isnull((Sum(a.num_habersol)-Sum(a.num_debesol)),0)
	From scp_comprobantedetalle a,
			(SELECT distinct [txt_anoproceso]+[cod_filial]+[cod_mes]+[cod_origen]+[cod_comprobante] codigo_comprobante
			FROM [SCP].[dbo].[scp_comprobantedetalle]
			where Substring(Ltrim(cod_ctacontable),1,3) in ('101','104','106')and cod_mes not in ('13') and
			(fec_comprobante >= Convert(date, @FechaInicial, 103) And fec_comprobante <= Convert(date, @FechaFinal, 103))) b
	Where
	a.[txt_anoproceso]+a.[cod_filial]+a.[cod_mes]+a.[cod_origen]+a.[cod_comprobante]=b.codigo_comprobante and
	(Substring(Ltrim(a.cod_ctacontable),1,3)<>'101' And Substring(Ltrim(a.cod_ctacontable),1,3)<>'104' 
	And Substring(Ltrim(a.cod_ctacontable),1,3)<>'106') And 
	Ltrim(Rtrim(a.cod_tercero)) = @Codigo and 
	a.txt_anoproceso=SUBSTRING(@FechaFinal,7,4) And 
	a.cod_tipomoneda=0
	Group By a.txt_anoproceso,a.cod_tipomoneda,a.cod_tercero

 --USD
	Select @SaldoUSD_contabilidad=isnull((Sum(a.num_haberdolar)-Sum(a.num_debedolar)),0)
	From scp_comprobantedetalle a,
			(SELECT distinct [txt_anoproceso]+[cod_filial]+[cod_mes]+[cod_origen]+[cod_comprobante] codigo_comprobante
			FROM [SCP].[dbo].[scp_comprobantedetalle]
			where Substring(Ltrim(cod_ctacontable),1,3) in ('101','104','106')and cod_mes not in ('13') and
			(fec_comprobante >= Convert(date, @FechaInicial, 103) And fec_comprobante <= Convert(date, @FechaFinal, 103))) b
	Where
	a.[txt_anoproceso]+a.[cod_filial]+a.[cod_mes]+a.[cod_origen]+a.[cod_comprobante]=b.codigo_comprobante and
	(Substring(Ltrim(a.cod_ctacontable),1,3)<>'101' And Substring(Ltrim(a.cod_ctacontable),1,3)<>'104' 
	And Substring(Ltrim(a.cod_ctacontable),1,3)<>'106') And 
	Ltrim(Rtrim(a.cod_tercero)) = @Codigo and 
	a.txt_anoproceso=SUBSTRING(@FechaFinal,7,4) And 
	a.cod_tipomoneda=1
	Group By a.txt_anoproceso,a.cod_tipomoneda,a.cod_tercero

  --EUR
	Select @SaldoEUR_contabilidad=isnull((Sum(a.num_habermo)-Sum(a.num_debemo)),0)
	From scp_comprobantedetalle a,
			(SELECT distinct [txt_anoproceso]+[cod_filial]+[cod_mes]+[cod_origen]+[cod_comprobante] codigo_comprobante
			FROM [SCP].[dbo].[scp_comprobantedetalle]
			where Substring(Ltrim(cod_ctacontable),1,3) in ('101','104','106')and 
				cod_mes not in ('13') and
			(fec_comprobante >= Convert(date, @FechaInicial, 103) And fec_comprobante <= Convert(date, @FechaFinal, 103))) b
	Where
	a.[txt_anoproceso]+a.[cod_filial]+a.[cod_mes]+a.[cod_origen]+a.[cod_comprobante]=b.codigo_comprobante and
	(Substring(Ltrim(a.cod_ctacontable),1,3)<>'101' And Substring(Ltrim(a.cod_ctacontable),1,3)<>'104' 
	And Substring(Ltrim(a.cod_ctacontable),1,3)<>'106') And 
	Ltrim(Rtrim(a.cod_tercero)) = @Codigo and 
	a.txt_anoproceso=SUBSTRING(@FechaFinal,7,4) And 
	a.cod_tipomoneda=2
	Group By a.txt_anoproceso,a.cod_tipomoneda,a.cod_tercero
END

Print 'Contabilidad  PEN:'+CONVERT(char(14),@SaldoPEN_contabilidad ,121)
+' USD:'+CONVERT(char(14),@SaldoUSD_contabilidad ,121)
+' EUR:'+CONVERT(char(14),@SaldoEUR_contabilidad ,121)

/*

Exec usp_scp_vsj_getSaldoProyectoAlDia_contabilidad 1,'01/01/2016','09/09/2016','023017',0,0,0

*/
GO


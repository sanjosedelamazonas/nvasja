
GO
/****** Object:  StoredProcedure [dbo].[usp_scp_vsj_enviarAContabilidadBanco]    Script Date: 10/23/2016 16:55:48 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[usp_scp_vsj_enviarAContabilidadBanco]
	@cod_bancocabecera int, --id de operacion de scp_bancocabecera
	@user varchar(15), -- nombre del usuario segun SCP
	@fecha_operacion varchar(10),-- fecha de operacion en formato 'dd/mm/yyyy'
	@cod_moneda char(1), -- 0 pen, 1 usd, 2 eur
	@Return_Message VARCHAR(1024) = ''  OUT
AS
  SET NOCOUNT ON;

declare @cod_comprobante varchar(6)
declare @cod_origen varchar(6)
declare @cod_filial varchar(2)
declare @num_tc_usd float
declare @num_tc_mo float
declare @test float
declare @txt_mes varchar(2)
declare @txt_ano varchar(4)
declare @txt_anadir varchar(6)
declare @num_item numeric(4,0)

DECLARE @ErrorCode  int
DECLARE @ErrorStep  varchar(200)


set @cod_origen='02'
set @cod_filial='01'
set @num_tc_usd=0.00
set @num_tc_mo=0.00


set @txt_ano=SUBSTRING(@fecha_operacion,7,4)
set @txt_mes=SUBSTRING(@fecha_operacion,4,2)

select @ErrorCode = @@ERROR

BEGIN TRY

    BEGIN TRANSACTION
        /****************************************************************************
        * Step 1
        * Busca tipo de cambio para el dia de la operacion
        * Calcula los montos en dolares y soles, segun la moneda de ingreso y tipo de cambio del dia
        ****************************************************************************/

        select @num_tc_usd=isnull(num_tcvdolar,0), @num_tc_mo=isnull(num_tcveuro,0)
        from dbo.scp_tipocambio
		where fec_fechacambio= Convert(date, @fecha_operacion, 103)

		SELECT @ErrorStep = 'No existe tipo de cambio USD';
		select @test=1/@num_tc_usd

		/****************************************************************************
        * Step 2
        * Busca el numero para nuevo comprobante contable
        ****************************************************************************/
		Select @cod_comprobante=(REPLICATE('0',6-LEN(RTRIM(Isnull(Max(cod_comprobante),0)+1))) + RTRIM(Isnull(Max(cod_comprobante),0)+1))
  		From scp_comprobantecabecera
				Where Ltrim(txt_anoproceso) = @txt_ano And
				Ltrim(cod_mes) = @txt_mes And
				Ltrim(cod_origen) = @cod_origen

		/****************************************************************************
        * Step 3
        * Inserta cabezera del comprobante
        ****************************************************************************/
		SELECT  @ErrorStep = 'Error al insertar cabecera de comprobante'
		insert into dbo.scp_comprobantecabecera (
		[txt_anoproceso]
			  ,[cod_filial]
			  ,[cod_mes]
			  ,[cod_origen]
			  ,[cod_comprobante]
			  ,[fec_comprobante]
			  ,[cod_tipooperacion]
			  ,[cod_mediopago]
			  ,[cod_tipomoneda]
			  ,[txt_glosa]
			  ,[cod_destino]
			  ,[cod_banco]
			  ,[cod_ctacontable]
			  ,[flg_im]
			  ,[fec_fregistro]
			  ,[cod_uregistro]
			  ,[fec_factualiza]
			  ,[cod_uactualiza])
		(SELECT [txt_anoproceso]
			  ,@cod_filial
			  ,[cod_mes]
			  ,@cod_origen
			  ,@cod_comprobante
			  ,fec_fecha
			  ,''--[cod_tipooperacion]
			  ,''--[cod_mediopago]
			  ,[cod_tipomoneda]
			  ,[txt_glosa]
			  ,[cod_destino]
			  ,''--[cod_banco]
			  ,[cod_ctacontable]
			  ,'1'--[flg_im]
			  ,GETDATE()
			  ,@user
			  ,GETDATE()
			  ,@user
		  FROM dbo.scp_bancocabecera
		  where cod_bancocabecera=@cod_bancocabecera)

		/****************************************************************************
        * Step 4
        * Inserta la linias del comprobante
        ****************************************************************************/
      SELECT  @ErrorStep = 'Error al insertar lineas de detalle de comprobante'

       if (@cod_moneda='0') -- Moneda PEN
		begin
		  insert into dbo.scp_comprobantedetalle(
		  [txt_anoproceso],[cod_filial],[cod_mes],[cod_origen],[cod_comprobante],[num_nroitem]
			  ,[fec_comprobante],[cod_tipomoneda],[txt_glosaitem],[cod_destino],[txt_cheque]
			  ,[flg_chequecobrado],[cod_mescobr],[cod_tipocomprobantepago],[txt_seriecomprobantepago]
			  ,[txt_comprobantepago],[fec_comprobantepago],[fec_pagocomprobantepago],[cod_reftipocomprobantepago]
			  ,[txt_refseriecomprobantepago],[txt_refcomprobantepago],[fec_refcomprobantepago]
			  ,[cod_registrocompraventa],[cod_evento],[num_refnroitem],[cod_reforigen]
			  ,[cod_refcomprobante],[fec_refcomprobante],[cod_proyecto],[cod_ctaproyecto]
			  ,[cod_ctacontable],[cod_ctacontable9],[cod_ctacontable79],[cod_ctaarea]
			  ,[cod_ctaactividad],[cod_ctaespecial],[cod_financiera],[cod_flujocaja]
			  ,[num_tcvdolar],[num_debesol],[num_habersol],[num_debedolar],[num_haberdolar]
			  ,[num_tcmo],[num_debemo],[num_habermo],[cod_monedaoriginal],[flg_tcreferencia]
			  ,[flg_conversion],[cod_pais],[cod_departamento],[flg_recuperaigv],[por_igv]
			  ,[por_ies],[num_nroitem2],[cod_contraparte],[txt_nroretencion],[fec_retencion]
			  ,[flg_esactivo],[txt_NroCompSujNoDomi],[flg_RetieneCuarta],[cod_gastofijo]
			  ,[flg_distribuir],[flg_distribuido],[cod_TipoRegistro]
			  ,[num_tcmc],[num_debemc],[num_habermc],[cod_tercero],[flg_im]
			  ,[fec_fregistro],[cod_uregistro],[fec_factualiza],[cod_uactualiza])
		   (select
			  b.[txt_anoproceso]
			  ,@cod_filial
			  ,b.[cod_mes]
			  ,@cod_origen
			  ,@cod_comprobante
			  ,b.[num_item]*2-1
			  ,b.fec_fecha
			  ,b.[cod_tipomoneda]
			  ,b.[txt_glosaitem]
			  ,b.[cod_destino]
			  ,b.[txt_cheque]
			  ,'0' --!!!!corregir cuando se anade a scp_bancodetalle [flg_chequecobrado]
			  ,'' ----!!!!corregir cuando se anade a scp_bancodetalle [cod_mescobr]
			  ,b.[cod_tipocomprobantepago]
			  ,b.[txt_seriecomprobantepago]
			  ,b.[txt_comprobantepago]
			  ,b.[fec_comprobantepago]
			  , Convert(date, '01/01/1900', 103)--[fec_pagocomprobantepago]
			  ,''--[cod_reftipocomprobantepago]
			  ,''--[txt_refseriecomprobantepago]
			  ,''--[txt_refcomprobantepago]
			  ,Convert(date, '01/01/1900', 103) --[fec_refcomprobantepago]
			  ,''--[cod_registrocompraventa]
			  ,''--[cod_evento]
			  ,0 --[num_refnroitem]
			  ,'' --[cod_reforigen]
			  ,'' --[cod_refcomprobante]
			  ,Convert(date, '01/01/1900', 103) --[fec_refcomprobante]
			  ,b.[cod_proyecto]
			  ,b.[cod_ctaproyecto]
			  ,b.[cod_contracta] --[cod_ctacontable]
			  ,'' --[cod_ctacontable9]
			  ,'' --[cod_ctacontable79]
			  ,'' --[cod_ctaarea]
			  ,'' --[cod_ctaactividad]
			  ,b.[cod_ctaespecial]
			  ,b.[cod_financiera]
			  ,'' --[cod_flujocaja]
			  ,@num_tc_usd --[num_tcvdolar]
			  ,b.[num_habersol]
			  ,b.[num_debesol]
			  ,b.[num_habersol]/@num_tc_usd
			  ,b.[num_debesol]/@num_tc_usd
			  ,0 --[num_tcmo]
			  ,0--[num_debemo]
			  ,0--[num_habermo]
			  ,'' --[cod_monedaoriginal]
			  ,0--[flg_tcreferencia]
			  ,0--[flg_conversion]
			  ,'' --[cod_pais]
			  ,'' --[cod_departamento]
			  ,'1'--[flg_recuperaigv]
			  ,0--[por_igv]
			  ,0--[por_ies]
			  ,1--[num_nroitem2]
			  ,b.[cod_contraparte]
			  ,'' --[txt_nroretencion]
			  ,Convert(date, '01/01/1900', 103)--[fec_retencion]
			  ,'0' --[flg_esactivo]
			  ,'' --[txt_NroCompSujNoDomi]
			  ,'1' --[flg_RetieneCuarta]
			  ,'' --[cod_gastofijo]
			  ,'' --[flg_distribuir]
			  ,'' --[flg_distribuido]
			  ,'' --[cod_TipoRegistro]
			  ,0--[num_tcmc]
			  ,0--[num_debemc]
			  ,0--[num_habermc]
			  ,b.[cod_tercero]
			  ,'2' --[flg_im]
			  ,GETDATE()
			  ,@user
			  ,GETDATE()
			  ,@user
		  FROM dbo.scp_bancodetalle b
		  where b.cod_bancocabecera=@cod_bancocabecera
		  union all
		  select --insertar linea del banco cta 104 o 106..
			 bd.[txt_anoproceso]
			  ,@cod_filial
			  ,bd.[cod_mes]
			  ,@cod_origen
			  ,@cod_comprobante
			  ,[num_item]*2
			  ,bd.fec_fecha
			  ,bd.[cod_tipomoneda]
			  ,bd.[txt_glosaitem]
			  ,bd.[cod_destino]
			  ,bd.[txt_cheque]
			  ,'0' --!!!!corregir cuando se anade a scp_bancodetalle [flg_chequecobrado]
			  ,'' ----!!!!corregir cuando se anade a scp_bancodetalle [cod_mescobr]
			  ,bd.[cod_tipocomprobantepago]
			  ,bd.[txt_seriecomprobantepago]
			  ,bd.[txt_comprobantepago]
			  ,bd.[fec_comprobantepago]
			  , Convert(date, '01/01/1900', 103)--[fec_pagocomprobantepago]
			  ,''--[cod_reftipocomprobantepago]
			  ,''--[txt_refseriecomprobantepago]
			  ,''--[txt_refcomprobantepago]
			  ,Convert(date, '01/01/1900', 103) --[fec_refcomprobantepago]
			  ,''--[cod_registrocompraventa]
			  ,''--[cod_evento]
			  ,0 --[num_refnroitem]
			  ,'' --[cod_reforigen]
			  ,'' --[cod_refcomprobante]
			  ,Convert(date, '01/01/1900', 103) --[fec_refcomprobante]
			  ,bd.[cod_proyecto]
			  ,bd.[cod_ctaproyecto]
			  ,bc.[cod_ctacontable]  --cod_ctacontable
			  ,'' --[cod_ctacontable9]
			  ,'' --[cod_ctacontable79]
			  ,'' --[cod_ctaarea]
			  ,'' --[cod_ctaactividad]
			  ,bd.[cod_ctaespecial]
			  ,bd.[cod_financiera]
			  ,'' --[cod_flujocaja]
			  ,@num_tc_usd --[num_tcvdolar]
			  ,bd.[num_debesol]
			  ,bd.[num_habersol]
			    ,bd.[num_debesol]/@num_tc_usd--[num_debedolar]
			  ,bd.[num_habersol]/@num_tc_usd --[num_haberdolar]
			  ,0--@num_tc_mo
			  ,0--@num_haber_mo
			  ,0-- @num_debe_mo
			  ,'' --[cod_monedaoriginal]
			  ,0--[flg_tcreferencia]
			  ,0--[flg_conversion]
			  ,'' --[cod_pais]
			  ,'' --[cod_departamento]
			  ,'1'--[flg_recuperaigv]
			  ,0--[por_igv]
			  ,0--[por_ies]
			  ,1--[num_nroitem2]
			  ,bd.[cod_contraparte]
			  ,'' --[txt_nroretencion]
			  ,Convert(date, '01/01/1900', 103)--[fec_retencion]
			  ,'0' --[flg_esactivo]
			  ,'' --[txt_NroCompSujNoDomi]
			  ,'1' --[flg_RetieneCuarta]
			  ,'' --[cod_gastofijo]
			  ,'' --[flg_distribuir]
			  ,'' --[flg_distribuido]
			  ,'' --[cod_TipoRegistro]
			  ,0--[num_tcmc]
			  ,0--[num_debemc]
			  ,0--[num_habermc]
			  ,bd.[cod_tercero]
			  ,'2' --[flg_im]
			  ,GETDATE()
			  ,@user
			  ,GETDATE()
			  ,@user
		  FROM dbo.scp_bancodetalle bd, dbo.scp_bancocabecera bc
		  where bd.cod_bancocabecera=@cod_bancocabecera
		  and bc.cod_bancocabecera =bd.cod_bancocabecera )

	end
		else if(@cod_moneda='1') --Moneda USD
			begin

				  insert into dbo.scp_comprobantedetalle(
		  [txt_anoproceso],[cod_filial],[cod_mes],[cod_origen],[cod_comprobante],[num_nroitem]
			  ,[fec_comprobante],[cod_tipomoneda],[txt_glosaitem],[cod_destino],[txt_cheque]
			  ,[flg_chequecobrado],[cod_mescobr],[cod_tipocomprobantepago],[txt_seriecomprobantepago]
			  ,[txt_comprobantepago],[fec_comprobantepago],[fec_pagocomprobantepago],[cod_reftipocomprobantepago]
			  ,[txt_refseriecomprobantepago],[txt_refcomprobantepago],[fec_refcomprobantepago]
			  ,[cod_registrocompraventa],[cod_evento],[num_refnroitem],[cod_reforigen]
			  ,[cod_refcomprobante],[fec_refcomprobante],[cod_proyecto],[cod_ctaproyecto]
			  ,[cod_ctacontable],[cod_ctacontable9],[cod_ctacontable79],[cod_ctaarea]
			  ,[cod_ctaactividad],[cod_ctaespecial],[cod_financiera],[cod_flujocaja]
			  ,[num_tcvdolar],[num_debesol],[num_habersol],[num_debedolar],[num_haberdolar]
			  ,[num_tcmo],[num_debemo],[num_habermo],[cod_monedaoriginal],[flg_tcreferencia]
			  ,[flg_conversion],[cod_pais],[cod_departamento],[flg_recuperaigv],[por_igv]
			  ,[por_ies],[num_nroitem2],[cod_contraparte],[txt_nroretencion],[fec_retencion]
			  ,[flg_esactivo],[txt_NroCompSujNoDomi],[flg_RetieneCuarta],[cod_gastofijo]
			  ,[flg_distribuir],[flg_distribuido],[cod_TipoRegistro]
			  ,[num_tcmc],[num_debemc],[num_habermc],[cod_tercero],[flg_im]
			  ,[fec_fregistro],[cod_uregistro],[fec_factualiza],[cod_uactualiza])
		   (select
			  b.[txt_anoproceso]
			  ,@cod_filial
			  ,b.[cod_mes]
			  ,@cod_origen
			  ,@cod_comprobante
			  ,b.[num_item]*2-1
			  ,b.fec_fecha
			  ,b.[cod_tipomoneda]
			  ,b.[txt_glosaitem]
			  ,b.[cod_destino]
			  ,b.[txt_cheque]
			  ,'0' --!!!!corregir cuando se anade a scp_bancodetalle [flg_chequecobrado]
			  ,'' ----!!!!corregir cuando se anade a scp_bancodetalle [cod_mescobr]
			  ,b.[cod_tipocomprobantepago]
			  ,b.[txt_seriecomprobantepago]
			  ,b.[txt_comprobantepago]
			  ,b.[fec_comprobantepago]
			  , Convert(date, '01/01/1900', 103)--[fec_pagocomprobantepago]
			  ,''--[cod_reftipocomprobantepago]
			  ,''--[txt_refseriecomprobantepago]
			  ,''--[txt_refcomprobantepago]
			  ,Convert(date, '01/01/1900', 103) --[fec_refcomprobantepago]
			  ,''--[cod_registrocompraventa]
			  ,''--[cod_evento]
			  ,0 --[num_refnroitem]
			  ,'' --[cod_reforigen]
			  ,'' --[cod_refcomprobante]
			  ,Convert(date, '01/01/1900', 103) --[fec_refcomprobante]
			  ,b.[cod_proyecto]
			  ,b.[cod_ctaproyecto]
			  ,b.[cod_contracta] --[cod_ctacontable]
			  ,'' --[cod_ctacontable9]
			  ,'' --[cod_ctacontable79]
			  ,'' --[cod_ctaarea]
			  ,'' --[cod_ctaactividad]
			  ,b.[cod_ctaespecial]
			  ,b.[cod_financiera]
			  ,'' --[cod_flujocaja]
			  ,@num_tc_usd --[num_tcvdolar]
			  ,b.[num_haberdolar]*@num_tc_usd
			  ,b.[num_debedolar]*@num_tc_usd
			  ,b.[num_haberdolar]
			  ,b.[num_debedolar]
			  ,0 --[num_tcmo]
			  ,0--[num_debemo]
			  ,0--[num_habermo]
			  ,'' --[cod_monedaoriginal]
			  ,0--[flg_tcreferencia]
			  ,0--[flg_conversion]
			  ,'' --[cod_pais]
			  ,'' --[cod_departamento]
			  ,'1'--[flg_recuperaigv]
			  ,0--[por_igv]
			  ,0--[por_ies]
			  ,1--[num_nroitem2]
			  ,b.[cod_contraparte]
			  ,'' --[txt_nroretencion]
			  ,Convert(date, '01/01/1900', 103)--[fec_retencion]
			  ,'0' --[flg_esactivo]
			  ,'' --[txt_NroCompSujNoDomi]
			  ,'1' --[flg_RetieneCuarta]
			  ,'' --[cod_gastofijo]
			  ,'' --[flg_distribuir]
			  ,'' --[flg_distribuido]
			  ,'' --[cod_TipoRegistro]
			  ,0--[num_tcmc]
			  ,0--[num_debemc]
			  ,0--[num_habermc]
			  ,b.[cod_tercero]
			  ,'2' --[flg_im]
			  ,GETDATE()
			  ,@user
			  ,GETDATE()
			  ,@user
		  FROM dbo.scp_bancodetalle b
		  where b.cod_bancocabecera=@cod_bancocabecera
		  union all
		  select --insertar linea del banco cta 104 o 106..
			 bd.[txt_anoproceso]
			  ,@cod_filial
			  ,bd.[cod_mes]
			  ,@cod_origen
			  ,@cod_comprobante
			  ,[num_item]*2
			  ,bd.fec_fecha
			  ,bd.[cod_tipomoneda]
			  ,bd.[txt_glosaitem]
			  ,bd.[cod_destino]
			  ,bd.[txt_cheque]
			  ,'0' --!!!!corregir cuando se anade a scp_bancodetalle [flg_chequecobrado]
			  ,'' ----!!!!corregir cuando se anade a scp_bancodetalle [cod_mescobr]
			  ,bd.[cod_tipocomprobantepago]
			  ,bd.[txt_seriecomprobantepago]
			  ,bd.[txt_comprobantepago]
			  ,bd.[fec_comprobantepago]
			  , Convert(date, '01/01/1900', 103)--[fec_pagocomprobantepago]
			  ,''--[cod_reftipocomprobantepago]
			  ,''--[txt_refseriecomprobantepago]
			  ,''--[txt_refcomprobantepago]
			  ,Convert(date, '01/01/1900', 103) --[fec_refcomprobantepago]
			  ,''--[cod_registrocompraventa]
			  ,''--[cod_evento]
			  ,0 --[num_refnroitem]
			  ,'' --[cod_reforigen]
			  ,'' --[cod_refcomprobante]
			  ,Convert(date, '01/01/1900', 103) --[fec_refcomprobante]
			  ,bd.[cod_proyecto]
			  ,bd.[cod_ctaproyecto]
			  ,bc.[cod_ctacontable]  --cod_ctacontable
			  ,'' --[cod_ctacontable9]
			  ,'' --[cod_ctacontable79]
			  ,'' --[cod_ctaarea]
			  ,'' --[cod_ctaactividad]
			  ,bd.[cod_ctaespecial]
			  ,bd.[cod_financiera]
			  ,'' --[cod_flujocaja]
			  ,@num_tc_usd --[num_tcvdolar]
			  ,bd.[num_debedolar]*@num_tc_usd
			  ,bd.[num_haberdolar]*@num_tc_usd
			    ,bd.[num_debedolar]--[num_debedolar]
			  ,bd.[num_haberdolar]
			  ,0--@num_tc_mo
			  ,0--@num_haber_mo
			  ,0-- @num_debe_mo
			  ,'' --[cod_monedaoriginal]
			  ,0--[flg_tcreferencia]
			  ,0--[flg_conversion]
			  ,'' --[cod_pais]
			  ,'' --[cod_departamento]
			  ,'1'--[flg_recuperaigv]
			  ,0--[por_igv]
			  ,0--[por_ies]
			  ,1--[num_nroitem2]
			  ,bd.[cod_contraparte]
			  ,'' --[txt_nroretencion]
			  ,Convert(date, '01/01/1900', 103)--[fec_retencion]
			  ,'0' --[flg_esactivo]
			  ,'' --[txt_NroCompSujNoDomi]
			  ,'1' --[flg_RetieneCuarta]
			  ,'' --[cod_gastofijo]
			  ,'' --[flg_distribuir]
			  ,'' --[flg_distribuido]
			  ,'' --[cod_TipoRegistro]
			  ,0--[num_tcmc]
			  ,0--[num_debemc]
			  ,0--[num_habermc]
			  ,bd.[cod_tercero]
			  ,'2' --[flg_im]
			  ,GETDATE()
			  ,@user
			  ,GETDATE()
			  ,@user
		  FROM dbo.scp_bancodetalle bd, dbo.scp_bancocabecera bc
		  where bd.cod_bancocabecera=@cod_bancocabecera
		  and bc.cod_bancocabecera =bd.cod_bancocabecera )
		end

		else if(@cod_moneda='2') --Moneda EUR
			begin
			SELECT @ErrorStep = 'No existe tipo de cambio EUR';
			select @test=1/@num_tc_mo
		   SELECT  @ErrorStep = 'Error al insertar lineas de detalle de comprobante'

				  insert into dbo.scp_comprobantedetalle(
		  [txt_anoproceso],[cod_filial],[cod_mes],[cod_origen],[cod_comprobante],[num_nroitem]
			  ,[fec_comprobante],[cod_tipomoneda],[txt_glosaitem],[cod_destino],[txt_cheque]
			  ,[flg_chequecobrado],[cod_mescobr],[cod_tipocomprobantepago],[txt_seriecomprobantepago]
			  ,[txt_comprobantepago],[fec_comprobantepago],[fec_pagocomprobantepago],[cod_reftipocomprobantepago]
			  ,[txt_refseriecomprobantepago],[txt_refcomprobantepago],[fec_refcomprobantepago]
			  ,[cod_registrocompraventa],[cod_evento],[num_refnroitem],[cod_reforigen]
			  ,[cod_refcomprobante],[fec_refcomprobante],[cod_proyecto],[cod_ctaproyecto]
			  ,[cod_ctacontable],[cod_ctacontable9],[cod_ctacontable79],[cod_ctaarea]
			  ,[cod_ctaactividad],[cod_ctaespecial],[cod_financiera],[cod_flujocaja]
			  ,[num_tcvdolar],[num_debesol],[num_habersol],[num_debedolar],[num_haberdolar]
			  ,[num_tcmo],[num_debemo],[num_habermo],[cod_monedaoriginal],[flg_tcreferencia]
			  ,[flg_conversion],[cod_pais],[cod_departamento],[flg_recuperaigv],[por_igv]
			  ,[por_ies],[num_nroitem2],[cod_contraparte],[txt_nroretencion],[fec_retencion]
			  ,[flg_esactivo],[txt_NroCompSujNoDomi],[flg_RetieneCuarta],[cod_gastofijo]
			  ,[flg_distribuir],[flg_distribuido],[cod_TipoRegistro]
			  ,[num_tcmc],[num_debemc],[num_habermc],[cod_tercero],[flg_im]
			  ,[fec_fregistro],[cod_uregistro],[fec_factualiza],[cod_uactualiza])
		   (select
			  b.[txt_anoproceso]
			  ,@cod_filial
			  ,b.[cod_mes]
			  ,@cod_origen
			  ,@cod_comprobante
			  ,b.[num_item]*2-1
			  ,b.fec_fecha
			  ,b.[cod_tipomoneda]
			  ,b.[txt_glosaitem]
			  ,b.[cod_destino]
			  ,b.[txt_cheque]
			  ,'0' --!!!!corregir cuando se anade a scp_bancodetalle [flg_chequecobrado]
			  ,'' ----!!!!corregir cuando se anade a scp_bancodetalle [cod_mescobr]
			  ,b.[cod_tipocomprobantepago]
			  ,b.[txt_seriecomprobantepago]
			  ,b.[txt_comprobantepago]
			  ,b.[fec_comprobantepago]
			  , Convert(date, '01/01/1900', 103)--[fec_pagocomprobantepago]
			  ,''--[cod_reftipocomprobantepago]
			  ,''--[txt_refseriecomprobantepago]
			  ,''--[txt_refcomprobantepago]
			  ,Convert(date, '01/01/1900', 103) --[fec_refcomprobantepago]
			  ,''--[cod_registrocompraventa]
			  ,''--[cod_evento]
			  ,0 --[num_refnroitem]
			  ,'' --[cod_reforigen]
			  ,'' --[cod_refcomprobante]
			  ,Convert(date, '01/01/1900', 103) --[fec_refcomprobante]
			  ,b.[cod_proyecto]
			  ,b.[cod_ctaproyecto]
			  ,b.[cod_contracta] --[cod_ctacontable]
			  ,'' --[cod_ctacontable9]
			  ,'' --[cod_ctacontable79]
			  ,'' --[cod_ctaarea]
			  ,'' --[cod_ctaactividad]
			  ,b.[cod_ctaespecial]
			  ,b.[cod_financiera]
			  ,'' --[cod_flujocaja]
			  ,@num_tc_usd --[num_tcvdolar]
			,([num_debemo]*@num_tc_mo)
			  ,([num_habermo]*@num_tc_mo)
			  ,([num_debemo]*@num_tc_mo)/@num_tc_usd
				  ,([num_habermo]*@num_tc_mo)/@num_tc_usd
				  ,@num_tc_mo
				  ,[num_debemo]
				  ,[num_habermo]
			  ,'' --[cod_monedaoriginal]
			  ,0--[flg_tcreferencia]
			  ,0--[flg_conversion]
			  ,'' --[cod_pais]
			  ,'' --[cod_departamento]
			  ,'1'--[flg_recuperaigv]
			  ,0--[por_igv]
			  ,0--[por_ies]
			  ,1--[num_nroitem2]
			  ,b.[cod_contraparte]
			  ,'' --[txt_nroretencion]
			  ,Convert(date, '01/01/1900', 103)--[fec_retencion]
			  ,'0' --[flg_esactivo]
			  ,'' --[txt_NroCompSujNoDomi]
			  ,'1' --[flg_RetieneCuarta]
			  ,'' --[cod_gastofijo]
			  ,'' --[flg_distribuir]
			  ,'' --[flg_distribuido]
			  ,'' --[cod_TipoRegistro]
			  ,0--[num_tcmc]
			  ,0--[num_debemc]
			  ,0--[num_habermc]
			  ,b.[cod_tercero]
			  ,'2' --[flg_im]
			  ,GETDATE()
			  ,@user
			  ,GETDATE()
			  ,@user
		  FROM dbo.scp_bancodetalle b
		  where b.cod_bancocabecera=@cod_bancocabecera
		  union all
		  select --insertar linea del banco cta 104 o 106..
			 bd.[txt_anoproceso]
			  ,@cod_filial
			  ,bd.[cod_mes]
			  ,@cod_origen
			  ,@cod_comprobante
			  ,[num_item]*2
			  ,bd.fec_fecha
			  ,bd.[cod_tipomoneda]
			  ,bd.[txt_glosaitem]
			  ,bd.[cod_destino]
			  ,bd.[txt_cheque]
			  ,'0' --!!!!corregir cuando se anade a scp_bancodetalle [flg_chequecobrado]
			  ,'' ----!!!!corregir cuando se anade a scp_bancodetalle [cod_mescobr]
			  ,bd.[cod_tipocomprobantepago]
			  ,bd.[txt_seriecomprobantepago]
			  ,bd.[txt_comprobantepago]
			  ,bd.[fec_comprobantepago]
			  , Convert(date, '01/01/1900', 103)--[fec_pagocomprobantepago]
			  ,''--[cod_reftipocomprobantepago]
			  ,''--[txt_refseriecomprobantepago]
			  ,''--[txt_refcomprobantepago]
			  ,Convert(date, '01/01/1900', 103) --[fec_refcomprobantepago]
			  ,''--[cod_registrocompraventa]
			  ,''--[cod_evento]
			  ,0 --[num_refnroitem]
			  ,'' --[cod_reforigen]
			  ,'' --[cod_refcomprobante]
			  ,Convert(date, '01/01/1900', 103) --[fec_refcomprobante]
			  ,bd.[cod_proyecto]
			  ,bd.[cod_ctaproyecto]
			  ,bc.[cod_ctacontable]  --cod_ctacontable
			  ,'' --[cod_ctacontable9]
			  ,'' --[cod_ctacontable79]
			  ,'' --[cod_ctaarea]
			  ,'' --[cod_ctaactividad]
			  ,bd.[cod_ctaespecial]
			  ,bd.[cod_financiera]
			  ,'' --[cod_flujocaja]
			  ,@num_tc_usd --[num_tcvdolar]
				  ,(bd.[num_habermo]*@num_tc_mo)
				  ,(bd.[num_debemo]*@num_tc_mo)
				  ,(bd.[num_habermo]*@num_tc_mo)/@num_tc_usd
				  ,(bd.[num_debemo]*@num_tc_mo)/@num_tc_usd
				  ,@num_tc_mo
				  ,bd.[num_habermo]
				  ,bd.[num_debemo]
			  ,'' --[cod_monedaoriginal]
			  ,0--[flg_tcreferencia]
			  ,0--[flg_conversion]
			  ,'' --[cod_pais]
			  ,'' --[cod_departamento]
			  ,'1'--[flg_recuperaigv]
			  ,0--[por_igv]
			  ,0--[por_ies]
			  ,1--[num_nroitem2]
			  ,bd.[cod_contraparte]
			  ,'' --[txt_nroretencion]
			  ,Convert(date, '01/01/1900', 103)--[fec_retencion]
			  ,'0' --[flg_esactivo]
			  ,'' --[txt_NroCompSujNoDomi]
			  ,'1' --[flg_RetieneCuarta]
			  ,'' --[cod_gastofijo]
			  ,'' --[flg_distribuir]
			  ,'' --[flg_distribuido]
			  ,'' --[cod_TipoRegistro]
			  ,0--[num_tcmc]
			  ,0--[num_debemc]
			  ,0--[num_habermc]
			  ,bd.[cod_tercero]
			  ,'2' --[flg_im]
			  ,GETDATE()
			  ,@user
			  ,GETDATE()
			  ,@user
		  FROM dbo.scp_bancodetalle bd, dbo.scp_bancocabecera bc
		  where bd.cod_bancocabecera=@cod_bancocabecera
		  and bc.cod_bancocabecera =bd.cod_bancocabecera )

		end


		/****************************************************************************
        * Step 6
        * Actualiza vsj_cajabanco cuando ya esta en contabilidad la operacion
        ****************************************************************************/
        SELECT  @ErrorStep = 'Error al actualizar el registro de caja'
		 update dbo.scp_bancocabecera
		 set [flg_enviado]='1'
			  ,[cod_origenenlace]=@cod_origen
			  ,[cod_comprobanteenlace]=@cod_comprobante
			  ,[fec_factualiza]=GETDATE()
			  ,[cod_uactualiza]=@user
		 where cod_bancocabecera =@cod_bancocabecera

 COMMIT TRANSACTION

    SELECT  @ErrorCode  = 0, @Return_Message = 'La operacion ha sido enviada a contabilidad correctamente'

    /*************************************
    *  Return from the Stored Procedure
    *************************************/
    RETURN @ErrorCode                               -- =0 if success,  <>0 if failure

END TRY

BEGIN CATCH
    /*************************************
    *  Get the Error Message for @@Error
    *************************************/
    IF @@TRANCOUNT > 0 ROLLBACK

    SELECT @ErrorCode = ERROR_NUMBER()
        , @Return_Message = @ErrorStep + ' '
        + cast(ERROR_NUMBER() as varchar(20)) + ' linia: '
        + cast(ERROR_LINE() as varchar(20)) + ' '
        + ERROR_MESSAGE() + ' > '
        + ERROR_PROCEDURE()

Print @ErrorCode+' '+@Return_Message

    /*************************************
    *  Return from the Stored Procedure
    *************************************/
    RETURN @ErrorCode                               -- =0 if success,  <>0 if failure

END CATCH


/*
Exec usp_scp_vsj_enviarAContabilidadBanco 10442,'abork','01/09/2016','0',0
*/
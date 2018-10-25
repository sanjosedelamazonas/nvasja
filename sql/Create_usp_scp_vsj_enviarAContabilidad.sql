USE [SCP]
GO
/****** Object:  StoredProcedure [dbo].[usp_scp_vsj_enviarAContabilidad]    Script Date: 10/25/2018 07:52:37 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[usp_scp_vsj_enviarAContabilidad]
	@cod_cajabanco int, --id de operacion de vsj_cajabanco
	@user varchar(15), -- nombre del usuario segun SCP
	@fecha_operacion varchar(10),-- fecha de operacion en formato 'dd/mm/yyyy'
	@cod_moneda char(1), -- 0 pen, 1 usd, 2 eur
	@num_debe_input decimal(12,2), -- el monto de debe en la moneda original
	@num_haber_input decimal(12,2), -- el monto de haber en la moneda original
	@codigo varchar(6), -- codigo de proyecto o de tercero
	@Return_Message VARCHAR(1024) = ''  OUT
AS
  SET NOCOUNT ON;

declare @cod_comprobante varchar(6)
declare @cod_origen varchar(6)
declare @cod_filial varchar(2)
declare @num_debe_pen decimal(12,2)
declare @num_haber_pen decimal(12,2)
declare @num_debe_usd decimal(12,2)
declare @num_haber_usd decimal(12,2)
declare @num_debe_mo decimal(12,2)
declare @num_haber_mo decimal(12,2)
declare @num_tc_usd float
declare @num_tc_mo float
declare @test float
declare @txt_mes varchar(2)
declare @txt_ano varchar(4)
declare @txt_anadir varchar(6)

DECLARE @ErrorCode  int
DECLARE @ErrorStep  varchar(200)


set @cod_origen='01'
set @cod_filial='01'
set @num_debe_pen=0.00
set @num_haber_pen=0.00
set @num_debe_usd=0.00
set @num_haber_usd=0.00
set @num_debe_mo=0.00
set @num_haber_mo=0.00
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
        SELECT @ErrorStep = 'Error en buscar tipo de cambio USD';
        select @num_tc_usd=isnull(num_tcvdolar,0) from dbo.scp_tipocambio
		where fec_fechacambio= Convert(date, @fecha_operacion, 103);
		--select @test=1/@num_tc_usd

		SELECT @ErrorStep = 'Error en buscar tipo de cambio EUR';
		select @num_tc_mo=isnull(num_tcveuro,0) from dbo.scp_tipocambio
		where fec_fechacambio= Convert(date, @fecha_operacion, 103);

		if (@cod_moneda='0') -- Moneda PEN
			begin
			SELECT  @ErrorStep = 'Error al calcular los montos en PEN'
			select @num_debe_pen=@num_debe_input,
				@num_haber_pen=@num_haber_input,
				@num_debe_mo=0,
				@num_haber_mo=0
				if(@num_tc_usd>0)
					begin
					select @num_debe_usd=(@num_debe_input/@num_tc_usd),
					@num_haber_usd=(@num_haber_input/@num_tc_usd)
					end
				else
				select @num_debe_usd=0,
					@num_haber_usd=0
			end
		else
			if(@cod_moneda='1') --Moneda USD
			begin
			SELECT  @ErrorStep = 'Error al calcular los montos en USD'
			select @num_debe_usd= @num_debe_input,
				@num_haber_usd=@num_haber_input,
				@num_debe_mo=0,
				@num_haber_mo=0
				if (@num_tc_usd>0)
				select  @num_debe_pen=(@num_debe_input*@num_tc_usd),
				@num_haber_pen=(@num_haber_input*@num_tc_usd)
				else
				select @num_debe_pen=0,
				@num_haber_pen=0
			end
		else if(@cod_moneda='2') --Moneda EUR
			begin
			SELECT  @ErrorStep = 'Error al calcular los montos en EUR'
			select @num_debe_mo= @num_debe_input,
				@num_haber_mo=@num_haber_input,
				@num_debe_usd=0,
				@num_haber_usd=0
				if (@num_tc_mo>0)
				select  @num_debe_pen=(@num_debe_input*@num_tc_mo),
				@num_haber_pen=(@num_haber_input*@num_tc_mo)
				else
				select @num_debe_pen=0,
				@num_haber_pen=0
			end
	    /****************************************************************************
        * Step 2
        * Busca el numero para nuevo comprobante contable y codigo para anadir en glosa
        ****************************************************************************/

		Select @cod_comprobante=(REPLICATE('0',6-LEN(RTRIM(@cod_cajabanco)))) + RTRIM(Isnull((@cod_cajabanco),0))

-- 		Select @cod_comprobante=(REPLICATE('0',6-LEN(RTRIM(Isnull(Max(cod_comprobante),0)+1))) + RTRIM(Isnull(Max(cod_comprobante),0)+1))
--   		From scp_comprobantecabecera
-- 				Where Ltrim(txt_anoproceso) = @txt_ano And
-- 				Ltrim(cod_mes) = @txt_mes And
-- 				Ltrim(cod_origen) = @cod_origen
--
		/****************************************************************************
        * Step 3
        * Inserta cabezera del comprobante
        ****************************************************************************/
		SELECT  @ErrorStep = 'Error al insertar cabezera de comprobante'
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
			  ,isnull(@cod_filial,'')
			  ,isnull([cod_mes],'')
			  ,isnull(@cod_origen,'')
			  ,@cod_comprobante
			  , Convert(date, fec_fecha, 103)
			  ,''--[cod_tipooperacion]
			  ,''--[cod_mediopago]
			  ,[cod_tipomoneda]
			  ,left(@codigo+' - '+[txt_glosaitem],70)
			  ,isnull([cod_destino],'')
			  ,''--[cod_banco]
			  ,isnull([cod_ctacontable],'')
			  ,'1'--[flg_im]
			  ,GETDATE()
			  ,@user
			  ,GETDATE()
			  ,@user
		  FROM dbo.vsj_cajabanco
		  where cod_cajabanco=@cod_cajabanco)

		/****************************************************************************
        * Step 4
        * Inserta la primera fila linia del comprobante - para cuenta de caja (101..)
        ****************************************************************************/
        SELECT  @ErrorStep = 'Error al insertar linea 1 de detalle de comprobante'
		  insert into dbo.scp_comprobantedetalle(
		  [txt_anoproceso]
			  ,[cod_filial]
			  ,[cod_mes]
			  ,[cod_origen]
			  ,[cod_comprobante]
			  ,[num_nroitem]
			  ,[fec_comprobante]
			  ,[cod_tipomoneda]
			  ,[txt_glosaitem]
			  ,[cod_destino]
			  ,[txt_cheque]
			  ,[flg_chequecobrado]
			  ,[cod_mescobr]
			  ,[cod_tipocomprobantepago]
			  ,[txt_seriecomprobantepago]
			  ,[txt_comprobantepago]
			  ,[fec_comprobantepago]
			  ,[fec_pagocomprobantepago]
			  ,[cod_reftipocomprobantepago]
			  ,[txt_refseriecomprobantepago]
			  ,[txt_refcomprobantepago]
			  ,[fec_refcomprobantepago]
			  ,[cod_registrocompraventa]
			  ,[cod_evento]
			  ,[num_refnroitem]
			  ,[cod_reforigen]
			  ,[cod_refcomprobante]
			  ,[fec_refcomprobante]
			  ,[cod_proyecto]
			  ,[cod_ctaproyecto]
			  ,[cod_ctacontable]
			  ,[cod_ctacontable9]
			  ,[cod_ctacontable79]
			  ,[cod_ctaarea]
			  ,[cod_ctaactividad]
			  ,[cod_ctaespecial]
			  ,[cod_financiera]
			  ,[cod_flujocaja]
			  ,[num_tcvdolar]
			  ,[num_debesol]
			  ,[num_habersol]
			  ,[num_debedolar]
			  ,[num_haberdolar]
			  ,[num_tcmo]
			  ,[num_debemo]
			  ,[num_habermo]
			  ,[cod_monedaoriginal]
			  ,[flg_tcreferencia]
			  ,[flg_conversion]
			  ,[cod_pais]
			  ,[cod_departamento]
			  ,[flg_recuperaigv]
			  ,[por_igv]
			  ,[por_ies]
			  ,[num_nroitem2]
			  ,[cod_contraparte]
			  ,[txt_nroretencion]
			  ,[fec_retencion]
			  ,[flg_esactivo]
			  ,[txt_NroCompSujNoDomi]
			  ,[flg_RetieneCuarta]
			  ,[cod_gastofijo]
			  ,[flg_distribuir]
			  ,[flg_distribuido]
			  ,[cod_TipoRegistro]
			  ,[num_tcmc]
			  ,[num_debemc]
			  ,[num_habermc]
			  ,[cod_tercero]
			  ,[flg_im]
			  ,[fec_fregistro]
			  ,[cod_uregistro]
			  ,[fec_factualiza]
			  ,[cod_uactualiza])
		  (select
			 [txt_anoproceso]
			  ,@cod_filial
			  ,[cod_mes]
			  ,@cod_origen
			  ,@cod_comprobante
			  ,1 --[num_nroitem]
			  ,fec_fecha
			  ,[cod_tipomoneda]
			  ,isnull([txt_glosaitem],'')
			  ,isnull([cod_destino],'')
			  ,'' --[txt_cheque]
			  ,'0' --[flg_chequecobrado]
			  ,'' --[cod_mescobr]
			  ,isnull([cod_tipocomprobantepago],'')
			  ,isnull([txt_seriecomprobantepago],'')
			  ,isnull([txt_comprobantepago],'')
			  ,isnull([fec_comprobantepago],'')
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
			  ,isnull([cod_proyecto],'')
			  ,isnull([cod_ctaproyecto],'')
			  ,isnull([cod_ctacontable],'')
			  ,'' --[cod_ctacontable9]
			  ,'' --[cod_ctacontable79]
			  ,'' --[cod_ctaarea]
			  ,'' --[cod_ctaactividad]
			  ,isnull([cod_ctaespecial],'')
			  ,isnull([cod_financiera],'')
			  ,'' --[cod_flujocaja]
			  ,@num_tc_usd --[num_tcvdolar]
			  ,@num_debe_pen --[num_debesol]
			  ,@num_haber_pen --[num_habersol]
			  ,@num_debe_usd --[num_debedolar]
			  ,@num_haber_usd --[num_haberdolar]
			  ,@num_tc_mo
			  ,@num_debe_mo
			  ,@num_haber_mo
			  ,'' --[cod_monedaoriginal]
			  ,0--[flg_tcreferencia]
			  ,0--[flg_conversion]
			  ,'' --[cod_pais]
			  ,'' --[cod_departamento]
			  ,'1'--[flg_recuperaigv]
			  ,0--[por_igv]
			  ,0--[por_ies]
			  ,1--[num_nroitem2]
			  ,isnull([cod_contraparte],'')
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
			  ,isnull([cod_tercero],'')
			  ,'2' --[flg_im]
			  ,GETDATE()
			  ,@user
			  ,GETDATE()
			  ,@user
		  FROM dbo.vsj_cajabanco
		  where cod_cajabanco=@cod_cajabanco)
		/****************************************************************************
        * Step 5
        * Inserta la segunda fila linia del comprobante - hay que invvertir debe y haber
        ****************************************************************************/
        SELECT  @ErrorStep = 'Error al insertar linea 2 de detalle de comprobante'
		insert into dbo.scp_comprobantedetalle(
		  [txt_anoproceso]
			  ,[cod_filial]
			  ,[cod_mes]
			  ,[cod_origen]
			  ,[cod_comprobante]
			  ,[num_nroitem]
			  ,[fec_comprobante]
			  ,[cod_tipomoneda]
			  ,[txt_glosaitem]
			  ,[cod_destino]
			  ,[txt_cheque]
			  ,[flg_chequecobrado]
			  ,[cod_mescobr]
			  ,[cod_tipocomprobantepago]
			  ,[txt_seriecomprobantepago]
			  ,[txt_comprobantepago]
			  ,[fec_comprobantepago]
			  ,[fec_pagocomprobantepago]
			  ,[cod_reftipocomprobantepago]
			  ,[txt_refseriecomprobantepago]
			  ,[txt_refcomprobantepago]
			  ,[fec_refcomprobantepago]
			  ,[cod_registrocompraventa]
			  ,[cod_evento]
			  ,[num_refnroitem]
			  ,[cod_reforigen]
			  ,[cod_refcomprobante]
			  ,[fec_refcomprobante]
			  ,[cod_proyecto]
			  ,[cod_ctaproyecto]
			  ,[cod_ctacontable]
			  ,[cod_ctacontable9]
			  ,[cod_ctacontable79]
			  ,[cod_ctaarea]
			  ,[cod_ctaactividad]
			  ,[cod_ctaespecial]
			  ,[cod_financiera]
			  ,[cod_flujocaja]
			  ,[num_tcvdolar]
			  ,[num_debesol]
			  ,[num_habersol]
			  ,[num_debedolar]
			  ,[num_haberdolar]
			  ,[num_tcmo]
			  ,[num_debemo]
			  ,[num_habermo]
			  ,[cod_monedaoriginal]
			  ,[flg_tcreferencia]
			  ,[flg_conversion]
			  ,[cod_pais]
			  ,[cod_departamento]
			  ,[flg_recuperaigv]
			  ,[por_igv]
			  ,[por_ies]
			  ,[num_nroitem2]
			  ,[cod_contraparte]
			  ,[txt_nroretencion]
			  ,[fec_retencion]
			  ,[flg_esactivo]
			  ,[txt_NroCompSujNoDomi]
			  ,[flg_RetieneCuarta]
			  ,[cod_gastofijo]
			  ,[flg_distribuir]
			  ,[flg_distribuido]
			  ,[cod_TipoRegistro]
			  ,[num_tcmc]
			  ,[num_debemc]
			  ,[num_habermc]
			  ,[cod_tercero]
			  ,[flg_im]
			  ,[fec_fregistro]
			  ,[cod_uregistro]
			  ,[fec_factualiza]
			  ,[cod_uactualiza])
		  (select
			 [txt_anoproceso]
			 	,@cod_filial
			  ,[cod_mes]
			  ,@cod_origen
			  ,@cod_comprobante
			  ,2 --[num_nroitem]
			  ,fec_fecha
			  ,[cod_tipomoneda]
			  ,isnull([txt_glosaitem],'')
			  ,isnull([cod_destino],'')
			  ,'' --[txt_cheque]
			  ,'0' --[flg_chequecobrado]
			  ,'' --[cod_mescobr]
			  ,isnull([cod_tipocomprobantepago],'')
			  ,isnull([txt_seriecomprobantepago],'')
			  ,isnull([txt_comprobantepago],'')
			  ,isnull([fec_comprobantepago],'')
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
			  ,isnull([cod_proyecto],'')
			  ,isnull([cod_ctaproyecto],'')
			  ,[cod_contracta]--[cod_ctacontable]
			  ,'' --[cod_ctacontable9]
			  ,'' --[cod_ctacontable79]
			  ,'' --[cod_ctaarea]
			  ,'' --[cod_ctaactividad]
			  ,isnull([cod_ctaespecial],'')
			  ,isnull([cod_financiera],'')
			  ,'' --[cod_flujocaja]
			  ,@num_tc_usd --[num_tcvdolar]
			  ,@num_haber_pen--[num_debesol]
			  ,@num_debe_pen  --[num_habersol]
			  ,@num_haber_usd  --[num_debedolar]
			  ,@num_debe_usd--[num_haberdolar]
			  ,@num_tc_mo
			  ,@num_haber_mo
			  ,@num_debe_mo
			  ,'' --[cod_monedaoriginal]
			  ,0--[flg_tcreferencia]
			  ,0--[flg_conversion]
			  ,'' --[cod_pais]
			  ,'' --[cod_departamento]
			  ,'1'--[flg_recuperaigv]
			  ,0--[por_igv]
			  ,0--[por_ies]
			  ,2--[num_nroitem2]
			  ,isnull([cod_contraparte],'')
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
			  ,isnull([cod_tercero],'')
			  ,'2' --[flg_im]
			  ,GETDATE()
			  ,@user
			  ,GETDATE()
			  ,@user
		  FROM dbo.vsj_cajabanco
		  where cod_cajabanco=@cod_cajabanco )
		/****************************************************************************
        * Step 6
        * Actualiza vsj_cajabanco cuando ya esta en contabilidad la operacion
        ****************************************************************************/
        SELECT  @ErrorStep = 'Error al actualizar el registro de caja'
		 update dbo.vsj_cajabanco
		 set [flg_enviado]='1'
			  ,[cod_origenenlace]=@cod_origen
			  ,[cod_comprobanteenlace]=@cod_comprobante
			  ,[fec_factualiza]=GETDATE()
			  ,[cod_uactualiza]=@user
		 where cod_cajabanco=@cod_cajabanco

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
Exec usp_scp_vsj_enviarAContabilidad 25557,'abork','24/08/2016','1',0,815,'170410',0
*/

USE [SCP]
GO
/****** Object:  StoredProcedure [dbo].[usp_scp_vsj_enviarAContabilidadRendicion]    Script Date: 15/03/2023 23:35:18 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
ALTER PROCEDURE [dbo].[usp_scp_vsj_enviarAContabilidadRendicion]
	@cod_rendicioncabecera int, --id de operacion de scp_rendicioncabecera
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
declare @cod_mescobr char(2)
declare @flg_chequecobrado char(1)

DECLARE @ErrorCode  int
DECLARE @ErrorStep  varchar(200)

set @cod_mescobr=''
set @cod_origen='08'
set @cod_filial='01'
set @num_tc_usd=0.00
set @num_tc_mo=0.00

set @num_tc_usd=0.00
set @num_tc_mo=0.00

set @txt_ano=SUBSTRING(@fecha_operacion,7,4)
set @txt_mes=SUBSTRING(@fecha_operacion,4,2)

select @ErrorCode = @@ERROR

BEGIN TRY

 --   BEGIN TRANSACTION
        /****************************************************************************
        * Step 1
        * Busca tipo de cambio para el dia de la operacion
        * Calcula los montos en dolares y soles, segun la moneda de ingreso y tipo de cambio del dia
        ****************************************************************************/

        select @num_tc_usd=isnull(num_tcvdolar,0), @num_tc_mo=isnull(num_tcveuro,0)
        from dbo.scp_tipocambio
        where fec_fechacambio= Convert(date, @fecha_operacion, 103)

        SELECT @ErrorStep = 'No existe tipo de cambio USD';
        --select @test=1/@num_tc_usd
        -- Get mes cobrado y flg_chequecobrado
       /* SELECT
          @cod_mescobr=isnull(cod_mescobrado,''),
          @flg_chequecobrado=case when isnull(ind_cobrado,0)=1 then '1' else '0' end
        FROM dbo.scp_rendicioncabecera where cod_rendicioncabecera=@cod_rendiconcabecera; */
		/****************************************************************************
        * Step 2
        * Busca el numero para nuevo comprobante contable
        ****************************************************************************/
    Select @cod_comprobante=(REPLICATE('0',6-LEN(RTRIM(@cod_rendicioncabecera)))) + RTRIM(Isnull((@cod_rendicioncabecera),0))

		/*Select @cod_comprobante=(REPLICATE('0',6-LEN(RTRIM(Isnull(Max(cod_comprobante),0)+1))) + RTRIM(Isnull(Max(cod_comprobante),0)+1))
  		From scp_comprobantecabecera
				Where Ltrim(txt_anoproceso) = @txt_ano And
				Ltrim(cod_mes) = @txt_mes And
				Ltrim(cod_origen) = @cod_origen
*/
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
			  ,Convert(date, fec_comprobante, 103)
			  ,''--[cod_tipooperacion]
			  ,''--[cod_mediopago]
			  ,[cod_tipomoneda]
			  ,[txt_glosa]
			  ,[cod_destino]
			  ,''--[cod_banco]
			  ,''--[cod_ctacontable]
			  ,'1'--[flg_im]
			  ,GETDATE()
			  ,@user
			  ,GETDATE()
			  ,@user
		  FROM dbo.scp_rendicioncabecera
		  where cod_rendicioncabecera=@cod_rendicioncabecera)




		/****************************************************************************
        * Step 4a)
        * Inserta la linias del comprobante  PEN
        ****************************************************************************/
      SELECT  @ErrorStep = 'Error al insertar lineas de detalle de comprobante'

    if (@cod_moneda='0') -- Moneda PEN
			begin

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
			  ,[num_nroitem]
			  ,fec_comprobante
			  ,[cod_tipomoneda]
			  ,isnull([txt_glosaitem],'')
			  ,isnull([cod_destino],'')
			  ,isnull([txt_cheque],'')
			  ,0
			  ,''
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
			  ,[num_debesol]
    		  ,[num_habersol]
			  ,case when @num_tc_usd>0 then [num_debesol]/@num_tc_usd else 0 end
			  ,case when @num_tc_usd>0 then [num_habersol]/@num_tc_usd else 0 end
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
        ,''--isnull([cod_tercero],'')
			  ,'2' --[flg_im]
			  ,GETDATE()
			  ,@user
			  ,GETDATE()
			  ,@user
		  FROM dbo.scp_rendiciondetalle
		  where cod_rendicioncabecera=@cod_rendicioncabecera )

	end

		/****************************************************************************
        * Step 5a)  USD
        * Inserta primera linea del comprobante USD
        ****************************************************************************/

		else if(@cod_moneda='1') --Moneda USD
			begin

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
	      ,[num_nroitem]
			  ,fec_comprobante
			  ,[cod_tipomoneda]
			  ,isnull([txt_glosaitem],'')
			  ,isnull([cod_destino],'')
			  ,isnull([txt_cheque],'')
			  ,0
			  ,''
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
			  ,[num_debedolar]*@num_tc_usd
			  ,[num_haberdolar]*@num_tc_usd
			  ,[num_debedolar]
			  ,[num_haberdolar]
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
			  ,''--isnull([cod_tercero],'')
			  ,'2' --[flg_im]
			  ,GETDATE()
			  ,@user
			  ,GETDATE()
			  ,@user
		  FROM dbo.scp_rendiciondetalle
		  where cod_rendicioncabecera=@cod_rendicioncabecera )


		end

		/****************************************************************************
        * Step 6a) EUR
        * Inserta la primera linea del comprobante  EUR
        ****************************************************************************/

		else if(@cod_moneda='2') --Moneda EUR
			begin
			SELECT @ErrorStep = 'No existe tipo de cambio EUR';
			--select @test=1/@num_tc_mo
		   SELECT  @ErrorStep = 'Error al insertar lineas de detalle de comprobante para EUR'
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
				  ,[num_nroitem]
				  ,fec_comprobante
				  ,[cod_tipomoneda]
          ,isnull([txt_glosaitem],'')
          ,isnull([cod_destino],'')
          ,isnull([txt_cheque],'')
          ,0
          ,''
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
				  ,([num_debemo]*@num_tc_mo) as num_debesol
				  ,([num_habermo]*@num_tc_mo) as num_habersol
				  ,0 as num_debedolar
				  ,0 as num_haberdolar
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
				  ,''--isnull([cod_tercero],'')
				  ,'2' --[flg_im]
				  ,GETDATE()
				  ,@user
				  ,GETDATE()
				  ,@user
			  FROM dbo.scp_rendiciondetalle
			  where cod_rendicioncabecera=@cod_rendicioncabecera )


		end


		/****************************************************************************
        * Step 6
        * Actualiza scp_rendicioncabecera  cuando ya esta en contabilidad la operacion
        ****************************************************************************/
        SELECT  @ErrorStep = 'Error al actualizar el registro de rendicion'
		 update dbo.scp_rendicioncabecera
		 set [flg_enviado]='1'
			  ,[cod_origenenlace]=@cod_origen
			  ,[cod_comprobanteenlace]=@cod_comprobante
			  ,[fec_factualiza]=GETDATE()
			  ,[cod_uactualiza]=@user
		 where cod_rendicioncabecera =@cod_rendicioncabecera

 --COMMIT TRANSACTION

    SELECT  @ErrorCode  = 0, @Return_Message = 'La operacion ha sido enviada a contabilidad correctamente'

    /*************************************
    *  Return from the Stored Procedure
    *************************************/
    RETURN @ErrorCode                         -- =0 if success,  <>0 if failure

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
Exec usp_scp_vsj_enviarAContabilidadRendicion 2116,'abork','20/10/2019','0',0
*/
/****** Object:  StoredProcedure [dbo].[usp_scp_vsj_getCociliacionDeSaldos]    Script Date: 09/12/2016 10:05:09 ******/
SET ANSI_NULLS ON

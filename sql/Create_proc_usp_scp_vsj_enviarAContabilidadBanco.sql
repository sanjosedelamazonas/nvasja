USE [SCP]
GO
/****** Object:  StoredProcedure [dbo].[usp_scp_vsj_enviarAContabilidadBanco]    Script Date: 14/03/2023 23:31:29 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
ALTER PROCEDURE [dbo].[usp_scp_vsj_enviarAContabilidadBanco]
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
declare @cod_mescobr char(2)
declare @flg_chequecobrado char(1)

DECLARE @ErrorCode  int
DECLARE @ErrorStep  varchar(200)

set @cod_mescobr=''
set @flg_chequecobrado='0'
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
        --select @test=1/@num_tc_usd
        -- Get mes cobrado y flg_chequecobrado
        SELECT
          @cod_mescobr=isnull(cod_mescobrado,''),
          @flg_chequecobrado=case when isnull(ind_cobrado,0)=1 then '1' else '0' end
        FROM dbo.scp_bancocabecera where cod_bancocabecera=@cod_bancocabecera;
		/****************************************************************************
        * Step 2
        * Busca el numero para nuevo comprobante contable
        ****************************************************************************/
    Select @cod_comprobante=(REPLICATE('0',6-LEN(RTRIM(@cod_bancocabecera)))) + RTRIM(Isnull((@cod_bancocabecera),0))

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
		(SELECT @txt_ano
			  ,@cod_filial
			  ,@txt_mes
			  ,@cod_origen
			  ,@cod_comprobante
			  ,Convert(date, fec_fecha, 103)
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
        * Step 4a)
        * Inserta la linias del comprobante  PEN
        ****************************************************************************/
      SELECT  @ErrorStep = 'Error al insertar lineas de detalle de comprobante'

  if (@cod_moneda='0') -- Moneda PEN
	begin
	  insert into dbo.scp_comprobantedetalle
   (
		  [txt_anoproceso],[cod_filial],[cod_mes],[cod_origen],[cod_comprobante]
			  ,[num_nroitem]
			  ,[fec_comprobante],[cod_tipomoneda],[txt_glosaitem]
			  ,[cod_destino],[txt_cheque],[flg_chequecobrado],[cod_mescobr]
			  ,[cod_tipocomprobantepago],[txt_seriecomprobantepago],[txt_comprobantepago]
			  ,[fec_comprobantepago],[fec_pagocomprobantepago],[cod_reftipocomprobantepago]
			  ,[txt_refseriecomprobantepago],[txt_refcomprobantepago],[fec_refcomprobantepago]
			  ,[cod_registrocompraventa],[cod_evento],[num_refnroitem]
			  ,[cod_reforigen],[cod_refcomprobante],[fec_refcomprobante]
			  ,[cod_proyecto],[cod_ctaproyecto],[cod_ctacontable]
			  ,[cod_ctacontable9],[cod_ctacontable79],[cod_ctaarea]
			  ,[cod_ctaactividad],[cod_ctaespecial],[cod_financiera]
			  ,[cod_flujocaja],[num_tcvdolar],[num_debesol]
			  ,[num_habersol],[num_debedolar],[num_haberdolar]
			  ,[num_tcmo],[num_debemo],[num_habermo]
			  ,[cod_monedaoriginal],[flg_tcreferencia],[flg_conversion],[cod_pais]
			  ,[cod_departamento],[flg_recuperaigv],[por_igv]
			  ,[por_ies],[num_nroitem2],[cod_contraparte]
			  ,[txt_nroretencion],[fec_retencion],[flg_esactivo]
			  ,[txt_NroCompSujNoDomi],[flg_RetieneCuarta],[cod_gastofijo]
			  ,[flg_distribuir],[flg_distribuido],[cod_TipoRegistro]
			  ,[num_tcmc],[num_debemc],[num_habermc],[cod_tercero]
			  ,[flg_im],[fec_fregistro],[cod_uregistro],[fec_factualiza],[cod_uactualiza]
    )
    (
			SELECT @txt_ano,cod_filial,@txt_mes,cod_origen,cod_comprobante,
			ROW_NUMBER() OVER(ORDER BY cod_proyecto,cod_contracta desc) AS num_nroitem
			  ,fec_fecha,cod_tipomoneda,txt_glosaitem,cod_destino,txt_cheque
			  ,flg_chequecobrado,cod_mescobr,cod_tipocomprobantepago,txt_seriecomprobantepago
			  ,txt_comprobantepago,fec_comprobantepago,[fec_pagocomprobantepago],[cod_reftipocomprobantepago]
			  ,[txt_refseriecomprobantepago],[txt_refcomprobantepago],[fec_refcomprobantepago]
			  ,[cod_registrocompraventa],[cod_evento],[num_refnroitem],[cod_reforigen]
			  ,[cod_refcomprobante],[fec_refcomprobante],cod_proyecto,cod_ctaproyecto
			  ,[cod_contracta],[cod_ctacontable9] ,[cod_ctacontable79],[cod_ctaarea]
			  ,[cod_ctaactividad],cod_ctaespecial,cod_financiera,[cod_flujocaja]
			  ,[num_tcvdolar],[num_habersol],[num_debesol],num_haberdolar
			  ,num_debedolar,[num_tcmo],[num_debemo],[num_habermo],[cod_monedaoriginal]
			  ,[flg_tcreferencia],[flg_conversion],[cod_pais],[cod_departamento]
			  ,[flg_recuperaigv],[por_igv],[por_ies],
			  ROW_NUMBER() OVER(ORDER BY cod_proyecto,cod_contracta desc) as num_nroitem2,
			  [cod_contraparte]
			  ,[txt_nroretencion],[fec_retencion],[flg_esactivo],[txt_NroCompSujNoDomi]
			  ,[flg_RetieneCuarta],[cod_gastofijo],[flg_distribuir]
			  ,[flg_distribuido],[cod_TipoRegistro],[num_tcmc],[num_debemc],[num_habermc]
			  ,[cod_tercero],[flg_im],[fec_fregistro],[cod_uregistro],[fec_factualiza],[cod_uactualiza]
			FROM
				(
			select [txt_anoproceso] as txt_anoproceso ,@cod_filial as cod_filial
			  ,[cod_mes] as cod_mes,@cod_origen as cod_origen,@cod_comprobante as cod_comprobante
			  ,[num_item] as num_item,fec_fecha as fec_fecha,[cod_tipomoneda] as cod_tipomoneda
			  ,isnull([txt_glosaitem],'') as txt_glosaitem,isnull([cod_destinoitem],'') as cod_destino
			  ,isnull([txt_cheque],'') as txt_cheque,@flg_chequecobrado as flg_chequecobrado
			  ,@cod_mescobr as cod_mescobr,isnull([cod_tipocomprobantepago],'') as cod_tipocomprobantepago
			  ,isnull([txt_seriecomprobantepago],'') as txt_seriecomprobantepago
			  ,isnull([txt_comprobantepago],'') as txt_comprobantepago
			  ,isnull([fec_comprobantepago],'') as fec_comprobantepago
			  , Convert(date, '01/01/1900', 103) as [fec_pagocomprobantepago]
			  ,'' as [cod_reftipocomprobantepago],'' as [txt_refseriecomprobantepago]
			  ,'' as[txt_refcomprobantepago],Convert(date, '01/01/1900', 103) as [fec_refcomprobantepago]
			  ,''as [cod_registrocompraventa],''as [cod_evento],0 as [num_refnroitem]
			  ,'' as [cod_reforigen],'' as [cod_refcomprobante]
			  ,Convert(date, '01/01/1900', 103) as [fec_refcomprobante]
			  ,isnull([cod_proyecto],'') as cod_proyecto,isnull([cod_ctaproyecto],'') as cod_ctaproyecto
			  ,isnull([cod_contracta],'') as cod_contracta,'' as [cod_ctacontable9]
			  ,'' as [cod_ctacontable79],'' as [cod_ctaarea],'' as [cod_ctaactividad]
			  ,isnull([cod_ctaespecial],'') as cod_ctaespecial,isnull([cod_financiera],'') as cod_financiera
			  ,'' as [cod_flujocaja],
			  @num_tc_usd as [num_tcvdolar],[num_habersol],[num_debesol]
			  ,case when @num_tc_usd>0 then [num_habersol]/@num_tc_usd else 0 end as num_haberdolar
			  ,case when @num_tc_usd>0 then [num_debesol]/@num_tc_usd else 0 end as num_debedolar
			  ,0 as [num_tcmo],0 as [num_debemo],0 as [num_habermo],'' as [cod_monedaoriginal]
			  ,0 as [flg_tcreferencia],0 as [flg_conversion],'' as[cod_pais],'' as [cod_departamento]
			  ,'1' as [flg_recuperaigv],0 as [por_igv],0 as [por_ies],1 as [num_nroitem2]
			  ,isnull([cod_contraparte],'') as cod_contraparte,'' as [txt_nroretencion]
			  ,Convert(date, '01/01/1900', 103) as [fec_retencion],'0' as [flg_esactivo]
			  ,'' as [txt_NroCompSujNoDomi],'1' as [flg_RetieneCuarta],'' as [cod_gastofijo]
			  ,'' as [flg_distribuir],'' as [flg_distribuido],'' as [cod_TipoRegistro]
			  ,0 as [num_tcmc],0 as [num_debemc],0 as [num_habermc],isnull([cod_tercero],'') as cod_tercero
			  ,'2' as [flg_im],GETDATE() as [fec_fregistro],@user as [cod_uregistro]
			  ,GETDATE() as [fec_factualiza],@user as[cod_uactualiza]
			 FROM dbo.scp_bancodetalle
				where cod_bancocabecera=@cod_bancocabecera
				union
			select --insertar linea del banco cta 104 o 106..
			 bd.[txt_anoproceso] as txt_anoproceso,@cod_filial as cod_filial
			  ,bd.[cod_mes] as cod_mes,@cod_origen as cod_origen
			  ,@cod_comprobante as cod_comprobante,0 as num_item
			  ,bd.fec_fecha as fec_fecha,bd.[cod_tipomoneda] as cod_tipomoneda
		    ,bc.txt_glosa as txt_glosaitem,bc.cod_destino as cod_destino
			  ,isnull(bd.[txt_cheque],''),@flg_chequecobrado
			  ,@cod_mescobr,''--isnull(bd.[cod_tipocomprobantepago],'')
			  ,''--isnull(bd.[txt_seriecomprobantepago],'')
			  ,''----isnull(bd.[txt_comprobantepago],'')
			  ,''--isnull(bd.[fec_comprobantepago],'')
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
			  ,isnull(bd.[cod_proyecto],''),''--isnull(bd.[cod_ctaproyecto],'')
			  ,isnull(bc.[cod_ctacontable],''),'' --[cod_ctacontable9]
			  ,'' --[cod_ctacontable79]
			  ,'' --[cod_ctaarea]
			  ,'' --[cod_ctaactividad]
			  ,''--isnull(bd.[cod_ctaespecial],'')
			  ,isnull(bd.[cod_financiera],''),'' --[cod_flujocaja]
			  ,@num_tc_usd --[num_tcvdolar]
			  ,sum(bd.[num_debesol]),sum(bd.[num_habersol])
			  ,case when @num_tc_usd>0 then sum(bd.[num_debesol]/@num_tc_usd) else 0 end   --[num_haberdolar]
			  ,case when @num_tc_usd>0 then sum(bd.[num_habersol]/@num_tc_usd) else 0 end  --[num_debedolar]
			  ,@num_tc_mo
			  ,0 --@num_haber_mo
			  ,0 --@num_debe_mo
			  ,'' --[cod_monedaoriginal]
			  ,0--[flg_tcreferencia]
			  ,0--[flg_conversion]
			  ,'' --[cod_pais]
			  ,'' --[cod_departamento]
			  ,'1'--[flg_recuperaigv]
			  ,0--[por_igv]
			  ,0--[por_ies]
			  ,1--[num_nroitem2]
			  ,''--isnull(bd.[cod_contraparte],'')
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
			  ,isnull(bd.[cod_tercero],'')
			  ,'2' --[flg_im]
			  ,GETDATE(),@user,GETDATE(),@user
		  FROM dbo.scp_bancodetalle bd, dbo.scp_bancocabecera bc
		  where bd.cod_bancocabecera=@cod_bancocabecera
		  and bc.cod_bancocabecera =bd.cod_bancocabecera
		  group by
		   bd.cod_mes,bd.[txt_anoproceso] ,bd.fec_fecha,bd.txt_cheque,bd.cod_financiera
		   ,bc.[cod_ctacontable],bd.[cod_tipomoneda] ,isnull(bd.[cod_tercero],'')
			,isnull(bd.[cod_proyecto],''),bc.cod_destino,bc.txt_glosa
			) as tables
	)
end

		/****************************************************************************
        * Step 5a)  USD
        ****************************************************************************/

else if(@cod_moneda='1') --Moneda USD
begin

	  insert into dbo.scp_comprobantedetalle
   (
		  [txt_anoproceso],[cod_filial],[cod_mes],[cod_origen],[cod_comprobante]
			  ,[num_nroitem]
			  ,[fec_comprobante],[cod_tipomoneda],[txt_glosaitem]
			  ,[cod_destino],[txt_cheque],[flg_chequecobrado],[cod_mescobr]
			  ,[cod_tipocomprobantepago],[txt_seriecomprobantepago],[txt_comprobantepago]
			  ,[fec_comprobantepago],[fec_pagocomprobantepago],[cod_reftipocomprobantepago]
			  ,[txt_refseriecomprobantepago],[txt_refcomprobantepago],[fec_refcomprobantepago]
			  ,[cod_registrocompraventa],[cod_evento],[num_refnroitem]
			  ,[cod_reforigen],[cod_refcomprobante],[fec_refcomprobante]
			  ,[cod_proyecto],[cod_ctaproyecto],[cod_ctacontable]
			  ,[cod_ctacontable9],[cod_ctacontable79],[cod_ctaarea]
			  ,[cod_ctaactividad],[cod_ctaespecial],[cod_financiera]
			  ,[cod_flujocaja],[num_tcvdolar],[num_debesol]
			  ,[num_habersol],[num_debedolar],[num_haberdolar]
			  ,[num_tcmo],[num_debemo],[num_habermo]
			  ,[cod_monedaoriginal],[flg_tcreferencia],[flg_conversion],[cod_pais]
			  ,[cod_departamento],[flg_recuperaigv],[por_igv]
			  ,[por_ies],[num_nroitem2],[cod_contraparte]
			  ,[txt_nroretencion],[fec_retencion],[flg_esactivo]
			  ,[txt_NroCompSujNoDomi],[flg_RetieneCuarta],[cod_gastofijo]
			  ,[flg_distribuir],[flg_distribuido],[cod_TipoRegistro]
			  ,[num_tcmc],[num_debemc],[num_habermc],[cod_tercero]
			  ,[flg_im],[fec_fregistro],[cod_uregistro],[fec_factualiza],[cod_uactualiza]
    )
    (
			SELECT @txt_ano,cod_filial,@txt_mes,cod_origen,cod_comprobante,
			ROW_NUMBER() OVER(ORDER BY cod_proyecto,cod_contracta desc) AS num_nroitem
			  ,fec_fecha,cod_tipomoneda,txt_glosaitem,cod_destino,txt_cheque
			  ,flg_chequecobrado,cod_mescobr,cod_tipocomprobantepago,txt_seriecomprobantepago
			  ,txt_comprobantepago,fec_comprobantepago,[fec_pagocomprobantepago],[cod_reftipocomprobantepago]
			  ,[txt_refseriecomprobantepago],[txt_refcomprobantepago],[fec_refcomprobantepago]
			  ,[cod_registrocompraventa],[cod_evento],[num_refnroitem],[cod_reforigen]
			  ,[cod_refcomprobante],[fec_refcomprobante],cod_proyecto,cod_ctaproyecto
			  ,[cod_contracta],[cod_ctacontable9] ,[cod_ctacontable79],[cod_ctaarea]
			  ,[cod_ctaactividad],cod_ctaespecial,cod_financiera,[cod_flujocaja]
			  ,[num_tcvdolar],[num_habersol],[num_debesol],num_haberdolar
			  ,num_debedolar,[num_tcmo],[num_debemo],[num_habermo],[cod_monedaoriginal]
			  ,[flg_tcreferencia],[flg_conversion],[cod_pais],[cod_departamento]
			  ,[flg_recuperaigv],[por_igv],[por_ies],
			  ROW_NUMBER() OVER(ORDER BY cod_proyecto,cod_contracta desc) as num_nroitem2,
			  [cod_contraparte]
			  ,[txt_nroretencion],[fec_retencion],[flg_esactivo],[txt_NroCompSujNoDomi]
			  ,[flg_RetieneCuarta],[cod_gastofijo],[flg_distribuir]
			  ,[flg_distribuido],[cod_TipoRegistro],[num_tcmc],[num_debemc],[num_habermc]
			  ,[cod_tercero],[flg_im],[fec_fregistro],[cod_uregistro],[fec_factualiza],[cod_uactualiza]
			FROM
				(
			select [txt_anoproceso] as txt_anoproceso ,@cod_filial as cod_filial
			  ,[cod_mes] as cod_mes,@cod_origen as cod_origen,@cod_comprobante as cod_comprobante
			  ,[num_item] as num_item,fec_fecha as fec_fecha,[cod_tipomoneda] as cod_tipomoneda
			  ,isnull([txt_glosaitem],'') as txt_glosaitem,isnull([cod_destinoitem],'') as cod_destino
			  ,isnull([txt_cheque],'') as txt_cheque,@flg_chequecobrado as flg_chequecobrado
			  ,@cod_mescobr as cod_mescobr,isnull([cod_tipocomprobantepago],'') as cod_tipocomprobantepago
			  ,isnull([txt_seriecomprobantepago],'') as txt_seriecomprobantepago
			  ,isnull([txt_comprobantepago],'') as txt_comprobantepago
			  ,isnull([fec_comprobantepago],'') as fec_comprobantepago
			  , Convert(date, '01/01/1900', 103) as [fec_pagocomprobantepago]
			  ,'' as [cod_reftipocomprobantepago],'' as [txt_refseriecomprobantepago]
			  ,'' as[txt_refcomprobantepago],Convert(date, '01/01/1900', 103) as [fec_refcomprobantepago]
			  ,''as [cod_registrocompraventa],''as [cod_evento],0 as [num_refnroitem]
			  ,'' as [cod_reforigen],'' as [cod_refcomprobante]
			  ,Convert(date, '01/01/1900', 103) as [fec_refcomprobante]
			  ,isnull([cod_proyecto],'') as cod_proyecto,isnull([cod_ctaproyecto],'') as cod_ctaproyecto
			  ,isnull([cod_contracta],'') as cod_contracta,'' as [cod_ctacontable9]
			  ,'' as [cod_ctacontable79],'' as [cod_ctaarea],'' as [cod_ctaactividad]
			  ,isnull([cod_ctaespecial],'') as cod_ctaespecial,isnull([cod_financiera],'') as cod_financiera
			  ,'' as [cod_flujocaja],
			  @num_tc_usd as [num_tcvdolar]
			  ,case when @num_tc_usd>0 then [num_haberdolar]*@num_tc_usd else 0 end as num_habersol
			  ,case when @num_tc_usd>0 then [num_debedolar]*@num_tc_usd else 0 end as num_debesol
				,[num_haberdolar]
			  ,[num_debedolar]
			  ,@num_tc_mo as [num_tcmo]
			  ,0 as [num_debemo] --case when @num_tc_usd>0 then ([num_haberdolar]/@num_tc_usd)*@num_tc_mo else 0 end as [num_debemo]
			  ,0 as [num_habermo]--case when @num_tc_usd>0 then ([num_debedolar]/@num_tc_usd)*@num_tc_mo else 0 end as [num_habermo]
			  ,'' as [cod_monedaoriginal]
			  ,0 as [flg_tcreferencia],0 as [flg_conversion],'' as[cod_pais],'' as [cod_departamento]
			  ,'1' as [flg_recuperaigv],0 as [por_igv],0 as [por_ies],1 as [num_nroitem2]
			  ,isnull([cod_contraparte],'') as cod_contraparte,'' as [txt_nroretencion]
			  ,Convert(date, '01/01/1900', 103) as [fec_retencion],'0' as [flg_esactivo]
			  ,'' as [txt_NroCompSujNoDomi],'1' as [flg_RetieneCuarta],'' as [cod_gastofijo]
			  ,'' as [flg_distribuir],'' as [flg_distribuido],'' as [cod_TipoRegistro]
			  ,0 as [num_tcmc],0 as [num_debemc],0 as [num_habermc],isnull([cod_tercero],'') as cod_tercero
			  ,'2' as [flg_im],GETDATE() as [fec_fregistro],@user as [cod_uregistro]
			  ,GETDATE() as [fec_factualiza],@user as[cod_uactualiza]
			 FROM dbo.scp_bancodetalle
				where cod_bancocabecera=@cod_bancocabecera
				union
			select --insertar linea del banco cta 104 o 106..
			 bd.[txt_anoproceso] as txt_anoproceso,@cod_filial as cod_filial
			  ,bd.[cod_mes] as cod_mes,@cod_origen as cod_origen
			  ,@cod_comprobante as cod_comprobante,0 as num_item
			  ,bd.fec_fecha as fec_fecha,bd.[cod_tipomoneda] as cod_tipomoneda
		    ,bc.txt_glosa as txt_glosaitem,bc.cod_destino as cod_destino
			  ,isnull(bd.[txt_cheque],''),@flg_chequecobrado
			  ,@cod_mescobr,''--isnull(bd.[cod_tipocomprobantepago],'')
			  ,''--isnull(bd.[txt_seriecomprobantepago],'')
			  ,''----isnull(bd.[txt_comprobantepago],'')
			  ,''--isnull(bd.[fec_comprobantepago],'')
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
			  ,isnull(bd.[cod_proyecto],''),''--isnull(bd.[cod_ctaproyecto],'')
			  ,isnull(bc.[cod_ctacontable],''),'' --[cod_ctacontable9]
			  ,'' --[cod_ctacontable79]
			  ,'' --[cod_ctaarea]
			  ,'' --[cod_ctaactividad]
			  ,''--isnull(bd.[cod_ctaespecial],'')
			  ,isnull(bd.[cod_financiera],''),'' --[cod_flujocaja]
			,  @num_tc_usd as [num_tcvdolar]
			,case when @num_tc_usd>0 then sum(bd.[num_debedolar]*@num_tc_usd) else 0 end as num_debesol
			  ,case when @num_tc_usd>0 then sum(bd.[num_haberdolar]*@num_tc_usd) else 0 end as num_habersol
			  ,sum(bd.num_debedolar) as num_debedolar
			  ,sum(bd.num_haberdolar) as num_haberdolar
				  ,@num_tc_mo
			  ,0 as num_debemo--case when @num_tc_mo>0 then sum((bd.[num_debedolar]*@num_tc_usd)/@num_tc_mo) else 0 end as num_debemo
			  ,0 as num_habermo --case when @num_tc_mo>0 then sum((bd.[num_haberdolar]*@num_tc_usd)/@num_tc_mo) else 0 end as num_habermo
			  ,'' --[cod_monedaoriginal]
			  ,0--[flg_tcreferencia]
			  ,0--[flg_conversion]
			  ,'' --[cod_pais]
			  ,'' --[cod_departamento]
			  ,'1'--[flg_recuperaigv]
			  ,0--[por_igv]
			  ,0--[por_ies]
			  ,1--[num_nroitem2]
			  ,''--isnull(bd.[cod_contraparte],'')
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
			  ,isnull(bd.[cod_tercero],'')
			  ,'2' --[flg_im]
			  ,GETDATE(),@user,GETDATE(),@user
		  FROM dbo.scp_bancodetalle bd, dbo.scp_bancocabecera bc
		  where bd.cod_bancocabecera=@cod_bancocabecera
		  and bc.cod_bancocabecera =bd.cod_bancocabecera
		  group by
		   bd.cod_mes,bd.[txt_anoproceso] ,bd.fec_fecha,bd.txt_cheque,bd.cod_financiera
		   ,bc.[cod_ctacontable],bd.[cod_tipomoneda] ,isnull(bd.[cod_tercero],'')
			,isnull(bd.[cod_proyecto],''),bc.cod_destino,bc.txt_glosa
			) as tables
	)

end

		/****************************************************************************
        * Step 6a) EUR
        * Inserta la primera linea del comprobante  EUR
        ****************************************************************************/

		else if(@cod_moneda='2') --Moneda EUR
begin
			SELECT @ErrorStep = 'No existe tipo de cambio EUR';
			--select @test=1/@num_tc_mo
		   SELECT  @ErrorStep = 'Error al insertar lineas de detalle de comprobante para EUR';
	 insert into dbo.scp_comprobantedetalle
   (
		  [txt_anoproceso],[cod_filial],[cod_mes],[cod_origen],[cod_comprobante]
			  ,[num_nroitem]
			  ,[fec_comprobante],[cod_tipomoneda],[txt_glosaitem]
			  ,[cod_destino],[txt_cheque],[flg_chequecobrado],[cod_mescobr]
			  ,[cod_tipocomprobantepago],[txt_seriecomprobantepago],[txt_comprobantepago]
			  ,[fec_comprobantepago],[fec_pagocomprobantepago],[cod_reftipocomprobantepago]
			  ,[txt_refseriecomprobantepago],[txt_refcomprobantepago],[fec_refcomprobantepago]
			  ,[cod_registrocompraventa],[cod_evento],[num_refnroitem]
			  ,[cod_reforigen],[cod_refcomprobante],[fec_refcomprobante]
			  ,[cod_proyecto],[cod_ctaproyecto],[cod_ctacontable]
			  ,[cod_ctacontable9],[cod_ctacontable79],[cod_ctaarea]
			  ,[cod_ctaactividad],[cod_ctaespecial],[cod_financiera]
			  ,[cod_flujocaja],[num_tcvdolar],[num_debesol]
			  ,[num_habersol],[num_debedolar],[num_haberdolar]
			  ,[num_tcmo],[num_debemo],[num_habermo]
			  ,[cod_monedaoriginal],[flg_tcreferencia],[flg_conversion],[cod_pais]
			  ,[cod_departamento],[flg_recuperaigv],[por_igv]
			  ,[por_ies],[num_nroitem2],[cod_contraparte]
			  ,[txt_nroretencion],[fec_retencion],[flg_esactivo]
			  ,[txt_NroCompSujNoDomi],[flg_RetieneCuarta],[cod_gastofijo]
			  ,[flg_distribuir],[flg_distribuido],[cod_TipoRegistro]
			  ,[num_tcmc],[num_debemc],[num_habermc],[cod_tercero]
			  ,[flg_im],[fec_fregistro],[cod_uregistro],[fec_factualiza],[cod_uactualiza]
    )
    (
			SELECT @txt_ano,cod_filial,@txt_mes,cod_origen,cod_comprobante,
			ROW_NUMBER() OVER(ORDER BY cod_proyecto,cod_contracta desc) AS num_nroitem
			  ,fec_fecha,cod_tipomoneda,txt_glosaitem,cod_destino,txt_cheque
			  ,flg_chequecobrado,cod_mescobr,cod_tipocomprobantepago,txt_seriecomprobantepago
			  ,txt_comprobantepago,fec_comprobantepago,[fec_pagocomprobantepago],[cod_reftipocomprobantepago]
			  ,[txt_refseriecomprobantepago],[txt_refcomprobantepago],[fec_refcomprobantepago]
			  ,[cod_registrocompraventa],[cod_evento],[num_refnroitem],[cod_reforigen]
			  ,[cod_refcomprobante],[fec_refcomprobante],cod_proyecto,cod_ctaproyecto
			  ,[cod_contracta],[cod_ctacontable9] ,[cod_ctacontable79],[cod_ctaarea]
			  ,[cod_ctaactividad],cod_ctaespecial,cod_financiera,[cod_flujocaja]
			  ,[num_tcvdolar],[num_habersol],[num_debesol],num_haberdolar
			  ,num_debedolar,[num_tcmo],[num_habermo],[num_debemo],[cod_monedaoriginal]
			  ,[flg_tcreferencia],[flg_conversion],[cod_pais],[cod_departamento]
			  ,[flg_recuperaigv],[por_igv],[por_ies],
			  ROW_NUMBER() OVER(ORDER BY cod_proyecto,cod_contracta desc) as num_nroitem2,
			  [cod_contraparte]
			  ,[txt_nroretencion],[fec_retencion],[flg_esactivo],[txt_NroCompSujNoDomi]
			  ,[flg_RetieneCuarta],[cod_gastofijo],[flg_distribuir]
			  ,[flg_distribuido],[cod_TipoRegistro],[num_tcmc],[num_debemc],[num_habermc]
			  ,[cod_tercero],[flg_im],[fec_fregistro],[cod_uregistro],[fec_factualiza],[cod_uactualiza]
			FROM
				(
			select [txt_anoproceso] as txt_anoproceso ,@cod_filial as cod_filial
			  ,[cod_mes] as cod_mes,@cod_origen as cod_origen,@cod_comprobante as cod_comprobante
			  ,[num_item] as num_item,fec_fecha as fec_fecha,[cod_tipomoneda] as cod_tipomoneda
			  ,isnull([txt_glosaitem],'') as txt_glosaitem,isnull([cod_destinoitem],'') as cod_destino
			  ,isnull([txt_cheque],'') as txt_cheque,@flg_chequecobrado as flg_chequecobrado
			  ,@cod_mescobr as cod_mescobr,isnull([cod_tipocomprobantepago],'') as cod_tipocomprobantepago
			  ,isnull([txt_seriecomprobantepago],'') as txt_seriecomprobantepago
			  ,isnull([txt_comprobantepago],'') as txt_comprobantepago
			  ,isnull([fec_comprobantepago],'') as fec_comprobantepago
			  , Convert(date, '01/01/1900', 103) as [fec_pagocomprobantepago]
			  ,'' as [cod_reftipocomprobantepago],'' as [txt_refseriecomprobantepago]
			  ,'' as[txt_refcomprobantepago],Convert(date, '01/01/1900', 103) as [fec_refcomprobantepago]
			  ,''as [cod_registrocompraventa],''as [cod_evento],0 as [num_refnroitem]
			  ,'' as [cod_reforigen],'' as [cod_refcomprobante]
			  ,Convert(date, '01/01/1900', 103) as [fec_refcomprobante]
			  ,isnull([cod_proyecto],'') as cod_proyecto,isnull([cod_ctaproyecto],'') as cod_ctaproyecto
			  ,isnull([cod_contracta],'') as cod_contracta,'' as [cod_ctacontable9]
			  ,'' as [cod_ctacontable79],'' as [cod_ctaarea],'' as [cod_ctaactividad]
			  ,isnull([cod_ctaespecial],'') as cod_ctaespecial,isnull([cod_financiera],'') as cod_financiera
			  ,'' as [cod_flujocaja],
			  @num_tc_usd as [num_tcvdolar]
			  ,case when @num_tc_mo>0 then [num_habermo]*@num_tc_mo else 0 end as num_habersol
			  ,case when @num_tc_mo>0 then [num_debemo]*@num_tc_mo else 0 end as num_debesol
				,0 as [num_haberdolar]
			  ,0 as [num_debedolar]
			  ,@num_tc_mo as [num_tcmo]
			  ,[num_habermo] --case when @num_tc_usd>0 then ([num_haberdolar]/@num_tc_usd)*@num_tc_mo else 0 end as [num_debemo]
			  ,[num_debemo]--case when @num_tc_usd>0 then ([num_debedolar]/@num_tc_usd)*@num_tc_mo else 0 end as [num_habermo]
			  ,'' as [cod_monedaoriginal]
			  ,0 as [flg_tcreferencia],0 as [flg_conversion],'' as[cod_pais],'' as [cod_departamento]
			  ,'1' as [flg_recuperaigv],0 as [por_igv],0 as [por_ies],1 as [num_nroitem2]
			  ,isnull([cod_contraparte],'') as cod_contraparte,'' as [txt_nroretencion]
			  ,Convert(date, '01/01/1900', 103) as [fec_retencion],'0' as [flg_esactivo]
			  ,'' as [txt_NroCompSujNoDomi],'1' as [flg_RetieneCuarta],'' as [cod_gastofijo]
			  ,'' as [flg_distribuir],'' as [flg_distribuido],'' as [cod_TipoRegistro]
			  ,0 as [num_tcmc],0 as [num_debemc],0 as [num_habermc],isnull([cod_tercero],'') as cod_tercero
			  ,'2' as [flg_im],GETDATE() as [fec_fregistro],@user as [cod_uregistro]
			  ,GETDATE() as [fec_factualiza],@user as[cod_uactualiza]
			 FROM dbo.scp_bancodetalle
				where cod_bancocabecera=@cod_bancocabecera
				union
			select --insertar linea del banco cta 104 o 106..
			 bd.[txt_anoproceso] as txt_anoproceso,@cod_filial as cod_filial
			  ,bd.[cod_mes] as cod_mes,@cod_origen as cod_origen
			  ,@cod_comprobante as cod_comprobante,0 as num_item
			  ,bd.fec_fecha as fec_fecha,bd.[cod_tipomoneda] as cod_tipomoneda
		    ,bc.txt_glosa as txt_glosaitem,bc.cod_destino as cod_destino
			  ,isnull(bd.[txt_cheque],''),@flg_chequecobrado
			  ,@cod_mescobr,''--isnull(bd.[cod_tipocomprobantepago],'')
			  ,''--isnull(bd.[txt_seriecomprobantepago],'')
			  ,''----isnull(bd.[txt_comprobantepago],'')
			  ,''--isnull(bd.[fec_comprobantepago],'')
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
			  ,isnull(bd.[cod_proyecto],''),''--isnull(bd.[cod_ctaproyecto],'')
			  ,isnull(bc.[cod_ctacontable],''),'' --[cod_ctacontable9]
			  ,'' --[cod_ctacontable79]
			  ,'' --[cod_ctaarea]
			  ,'' --[cod_ctaactividad]
			  ,''--isnull(bd.[cod_ctaespecial],'')
			  ,isnull(bd.[cod_financiera],''),'' --[cod_flujocaja]
			,  @num_tc_usd as [num_tcvdolar]
			,case when @num_tc_mo>0 then sum(bd.[num_debemo]*@num_tc_mo) else 0 end as num_debesol
			  ,case when @num_tc_mo>0 then sum(bd.[num_habermo]*@num_tc_mo) else 0 end as num_habersol
			  ,0 as num_debedolar
			  ,0 as num_haberdolar
				  ,@num_tc_mo
			  ,sum(bd.num_debemo) as num_debemo--case when @num_tc_mo>0 then sum((bd.[num_debedolar]*@num_tc_usd)/@num_tc_mo) else 0 end as num_debemo
			  ,sum(bd.num_habermo) as num_habermo--case when @num_tc_mo>0 then sum((bd.[num_haberdolar]*@num_tc_usd)/@num_tc_mo) else 0 end as num_habermo
			  ,'' --[cod_monedaoriginal]
			  ,0--[flg_tcreferencia]
			  ,0--[flg_conversion]
			  ,'' --[cod_pais]
			  ,'' --[cod_departamento]
			  ,'1'--[flg_recuperaigv]
			  ,0--[por_igv]
			  ,0--[por_ies]
			  ,1--[num_nroitem2]
			  ,''--isnull(bd.[cod_contraparte],'')
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
			  ,isnull(bd.[cod_tercero],'')
			  ,'2' --[flg_im]
			  ,GETDATE(),@user,GETDATE(),@user
		  FROM dbo.scp_bancodetalle bd, dbo.scp_bancocabecera bc
		  where bd.cod_bancocabecera=@cod_bancocabecera
		  and bc.cod_bancocabecera =bd.cod_bancocabecera
		  group by
		   bd.cod_mes,bd.[txt_anoproceso] ,bd.fec_fecha,bd.txt_cheque,bd.cod_financiera
		   ,bc.[cod_ctacontable],bd.[cod_tipomoneda] ,isnull(bd.[cod_tercero],'')
			,isnull(bd.[cod_proyecto],''),bc.cod_destino,bc.txt_glosa
			) as tables
	)

end
		/****************************************************************************
        * Step 6
        * Actualiza scp_cajabanco cuando ya esta en contabilidad la operacion
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
Exec usp_scp_vsj_enviarAContabilidadBanco 13821, 'abork', '24/12/2019', 1,0
*/
/****** Object:  StoredProcedure [dbo].[usp_scp_vsj_getCociliacionDeSaldos]    Script Date: 09/12/2016 10:05:09 ******/
SET ANSI_NULLS ON

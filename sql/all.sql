USE [SCP]
GO

/****** Object:  Table [dbo].[scp_bancocabecera]    Script Date: 09/26/2016 02:30:46 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING OFF
GO

CREATE TABLE [dbo].[scp_bancocabecera](
	[cod_bancocabecera] [int] IDENTITY(1,1) NOT NULL,
	[txt_anoproceso] [varchar](4) NOT NULL,
	[ind_tipocuenta] [char](1) NOT NULL,
	[flg_saldo] [char](1) NOT NULL,
	[txt_correlativo] [varchar](8) NULL,
	[cod_mes] [varchar](2) NULL,
	[fec_fecha] [datetime] NULL,
	[cod_ctacontable] [varchar](14) NULL,
	[cod_destino] [varchar](11) NULL,
	[cod_tipomoneda] [char](1) NULL,
	[txt_glosa] [varchar](150) NULL,
	[txt_cheque] [varchar](20) NULL,
	[num_debesol] [decimal](12, 2) NULL,
	[num_habersol] [decimal](12, 2) NULL,
	[num_debedolar] [decimal](12, 2) NULL,
	[num_haberdolar] [decimal](12, 2) NULL,
	[num_debemo] [decimal](12, 2) NULL,
	[num_habermo] [decimal](12, 2) NULL,
	[flg_enviado] [char](1) NULL,
	[cod_origenenlace] [varchar](6) NULL,
	[cod_comprobanteenlace] [varchar](6) NULL,
	[flg_im] [char](1) NULL,
	[fec_fregistro] [datetime] NULL,
	[cod_uregistro] [varchar](15) NULL,
	[fec_factualiza] [datetime] NULL,
	[cod_uactualiza] [varchar](15) NULL,
	[ind_cobrado] [bit] NULL,
	[flg_Anula] [char](1) NULL
) ON [PRIMARY]
SET ANSI_PADDING ON
ALTER TABLE [dbo].[scp_bancocabecera] ADD [cod_mescobrado] [varchar](2) NULL
--ALTER TABLE [dbo].[scp_bancocabecera] ADD [flg_Anula] [char](1) NULL
/****** Object:  Index [PK_scp_bancocabecera]    Script Date: 09/26/2016 02:30:46 ******/
ALTER TABLE [dbo].[scp_bancocabecera] ADD  CONSTRAINT [PK_scp_bancocabecera] PRIMARY KEY CLUSTERED
(
	[cod_bancocabecera] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO
--USE [SCP]
GO

/****** Object:  Table [dbo].[scp_bancodetalle]    Script Date: 09/24/2016 13:25:46 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[scp_bancodetalle](
	[num_item] int NOT NULL,
	[cod_bancocabecera] int NOT NULL,
	[txt_anoproceso] [varchar](4) NOT NULL,
	[ind_tipocuenta] [char](1) NOT NULL,
	[flg_saldo] [char](1) NOT NULL,
	[txt_correlativo] [varchar](8) NULL,
	[cod_mes] [varchar](2) NULL,
	[fec_fecha] [datetime] NULL,
	[cod_proyecto] [varchar](6) NULL,
	[cod_tercero] [varchar](11) NULL,
	[txt_glosaitem] [varchar](70) NULL,
	[cod_destino] [varchar](11) NULL,
	[txt_cheque] [varchar](20) NULL,
	[cod_tipomoneda] [char](1) NULL,
	[cod_ctaespecial] [varchar](14) NULL,
	[cod_ctaproyecto] [varchar](14) NULL,
	[cod_contraparte] [varchar](6) NULL,
	[cod_financiera] [varchar](6) NULL,
	[cod_ctacontable] [varchar](14) NULL,
	[cod_contracta] [varchar](14) NULL,
	[num_tcvdolar] [decimal](12, 2) NULL,
	[num_debesol] [decimal](12, 2) NULL,
	[num_habersol] [decimal](12, 2) NULL,
	[num_debedolar] [decimal](12, 2) NULL,
	[num_haberdolar] [decimal](12, 2) NULL,
	[num_tcmo] [float] NULL,
	[num_debemo] [decimal](12, 2) NULL,
	[num_habermo] [decimal](12, 2) NULL,
	[cod_tipogasto] [varchar](2) NULL,
	[cod_tipoingreso] [varchar](2) NULL,
	[cod_formapago] [varchar](2) NULL,
	[txt_detallepago] [varchar](70) NULL,
	[cod_destinoitem] [varchar](11) NULL,
	[cod_tipocomprobantepago] [varchar](2) NULL,
	[txt_seriecomprobantepago] [varchar](5) NULL,
	[txt_comprobantepago] [varchar](20) NULL,
	[fec_comprobantepago] [datetime] NULL,
	[flg_Anula] [char](1) NULL,
	[num_saldosol] [decimal](12, 2) NULL,
	[num_saldodolar] [decimal](12, 2) NULL,
	[num_saldomo] [decimal](12, 2) NULL,
	[flg_im] [char](1) NULL,
	[fec_fregistro] [datetime] NULL,
	[cod_uregistro] [varchar](15) NULL,
	[fec_factualiza] [datetime] NULL,
	[cod_uactualiza] [varchar](15) NULL,
	[cod_tipomov] [int] NULL
CONSTRAINT [FK_scp_bancocabecera] FOREIGN KEY
(cod_bancocabecera) REFERENCES scp_bancocabecera(cod_bancocabecera)
ON UPDATE CASCADE,
 CONSTRAINT [PK_scp_bancodetalle] PRIMARY KEY CLUSTERED
(
	[num_item] ASC,
	[cod_bancocabecera] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO


--USE [SCP]
GO

/****** Object:  Table [dbo].[scp_cajabanco]    Script Date: 09/08/2016 12:47:56 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[vsj_cajabanco](
	[cod_cajabanco] [int] IDENTITY(1,1) not null primary key,	
	[txt_anoproceso] [varchar](4) NOT NULL,
	[ind_tipocuenta] [char](1) NOT NULL,
	[txt_correlativo] [varchar](8) NULL,
	[cod_mes] [varchar](2) NOT NULL,
	[fec_fecha] [datetime] NOT NULL,
	[cod_proyecto] [varchar](6) NULL,
	[cod_tercero] [varchar](11) NULL,
	[txt_glosaitem] [varchar](70) NOT NULL,
	[cod_destino] [varchar](11) NOT NULL,

	[cod_tipomoneda] [char](1) NOT NULL,
	[cod_ctaespecial] [varchar](14) NULL,
	[cod_ctaproyecto] [varchar](14) NULL,
	[cod_contraparte] [varchar](6) NULL, --(lugar de gasto)
	[cod_financiera] [varchar](6) NULL,
	[cod_ctacontable] [varchar](14) NOT NULL,
	[cod_contracta] [varchar](14) NULL,
	[num_debesol] [decimal](12, 2) NULL,
	[num_habersol] [decimal](12, 2) NULL,
	[num_debedolar] [decimal](12, 2) NULL,
	[num_haberdolar] [decimal](12, 2) NULL,
	[flg_Anula] [char](1) NULL,
	[flg_enviado] [char](1) NULL,   		--  enviado a conta
	[cod_origenenlace] [varchar](6) NULL,		-- cod tipo en conta
	[cod_comprobanteenlace] [varchar](6) NULL,	-- num voucher en conta	
	[cod_destinoitem] [varchar](11) NOT NULL,		-- auxiliar
	[cod_tipocomprobantepago] [varchar](2) NULL,
	[txt_seriecomprobantepago] [varchar](5) NULL,
	[txt_comprobantepago] [varchar](20) NULL,
	[fec_comprobantepago] [datetime] NULL,
	[fec_fregistro] [datetime] NULL,
	[cod_uregistro] [varchar](15) NULL,
	[fec_factualiza] [datetime] NULL,
	[cod_uactualiza] [varchar](15) NULL)
GO

create function dbo.usp_vsj_cajabanco_gen_correlativo(@id int) 
returns char(8) 
as 
begin 
return right('00000000' + convert(varchar(10), @id), 8) 
end

GO

--alter [dbo].[vsj_cajabanco] add usp_vsj_cajabanco_gen_correlativo varchar(10) 
create trigger vsj_cajabanco_insert on [dbo].[vsj_cajabanco]
after insert as 
update 
    vsj_cajabanco 
set 
    vsj_cajabanco.txt_correlativo = dbo.usp_vsj_cajabanco_gen_correlativo(vsj_cajabanco.cod_cajabanco) 
from 
    vsj_cajabanco 
inner join 
    inserted on vsj_cajabanco.cod_cajabanco = inserted.cod_cajabanco
    
GO

SET ANSI_PADDING OFF
GO



--USE [SCP]
GO

/****** Object:  Table [dbo].[vsj_configuracioncaja]    Script Date: 09/12/2016 10:02:43 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[vsj_configuracioncaja](
	[cod_configuracion] [int] IDENTITY(1,1) NOT NULL,
	[txt_configuracion] [varchar](50) NOT NULL,
	[cod_ctacontable] [varchar](14) NULL,
	[cod_categoriaproyecto] [varchar](2) NULL,
	[cod_proyecto] [varchar](6) NULL,
	[cod_destino] [varchar](11) NULL,
	[ind_tipomoneda] [char](1) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[cod_configuracion] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

--USE [SCP]
GO

/****** Object:  Table [dbo].[vsj_configuractacajabanco]    Script Date: 09/12/2016 10:02:52 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[vsj_configuractacajabanco](
    [id] [int] IDENTITY(1,1) NOT NULL,
	[cod_tipocuenta] [varchar](2) NOT NULL,
	[txt_tipocuenta] [varchar](50) NOT NULL,
	[cod_ctacontablecaja] [varchar](14) NULL,
	[cod_ctacontablegasto] [varchar](14) NULL,
	[cod_ctaespecial] [varchar](14) NULL,
	[para_caja] [bit] NOT NULL,
	[para_banco] [bit] NOT NULL,
	[para_proyecto] [bit] NOT NULL,
	[para_tercero] [bit] NOT NULL,
	[activo] [bit] NOT NULL,
 CONSTRAINT [PK_vsj_configuractacajabanco] PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[vsj_configuractacajabanco] ADD  DEFAULT ('TRUE') FOR [para_caja]
GO

ALTER TABLE [dbo].[vsj_configuractacajabanco] ADD  DEFAULT ('TRUE') FOR [para_banco]
GO

ALTER TABLE [dbo].[vsj_configuractacajabanco] ADD  DEFAULT ('TRUE') FOR [para_proyecto]
GO

ALTER TABLE [dbo].[vsj_configuractacajabanco] ADD  DEFAULT ('TRUE') FOR [para_tercero]
GO

ALTER TABLE [dbo].[vsj_configuractacajabanco] ADD  DEFAULT ('TRUE') FOR [activo]
GO

--USE [SCP]
GO

/****** Object:  Table [dbo].[vsj_propiedad]    Script Date: 09/08/2016 18:48:05 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[vsj_propiedad](
	[cod_propiedad] [int] IDENTITY(1,1) NOT NULL,
	[nombre] [varchar](50) NOT NULL,
	[valor] [varchar](250) NOT NULL,
 CONSTRAINT [PK_vsj_propiedad] PRIMARY KEY CLUSTERED 
(
	[cod_propiedad] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO


--USE [SCP]
GO
/****** Object:  StoredProcedure [dbo].[usp_scp_vsj_enviarAContabilidadBanco]    Script Date: 09/25/2016 20:53:19 ******/
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
set @num_item=0

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

      select @num_item=@num_item+1

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
			  ,@num_item
			  ,fec_fecha
			  ,[cod_tipomoneda]
			  ,[txt_glosaitem]
			  ,[cod_destino]
			  ,[txt_cheque]
			  ,'0' --!!!!corregir cuando se anade a scp_bancodetalle [flg_chequecobrado]
			  ,'' ----!!!!corregir cuando se anade a scp_bancodetalle [cod_mescobr]
			  ,[cod_tipocomprobantepago]
			  ,[txt_seriecomprobantepago]
			  ,[txt_comprobantepago]
			  ,[fec_comprobantepago]
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
			  ,[cod_proyecto]
			  ,[cod_ctaproyecto]
			  ,[cod_ctacontable]
			  ,'' --[cod_ctacontable9]
			  ,'' --[cod_ctacontable79]
			  ,'' --[cod_ctaarea]
			  ,'' --[cod_ctaactividad]
			  ,[cod_ctaespecial]
			  ,[cod_financiera]
			  ,'' --[cod_flujocaja]
			  ,@num_tc_usd --[num_tcvdolar]
			  ,[num_debesol]
			  ,[num_habersol]
			  ,[num_debesol]/@num_tc_usd
			  ,[num_habersol]/@num_tc_usd
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
			  ,[cod_contraparte]
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
			  ,[cod_tercero]
			  ,'2' --[flg_im]
			  ,GETDATE()
			  ,@user
			  ,GETDATE()
			  ,@user
		  FROM dbo.scp_bancodetalle
		  where cod_bancocabecera=@cod_bancocabecera )
		   (select --insertar linea del banco cta 104 o 106..
			 bd.[txt_anoproceso]
			  ,@cod_filial
			  ,bd.[cod_mes]
			  ,@cod_origen
			  ,@cod_comprobante
			  ,@num_item+1
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
			  ,bc.[cod_ctacontable]
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
			  ,bd.[num_habersol]/@num_tc_usd --[num_debedolar]
			  ,bd.[num_debesol]/@num_tc_usd--[num_haberdolar]
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

		 select @num_item=@num_item+1
	end
		else if(@cod_moneda='1') --Moneda USD
			begin
			select @num_item=@num_item+1

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
			  ,@num_item
			  ,fec_fecha
			  ,[cod_tipomoneda]
			  ,[txt_glosaitem]
			  ,[cod_destino]
			  ,[txt_cheque]
			  ,'0' --!!!!corregir cuando se anade a scp_bancodetalle [flg_chequecobrado]
			  ,'' ----!!!!corregir cuando se anade a scp_bancodetalle [cod_mescobr]
			  ,[cod_tipocomprobantepago]
			  ,[txt_seriecomprobantepago]
			  ,[txt_comprobantepago]
			  ,[fec_comprobantepago]
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
			  ,[cod_proyecto]
			  ,[cod_ctaproyecto]
			  ,[cod_ctacontable]
			  ,'' --[cod_ctacontable9]
			  ,'' --[cod_ctacontable79]
			  ,'' --[cod_ctaarea]
			  ,'' --[cod_ctaactividad]
			  ,[cod_ctaespecial]
			  ,[cod_financiera]
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
			  ,[cod_contraparte]
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
			  ,[cod_tercero]
			  ,'2' --[flg_im]
			  ,GETDATE()
			  ,@user
			  ,GETDATE()
			  ,@user
		  FROM dbo.scp_bancodetalle
		  where cod_bancocabecera=@cod_bancocabecera )
		   (select --insertar linea del banco cta 104 o 106..
			 bd.[txt_anoproceso]
			  ,@cod_filial
			  ,bd.[cod_mes]
			  ,@cod_origen
			  ,@cod_comprobante
			  ,@num_item+1
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
			  ,bc.[cod_ctacontable]
			  ,'' --[cod_ctacontable9]
			  ,'' --[cod_ctacontable79]
			  ,'' --[cod_ctaarea]
			  ,'' --[cod_ctaactividad]
			  ,bd.[cod_ctaespecial]
			  ,bd.[cod_financiera]
			  ,'' --[cod_flujocaja]
			  ,@num_tc_usd --[num_tcvdolar]
			  ,bd.[num_haberdolar]*@num_tc_usd
			  ,bd.[num_debedolar]*@num_tc_usd
			  ,bd.[num_haberdolar]   --[num_debedolar]
			  ,bd.[num_debedolar] --[num_haberdolar]
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

		 select @num_item=@num_item+1
		end

		else if(@cod_moneda='2') --Moneda EUR
			begin
			SELECT @ErrorStep = 'No existe tipo de cambio EUR';
			select @test=1/@num_tc_mo
		   SELECT  @ErrorStep = 'Error al insertar lineas de detalle de comprobante'
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
				  ,@num_item
				  ,fec_fecha
				  ,[cod_tipomoneda]
				  ,[txt_glosaitem]
				  ,[cod_destino]
				  ,[txt_cheque]
				  ,'0' --!!!!corregir cuando se anade a scp_bancodetalle [flg_chequecobrado]
				  ,'' ----!!!!corregir cuando se anade a scp_bancodetalle [cod_mescobr]
				  ,[cod_tipocomprobantepago]
				  ,[txt_seriecomprobantepago]
				  ,[txt_comprobantepago]
				  ,[fec_comprobantepago]
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
				  ,[cod_proyecto]
				  ,[cod_ctaproyecto]
				  ,[cod_ctacontable]
				  ,'' --[cod_ctacontable9]
				  ,'' --[cod_ctacontable79]
				  ,'' --[cod_ctaarea]
				  ,'' --[cod_ctaactividad]
				  ,[cod_ctaespecial]
				  ,[cod_financiera]
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
				  ,[cod_contraparte]
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
				  ,[cod_tercero]
				  ,'2' --[flg_im]
				  ,GETDATE()
				  ,@user
				  ,GETDATE()
				  ,@user
			  FROM dbo.scp_bancodetalle
			  where cod_bancocabecera=@cod_bancocabecera )
			   (select --insertar linea del banco cta 104 o 106..
				 bd.[txt_anoproceso]
				  ,@cod_filial
				  ,bd.[cod_mes]
				  ,@cod_origen
				  ,@cod_comprobante
				  ,@num_item+1
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
				  ,bc.[cod_ctacontable]
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
				  ,(bd.[num_habermo]*@num_tc_mo)/@num_tc_usd --[num_debedolar]
				  ,(bd.[num_debemo]*@num_tc_mo)/@num_tc_usd--[num_haberdolar]
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

			 select @num_item=@num_item+1
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
Exec usp_scp_vsj_enviarAContabilidadBanco 10437,'abork','01/09/2016','0',0
*/
--USE [SCP]
GO
/****** Object:  StoredProcedure [dbo].[usp_scp_vsj_enviarContabilidad]    Script Date: 09/20/2016 15:35:37 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[usp_scp_vsj_enviarAContabilidad]
	@cod_cajabanco int, --id de operacion de vsj_cajabanco
	@user varchar(15), -- nombre del usuario segun SCP
	@fecha_operacion varchar(10),-- fecha de operacion en formato 'dd/mm/yyyy'
	@cod_moneda char(1), -- 0 pen, 1 usd
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
declare @num_tc_usd float
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
set @num_tc_usd=0.00

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
		where fec_fechacambio= Convert(date, @fecha_operacion, 103)
		select @test=1/@num_tc_usd 
        
		if (@cod_moneda='0') -- Moneda PEN
			begin
			SELECT  @ErrorStep = 'Error al calcular los montos en USD'
			select @num_debe_pen=@num_debe_input, 
				@num_haber_pen=@num_haber_input, 
				@num_debe_usd=(@num_debe_input/@num_tc_usd), 
				@num_haber_usd=(@num_haber_input/@num_tc_usd)
			end
		else if(@cod_moneda='1') --Moneda USD
			begin
			SELECT  @ErrorStep = 'Error al calcular los montos en PEN'
			select @num_debe_pen=(@num_debe_input*@num_tc_usd), 
				@num_haber_pen=(@num_haber_input*@num_tc_usd),
				@num_debe_usd= @num_debe_input,
				@num_haber_usd=@num_haber_input
			end
	
	    /****************************************************************************
        * Step 2
        * Busca el numero para nuevo comprobante contable y codigo para anadir en glosa
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
			  ,@cod_filial
			  ,[cod_mes]
			  ,@cod_origen
			  ,@cod_comprobante
			  ,fec_fecha
			  ,''--[cod_tipooperacion]
			  ,''--[cod_mediopago]
			  ,[cod_tipomoneda]
			  ,left(@codigo+' - '+[txt_glosaitem],70)
			  ,[cod_destino]
			  ,''--[cod_banco]
			  ,[cod_ctacontable]
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
			  ,[txt_glosaitem]
			  ,[cod_destino]
			  ,'' --[txt_cheque]
			  ,'0' --[flg_chequecobrado]
			  ,'' --[cod_mescobr]
			  ,[cod_tipocomprobantepago]
			  ,[txt_seriecomprobantepago]
			  ,[txt_comprobantepago]
			  ,[fec_comprobantepago]
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
			  ,[cod_proyecto]
			  ,[cod_ctaproyecto]
			  ,[cod_ctacontable]
			  ,'' --[cod_ctacontable9]
			  ,'' --[cod_ctacontable79]
			  ,'' --[cod_ctaarea]
			  ,'' --[cod_ctaactividad]
			  ,[cod_ctaespecial]
			  ,[cod_financiera]
			  ,'' --[cod_flujocaja]
			  ,@num_tc_usd --[num_tcvdolar]
			  ,@num_debe_pen --[num_debesol]
			  ,@num_haber_pen --[num_habersol]
			  ,@num_debe_usd --[num_debedolar]
			  ,@num_haber_usd --[num_haberdolar] 
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
			  ,[cod_contraparte]
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
			  ,[cod_tercero]
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
			  ,[txt_glosaitem]
			  ,[cod_destino]
			  ,'' --[txt_cheque]
			  ,'0' --[flg_chequecobrado]
			  ,'' --[cod_mescobr]
			  ,[cod_tipocomprobantepago]
			  ,[txt_seriecomprobantepago]
			  ,[txt_comprobantepago]
			  ,[fec_comprobantepago]
			  ,Convert(date, '01/01/1900', 103)--[fec_pagocomprobantepago]
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
			  ,[cod_proyecto]
			  ,[cod_ctaproyecto]
			  ,[cod_contracta]--[cod_ctacontable]
			  ,'' --[cod_ctacontable9]
			  ,'' --[cod_ctacontable79]
			  ,'' --[cod_ctaarea]
			  ,'' --[cod_ctaactividad]
			  ,[cod_ctaespecial]
			  ,[cod_financiera]
			  ,'' --[cod_flujocaja]
			  ,@num_tc_usd --[num_tcvdolar]
			  ,@num_haber_pen--[num_debesol]
			  ,@num_debe_pen  --[num_habersol]
			  ,@num_haber_usd  --[num_debedolar]
			  ,@num_debe_usd--[num_haberdolar] 
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
			  ,2--[num_nroitem2]
			  ,[cod_contraparte]
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
			  ,[cod_tercero]
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
--USE [SCP]
GO

/****** Object:  StoredProcedure [dbo].[usp_scp_vsj_getSaldoAlDia]    Script Date: 09/12/2016 10:05:34 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[usp_scp_vsj_getSaldoAlDia]
	@Tipo char(1), -- 0 proyecto, 1 tercero
	@FechaFinal varchar(10),            -- Fecha para saldo formato dd/mm/yyyy
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
Set @FechaInicial='01/01/'+SUBSTRING(@FechaFinal,7,4)

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

--USE [SCP]
GO

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

--USE [SCP]
GO
/****** Object:  StoredProcedure [dbo].[usp_scp_vsj_GetSaldoAlDiaBanco]    Script Date: 10/14/2016 03:30:47 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
--USE [SCP]
GO
/****** Object:  StoredProcedure [dbo].[usp_scp_vsj_GetSaldoAlDiaBanco]    Script Date: 10/30/2016 21:21:26 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[usp_scp_vsj_GetSaldoAlDiaBanco]
	@Fecha varchar(19), -- Fecha para saldo formato yyyy-dd-mm hh:mi:ss(24h)
	@Cuenta varchar(7), -- Cuenta de banco por ejemplo '1040103'
	@Moneda varchar(1),  -- 0 PEN, 1 USD
	@Saldo decimal(12,2) OUTPUT

AS

Declare @Ano varchar(4)
Declare @FechaInicial char(10)
Declare @SaldoInicial decimal(12,2)
Declare @SaldoCaja decimal(12,2)

Set @Ano=SUBSTRING(@Fecha,1,4)
-- Set @FechaInicial='01/01/'+SUBSTRING(@Fecha,7,4)
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

	Print 'Inicial: '+CONVERT(char(14),@SaldoInicial,14)

	Select @SaldoCaja = Sum(A.num_habersol)-Sum(A.num_debesol)
	From scp_bancocabecera A
	Where (A.fec_fecha >= Convert(datetime, @FechaInicial, 20) And A.fec_fecha <= Convert(datetime, @Fecha, 20))
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

	Print 'Inicial: '+CONVERT(char(14),@SaldoInicial,14)

	Select @SaldoCaja = Sum(A.num_haberdolar)-Sum(A.num_debedolar)
	From scp_bancocabecera A
	Where (A.fec_fecha >= Convert(datetime, @FechaInicial, 20) And A.fec_fecha <= Convert(datetime, @Fecha, 20))
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

	Print 'Inicial: '+CONVERT(char(14),@SaldoInicial,14)

	Select @SaldoCaja = Sum(A.num_habermo)-Sum(A.num_debemo)
	From scp_bancocabecera A
	Where (A.fec_fecha >= Convert(datetime, @FechaInicial, 20) And A.fec_fecha <= Convert(datetime, @Fecha, 20))
	And A.cod_ctacontable=@Cuenta And a.cod_tipomoneda=@Moneda
	Group By A.cod_ctacontable, a.cod_tipomoneda
END
Print 'Banco : '+CONVERT(char(14),@SaldoCaja,14)

select @Saldo=-(@SaldoInicial+@SaldoCaja)

Print 'Saldo : '+CONVERT(char(14),@Saldo,14)

-- Exec usp_scp_vsj_GetSaldoAlDiaBanco '2016-08-18 23:59:59','1060104','2',0--USE [SCP]
GO
/****** Object:  StoredProcedure [dbo].[usp_scp_vsj_GetSaldoAlDiaCaja]    Script Date: 09/22/2016 23:08:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[usp_scp_vsj_GetSaldoAlDiaCaja]
	@Fecha varchar(19), -- Fecha para saldo formato yyyy-mm-dd hh:mi:ss(24h)
	@Cuenta varchar(7), -- Cuenta de caja por ejemplo '1011101'
	@Moneda varchar(1),  -- 0 PEN, 1 USD
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

--Print 'Caja : '+CONVERT(char(14),@SaldoCaja,14)

select @Saldo=-(@SaldoInicial+@SaldoCaja)

--Print 'Saldo : '+CONVERT(char(14),@Saldo,14)

-- Exec usp_scp_vsj_GetSaldoAlDiaCaja '2016-08-31 23:59:59','1011101','0',0
-- Exec usp_scp_vsj_GetSaldoAlDiaCaja '2016-08-31 00:00:00','1011101','0',0--USE [SCP]
GO

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

--USE [SCP]
GO

/****** Object:  StoredProcedure [dbo].[usp_scp_vsj_getSaldoAlDia_NoEnviadosBancos]    Script Date: 09/12/2016 10:06:13 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO


CREATE PROCEDURE [dbo].[usp_scp_vsj_getSaldoAlDia_NoEnviadosBancos]
(@Tipo int, -- 0 proyecto, 1 tercero
@FechaInicial char(10),
 @FechaFinal char(10),
 @Codigo varchar(6), 
 @SaldoPEN_banco decimal(12,2) OUTPUT,	
 @SaldoUSD_banco decimal(12,2) OUTPUT,
 @SaldoEUR_banco decimal(12,2) OUTPUT )

As

Set @SaldoPEN_banco=0.00
Set @SaldoUSD_banco =0.00
Set @SaldoEUR_banco=0.00

if (@Tipo=1)
BEGIN
  --PEN
	Select @SaldoPEN_banco=isnull((Sum(b.num_habersol)-Sum(b.num_debesol)),0)
	From scp_bancocabecera A
	INNER JOIN scp_bancodetalle B ON A.txt_anoproceso = B.txt_anoproceso And
	A.cod_mes+A.ind_tipocuenta+A.txt_correlativo=B.cod_mes+B.ind_tipocuenta+B.txt_correlativo 
	Where (a.fec_fecha >= Convert(date, @FechaInicial, 103) And A.fec_fecha <= Convert(date, @FechaFinal, 103)) And 
	Ltrim(Rtrim(b.cod_proyecto)) = @Codigo And 
	a.txt_anoproceso=SUBSTRING(@FechaFinal,7,4) and
	A.flg_enviado=0 and
	A.cod_tipomoneda=0
	Group By B.cod_proyecto,A.cod_tipomoneda

  --USD
	Select @SaldoUSD_banco=isnull((Sum(b.num_haberdolar)-Sum(b.num_debedolar)),0)
	From scp_bancocabecera A
	INNER JOIN scp_bancodetalle B ON A.txt_anoproceso = B.txt_anoproceso And
	A.cod_mes+A.ind_tipocuenta+A.txt_correlativo=B.cod_mes+B.ind_tipocuenta+B.txt_correlativo 
	Where (a.fec_fecha >= Convert(date, @FechaInicial, 103) And A.fec_fecha <= Convert(date, @FechaFinal, 103)) And 
	Ltrim(Rtrim(b.cod_proyecto)) = @Codigo And 
	a.txt_anoproceso=SUBSTRING(@FechaFinal,7,4) and
	A.flg_enviado=0 and
	A.cod_tipomoneda=1
	Group By B.cod_proyecto,A.cod_tipomoneda

  --EUR
	Select @SaldoEUR_banco=isnull((Sum(b.num_habermo)-Sum(b.num_debemo)),0)
	From scp_bancocabecera A
	INNER JOIN scp_bancodetalle B ON A.txt_anoproceso = B.txt_anoproceso And
	A.cod_mes+A.ind_tipocuenta+A.txt_correlativo=B.cod_mes+B.ind_tipocuenta+B.txt_correlativo 
	Where (a.fec_fecha >= Convert(date, @FechaInicial, 103) And A.fec_fecha <= Convert(date, @FechaFinal, 103)) And 
	Ltrim(Rtrim(b.cod_proyecto)) = @Codigo And 
	a.txt_anoproceso=SUBSTRING(@FechaFinal,7,4) and
	A.flg_enviado=0 and
	A.cod_tipomoneda=2
	Group By B.cod_proyecto,A.cod_tipomoneda
END
else if (@Tipo=2) -- Tercero
BEGIN
  --PEN
	Select @SaldoPEN_banco=isnull((Sum(b.num_habersol)-Sum(b.num_debesol)),0)
	From scp_bancocabecera A
	INNER JOIN scp_bancodetalle B ON A.txt_anoproceso = B.txt_anoproceso And
	A.cod_mes+A.ind_tipocuenta+A.txt_correlativo=B.cod_mes+B.ind_tipocuenta+B.txt_correlativo 
	Where (a.fec_fecha >= Convert(date, @FechaInicial, 103) And A.fec_fecha <= Convert(date, @FechaFinal, 103)) And 
	Ltrim(Rtrim(b.cod_tercero)) = @Codigo And 
	a.txt_anoproceso=SUBSTRING(@FechaFinal,7,4) and
	A.flg_enviado=0 and
	A.cod_tipomoneda=0
	Group By B.cod_tercero,A.cod_tipomoneda

  --USD
	Select @SaldoUSD_banco=isnull((Sum(b.num_haberdolar)-Sum(b.num_debedolar)),0)
	From scp_bancocabecera A
	INNER JOIN scp_bancodetalle B ON A.txt_anoproceso = B.txt_anoproceso And
	A.cod_mes+A.ind_tipocuenta+A.txt_correlativo=B.cod_mes+B.ind_tipocuenta+B.txt_correlativo 
	Where (a.fec_fecha >= Convert(date, @FechaInicial, 103) And A.fec_fecha <= Convert(date, @FechaFinal, 103)) And 
	Ltrim(Rtrim(b.cod_tercero)) = @Codigo And 
	a.txt_anoproceso=SUBSTRING(@FechaFinal,7,4) and
	A.flg_enviado=0 and
	A.cod_tipomoneda=1
	Group By B.cod_tercero,A.cod_tipomoneda

  --EUR
	Select @SaldoEUR_banco=isnull((Sum(b.num_habermo)-Sum(b.num_debemo)),0)
	From scp_bancocabecera A
	INNER JOIN scp_bancodetalle B ON A.txt_anoproceso = B.txt_anoproceso And
	A.cod_mes+A.ind_tipocuenta+A.txt_correlativo=B.cod_mes+B.ind_tipocuenta+B.txt_correlativo 
	Where (a.fec_fecha >= Convert(date, @FechaInicial, 103) And A.fec_fecha <= Convert(date, @FechaFinal, 103)) And 
	Ltrim(Rtrim(b.cod_tercero)) = @Codigo And 
	a.txt_anoproceso=SUBSTRING(@FechaFinal,7,4) and
	A.flg_enviado=0 and
	A.cod_tipomoneda=2
	Group By B.cod_tercero,A.cod_tipomoneda
END

Print 'Bancos PEN: '+CONVERT(char(14),@SaldoPEN_banco ,121)
+' USD: '+CONVERT(char(14),@SaldoUSD_banco ,121)
+' EUR: '+CONVERT(char(14),@SaldoEUR_banco ,121)

/*

Exec usp_scp_vsj_getSaldoProyectoAlDia_NoEnviadosBancos 2,'01/01/2016','09/09/2016','190420',0,0,0

*/


GO

--USE [SCP]
GO

/****** Object:  StoredProcedure [dbo].[usp_scp_vsj_getSaldoAlDia_NoEnviadosCaja]    Script Date: 09/12/2016 10:06:29 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[usp_scp_vsj_getSaldoAlDia_NoEnviadosCaja]
(@Tipo int, --1 Proyecto, 2 Tercero
 @FechaInicial char(10),
 @FechaFinal varchar(10),
 @Codigo varchar(6), 
 @SaldoPEN_caja decimal(12,2) OUTPUT,	
 @SaldoUSD_caja decimal(12,2) OUTPUT,
 @SaldoEUR_caja decimal(12,2) OUTPUT )

As

Set @SaldoPEN_caja=0.00
Set @SaldoUSD_caja=0.00
Set @SaldoEUR_caja=0.00

if (@Tipo=1) -- Proyecto
BEGIN
   --PEN
	Select @SaldoPEN_caja=isnull((Sum(a.num_habersol)-Sum(a.num_debesol)),0)
	From scp_cajabanco a
	Where (a.fec_fecha >= Convert(date, @FechaInicial, 103) And A.fec_fecha <= Convert(date, @FechaFinal, 103)) And
	Ltrim(Rtrim(a.cod_proyecto)) = @Codigo and 
	a.txt_anoproceso=SUBSTRING(@FechaFinal,7,4) and 
	a.cod_tipomoneda=0 and 
	a.flg_enviado=0

  --USD
	Select @SaldoUSD_caja=isnull((Sum(a.num_haberdolar)-Sum(a.num_debedolar)),0)
	From scp_cajabanco a
	Where (a.fec_fecha >= Convert(date, @FechaInicial, 103) And A.fec_fecha <= Convert(date, @FechaFinal, 103)) And
	Ltrim(Rtrim(a.cod_proyecto)) = @Codigo and 
	a.txt_anoproceso=SUBSTRING(@FechaFinal,7,4) and 
	a.cod_tipomoneda=1 and 
	a.flg_enviado=0
END

else if(@Tipo=2)-- TERCERO

BEGIN
  --PEN
	Select @SaldoPEN_caja=isnull((Sum(a.num_habersol)-Sum(a.num_debesol)),0)
	From scp_cajabanco a
	Where 
	(a.fec_fecha >= Convert(date, @FechaInicial, 103) And A.fec_fecha <= Convert(date, @FechaFinal, 103)) And
	isnull(Ltrim(Rtrim(a.cod_tercero)),0) = @Codigo and 
	a.txt_anoproceso=SUBSTRING(@FechaFinal,7,4)  
	and a.cod_tipomoneda=0
	and a.flg_enviado=0

  --USD
	Select @SaldoUSD_caja=isnull((Sum(a.num_haberdolar)-Sum(a.num_debedolar)),0)
	From scp_cajabanco a
	Where (a.fec_fecha >= Convert(date, @FechaInicial, 103) And A.fec_fecha <= Convert(date, @FechaFinal, 103)) And
	isnull(Ltrim(Rtrim(a.cod_tercero)),0) = @Codigo and 
	a.txt_anoproceso=SUBSTRING(@FechaFinal,7,4)  
	and a.cod_tipomoneda=1
	and a.flg_enviado=0
END
Print 'Caja PEN:'+CONVERT(char(14),@SaldoPEN_caja ,121)+' USD:'+CONVERT(char(14),@SaldoUSD_caja ,121)+' EUR:'+CONVERT(char(14),@SaldoEUR_caja,121)

/*

Exec usp_scp_vsj_getSaldoProyectoAlDia_NoEnviadosCaja 2,'01/01/2016','09/09/2016','190410',0,0,0

*/
GO

--USE [SCP]
GO
/****** Object:  StoredProcedure [dbo].[usp_scp_vsj_GetSaldoAlDiaBanco]    Script Date: 10/14/2016 03:30:47 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
--USE [SCP]
GO
/****** Object:  StoredProcedure [dbo].[usp_scp_vsj_GetSaldoAlDiaBanco]    Script Date: 10/30/2016 21:21:26 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE FUNCTION [dbo].[fun_scp_vsj_GetSaldosAlDiaBanco] (
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
			From scp_bancocabecera A
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
			From scp_bancocabecera A
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
			From scp_bancocabecera A
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

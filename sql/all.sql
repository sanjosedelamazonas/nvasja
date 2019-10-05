/* To prevent any potential data loss issues, you should review this script in detail before running it outside the context of the database designer.*/
BEGIN TRANSACTION
SET QUOTED_IDENTIFIER ON
SET ARITHABORT ON
SET NUMERIC_ROUNDABORT OFF
SET CONCAT_NULL_YIELDS_NULL ON
SET ANSI_NULLS ON
SET ANSI_PADDING ON
SET ANSI_WARNINGS ON
COMMIT
BEGIN TRANSACTION
GO
ALTER TABLE dbo.scp_bancocabecera
	DROP CONSTRAINT PK_scp_bancocabecera
GO
ALTER TABLE dbo.scp_bancocabecera SET (LOCK_ESCALATION = TABLE)
GO
COMMIT

/* To prevent any potential data loss issues, you should review this script in detail before running it outside the context of the database designer.*/
BEGIN TRANSACTION
SET QUOTED_IDENTIFIER ON
SET ARITHABORT ON
SET NUMERIC_ROUNDABORT OFF
SET CONCAT_NULL_YIELDS_NULL ON
SET ANSI_NULLS ON
SET ANSI_PADDING ON
SET ANSI_WARNINGS ON
COMMIT
BEGIN TRANSACTION
GO
ALTER TABLE dbo.scp_bancocabecera ADD
	cod_bancocabecera [int] IDENTITY(1,1) NOT NULL ,
	ind_cobrado bit NULL DEFAULT 0,
	flg_Anula char(1) NULL DEFAULT '0',
	cod_mescobrado char(2) NULL
GO
ALTER TABLE dbo.scp_bancocabecera ADD CONSTRAINT
	PK_scp_bancocabecera PRIMARY KEY CLUSTERED
	(
	cod_bancocabecera
	) WITH( STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]

GO
ALTER TABLE dbo.scp_bancocabecera SET (LOCK_ESCALATION = TABLE)
GO
COMMIT
update [dbo].[scp_bancocabecera] set
	[ind_cobrado ] = 0,
	[flg_Anula] =0,
	[cod_mescobrado]=''
	where 1=1

-- BANCO DETALLE

/* To prevent any potential data loss issues, you should review this script in detail before running it outside the context of the database designer.*/
BEGIN TRANSACTION
SET QUOTED_IDENTIFIER ON
SET ARITHABORT ON
SET NUMERIC_ROUNDABORT OFF
SET CONCAT_NULL_YIELDS_NULL ON
SET ANSI_NULLS ON
SET ANSI_PADDING ON
SET ANSI_WARNINGS ON
COMMIT
BEGIN TRANSACTION
GO
ALTER TABLE dbo.scp_bancodetalle ADD
	cod_tipomov int NULL,
	cod_bancocabecera int NOT NULL DEFAULT 0
GO
ALTER TABLE dbo.scp_bancodetalle
	DROP CONSTRAINT PK_scp_bancodetalle
GO

UPDATE bd SET bd.cod_bancocabecera = cb.cod_bancocabecera FROM  scp_bancodetalle AS bd INNER JOIN scp_bancocabecera AS cb ON
cb.txt_anoproceso = bd.txt_anoproceso AND cb.flg_saldo=bd.flg_saldo AND
cb.ind_tipocuenta=bd.ind_tipocuenta AND cb.txt_correlativo=bd.txt_correlativo

ALTER TABLE dbo.scp_bancodetalle ADD CONSTRAINT
	PK_scp_bancodetalle PRIMARY KEY CLUSTERED
	(
	num_item,
	cod_bancocabecera
	) WITH( STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]

GO
ALTER TABLE dbo.scp_bancodetalle SET (LOCK_ESCALATION = TABLE)
GO
COMMIT
;
update [dbo].[scp_bancodetalle] set
	[cod_tipomov] =0
	where 1=1;	/* To prevent any potential data loss issues, you should review this script in detail before running it outside the context of the database designer.*/
BEGIN TRANSACTION
SET QUOTED_IDENTIFIER ON
SET ARITHABORT ON
SET NUMERIC_ROUNDABORT OFF
SET CONCAT_NULL_YIELDS_NULL ON
SET ANSI_NULLS ON
SET ANSI_PADDING ON
SET ANSI_WARNINGS ON
COMMIT
BEGIN TRANSACTION

ALTER TABLE dbo.scp_cajabanco SET (LOCK_ESCALATION = TABLE)
GO
ALTER TABLE dbo.scp_cajabanco
	DROP CONSTRAINT PK_scp_cajabanco
GO
ALTER TABLE dbo.scp_cajabanco SET (LOCK_ESCALATION = TABLE)
GO

Alter TABLE [dbo].[scp_cajabanco] add
	[cod_cajabanco] [int] IDENTITY(1,1) NOT NULL ,
	[cod_transcorrelativo] [varchar](255) NULL DEFAULT '',
	[cod_tipomov] [int] NULL DEFAULT 0
;
GO
ALTER TABLE dbo.scp_cajabanco ADD CONSTRAINT
	PK_scp_cajabanco PRIMARY KEY CLUSTERED
	(
	cod_cajabanco
	) WITH( STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]

GO
ALTER TABLE dbo.scp_cajabanco SET (LOCK_ESCALATION = TABLE)
GO
COMMIT
;
update [dbo].[scp_cajabanco] set
	[cod_transcorrelativo] ='',
	[cod_tipomov] =0
	where 1=1;
;
GO
;
create function [dbo].[usp_vsj_cajabanco_gen_correlativo](@id int)
returns char(8)
as
begin
return right('00000000' + convert(varchar(10), @id), 8)
end
GO
;
create trigger [dbo].[vsj_scp_cajabanco_insert] on [dbo].[scp_cajabanco]
after insert as
update
    scp_cajabanco
set
    scp_cajabanco.txt_correlativo = dbo.usp_vsj_cajabanco_gen_correlativo(scp_cajabanco.cod_cajabanco)
from
    scp_cajabanco
inner join
    inserted on scp_cajabanco.cod_cajabanco = inserted.cod_cajabanco
GO
/* To prevent any potential data loss issues, you should review this script in detail before running it outside the context of the database designer.*/
BEGIN TRANSACTION
SET QUOTED_IDENTIFIER ON
SET ARITHABORT ON
SET NUMERIC_ROUNDABORT OFF
SET CONCAT_NULL_YIELDS_NULL ON
SET ANSI_NULLS ON
SET ANSI_PADDING ON
SET ANSI_WARNINGS ON
COMMIT
BEGIN TRANSACTION
GO
ALTER TABLE [dbo].[scp_rendicioncabecera] DROP CONSTRAINT [PK_scp_rendicioncabecera] WITH ( ONLINE = OFF )
GO
ALTER TABLE dbo.scp_rendicioncabecera SET (LOCK_ESCALATION = TABLE)
GO
COMMIT

/* To prevent any potential data loss issues, you should review this script in detail before running it outside the context of the database designer.*/
BEGIN TRANSACTION
SET QUOTED_IDENTIFIER ON
SET ARITHABORT ON
SET NUMERIC_ROUNDABORT OFF
SET CONCAT_NULL_YIELDS_NULL ON
SET ANSI_NULLS ON
SET ANSI_PADDING ON
SET ANSI_WARNINGS ON
COMMIT
BEGIN TRANSACTION
GO
ALTER TABLE dbo.scp_rendicioncabecera ADD
	cod_rendicioncabecera [int] IDENTITY(1,1) NOT NULL
GO
ALTER TABLE dbo.scp_rendicioncabecera ADD CONSTRAINT
	PK_scp_rendicioncabecera PRIMARY KEY CLUSTERED
	(
	cod_rendicioncabecera
	) WITH( STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]

GO
ALTER TABLE dbo.scp_rendicioncabecera SET (LOCK_ESCALATION = TABLE)
GO
COMMIT
-- rendicion DETALLE

/* To prevent any potential data loss issues, you should review this script in detail before running it outside the context of the database designer.*/
BEGIN TRANSACTION
SET QUOTED_IDENTIFIER ON
SET ARITHABORT ON
SET NUMERIC_ROUNDABORT OFF
SET CONCAT_NULL_YIELDS_NULL ON
SET ANSI_NULLS ON
SET ANSI_PADDING ON
SET ANSI_WARNINGS ON
COMMIT
BEGIN TRANSACTION
GO
ALTER TABLE dbo.scp_rendiciondetalle ADD
	cod_tipomov int NULL,
	cod_rendicioncabecera int NOT NULL DEFAULT 0
GO

ALTER TABLE SCP.dbo.scp_rendicioncabecera ALTER COLUMN cod_comprobante varchar(6)
GO

ALTER TABLE [dbo].[scp_rendiciondetalle] DROP CONSTRAINT [PK_scp_rendiciondetalle] WITH ( ONLINE = OFF )
GO

UPDATE bd SET bd.cod_rendicioncabecera = cb.cod_rendicioncabecera FROM scp_rendiciondetalle AS bd INNER JOIN scp_rendicioncabecera AS cb ON
cb.txt_anoproceso = bd.txt_anoproceso AND cb.cod_filial=bd.cod_filial AND
cb.cod_mes = bd.cod_mes AND cb.cod_origen =bd.cod_origen AND cb.cod_comprobante = bd.cod_comprobante

ALTER TABLE dbo.scp_rendiciondetalle ADD CONSTRAINT
	PK_scp_rendiciondetalle PRIMARY KEY CLUSTERED
	(
	num_nroitem,
	cod_rendicioncabecera
	) WITH( STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]

GO
ALTER TABLE dbo.scp_rendiciondetalle SET (LOCK_ESCALATION = TABLE)
GO
COMMIT
;
update [dbo].[scp_rendiciondetalle] set
	[cod_tipomov] =0
	where 1=1;PRINT '1. Alter DONE'
DROP INDEX IF EXISTS scp_bancocabecera_cod_destino_index ON SCP.dbo.scp_bancocabecera
GO
CREATE INDEX scp_bancocabecera_cod_destino_index ON SCP.dbo.scp_bancocabecera (cod_destino);
GO
DROP INDEX IF EXISTS scp_bancocabecera_fec_fecha_index ON SCP.dbo.scp_bancocabecera
GO
CREATE INDEX scp_bancocabecera_fec_fecha_index ON SCP.dbo.scp_bancocabecera (fec_fecha DESC);
GO
DROP INDEX IF EXISTS scp_bancocabecera_fec_fecha_cod_ctacontable_index ON SCP.dbo.scp_bancocabecera;
GO
CREATE INDEX scp_bancocabecera_fec_fecha_cod_ctacontable_index ON SCP.dbo.scp_bancocabecera (fec_fecha DESC, cod_ctacontable);
GO
DROP INDEX IF EXISTS scp_bancocabecera_ano_flg_enviado_cod_tipomoneda_cod_mes_txt_correlativo_ind_tipocuenta_index ON SCP.dbo.scp_bancocabecera
GO
CREATE INDEX scp_bancocabecera_ano_flg_enviado_cod_tipomoneda_cod_mes_txt_correlativo_ind_tipocuenta_index ON SCP.dbo.scp_bancocabecera (txt_anoproceso, flg_enviado, cod_tipomoneda, cod_mes, txt_correlativo, ind_tipocuenta)
GO
GO
DROP INDEX IF EXISTS scp_bancodetalle_cod_bancocabecera_index ON SCP.dbo.scp_bancodetalle ;
GO
CREATE INDEX scp_bancodetalle_cod_bancocabecera_index ON SCP.dbo.scp_bancodetalle (cod_bancocabecera);
GO
DROP INDEX IF EXISTS scp_bancodetalle_txt_anoproceso_cod_mes_ind_tipocuenta_txt_correlativo_index ON SCP.dbo.scp_bancodetalle;
GO
CREATE INDEX scp_bancodetalle_txt_anoproceso_cod_mes_ind_tipocuenta_txt_correlativo_index ON SCP.dbo.scp_bancodetalle (txt_anoproceso, cod_mes, ind_tipocuenta, txt_correlativo);
GO
DROP INDEX IF EXISTS scp_cajabanco_cod_destino_cod_destinoitem_index ON SCP.dbo.scp_cajabanco ;
GO
CREATE INDEX scp_cajabanco_cod_destino_cod_destinoitem_index ON SCP.dbo.scp_cajabanco (cod_destino, cod_destinoitem);
GO
DROP INDEX IF EXISTS scp_cajabanco_cod_transcorrelativo_index ON SCP.dbo.scp_cajabanco ;
GO
CREATE INDEX scp_cajabanco_cod_transcorrelativo_index ON SCP.dbo.scp_cajabanco (cod_transcorrelativo)
GO
DROP INDEX IF EXISTS scp_cajabanco_fec_fecha_index ON SCP.dbo.scp_cajabanco ;
GO
CREATE INDEX scp_cajabanco_fec_fecha_index ON SCP.dbo.scp_cajabanco (fec_fecha DESC);
GO
DROP INDEX IF EXISTS scp_cajabanco_fec_fecha_cod_proyecto_cod_tipomoneda_flg_enviado_index ON SCP.dbo.scp_cajabanco;
GO
CREATE INDEX scp_cajabanco_fec_fecha_cod_proyecto_cod_tipomoneda_flg_enviado_index ON SCP.dbo.scp_cajabanco (fec_fecha, cod_proyecto, cod_tipomoneda, flg_enviado);
GO
GO
DROP INDEX IF EXISTS scp_comprobantedetalle_txt_anoproceso_cod_mes_cod_origen_cod_comprobante_cod_ctacontable_index ON SCP.dbo.scp_comprobantedetalle ;
GO
CREATE INDEX scp_comprobantedetalle_txt_anoproceso_cod_mes_cod_origen_cod_comprobante_cod_ctacontable_index ON SCP.dbo.scp_comprobantedetalle (txt_anoproceso, cod_mes, cod_origen, cod_comprobante, cod_ctacontable)
GO
DROP INDEX IF EXISTS scp_comprobantedetalle_ano_filial_mes_origen_comprobante_ctacontable_proyecto_tipomoneda_idx ON SCP.dbo.scp_comprobantedetalle ;
GO
CREATE INDEX scp_comprobantedetalle_ano_filial_mes_origen_comprobante_ctacontable_proyecto_tipomoneda_idx ON SCP.dbo.scp_comprobantedetalle (txt_anoproceso DESC, cod_filial, cod_mes, cod_origen, cod_comprobante, cod_ctacontable, cod_proyecto, cod_tipomoneda)
GO
DROP INDEX IF EXISTS scp_comprobantedetalle_cod_ctacontable_fec_comprobante_index ON SCP.dbo.scp_comprobantedetalle ;
GO
CREATE INDEX scp_comprobantedetalle_cod_ctacontable_fec_comprobante_index ON SCP.dbo.scp_comprobantedetalle (cod_ctacontable, fec_comprobante);
GO
DROP INDEX IF EXISTS scp_comprobantedetalle_txt_anoproceso_ctacontable_proyecto_origen_mes_tipomoneda_index ON SCP.dbo.scp_comprobantedetalle ;
GO
CREATE INDEX scp_comprobantedetalle_txt_anoproceso_ctacontable_proyecto_origen_mes_tipomoneda_index ON SCP.dbo.scp_comprobantedetalle (txt_anoproceso, cod_ctacontable, cod_proyecto, cod_origen, cod_mes, cod_tipomoneda);
GO
USE SCP
DROP INDEX IF EXISTS  [TipoDestino] ON [dbo].[scp_destino];
GO
CREATE NONCLUSTERED INDEX [TipoDestino] ON [dbo].[scp_destino]
(
	[ind_tipodestino] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON);
GO
USE SCP
DROP INDEX IF EXISTS [FindCuentasInPlanContable] ON [dbo].[scp_plancontable];
GO
CREATE NONCLUSTERED INDEX [FindCuentasInPlanContable] ON [dbo].[scp_plancontable]
(
	[txt_anoproceso] ASC,
	[cod_ctacontable] ASC,
	[flg_movimiento] ASC,
	[flg_estadocuenta] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON);
GO
DROP INDEX IF EXISTS [FindCuentasByAnoAndCtaContable] ON [dbo].[scp_plancontable];
GO
CREATE NONCLUSTERED INDEX [FindCuentasByAnoAndCtaContable] ON [dbo].[scp_plancontable]
    (
     [txt_anoproceso] ASC,
     [cod_ctacontable] ASC
        )WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON);
GO
DROP INDEX IF EXISTS scp_rendicioncabecera_cod_destino_index ON SCP.dbo.scp_rendicioncabecera ;
GO
CREATE INDEX scp_rendicioncabecera_cod_destino_index ON SCP.dbo.scp_rendicioncabecera (cod_destino)
GO
DROP INDEX IF EXISTS scp_rendicioncabecera_fec_comprobante_index ON SCP.dbo.scp_rendicioncabecera;
GO
CREATE INDEX scp_rendicioncabecera_fec_comprobante_index ON SCP.dbo.scp_rendicioncabecera (fec_comprobante DESC);
GO
DROP INDEX IF EXISTS scp_rendiciondetalle_cod_destino__index ON SCP.dbo.scp_rendiciondetalle ;
GO
CREATE INDEX scp_rendiciondetalle_cod_destino__index ON SCP.dbo.scp_rendiciondetalle (cod_destino);
GO

PRINT '2. Create Idx DONE'
/****** Object:  Table [dbo].[vsj_configuracioncaja]    Script Date: 09/12/2016 10:02:43 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO
drop table if exists [dbo].[vsj_configuracioncaja];
go
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

/****** Object:  Table [dbo].[vsj_configuractacajabanco]    Script Date: 09/12/2016 10:02:52 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO
drop table if exists [dbo].[vsj_configuractacajabanco];
go

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

/****** Object:  Table [dbo].[vsj_propiedad]    Script Date: 09/08/2016 18:48:05 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO
drop table if exists [dbo].[vsj_propiedad];
go
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


PRINT '3. Create_vsj DONE'
DROP PROCEDURE if exists [dbo].[usp_scp_vsj_enviarAContabilidad];
go
/****** Object:  StoredProcedure [dbo].[usp_scp_vsj_enviarAContabilidad]    Script Date: 10/25/2018 07:52:37 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[usp_scp_vsj_enviarAContabilidad]
	@cod_cajabanco int, --id de operacion de scp_cajabanco
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
		  FROM dbo.scp_cajabanco
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
		  FROM dbo.scp_cajabanco
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
		  FROM dbo.scp_cajabanco
		  where cod_cajabanco=@cod_cajabanco )
		/****************************************************************************
        * Step 6
        * Actualiza scp_cajabanco cuando ya esta en contabilidad la operacion
        ****************************************************************************/
        SELECT  @ErrorStep = 'Error al actualizar el registro de caja'
		 update dbo.scp_cajabanco
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
DROP PROCEDURE if exists [dbo].[usp_scp_vsj_enviarAContabilidadBanco];
go

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
		(SELECT [txt_anoproceso]
			  ,@cod_filial
			  ,[cod_mes]
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
			  ,[num_item]
			  ,fec_fecha
			  ,[cod_tipomoneda]
			  ,isnull([txt_glosaitem],'')
			  ,isnull([cod_destino],'')
			  ,isnull([txt_cheque],'')
			  ,@flg_chequecobrado
			  ,@cod_mescobr
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
			  ,isnull([cod_contracta],'')
			  ,'' --[cod_ctacontable9]
			  ,'' --[cod_ctacontable79]
			  ,'' --[cod_ctaarea]
			  ,'' --[cod_ctaactividad]
			  ,isnull([cod_ctaespecial],'')
			  ,isnull([cod_financiera],'')
			  ,'' --[cod_flujocaja]
			  ,@num_tc_usd --[num_tcvdolar]
			  ,[num_habersol]
			  ,[num_debesol]
			  ,case when @num_tc_usd>0 then [num_habersol]/@num_tc_usd else 0 end
			  ,case when @num_tc_usd>0 then [num_debesol]/@num_tc_usd else 0 end
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
        ,isnull([cod_tercero],'')
			  ,'2' --[flg_im]
			  ,GETDATE()
			  ,@user
			  ,GETDATE()
			  ,@user
		  FROM dbo.scp_bancodetalle
		  where cod_bancocabecera=@cod_bancocabecera )

		/****************************************************************************
        * Step 4b)
        * Inserta secunda linea del comprobante PEN
        ****************************************************************************/

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
		   (select --insertar linea del banco cta 104 o 106..
			 bd.[txt_anoproceso]
			  ,@cod_filial
			  ,bd.[cod_mes]
			  ,@cod_origen
			  ,@cod_comprobante
			  ,[num_item]+1000
			  ,bd.fec_fecha
			  ,bd.[cod_tipomoneda]
		    ,isnull(bd.[txt_glosaitem],'')
			  ,isnull(bd.[cod_destino],'')
			  ,isnull(bd.[txt_cheque],'')
			  ,@flg_chequecobrado
			  ,@cod_mescobr
			  ,isnull(bd.[cod_tipocomprobantepago],'')
			  ,isnull(bd.[txt_seriecomprobantepago],'')
			  ,isnull(bd.[txt_comprobantepago],'')
			  ,isnull(bd.[fec_comprobantepago],'')
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
			  ,isnull(bd.[cod_proyecto],'')
			  ,isnull(bd.[cod_ctaproyecto],'')
			  ,isnull(bc.[cod_ctacontable],'')
			  ,'' --[cod_ctacontable9]
			  ,'' --[cod_ctacontable79]
			  ,'' --[cod_ctaarea]
			  ,'' --[cod_ctaactividad]
			  ,isnull(bd.[cod_ctaespecial],'')
			  ,isnull(bd.[cod_financiera],'')
        ,'' --[cod_flujocaja]
			  ,@num_tc_usd --[num_tcvdolar]
			  ,bd.[num_debesol]
			  ,bd.[num_habersol]
			  ,case when @num_tc_usd>0 then bd.[num_debesol]/@num_tc_usd else 0 end   --[num_haberdolar]
			  ,case when @num_tc_usd>0 then bd.[num_habersol]/@num_tc_usd else 0 end  --[num_debedolar]
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
			  ,isnull(bd.[cod_contraparte],'')
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
			  ,GETDATE()
			  ,@user
			  ,GETDATE()
			  ,@user
		  FROM dbo.scp_bancodetalle bd, dbo.scp_bancocabecera bc
		  where bd.cod_bancocabecera=@cod_bancocabecera
		  and bc.cod_bancocabecera =bd.cod_bancocabecera )
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
	      ,[num_item]
			  ,fec_fecha
			  ,[cod_tipomoneda]
			  ,isnull([txt_glosaitem],'')
			  ,isnull([cod_destino],'')
			  ,isnull([txt_cheque],'')
			  ,@flg_chequecobrado
			  ,@cod_mescobr
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
			  ,isnull([cod_tercero],'')
			  ,'2' --[flg_im]
			  ,GETDATE()
			  ,@user
			  ,GETDATE()
			  ,@user
		  FROM dbo.scp_bancodetalle
		  where cod_bancocabecera=@cod_bancocabecera )

		  		/****************************************************************************
        * Step 5b)  USD
        * Inserta secunda linea del comprobante USD
        ****************************************************************************/

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
		   (select --insertar linea del banco cta 104 o 106..
			 bd.[txt_anoproceso]
			  ,@cod_filial
			  ,bd.[cod_mes]
			  ,@cod_origen
			  ,@cod_comprobante
			  ,[num_item]+1000
			  ,bd.fec_fecha
			  ,bd.[cod_tipomoneda]
			  ,isnull(bd.[txt_glosaitem],'')
			  ,isnull(bd.[cod_destino],'')
			  ,isnull(bd.[txt_cheque],'')
			  ,@flg_chequecobrado
			  ,@cod_mescobr
			  ,isnull(bd.[cod_tipocomprobantepago],'')
			  ,isnull(bd.[txt_seriecomprobantepago],'')
			  ,isnull(bd.[txt_comprobantepago],'')
			  ,isnull(bd.[fec_comprobantepago],'')
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
			  ,isnull(bd.[cod_proyecto],'')
			  ,isnull(bd.[cod_ctaproyecto],'')
			  ,isnull(bc.[cod_ctacontable],'')
			  ,'' --[cod_ctacontable9]
			  ,'' --[cod_ctacontable79]
			  ,'' --[cod_ctaarea]
			  ,'' --[cod_ctaactividad]
			  ,isnull(bd.[cod_ctaespecial],'')
			  ,isnull(bd.[cod_financiera],'')
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
			  ,isnull(bd.[cod_contraparte],'')
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
			  ,GETDATE()
			  ,@user
			  ,GETDATE()
			  ,@user
		  FROM dbo.scp_bancodetalle bd, dbo.scp_bancocabecera bc
		  where bd.cod_bancocabecera=@cod_bancocabecera
		  and bc.cod_bancocabecera =bd.cod_bancocabecera )
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
				  ,[num_item]
				  ,fec_fecha
				  ,[cod_tipomoneda]
          ,isnull([txt_glosaitem],'')
          ,isnull([cod_destino],'')
          ,isnull([txt_cheque],'')
          ,@flg_chequecobrado
          ,@cod_mescobr
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
				  ,([num_debemo]*@num_tc_mo)
				  ,([num_habermo]*@num_tc_mo)
			    ,case when @num_tc_usd>0 then ([num_debemo]*@num_tc_mo)/@num_tc_usd else 0 end   --[num_haberdolar]
				  ,case when @num_tc_usd>0 then ([num_habermo]*@num_tc_mo)/@num_tc_usd else 0 end  --[num_debedolar]
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
				  ,isnull([cod_tercero],'')
				  ,'2' --[flg_im]
				  ,GETDATE()
				  ,@user
				  ,GETDATE()
				  ,@user
			  FROM dbo.scp_bancodetalle
			  where cod_bancocabecera=@cod_bancocabecera )

		/****************************************************************************
        * Step 6b) EUR
        * Inserta la secunda del comprobante  EUR
        ****************************************************************************/

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
			   (select --insertar linea del banco cta 104 o 106..
				 bd.[txt_anoproceso]
				  ,@cod_filial
				  ,bd.[cod_mes]
				  ,@cod_origen
				  ,@cod_comprobante
				  ,[num_item]+1000
				  ,bd.fec_fecha
				  ,bd.[cod_tipomoneda]
				  ,isnull(bd.[txt_glosaitem],'')
				  ,isnull(bd.[cod_destino],'')
				  ,isnull(bd.[txt_cheque],'')
          ,@flg_chequecobrado
          ,@cod_mescobr
				  ,isnull(bd.[cod_tipocomprobantepago],'')
				  ,isnull(bd.[txt_seriecomprobantepago],'')
				  ,isnull(bd.[txt_comprobantepago],'')
				  ,isnull(bd.[fec_comprobantepago],'')
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
				  ,isnull(bd.[cod_proyecto],'')
				  ,isnull(bd.[cod_ctaproyecto],'')
				  ,isnull(bc.[cod_ctacontable],'')
				  ,'' --[cod_ctacontable9]
				  ,'' --[cod_ctacontable79]
				  ,'' --[cod_ctaarea]
				  ,'' --[cod_ctaactividad]
				  ,isnull(bd.[cod_ctaespecial],'')
				  ,isnull(bd.[cod_financiera],'')
				  ,'' --[cod_flujocaja]
				  ,@num_tc_usd --[num_tcvdolar]
				  ,(bd.[num_habermo]*@num_tc_mo)
				  ,(bd.[num_debemo]*@num_tc_mo)
				  ,case when @num_tc_usd>0 then (bd.[num_habermo]*@num_tc_mo)/@num_tc_usd else 0 end  --[num_debedolar]
  		    ,case when @num_tc_usd>0 then (bd.[num_debemo]*@num_tc_mo)/@num_tc_usd else 0 end   --[num_haberdolar]
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
				  ,isnull(bd.[cod_contraparte],'')
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
Exec usp_scp_vsj_enviarAContabilidadBanco 10437,'abork','01/09/2016','0',0
*/
PRINT '4. Cr usp'
/****** Object:  StoredProcedure [dbo].[usp_scp_vsj_getSaldoAlDia]    Script Date: 09/12/2016 10:05:34 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO
DROP PROCEDURE if exists [dbo].[usp_scp_vsj_getSaldoAlDia]
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

Declare @SaldoPEN_inicial decimal(12,2)
Declare @SaldoUSD_inicial decimal(12,2)
Declare @SaldoEUR_inicial decimal(12,2)

Declare @FechaInicial char(10)
Set @FechaInicial='01/01/'+SUBSTRING(@FechaFinal,0,5)
--Set @FechaInicial='01/01/1900'

Set @SaldoPEN = 0.00
Set @SaldoUSD = 0.00
Set @SaldoEUR = 0.00

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

Exec usp_scp_vsj_getSaldoAlDia 2,'09/09/2016','100310', 0,0,0

*/
GO

PRINT '5. Cr proc1'
USE [SCP]
GO
/****** Object:  StoredProcedure [dbo].[usp_scp_vsj_GetSaldoAlDiaBanco]    Script Date: 03/10/2019 22:39:30 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
drop PROCEDURE if exists [dbo].[usp_scp_vsj_GetSaldoAlDiaBanco];
go

ALTER PROCEDURE [dbo].[usp_scp_vsj_GetSaldoAlDiaBanco]
	@Fecha varchar(19), -- Fecha para saldo formato yyyy-dd-mm hh:mi:ss(24h)
--	@FechaFinal varchar(19), -- Fecha para saldo formato yyyy-dd-mm hh:mi:ss(24h)
	@Cuenta varchar(7), -- Cuenta de banco por ejemplo '1040103'
	@Moneda varchar(1),  -- 0 PEN, 1 USD, 2 EUR
	@SaldoLibro decimal(12,2) OUTPUT,
	@SaldoBanco decimal(12,2) OUTPUT
AS

Declare @Ano varchar(4)
Declare @SaldoInicial decimal(12,2)
Declare @ChequesDelAnoAnt decimal(12,2)
Declare @ChequesCont decimal(12,2)
Declare @ChequesNoCobrados decimal(12,2)
Declare @SaldoNoEnviados decimal(12,2)
Declare @SaldoNoEnviadosNocobrados decimal(12,2)

BEGIN

Set @Ano=SUBSTRING(@Fecha,1,4)
Set @SaldoInicial=0.00
Set @ChequesDelAnoAnt=0.00
Set @ChequesCont=0.00
Set @ChequesNoCobrados=0.00
Set @SaldoNoEnviados=0.00
Set @SaldoNoEnviadosNocobrados=0.00
Set @SaldoBanco=0.00
Set @SaldoLibro=0.00

--select @FechaInicial=(SUBSTRING(@Fecha,1,4)+'-01-01 00:00:00')

if (@Moneda='0')
BEGIN
	Select @SaldoNoEnviados= isnull(Sum(A.num_debesol)-Sum(A.num_habersol),0)
	From scp_bancocabecera A
	Where a.txt_anoproceso=@Ano
	And A.cod_ctacontable=@Cuenta
	And a.cod_tipomoneda=@Moneda
	and a.flg_enviado='0'
	and a.fec_fecha <= Convert(datetime, @Fecha, 20)
	Group By A.cod_ctacontable, a.cod_tipomoneda;

	SELECT @SaldoLibro= isnull(Sum(A.num_debesol)-Sum(A.num_habersol),0)+@SaldoNoEnviados
    FROM [SCP].[dbo].[scp_comprobantedetalle] a
    where  a.txt_anoproceso=@Ano
	And a.cod_ctacontable=@Cuenta
	and a.cod_origen in ('02','10')
	and a.cod_mes not in ('13')
	and fec_comprobante<=Convert(datetime, @Fecha, 20);

	--Print 'Saldo libro: '+CONVERT(char(14),@SaldoLibro,14)

	select @ChequesDelAnoAnt= isnull(Sum(A.num_habersol),0)
	From scp_chequependiente a
	Where a.txt_anoproceso=@Ano
	And A.cod_ctacontable=@Cuenta
	and (a.flg_chequecobrado='0' or
	(a.flg_chequecobrado='1'and a.cod_mescobrado>'0'+CONVERT(varchar, month(@Fecha))))
	Group By A.cod_ctacontable

	--Print 'Cheques no cobrados del periodo anterior: '+CONVERT(char(14),@ChequesDelAnoAnt,14)

	SELECT @ChequesNoCobrados= isnull(Sum(A.num_debesol)-Sum(A.num_habersol),0)-@ChequesDelAnoAnt
	--   ,sum([num_debedolar]), sum([num_haberdolar])
    --  ,sum([num_debemo]), sum([num_habermo])
    FROM [SCP].[dbo].[scp_comprobantedetalle] a
    where  a.txt_anoproceso=@Ano
	And a.cod_ctacontable=@Cuenta
	and a.cod_origen='02'
	and (a.flg_chequecobrado='0' or (a.flg_chequecobrado='1' and a.cod_mescobr>'0'+CONVERT(varchar, month(@Fecha))))
	and a.fec_comprobante<=Convert(datetime, @Fecha, 20);

	--Print 'Cheques no cobrados seg contabilidad: '+CONVERT(char(14),@ChequesNoCobrados,14)

	Select @SaldoNoEnviadosNocobrados= isnull(Sum(A.num_debesol)-Sum(A.num_habersol),0)
	From scp_bancocabecera A
	Where a.txt_anoproceso=@Ano
	And A.cod_ctacontable=@Cuenta
	And a.cod_tipomoneda=@Moneda
	and a.flg_enviado='0'
	and (a.ind_cobrado='0' or (a.ind_cobrado='1' and a.cod_mescobrado>month(@Fecha)))
	and a.fec_fecha <= Convert(datetime, @Fecha, 20)
	Group By A.cod_ctacontable, a.cod_tipomoneda;

	--Print 'Cheques del periodo no enviados a cont no cobrados: '+CONVERT(char(14),@SaldoNoEnviadosNocobrados,14)
END

ELse if (@Moneda='1')
BEGIN
	Select @SaldoNoEnviados= isnull(Sum(A.num_debedolar)-Sum(A.num_haberdolar),0)
	From scp_bancocabecera A
	Where a.txt_anoproceso=@Ano
	And A.cod_ctacontable=@Cuenta
	And a.cod_tipomoneda=@Moneda
	and a.flg_enviado='0'
	and a.fec_fecha <= Convert(datetime, @Fecha, 20)
	Group By A.cod_ctacontable, a.cod_tipomoneda;

	SELECT @SaldoLibro= isnull(Sum(A.num_debedolar)-Sum(A.num_haberdolar),0)+@SaldoNoEnviados
    FROM [SCP].[dbo].[scp_comprobantedetalle] a
    where  a.txt_anoproceso=@Ano
	And a.cod_ctacontable=@Cuenta
	and a.cod_origen in ('02','10')
	and a.cod_mes not in ('13')
	and fec_comprobante<=Convert(datetime, @Fecha, 20);

	--Print 'Saldo libro: '+CONVERT(char(14),@SaldoLibro,14)

	select @ChequesDelAnoAnt= isnull(Sum(A.num_haberdolar),0)
	From scp_chequependiente a
	Where a.txt_anoproceso=@Ano
	And A.cod_ctacontable=@Cuenta
	and (a.flg_chequecobrado='0' or
	(a.flg_chequecobrado='1'and a.cod_mescobrado>'0'+CONVERT(varchar, month(@Fecha))))
	Group By A.cod_ctacontable

	--Print 'Cheques no cobrados del periodo anterior: '+CONVERT(char(14),@ChequesDelAnoAnt,14)

	SELECT @ChequesNoCobrados= isnull(Sum(A.num_debedolar)-Sum(A.num_haberdolar),0)-@ChequesDelAnoAnt
	--   ,sum([num_debedolar]), sum([num_haberdolar])
    --  ,sum([num_debemo]), sum([num_habermo])
    FROM [SCP].[dbo].[scp_comprobantedetalle] a
    where  a.txt_anoproceso=@Ano
	And a.cod_ctacontable=@Cuenta
	and a.cod_origen='02'
	and (a.flg_chequecobrado='0' or (a.flg_chequecobrado='1' and a.cod_mescobr>'0'+CONVERT(varchar, month(@Fecha))))
	and a.fec_comprobante<=Convert(datetime, @Fecha, 20);

	--Print 'Cheques no cobrados seg contabilidad: '+CONVERT(char(14),@ChequesNoCobrados,14)

	Select @SaldoNoEnviadosNocobrados= isnull(Sum(A.num_debedolar)-Sum(A.num_haberdolar),0)
	From scp_bancocabecera A
	Where a.txt_anoproceso=@Ano
	And A.cod_ctacontable=@Cuenta
	And a.cod_tipomoneda=@Moneda
	and a.flg_enviado='0'
	and (a.ind_cobrado='0' or (a.ind_cobrado='1' and a.cod_mescobrado>month(@Fecha)))
	and a.fec_fecha <= Convert(datetime, @Fecha, 20)
	Group By A.cod_ctacontable, a.cod_tipomoneda;

	--Print 'Cheques del periodo no enviados a cont no cobrados: '+CONVERT(char(14),@SaldoNoEnviadosNocobrados,14)
END
ELse if (@Moneda='2')
BEGIN
	Select @SaldoNoEnviados= isnull(Sum(A.num_debemo)-Sum(A.num_habermo),0)
	From scp_bancocabecera A
	Where a.txt_anoproceso=@Ano
	And A.cod_ctacontable=@Cuenta
	And a.cod_tipomoneda=@Moneda
	and a.flg_enviado='0'
	and a.fec_fecha <= Convert(datetime, @Fecha, 20)
	Group By A.cod_ctacontable, a.cod_tipomoneda;

	SELECT @SaldoLibro= isnull(Sum(A.num_debemo)-Sum(A.num_habermo),0)+@SaldoNoEnviados
    FROM [SCP].[dbo].[scp_comprobantedetalle] a
    where  a.txt_anoproceso=@Ano
	And a.cod_ctacontable=@Cuenta
	and a.cod_origen in ('02','10')
	and a.cod_mes not in ('13')
	and fec_comprobante<=Convert(datetime, @Fecha, 20);

	--Print 'Saldo libro: '+CONVERT(char(14),@SaldoLibro,14)

	select @ChequesDelAnoAnt= isnull(Sum(A.num_habermo),0)
	From scp_chequependiente a
	Where a.txt_anoproceso=@Ano
	And A.cod_ctacontable=@Cuenta
	and (a.flg_chequecobrado='0' or
	(a.flg_chequecobrado='1'and a.cod_mescobrado>'0'+CONVERT(varchar, month(@Fecha))))
	Group By A.cod_ctacontable

	--Print 'Cheques no cobrados del periodo anterior: '+CONVERT(char(14),@ChequesDelAnoAnt,14)

	SELECT @ChequesNoCobrados= isnull(Sum(A.num_debemo)-Sum(A.num_habermo),0)-@ChequesDelAnoAnt
	--   ,sum([num_debedolar]), sum([num_haberdolar])
    --  ,sum([num_debemo]), sum([num_habermo])
    FROM [SCP].[dbo].[scp_comprobantedetalle] a
    where  a.txt_anoproceso=@Ano
	And a.cod_ctacontable=@Cuenta
	and a.cod_origen='02'
	and (a.flg_chequecobrado='0' or (a.flg_chequecobrado='1' and a.cod_mescobr>'0'+CONVERT(varchar, month(@Fecha))))
	and a.fec_comprobante<=Convert(datetime, @Fecha, 20);

	--Print 'Cheques no cobrados seg contabilidad: '+CONVERT(char(14),@ChequesNoCobrados,14)

	Select @SaldoNoEnviadosNocobrados=isnull(Sum(A.num_debemo)-Sum(A.num_habermo),0)
	From scp_bancocabecera A
	Where a.txt_anoproceso=@Ano
	And A.cod_ctacontable=@Cuenta
	And a.cod_tipomoneda=@Moneda
	and a.flg_enviado='0'
	and (a.ind_cobrado='0' or (a.ind_cobrado='1' and a.cod_mescobrado>month(@Fecha)))
	and a.fec_fecha <= Convert(datetime, @Fecha, 20)
	Group By A.cod_ctacontable, a.cod_tipomoneda;

	--Print 'Cheques del periodo no enviados a cont no cobrados: '+CONVERT(char(14),@SaldoNoEnviadosNocobrados,14)
END
select @SaldoBanco=(@SaldoLibro-@ChequesNoCobrados+@SaldoNoEnviados-@SaldoNoEnviadosNocobrados)
--Print 'Saldo banco: '+CONVERT(char(14),@SaldoBanco,14)
END
-- Exec usp_scp_vsj_GetSaldoAlDiaBanco '2019-07-31 23:59:59','1060104','2',0,0

/****** Object:  StoredProcedure [dbo].[usp_scp_vsj_GetSaldoAlDiaCaja]    Script Date: 09/22/2016 23:08:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
DROP  PROCEDURE  if exists [dbo].[usp_scp_vsj_GetSaldoAlDiaCaja];
GO
CREATE PROCEDURE [dbo].[usp_scp_vsj_GetSaldoAlDiaCaja]
	@Fecha varchar(19), -- Fecha para saldo formato yyyy-mm-dd hh:mi:ss(24h)
	@Cuenta varchar(7), -- Cuenta de caja por ejemplo '1011101'
	@Moneda varchar(1),  -- 0 PEN, 1 USD, 2 EUR
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

BEGIN
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
	From scp_cajabanco A
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
	From scp_cajabanco A
	Where (A.fec_fecha >= Convert(datetime, @FechaInicial, 20) And A.fec_fecha < Convert(datetime, @Fecha, 20))
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

	--Print 'Fechas '+@Ano+'fecha inicial '+@FechaInicial+'fecha input: '+@Fecha
	--Print 'Inicial: '+CONVERT(char(14),@SaldoInicial,14)

	Select @SaldoCaja = Sum(A.num_habermo)-Sum(A.num_debemo)
	From scp_cajabanco A
	Where (A.fec_fecha >= Convert(datetime, @FechaInicial, 20) And A.fec_fecha < Convert(datetime, @Fecha, 20))
	And A.cod_ctacontable=@Cuenta And a.cod_tipomoneda=@Moneda
	Group By A.cod_ctacontable, a.cod_tipomoneda
END

--Print 'Caja : '+CONVERT(char(14),@SaldoCaja,14)

select @Saldo=-(@SaldoInicial+@SaldoCaja)

--Print 'Saldo : '+CONVERT(char(14),@Saldo,14)

-- Exec usp_scp_vsj_GetSaldoAlDiaCaja '2016-08-31 23:59:59','1011101','0',0
-- Exec usp_scp_vsj_GetSaldoAlDiaCaja '2016-08-31 00:00:00','1011101','0',0

END
GO
;/****** Object:  StoredProcedure [dbo].[usp_scp_vsj_getCociliacionDeSaldos]    Script Date: 09/12/2016 10:05:09 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO
DROP  PROCEDURE  if exists [dbo].[usp_scp_vsj_getCociliacionDeSaldos];
GO
CREATE PROCEDURE [dbo].[usp_scp_vsj_getCociliacionDeSaldos]
	@Tipo char(1), -- 0 proyecto, 1 tercero
	@FechaFinal varchar(10),            -- Fecha para saldo formato dd/mm/yyyy
	@Codigo varchar(6),		-- codigo del proyecto o tercero
	@SaldoPEN decimal(12,2) OUTPUT,	
	@SaldoUSD decimal(12,2) OUTPUT,
	@SaldoEUR decimal(12,2) OUTPUT
AS
BEGIN
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

END
/* 

Exec usp_scp_vsj_getCociliacionDeSaldos 1,'31/12/2015','005013',0,0,0

*/
GO
;
/****** Object:  StoredProcedure [dbo].[usp_scp_vsj_getSaldoAlDia_NoEnviadosBancos]    Script Date: 09/12/2016 10:06:13 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO
drop PROCEDURE if exists [dbo].[usp_scp_vsj_getSaldoAlDia_NoEnviadosBancos]
go

CREATE PROCEDURE [dbo].[usp_scp_vsj_getSaldoAlDia_NoEnviadosBancos]
(@Tipo int, -- 0 proyecto, 1 tercero
@FechaInicial char(10),
 @FechaFinal char(19),
 @Codigo varchar(6),
 @SaldoPEN_banco decimal(12,2) OUTPUT,
 @SaldoUSD_banco decimal(12,2) OUTPUT,
 @SaldoEUR_banco decimal(12,2) OUTPUT )

As
BEGIN
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
	Where (a.fec_fecha >= Convert(date, @FechaInicial, 103) And A.fec_fecha <= Convert(date, @FechaFinal, 120)) And
	Ltrim(Rtrim(b.cod_proyecto)) = @Codigo And
--	a.txt_anoproceso=SUBSTRING(@FechaFinal,7,4) and
	A.flg_enviado=0 and
	A.cod_tipomoneda=0
	Group By B.cod_proyecto,A.cod_tipomoneda

  --USD
	Select @SaldoUSD_banco=isnull((Sum(b.num_haberdolar)-Sum(b.num_debedolar)),0)
	From scp_bancocabecera A
	INNER JOIN scp_bancodetalle B ON A.txt_anoproceso = B.txt_anoproceso And
	A.cod_mes+A.ind_tipocuenta+A.txt_correlativo=B.cod_mes+B.ind_tipocuenta+B.txt_correlativo
	Where (a.fec_fecha >= Convert(date, @FechaInicial, 103) And A.fec_fecha <= Convert(date, @FechaFinal, 120)) And
	Ltrim(Rtrim(b.cod_proyecto)) = @Codigo And
	--a.txt_anoproceso=SUBSTRING(@FechaFinal,7,4) and
	A.flg_enviado=0 and
	A.cod_tipomoneda=1
	Group By B.cod_proyecto,A.cod_tipomoneda

  --EUR
	Select @SaldoEUR_banco=isnull((Sum(b.num_habermo)-Sum(b.num_debemo)),0)
	From scp_bancocabecera A
	INNER JOIN scp_bancodetalle B ON A.txt_anoproceso = B.txt_anoproceso And
	A.cod_mes+A.ind_tipocuenta+A.txt_correlativo=B.cod_mes+B.ind_tipocuenta+B.txt_correlativo
	Where (a.fec_fecha >= Convert(date, @FechaInicial, 103) And A.fec_fecha <= Convert(date, @FechaFinal, 120)) And
	Ltrim(Rtrim(b.cod_proyecto)) = @Codigo And
	--a.txt_anoproceso=SUBSTRING(@FechaFinal,7,4) and
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
	Where (a.fec_fecha >= Convert(date, @FechaInicial, 103) And A.fec_fecha <= Convert(date, @FechaFinal, 120)) And
	Ltrim(Rtrim(b.cod_tercero)) = @Codigo And
--	a.txt_anoproceso=SUBSTRING(@FechaFinal,7,4) and
	A.flg_enviado=0 and
	A.cod_tipomoneda=0
	Group By B.cod_tercero,A.cod_tipomoneda

  --USD
	Select @SaldoUSD_banco=isnull((Sum(b.num_haberdolar)-Sum(b.num_debedolar)),0)
	From scp_bancocabecera A
	INNER JOIN scp_bancodetalle B ON A.txt_anoproceso = B.txt_anoproceso And
	A.cod_mes+A.ind_tipocuenta+A.txt_correlativo=B.cod_mes+B.ind_tipocuenta+B.txt_correlativo
	Where (a.fec_fecha >= Convert(date, @FechaInicial, 103) And A.fec_fecha <= Convert(date, @FechaFinal, 120)) And
	Ltrim(Rtrim(b.cod_tercero)) = @Codigo And
--	a.txt_anoproceso=SUBSTRING(@FechaFinal,7,4) and
	A.flg_enviado=0 and
	A.cod_tipomoneda=1
	Group By B.cod_tercero,A.cod_tipomoneda

  --EUR
	Select @SaldoEUR_banco=isnull((Sum(b.num_habermo)-Sum(b.num_debemo)),0)
	From scp_bancocabecera A
	INNER JOIN scp_bancodetalle B ON A.txt_anoproceso = B.txt_anoproceso And
	A.cod_mes+A.ind_tipocuenta+A.txt_correlativo=B.cod_mes+B.ind_tipocuenta+B.txt_correlativo
	Where (a.fec_fecha >= Convert(date, @FechaInicial, 103) And A.fec_fecha <= Convert(date, @FechaFinal, 120)) And
	Ltrim(Rtrim(b.cod_tercero)) = @Codigo And 
	--a.txt_anoproceso=SUBSTRING(@FechaFinal,7,4) and
	A.flg_enviado=0 and
	A.cod_tipomoneda=2
	Group By B.cod_tercero,A.cod_tipomoneda
END

--Print 'Bancos PEN: '+CONVERT(char(14),@SaldoPEN_banco ,121)
--+' USD: '+CONVERT(char(14),@SaldoUSD_banco ,121)
--+' EUR: '+CONVERT(char(14),@SaldoEUR_banco ,121)
END
/*

Exec usp_scp_vsj_getSaldoProyectoAlDia_NoEnviadosBancos 2,'01/01/2016','09/09/2016','190420',0,0,0

*/

GO
;

GO

/****** Object:  StoredProcedure [dbo].[usp_scp_vsj_getSaldoAlDia_NoEnviadosCaja]    Script Date: 09/12/2016 10:06:29 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO
drop PROCEDURE if exists [dbo].[usp_scp_vsj_getSaldoAlDia_NoEnviadosCaja]
go
CREATE PROCEDURE [dbo].[usp_scp_vsj_getSaldoAlDia_NoEnviadosCaja]
(@Tipo int, --1 Proyecto, 2 Tercero
 @FechaInicial char(10),
 @FechaFinal varchar(10),
 @Codigo varchar(6),
 @SaldoPEN_caja decimal(12,2) OUTPUT,
 @SaldoUSD_caja decimal(12,2) OUTPUT,
 @SaldoEUR_caja decimal(12,2) OUTPUT )

As
BEGIN
Set @SaldoPEN_caja=0.00
Set @SaldoUSD_caja=0.00
Set @SaldoEUR_caja=0.00



if (@Tipo=1) -- Proyecto
BEGIN
   --PEN
	Select @SaldoPEN_caja=isnull((Sum(a.num_habersol)-Sum(a.num_debesol)),0)
	From scp_cajabanco a
	Where (a.fec_fecha >= Convert(date, @FechaInicial, 103) And A.fec_fecha <= Convert(date, @FechaFinal, 120)) And
	Ltrim(Rtrim(a.cod_proyecto)) = @Codigo and
--	a.txt_anoproceso=SUBSTRING(@FechaFinal,7,4) and
	a.cod_tipomoneda=0 and
	a.flg_enviado=0

  --USD
	Select @SaldoUSD_caja=isnull((Sum(a.num_haberdolar)-Sum(a.num_debedolar)),0)
	From scp_cajabanco a
	Where (a.fec_fecha >= Convert(date, @FechaInicial, 103) And A.fec_fecha <= Convert(date, @FechaFinal, 120)) And
	Ltrim(Rtrim(a.cod_proyecto)) = @Codigo and
--	a.txt_anoproceso=SUBSTRING(@FechaFinal,7,4) and
	a.cod_tipomoneda=1 and
	a.flg_enviado=0

  --EUR
	Select @SaldoEUR_caja=isnull((Sum(a.num_habermo)-Sum(a.num_debemo)),0)
	From scp_cajabanco a
	Where (a.fec_fecha >= Convert(date, @FechaInicial, 103) And A.fec_fecha <= Convert(date, @FechaFinal, 120)) And
	Ltrim(Rtrim(a.cod_proyecto)) = @Codigo and
--	a.txt_anoproceso=SUBSTRING(@FechaFinal,7,4) and
	a.cod_tipomoneda=2 and
	a.flg_enviado=0
END

else if(@Tipo=2)-- TERCERO

BEGIN
  --PEN
	Select @SaldoPEN_caja=isnull((Sum(a.num_habersol)-Sum(a.num_debesol)),0)
	From scp_cajabanco a
	Where
	(a.fec_fecha >= Convert(date, @FechaInicial, 103) And A.fec_fecha <= Convert(date, @FechaFinal, 120)) And
	isnull(Ltrim(Rtrim(a.cod_tercero)),0) = @Codigo and
--	a.txt_anoproceso=SUBSTRING(@FechaFinal,7,4)
	 a.cod_tipomoneda=0
	and a.flg_enviado=0

  --USD
	Select @SaldoUSD_caja=isnull((Sum(a.num_haberdolar)-Sum(a.num_debedolar)),0)
	From scp_cajabanco a
	Where (a.fec_fecha >= Convert(date, @FechaInicial, 103) And A.fec_fecha <= Convert(date, @FechaFinal, 120)) And
	isnull(Ltrim(Rtrim(a.cod_tercero)),0) = @Codigo and
--	a.txt_anoproceso=SUBSTRING(@FechaFinal,7,4)
	 a.cod_tipomoneda=1
	and a.flg_enviado=0

  --EUR
	Select @SaldoEUR_caja=isnull((Sum(a.num_habermo)-Sum(a.num_debemo)),0)
	From scp_cajabanco a
	Where (a.fec_fecha >= Convert(date, @FechaInicial, 103) And A.fec_fecha <= Convert(date, @FechaFinal, 120)) And
	isnull(Ltrim(Rtrim(a.cod_tercero)),0) = @Codigo and
--	a.txt_anoproceso=SUBSTRING(@FechaFinal,7,4)
	a.cod_tipomoneda=2
	and a.flg_enviado=0
END
--Print 'Caja PEN:'+CONVERT(char(14),@SaldoPEN_caja ,121)+' USD:'+CONVERT(char(14),@SaldoUSD_caja ,121)+' EUR:'+CONVERT(char(14),@SaldoEUR_caja,121)

END
/*

Exec usp_scp_vsj_getSaldoProyectoAlDia_NoEnviadosCaja 2,'01/01/2016','09/09/2016','190410',0,0,0

*/
GO
;
/****** Object:  StoredProcedure [dbo].[usp_scp_vsj_getSaldoAlDia_contabilidad]    Script Date: 09/12/2016 10:05:54 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO
drop PROCEDURE if exists [dbo].[usp_scp_vsj_getSaldoAlDia_contabilidad]
GO

CREATE PROCEDURE [dbo].[usp_scp_vsj_getSaldoAlDia_contabilidad]
(@Tipo int, -- 1 proyecto, 2 tercero
 @FechaInicial varchar(10),
 @FechaFinal varchar(19),
 @Codigo varchar(6),
 @SaldoPEN_contabilidad decimal(12,2) OUTPUT,
 @SaldoUSD_contabilidad decimal(12,2) OUTPUT,
 @SaldoEUR_contabilidad decimal(12,2) OUTPUT )

As
BEGIN

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
			where Substring(Ltrim(cod_ctacontable),1,3) in ('101','104','106')and cod_mes not in ('00','13') and
			(fec_comprobante >= Convert(date, @FechaInicial, 103) And fec_comprobante <= Convert(date, @FechaFinal, 120))) b
	Where
	a.[txt_anoproceso]+a.[cod_filial]+a.[cod_mes]+a.[cod_origen]+a.[cod_comprobante]=b.codigo_comprobante and
	(Substring(Ltrim(a.cod_ctacontable),1,3)<>'101' And Substring(Ltrim(a.cod_ctacontable),1,3)<>'104'
	And Substring(Ltrim(a.cod_ctacontable),1,3)<>'106') And
	Ltrim(Rtrim(a.cod_proyecto)) = @Codigo and
	--a.txt_anoproceso=SUBSTRING(@FechaFinal,7,4) And
	a.cod_tipomoneda=0
	Group By a.cod_tipomoneda,a.cod_proyecto

 --USD
	Select @SaldoUSD_contabilidad=isnull((Sum(a.num_haberdolar)-Sum(a.num_debedolar)),0)
	From scp_comprobantedetalle a,
		(SELECT distinct [txt_anoproceso]+[cod_filial]+[cod_mes]+[cod_origen]+[cod_comprobante] codigo_comprobante
			FROM [SCP].[dbo].[scp_comprobantedetalle]
			where Substring(Ltrim(cod_ctacontable),1,3) in ('101','104','106')and cod_mes not in ('00','13')  and
			(fec_comprobante >= Convert(date, @FechaInicial, 103) And fec_comprobante <= Convert(date, @FechaFinal, 120))) b
	Where
	a.[txt_anoproceso]+a.[cod_filial]+a.[cod_mes]+a.[cod_origen]+a.[cod_comprobante]=b.codigo_comprobante and
	(Substring(Ltrim(a.cod_ctacontable),1,3)<>'101' And Substring(Ltrim(a.cod_ctacontable),1,3)<>'104'
	And Substring(Ltrim(a.cod_ctacontable),1,3)<>'106') And
	Ltrim(Rtrim(a.cod_proyecto)) = @Codigo and
--	a.txt_anoproceso=SUBSTRING(@FechaFinal,7,4) And
	a.cod_tipomoneda=1
	Group By a.cod_tipomoneda,a.cod_proyecto

  --EUR
	Select @SaldoEUR_contabilidad=isnull((Sum(a.num_habermo)-Sum(a.num_debemo)),0)
	From scp_comprobantedetalle a,
	(SELECT distinct [txt_anoproceso]+[cod_filial]+[cod_mes]+[cod_origen]+[cod_comprobante] codigo_comprobante
			FROM [SCP].[dbo].[scp_comprobantedetalle]
			where Substring(Ltrim(cod_ctacontable),1,3) in ('101','104','106') and cod_mes not in ('00','13')  and
			(fec_comprobante >= Convert(date, @FechaInicial, 103) And fec_comprobante <= Convert(date, @FechaFinal, 120))) b
	where
	a.[txt_anoproceso]+a.[cod_filial]+a.[cod_mes]+a.[cod_origen]+a.[cod_comprobante]=b.codigo_comprobante and
	(Substring(Ltrim(a.cod_ctacontable),1,3)<>'101' And Substring(Ltrim(a.cod_ctacontable),1,3)<>'104'
	And Substring(Ltrim(a.cod_ctacontable),1,3)<>'106') And
	Ltrim(Rtrim(a.cod_proyecto)) = @Codigo
	--a.txt_anoproceso=SUBSTRING(@FechaFinal,7,4) And
    and a.cod_tipomoneda=2
	Group By a.cod_tipomoneda,a.cod_proyecto
END
else if (@Tipo=2)
BEGIN
  --PEN
	Select @SaldoPEN_contabilidad=isnull((Sum(a.num_habersol)-Sum(a.num_debesol)),0)
	From scp_comprobantedetalle a,
			(SELECT distinct [txt_anoproceso]+[cod_filial]+[cod_mes]+[cod_origen]+[cod_comprobante] codigo_comprobante
			FROM [SCP].[dbo].[scp_comprobantedetalle]
			where Substring(Ltrim(cod_ctacontable),1,3) in ('101','104','106') and cod_mes not in ('00','13')  and
			(fec_comprobante >= Convert(date, @FechaInicial, 103) And fec_comprobante <= Convert(date, @FechaFinal, 120))) b
	Where
	a.[txt_anoproceso]+a.[cod_filial]+a.[cod_mes]+a.[cod_origen]+a.[cod_comprobante]=b.codigo_comprobante and
	(Substring(Ltrim(a.cod_ctacontable),1,3)<>'101' And Substring(Ltrim(a.cod_ctacontable),1,3)<>'104'
	And Substring(Ltrim(a.cod_ctacontable),1,3)<>'106') And
	Ltrim(Rtrim(a.cod_tercero)) = @Codigo and
--	a.txt_anoproceso=SUBSTRING(@FechaFinal,7,4) And
	a.cod_tipomoneda=0
	Group By a.cod_tipomoneda,a.cod_tercero

 --USD
	Select @SaldoUSD_contabilidad=isnull((Sum(a.num_haberdolar)-Sum(a.num_debedolar)),0)
	From scp_comprobantedetalle a,
			(SELECT distinct [txt_anoproceso]+[cod_filial]+[cod_mes]+[cod_origen]+[cod_comprobante] codigo_comprobante
			FROM [SCP].[dbo].[scp_comprobantedetalle]
			where Substring(Ltrim(cod_ctacontable),1,3) in ('101','104','106')and cod_mes not in ('00','13')  and
			(fec_comprobante >= Convert(date, @FechaInicial, 103) And fec_comprobante <= Convert(date, @FechaFinal, 120))) b
	Where
	a.[txt_anoproceso]+a.[cod_filial]+a.[cod_mes]+a.[cod_origen]+a.[cod_comprobante]=b.codigo_comprobante and
	(Substring(Ltrim(a.cod_ctacontable),1,3)<>'101' And Substring(Ltrim(a.cod_ctacontable),1,3)<>'104'
	And Substring(Ltrim(a.cod_ctacontable),1,3)<>'106') And
	Ltrim(Rtrim(a.cod_tercero)) = @Codigo and
--	a.txt_anoproceso=SUBSTRING(@FechaFinal,7,4) And
	a.cod_tipomoneda=1
	Group By a.cod_tipomoneda,a.cod_tercero

  --EUR
	Select @SaldoEUR_contabilidad=isnull((Sum(a.num_habermo)-Sum(a.num_debemo)),0)
	From scp_comprobantedetalle a,
			(SELECT distinct [txt_anoproceso]+[cod_filial]+[cod_mes]+[cod_origen]+[cod_comprobante] codigo_comprobante
			FROM [SCP].[dbo].[scp_comprobantedetalle]
			where Substring(Ltrim(cod_ctacontable),1,3) in ('101','104','106')and
				cod_mes not in ('00','13')  and
			(fec_comprobante >= Convert(date, @FechaInicial, 103) And fec_comprobante <= Convert(date, @FechaFinal, 120))) b
	Where
	a.[txt_anoproceso]+a.[cod_filial]+a.[cod_mes]+a.[cod_origen]+a.[cod_comprobante]=b.codigo_comprobante and
	(Substring(Ltrim(a.cod_ctacontable),1,3)<>'101' And Substring(Ltrim(a.cod_ctacontable),1,3)<>'104'
	And Substring(Ltrim(a.cod_ctacontable),1,3)<>'106') And
	Ltrim(Rtrim(a.cod_tercero)) = @Codigo and
--	a.txt_anoproceso=SUBSTRING(@FechaFinal,7,4) And
	a.cod_tipomoneda=2
	Group By a.cod_tipomoneda,a.cod_tercero
END

--Print 'Contabilidad  PEN:'+CONVERT(char(14),@SaldoPEN_contabilidad ,121)
--+' USD:'+CONVERT(char(14),@SaldoUSD_contabilidad ,121)
--+' EUR:'+CONVERT(char(14),@SaldoEUR_contabilidad ,121)
END
/*

Exec usp_scp_vsj_getSaldoProyectoAlDia_contabilidad 1,'01/01/2016','09/09/2016','023017',0,0,0

*/
GO
;
/****** Object:  StoredProcedure [dbo].[usp_scp_vsj_getSaldoAlDia_contabilidad]    Script Date: 09/12/2016 10:05:54 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO
drop PROCEDURE if exists [dbo].[usp_scp_vsj_getSaldoAlDia_inicial]
go
CREATE PROCEDURE [dbo].[usp_scp_vsj_getSaldoAlDia_inicial]
(@Tipo int, -- 1 proyecto, 2 tercero
 @FechaInicial varchar(10),
 @FechaFinal varchar(19),
 @Codigo varchar(6),
 @SaldoPEN_inicial decimal(12,2) OUTPUT,
 @SaldoUSD_inicial decimal(12,2) OUTPUT,
 @SaldoEUR_inicial decimal(12,2) OUTPUT )

As
BEGIN

Set @SaldoPEN_inicial=0.00
Set @SaldoUSD_inicial=0.00
Set @SaldoEUR_inicial=0.00

if (@Tipo=1)
BEGIN
  --PEN
	Select @SaldoPEN_inicial=isnull((Sum(a.num_habersol)-Sum(a.num_debesol)),0)
	From scp_comprobantedetalle a
	Where
	(Substring(Ltrim(a.cod_ctacontable),1,1)<>'3' And Substring(Ltrim(a.cod_ctacontable),1,2)<>'10')
	And Ltrim(Rtrim(a.cod_proyecto)) = @Codigo and
	a.txt_anoproceso=SUBSTRING(@FechaFinal,0,5)
	and a.cod_origen='10'
	and cod_mes='00'
	and a.cod_tipomoneda=0
	Group By a.cod_tipomoneda,a.cod_proyecto

 --USD
	Select @SaldoUSD_inicial=isnull((Sum(a.num_haberdolar)-Sum(a.num_debedolar)),0)
	From scp_comprobantedetalle a
	Where
	(Substring(Ltrim(a.cod_ctacontable),1,1)<>'3' And Substring(Ltrim(a.cod_ctacontable),1,2)<>'10')
	And Ltrim(Rtrim(a.cod_proyecto)) = @Codigo and
	a.txt_anoproceso=SUBSTRING(@FechaFinal,0,5)
	and a.cod_origen='10'
	and cod_mes='00'
	and a.cod_tipomoneda=1
	Group By a.cod_tipomoneda,a.cod_proyecto

  --EUR
	Select @SaldoEUR_inicial=isnull((Sum(a.num_habermo)-Sum(a.num_debemo)),0)
	From scp_comprobantedetalle a
	Where
	(Substring(Ltrim(a.cod_ctacontable),1,1)<>'3' And Substring(Ltrim(a.cod_ctacontable),1,2)<>'10')
	And Ltrim(Rtrim(a.cod_proyecto)) = @Codigo and
	a.txt_anoproceso=SUBSTRING(@FechaFinal,0,5)
	and a.cod_origen='10'
	and cod_mes='00'
	and a.cod_tipomoneda=2
	Group By a.cod_tipomoneda,a.cod_proyecto
END
else if (@Tipo=2)
BEGIN
  --PEN
	Select @SaldoPEN_inicial=isnull((Sum(a.num_habersol)-Sum(a.num_debesol)),0)
	From scp_comprobantedetalle a
	Where
	(Substring(Ltrim(a.cod_ctacontable),1,1)<>'3' And Substring(Ltrim(a.cod_ctacontable),1,2)<>'10')
	AND Ltrim(Rtrim(a.cod_tercero)) = @Codigo
	and a.txt_anoproceso=SUBSTRING(@FechaFinal,0,5)
	and a.cod_origen='10'
	and cod_mes='00'
	and a.cod_tipomoneda=0
	Group By a.cod_tipomoneda,a.cod_tercero

 --USD
	Select @SaldoUSD_inicial=isnull((Sum(a.num_haberdolar)-Sum(a.num_debedolar)),0)
	From scp_comprobantedetalle a
	Where
	(Substring(Ltrim(a.cod_ctacontable),1,1)<>'3' And Substring(Ltrim(a.cod_ctacontable),1,2)<>'10')
	AND Ltrim(Rtrim(a.cod_tercero)) = @Codigo
	and a.txt_anoproceso=SUBSTRING(@FechaFinal,0,5)
	and a.cod_origen='10'
	and cod_mes='00'
	and a.cod_tipomoneda=1
	Group By a.cod_tipomoneda,a.cod_tercero

  --EUR
	Select @SaldoEUR_inicial=isnull((Sum(a.num_habermo)-Sum(a.num_debemo)),0)
	From scp_comprobantedetalle a
	Where
	(Substring(Ltrim(a.cod_ctacontable),1,1)<>'3' And Substring(Ltrim(a.cod_ctacontable),1,2)<>'10')
	AND Ltrim(Rtrim(a.cod_tercero)) = @Codigo
	and a.txt_anoproceso=SUBSTRING(@FechaFinal,0,5)
	and a.cod_origen='10'
	and cod_mes='00'
	and a.cod_tipomoneda=2
	Group By a.cod_tipomoneda,a.cod_tercero
END

--Print 'Inicial  PEN:'+CONVERT(char(14),@SaldoPEN_inicial ,121)
--+' USD:'+CONVERT(char(14),@SaldoUSD_inicial ,121)
--+' EUR:'+CONVERT(char(14),@SaldoEUR_inicial ,121)
END
/*

Exec usp_scp_vsj_getSaldoProyectoAlDia_contabilidad 1,'01/01/2016','09/09/2016','023017',0,0,0

*/
GO
;
PRINT '6. Cr proc_usp*; GO;'
/****** Object:  UserDefinedFunction [dbo].[fun_scp_vsj_GetSaldosAlDiaBanco]    Script Date: 10/27/2018 01:37:37 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
DROP function if exists [dbo].[fun_scp_vsj_GetSaldosAlDiaBanco]
GO
CREATE FUNCTION [dbo].[fun_scp_vsj_GetSaldosAlDiaBanco] (
	@Fecha varchar(19), -- Fecha para saldo formato yyyy-dd-mm hh:mi:ss(24h)
	@Moneda varchar(1)  -- 0 PEN, 1 USD, 2 EUR
	) RETURNS @SaldosTable TABLE (
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
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

    DROP FUNCTION if exists [dbo].[fun_scp_vsj_getLetraDeNumero];
GO

CREATE FUNCTION [fun_scp_vsj_getLetraDeNumero] (@Numero NUMERIC(20,2)) RETURNS Varchar(200) AS
BEGIN
--SET NOCOUNT ON
DECLARE @lnEntero INT,
@lcRetorno VARCHAR(512),
@lnTerna INT,
@lcMiles VARCHAR(512),
@lcCadena VARCHAR(512),
@lnUnidades INT,
@lnDecenas INT,
@lnCentenas INT,
@lnFraccion INT
SELECT @lnEntero = CAST(@Numero AS INT),
@lnFraccion = (@Numero - @lnEntero) * 100,
@lcRetorno = '',
@lnTerna = 1
WHILE @lnEntero > 0
BEGIN /* WHILE */
-- Recorro terna por terna
SELECT @lcCadena = ''
SELECT @lnUnidades = @lnEntero % 10
SELECT @lnEntero = CAST(@lnEntero/10 AS INT)
SELECT @lnDecenas = @lnEntero % 10
SELECT @lnEntero = CAST(@lnEntero/10 AS INT)
SELECT @lnCentenas = @lnEntero % 10
SELECT @lnEntero = CAST(@lnEntero/10 AS INT)
-- Analizo las unidades
SELECT @lcCadena =
CASE /* UNIDADES */
WHEN @lnUnidades = 1 AND @lnTerna = 1 THEN 'uno ' + @lcCadena
WHEN @lnUnidades = 1 AND @lnTerna <> 1 THEN 'un ' + @lcCadena
WHEN @lnUnidades = 2 THEN 'dos ' + @lcCadena
WHEN @lnUnidades = 3 THEN 'tres ' + @lcCadena
WHEN @lnUnidades = 4 THEN 'cuatro ' + @lcCadena
WHEN @lnUnidades = 5 THEN 'cinco ' + @lcCadena
WHEN @lnUnidades = 6 THEN 'seis ' + @lcCadena
WHEN @lnUnidades = 7 THEN 'siete ' + @lcCadena
WHEN @lnUnidades = 8 THEN 'ocho ' + @lcCadena
WHEN @lnUnidades = 9 THEN 'nueve ' + @lcCadena
ELSE @lcCadena
END /* UNIDADES */
-- Analizo las decenas
SELECT @lcCadena =
CASE /* DECENAS */
WHEN @lnDecenas = 1 THEN
CASE @lnUnidades
WHEN 0 THEN 'diez '
WHEN 1 THEN 'once '
WHEN 2 THEN 'doce '
WHEN 3 THEN 'trece '
WHEN 4 THEN 'catorce '
WHEN 5 THEN 'quince '
ELSE 'dieci' + @lcCadena
END
WHEN @lnDecenas = 2 AND @lnUnidades = 0 THEN 'veinte ' + @lcCadena
WHEN @lnDecenas = 2 AND @lnUnidades <> 0 THEN 'veinti' + @lcCadena
WHEN @lnDecenas = 3 AND @lnUnidades = 0 THEN 'treinta ' + @lcCadena
WHEN @lnDecenas = 3 AND @lnUnidades <> 0 THEN 'treinta y ' + @lcCadena
WHEN @lnDecenas = 4 AND @lnUnidades = 0 THEN 'cuarenta ' + @lcCadena
WHEN @lnDecenas = 4 AND @lnUnidades <> 0 THEN 'cuarenta y ' + @lcCadena
WHEN @lnDecenas = 5 AND @lnUnidades = 0 THEN 'cincuenta ' + @lcCadena
WHEN @lnDecenas = 5 AND @lnUnidades <> 0 THEN 'cincuenta y ' + @lcCadena
WHEN @lnDecenas = 6 AND @lnUnidades = 0 THEN 'sesenta ' + @lcCadena
WHEN @lnDecenas = 6 AND @lnUnidades <> 0 THEN 'sesenta y ' + @lcCadena
WHEN @lnDecenas = 7 AND @lnUnidades = 0 THEN 'setenta ' + @lcCadena
WHEN @lnDecenas = 7 AND @lnUnidades <> 0 THEN 'setenta y ' + @lcCadena
WHEN @lnDecenas = 8 AND @lnUnidades = 0 THEN 'ochenta ' + @lcCadena
WHEN @lnDecenas = 8 AND @lnUnidades <> 0 THEN 'ochenta y ' + @lcCadena
WHEN @lnDecenas = 9 AND @lnUnidades = 0 THEN 'noventa ' + @lcCadena
WHEN @lnDecenas = 9 AND @lnUnidades <> 0 THEN 'noventa Y ' + @lcCadena
ELSE @lcCadena
END /* DECENAS */

-- Analizo las centenas
SELECT @lcCadena =
CASE /* CENTENAS */
WHEN @lnCentenas = 1 AND @lnUnidades = 0 AND @lnDecenas = 0 THEN 'cien ' +
@lcCadena
WHEN @lnCentenas = 1 AND NOT(@lnUnidades = 0 AND @lnDecenas = 0) THEN
'ciento ' + @lcCadena
WHEN @lnCentenas = 2 THEN 'doscientos ' + @lcCadena
WHEN @lnCentenas = 3 THEN 'trescientos ' + @lcCadena
WHEN @lnCentenas = 4 THEN 'cuatrocientos ' + @lcCadena
WHEN @lnCentenas = 5 THEN 'quinientos ' + @lcCadena
WHEN @lnCentenas = 6 THEN 'seiscientos ' + @lcCadena
WHEN @lnCentenas = 7 THEN 'setecientos ' + @lcCadena
WHEN @lnCentenas = 8 THEN 'ochocientos ' + @lcCadena
WHEN @lnCentenas = 9 THEN 'novecientos ' + @lcCadena
ELSE @lcCadena
END /* CENTENAS */
-- Analizo la terna
SELECT @lcCadena =
CASE /* TERNA */
WHEN @lnTerna = 1 THEN @lcCadena
WHEN @lnTerna = 2 AND (@lnUnidades + @lnDecenas + @lnCentenas <> 0) THEN
@lcCadena + ' mil '
WHEN @lnTerna = 3 AND (@lnUnidades + @lnDecenas + @lnCentenas <> 0) AND
@lnUnidades = 1 AND @lnDecenas = 0 AND @lnCentenas = 0 THEN @lcCadena + '
millon '
WHEN @lnTerna = 3 AND (@lnUnidades + @lnDecenas + @lnCentenas <> 0) AND
NOT (@lnUnidades = 1 AND @lnDecenas = 0 AND @lnCentenas = 0) THEN @lcCadena
+ ' millones '
WHEN @lnTerna = 4 AND (@lnUnidades + @lnDecenas + @lnCentenas <> 0) THEN
@lcCadena + ' mil millones '
ELSE ''
END /* TERNA */
-- Armo el retorno terna a terna
SELECT @lcRetorno = @lcCadena + @lcRetorno
SELECT @lnTerna = @lnTerna + 1
END /* WHILE */
IF @lnTerna = 1
SELECT @lcRetorno = 'cero'
RETURN RTRIM(@lcRetorno) + ' con ' + LTRIM(STR(@lnFraccion,2)) + '/100'
END

GO
PRINT '7. Cr function_scp'
USE [SCP]
GO
SET IDENTITY_INSERT [dbo].[vsj_configuracioncaja] ON 

INSERT [dbo].[vsj_configuracioncaja] ([cod_configuracion], [txt_configuracion], [cod_ctacontable], [cod_categoriaproyecto], [cod_proyecto], [cod_destino], [ind_tipomoneda]) VALUES (1, N'todo soles', N'1011101', NULL, NULL, NULL, N'0')
INSERT [dbo].[vsj_configuracioncaja] ([cod_configuracion], [txt_configuracion], [cod_ctacontable], [cod_categoriaproyecto], [cod_proyecto], [cod_destino], [ind_tipomoneda]) VALUES (2, N'Caja Misereor', N'1011107', NULL, N'023116', NULL, N'0')
INSERT [dbo].[vsj_configuracioncaja] ([cod_configuracion], [txt_configuracion], [cod_ctacontable], [cod_categoriaproyecto], [cod_proyecto], [cod_destino], [ind_tipomoneda]) VALUES (3, N'todo dolares', N'1011201', NULL, NULL, NULL, N'1')
INSERT [dbo].[vsj_configuracioncaja] ([cod_configuracion], [txt_configuracion], [cod_ctacontable], [cod_categoriaproyecto], [cod_proyecto], [cod_destino], [ind_tipomoneda]) VALUES (4, N'tod eur', N'1011203', NULL, NULL, NULL, N'2')
SET IDENTITY_INSERT [dbo].[vsj_configuracioncaja] OFF
USE [SCP]
GO
SET IDENTITY_INSERT [dbo].[vsj_configuractacajabanco] ON 

INSERT [dbo].[vsj_configuractacajabanco] ([id], [cod_tipocuenta], [txt_tipocuenta], [cod_ctacontablecaja], [cod_ctacontablegasto], [cod_ctaespecial], [para_caja], [para_banco], [para_proyecto], [para_tercero], [activo]) VALUES (1, N'01', N'Entregas a Rendir', N' ', N'1410301', N' ', 1, 0, 1, 0, 1)
INSERT [dbo].[vsj_configuractacajabanco] ([id], [cod_tipocuenta], [txt_tipocuenta], [cod_ctacontablecaja], [cod_ctacontablegasto], [cod_ctaespecial], [para_caja], [para_banco], [para_proyecto], [para_tercero], [activo]) VALUES (2, N'02', N'Gastos varios', N' ', N'6590999', N' ', 1, 0, 1, 0, 1)
INSERT [dbo].[vsj_configuractacajabanco] ([id], [cod_tipocuenta], [txt_tipocuenta], [cod_ctacontablecaja], [cod_ctacontablegasto], [cod_ctaespecial], [para_caja], [para_banco], [para_proyecto], [para_tercero], [activo]) VALUES (3, N'03', N'Movilidad', N' ', N'6310104', N'D.03.07', 1, 0, 1, 0, 1)
INSERT [dbo].[vsj_configuractacajabanco] ([id], [cod_tipocuenta], [txt_tipocuenta], [cod_ctacontablecaja], [cod_ctacontablegasto], [cod_ctaespecial], [para_caja], [para_banco], [para_proyecto], [para_tercero], [activo]) VALUES (4, N'04', N'Energía Eléctrica', N' ', N'6360101', N'D.01.01', 1, 0, 1, 0, 1)
INSERT [dbo].[vsj_configuractacajabanco] ([id], [cod_tipocuenta], [txt_tipocuenta], [cod_ctacontablecaja], [cod_ctacontablegasto], [cod_ctaespecial], [para_caja], [para_banco], [para_proyecto], [para_tercero], [activo]) VALUES (5, N'05', N'Teléfono/Celular', N' ', N'6360401', N'D.01.07', 1, 0, 1, 0, 1)
INSERT [dbo].[vsj_configuractacajabanco] ([id], [cod_tipocuenta], [txt_tipocuenta], [cod_ctacontablecaja], [cod_ctacontablegasto], [cod_ctaespecial], [para_caja], [para_banco], [para_proyecto], [para_tercero], [activo]) VALUES (6, N'06', N'Portes y comisiones bancarias', N' ', N'6390101', N'D.03.02', 1, 0, 1, 0, 1)
INSERT [dbo].[vsj_configuractacajabanco] ([id], [cod_tipocuenta], [txt_tipocuenta], [cod_ctacontablecaja], [cod_ctacontablegasto], [cod_ctaespecial], [para_caja], [para_banco], [para_proyecto], [para_tercero], [activo]) VALUES (7, N'07', N'Internet', N' ', N'6360501', N'D.01.08', 1, 0, 1, 0, 1)
INSERT [dbo].[vsj_configuractacajabanco] ([id], [cod_tipocuenta], [txt_tipocuenta], [cod_ctacontablecaja], [cod_ctacontablegasto], [cod_ctaespecial], [para_caja], [para_banco], [para_proyecto], [para_tercero], [activo]) VALUES (8, N'08', N'Fletes y embarques', N' ', N'6310101', N'D.03.06', 1, 0, 1, 0, 1)
INSERT [dbo].[vsj_configuractacajabanco] ([id], [cod_tipocuenta], [txt_tipocuenta], [cod_ctacontablecaja], [cod_ctacontablegasto], [cod_ctaespecial], [para_caja], [para_banco], [para_proyecto], [para_tercero], [activo]) VALUES (9, N'09', N'Pago de Sueldos', N' ', N'4110101', N' ', 1, 0, 1, 0, 1)
INSERT [dbo].[vsj_configuractacajabanco] ([id], [cod_tipocuenta], [txt_tipocuenta], [cod_ctacontablecaja], [cod_ctacontablegasto], [cod_ctaespecial], [para_caja], [para_banco], [para_proyecto], [para_tercero], [activo]) VALUES (10, N'10', N'Venta de vinos,velas', N' ', N'4630301', N'I.01.06.03', 1, 0, 1, 0, 1)
INSERT [dbo].[vsj_configuractacajabanco] ([id], [cod_tipocuenta], [txt_tipocuenta], [cod_ctacontablecaja], [cod_ctacontablegasto], [cod_ctaespecial], [para_caja], [para_banco], [para_proyecto], [para_tercero], [activo]) VALUES (11, N'11', N'Reembolso de caja', N' ', N'1030101', N' ', 1, 0, 1, 0, 1)
INSERT [dbo].[vsj_configuractacajabanco] ([id], [cod_tipocuenta], [txt_tipocuenta], [cod_ctacontablecaja], [cod_ctacontablegasto], [cod_ctaespecial], [para_caja], [para_banco], [para_proyecto], [para_tercero], [activo]) VALUES (12, N'12', N'Préstamos por cobrar proyectos', N' ', N'1720201', N' ', 1, 0, 1, 0, 1)
INSERT [dbo].[vsj_configuractacajabanco] ([id], [cod_tipocuenta], [txt_tipocuenta], [cod_ctacontablecaja], [cod_ctacontablegasto], [cod_ctaespecial], [para_caja], [para_banco], [para_proyecto], [para_tercero], [activo]) VALUES (13, N'13', N'Prestamos por pagar entre proyectos', N' ', N'4710101', N' ', 1, 0, 1, 0, 1)
INSERT [dbo].[vsj_configuractacajabanco] ([id], [cod_tipocuenta], [txt_tipocuenta], [cod_ctacontablecaja], [cod_ctacontablegasto], [cod_ctaespecial], [para_caja], [para_banco], [para_proyecto], [para_tercero], [activo]) VALUES (14, N'14', N'Donaciones y Subvenciones', N' ', N'4620301', N' ', 1, 0, 1, 0, 1)
INSERT [dbo].[vsj_configuractacajabanco] ([id], [cod_tipocuenta], [txt_tipocuenta], [cod_ctacontablecaja], [cod_ctacontablegasto], [cod_ctaespecial], [para_caja], [para_banco], [para_proyecto], [para_tercero], [activo]) VALUES (15, N'15', N'Ingresos - Partidas de sacramentos', N' ', N'4620301', N'I.01.02', 1, 0, 1, 0, 1)
INSERT [dbo].[vsj_configuractacajabanco] ([id], [cod_tipocuenta], [txt_tipocuenta], [cod_ctacontablecaja], [cod_ctacontablegasto], [cod_ctaespecial], [para_caja], [para_banco], [para_proyecto], [para_tercero], [activo]) VALUES (16, N'16', N'Alquileres', N' ', N'4620301', N'I.01.04', 1, 0, 1, 0, 1)
INSERT [dbo].[vsj_configuractacajabanco] ([id], [cod_tipocuenta], [txt_tipocuenta], [cod_ctacontablecaja], [cod_ctacontablegasto], [cod_ctaespecial], [para_caja], [para_banco], [para_proyecto], [para_tercero], [activo]) VALUES (17, N'17', N'Pago de Essalud', N' ', N'4030101', N' ', 1, 0, 1, 0, 1)
INSERT [dbo].[vsj_configuractacajabanco] ([id], [cod_tipocuenta], [txt_tipocuenta], [cod_ctacontablecaja], [cod_ctacontablegasto], [cod_ctaespecial], [para_caja], [para_banco], [para_proyecto], [para_tercero], [activo]) VALUES (18, N'18', N'Pago de ONP', N' ', N'4030201', N' ', 1, 0, 1, 0, 1)
INSERT [dbo].[vsj_configuractacajabanco] ([id], [cod_tipocuenta], [txt_tipocuenta], [cod_ctacontablecaja], [cod_ctacontablegasto], [cod_ctaespecial], [para_caja], [para_banco], [para_proyecto], [para_tercero], [activo]) VALUES (19, N'19', N'Pago de AFP', N' ', N'4070101', N' ', 1, 0, 1, 0, 1)
INSERT [dbo].[vsj_configuractacajabanco] ([id], [cod_tipocuenta], [txt_tipocuenta], [cod_ctacontablecaja], [cod_ctacontablegasto], [cod_ctaespecial], [para_caja], [para_banco], [para_proyecto], [para_tercero], [activo]) VALUES (20, N'20', N'Pago de pension de misionero', N' ', N'6220201', N'B.01.01', 1, 0, 1, 0, 1)
INSERT [dbo].[vsj_configuractacajabanco] ([id], [cod_tipocuenta], [txt_tipocuenta], [cod_ctacontablecaja], [cod_ctacontablegasto], [cod_ctaespecial], [para_caja], [para_banco], [para_proyecto], [para_tercero], [activo]) VALUES (21, N'21', N'Comision por transferencia de dinero', N' ', N'6390103', N'D.03.03', 1, 0, 1, 0, 1)
INSERT [dbo].[vsj_configuractacajabanco] ([id], [cod_tipocuenta], [txt_tipocuenta], [cod_ctacontablecaja], [cod_ctacontablegasto], [cod_ctaespecial], [para_caja], [para_banco], [para_proyecto], [para_tercero], [activo]) VALUES (22, N'22', N'Medicinas,atencion medica', N' ', N'6520301', N'B.01.02', 1, 0, 1, 0, 1)
INSERT [dbo].[vsj_configuractacajabanco] ([id], [cod_tipocuenta], [txt_tipocuenta], [cod_ctacontablecaja], [cod_ctacontablegasto], [cod_ctaespecial], [para_caja], [para_banco], [para_proyecto], [para_tercero], [activo]) VALUES (23, N'01', N'Movimiento de tercero', N' ', N'4630201', N' ', 1, 0, 0, 1, 1)
INSERT [dbo].[vsj_configuractacajabanco] ([id], [cod_tipocuenta], [txt_tipocuenta], [cod_ctacontablecaja], [cod_ctacontablegasto], [cod_ctaespecial], [para_caja], [para_banco], [para_proyecto], [para_tercero], [activo]) VALUES (24, N'03', N'Cuentas por cobrar tercero', N' ', N'1620201', N'L.03', 1, 0, 0, 1, 1)
INSERT [dbo].[vsj_configuractacajabanco] ([id], [cod_tipocuenta], [txt_tipocuenta], [cod_ctacontablecaja], [cod_ctacontablegasto], [cod_ctaespecial], [para_caja], [para_banco], [para_proyecto], [para_tercero], [activo]) VALUES (25, N'01', N'Entrega a rendir', N' ', N'1410301', N' ', 0, 1, 1, 0, 1)
INSERT [dbo].[vsj_configuractacajabanco] ([id], [cod_tipocuenta], [txt_tipocuenta], [cod_ctacontablecaja], [cod_ctacontablegasto], [cod_ctaespecial], [para_caja], [para_banco], [para_proyecto], [para_tercero], [activo]) VALUES (26, N'02', N'Gastos varios', N' ', N'6590999', N' ', 0, 1, 1, 0, 1)
INSERT [dbo].[vsj_configuractacajabanco] ([id], [cod_tipocuenta], [txt_tipocuenta], [cod_ctacontablecaja], [cod_ctacontablegasto], [cod_ctaespecial], [para_caja], [para_banco], [para_proyecto], [para_tercero], [activo]) VALUES (27, N'03', N'Movilidad', N' ', N'6310104', N' ', 0, 1, 1, 0, 1)
INSERT [dbo].[vsj_configuractacajabanco] ([id], [cod_tipocuenta], [txt_tipocuenta], [cod_ctacontablecaja], [cod_ctacontablegasto], [cod_ctaespecial], [para_caja], [para_banco], [para_proyecto], [para_tercero], [activo]) VALUES (28, N'04', N'Energía eléctrica', N' ', N'6360101', N'D.01.01', 0, 1, 1, 0, 1)
INSERT [dbo].[vsj_configuractacajabanco] ([id], [cod_tipocuenta], [txt_tipocuenta], [cod_ctacontablecaja], [cod_ctacontablegasto], [cod_ctaespecial], [para_caja], [para_banco], [para_proyecto], [para_tercero], [activo]) VALUES (29, N'05', N'Telefono/Celular', N' ', N'6360401', N'D.01.07', 0, 1, 1, 0, 1)
INSERT [dbo].[vsj_configuractacajabanco] ([id], [cod_tipocuenta], [txt_tipocuenta], [cod_ctacontablecaja], [cod_ctacontablegasto], [cod_ctaespecial], [para_caja], [para_banco], [para_proyecto], [para_tercero], [activo]) VALUES (30, N'06', N'Portes y comisiones bancarias', N' ', N'6390101', N'D.03.03', 0, 1, 1, 0, 1)
INSERT [dbo].[vsj_configuractacajabanco] ([id], [cod_tipocuenta], [txt_tipocuenta], [cod_ctacontablecaja], [cod_ctacontablegasto], [cod_ctaespecial], [para_caja], [para_banco], [para_proyecto], [para_tercero], [activo]) VALUES (31, N'07', N'Internet', N' ', N'6360501', N'D.01.08', 0, 1, 1, 0, 1)
INSERT [dbo].[vsj_configuractacajabanco] ([id], [cod_tipocuenta], [txt_tipocuenta], [cod_ctacontablecaja], [cod_ctacontablegasto], [cod_ctaespecial], [para_caja], [para_banco], [para_proyecto], [para_tercero], [activo]) VALUES (32, N'08', N'Flete y embarques', N' ', N'6310101', N' ', 0, 1, 1, 0, 1)
INSERT [dbo].[vsj_configuractacajabanco] ([id], [cod_tipocuenta], [txt_tipocuenta], [cod_ctacontablecaja], [cod_ctacontablegasto], [cod_ctaespecial], [para_caja], [para_banco], [para_proyecto], [para_tercero], [activo]) VALUES (33, N'09', N'Pago de sueldos', N' ', N'4110101', N' ', 0, 1, 1, 0, 1)
INSERT [dbo].[vsj_configuractacajabanco] ([id], [cod_tipocuenta], [txt_tipocuenta], [cod_ctacontablecaja], [cod_ctacontablegasto], [cod_ctaespecial], [para_caja], [para_banco], [para_proyecto], [para_tercero], [activo]) VALUES (34, N'10', N'Venta de vinos,velas', N' ', N'4630301', N'I.01.06.03', 0, 1, 1, 0, 1)
INSERT [dbo].[vsj_configuractacajabanco] ([id], [cod_tipocuenta], [txt_tipocuenta], [cod_ctacontablecaja], [cod_ctacontablegasto], [cod_ctaespecial], [para_caja], [para_banco], [para_proyecto], [para_tercero], [activo]) VALUES (35, N'11', N'Reembolso de caja', N' ', N'1030101', N' ', 0, 1, 1, 0, 1)
INSERT [dbo].[vsj_configuractacajabanco] ([id], [cod_tipocuenta], [txt_tipocuenta], [cod_ctacontablecaja], [cod_ctacontablegasto], [cod_ctaespecial], [para_caja], [para_banco], [para_proyecto], [para_tercero], [activo]) VALUES (36, N'12', N'Prestamos por cobrar proyectos', N' ', N'1720101', N' ', 0, 1, 1, 0, 1)
INSERT [dbo].[vsj_configuractacajabanco] ([id], [cod_tipocuenta], [txt_tipocuenta], [cod_ctacontablecaja], [cod_ctacontablegasto], [cod_ctaespecial], [para_caja], [para_banco], [para_proyecto], [para_tercero], [activo]) VALUES (37, N'13', N'Prestamos por pagar entre proyectos', N' ', N'4710101', N' ', 0, 1, 1, 0, 1)
INSERT [dbo].[vsj_configuractacajabanco] ([id], [cod_tipocuenta], [txt_tipocuenta], [cod_ctacontablecaja], [cod_ctacontablegasto], [cod_ctaespecial], [para_caja], [para_banco], [para_proyecto], [para_tercero], [activo]) VALUES (38, N'14', N'Pago de honorarios', N' ', N'6320909', N' ', 0, 1, 1, 0, 1)
INSERT [dbo].[vsj_configuractacajabanco] ([id], [cod_tipocuenta], [txt_tipocuenta], [cod_ctacontablecaja], [cod_ctacontablegasto], [cod_ctaespecial], [para_caja], [para_banco], [para_proyecto], [para_tercero], [activo]) VALUES (39, N'15', N'Ingresos-Partidas de sacramentos', N' ', N'4620301', N'I.01.02', 0, 1, 1, 0, 1)
INSERT [dbo].[vsj_configuractacajabanco] ([id], [cod_tipocuenta], [txt_tipocuenta], [cod_ctacontablecaja], [cod_ctacontablegasto], [cod_ctaespecial], [para_caja], [para_banco], [para_proyecto], [para_tercero], [activo]) VALUES (40, N'16', N'Alquileres', N' ', N'4620301', N'I.01.04', 0, 1, 1, 0, 1)
INSERT [dbo].[vsj_configuractacajabanco] ([id], [cod_tipocuenta], [txt_tipocuenta], [cod_ctacontablecaja], [cod_ctacontablegasto], [cod_ctaespecial], [para_caja], [para_banco], [para_proyecto], [para_tercero], [activo]) VALUES (41, N'17', N'Pago de Essalud', N' ', N'4030101', N' ', 0, 1, 1, 0, 1)
INSERT [dbo].[vsj_configuractacajabanco] ([id], [cod_tipocuenta], [txt_tipocuenta], [cod_ctacontablecaja], [cod_ctacontablegasto], [cod_ctaespecial], [para_caja], [para_banco], [para_proyecto], [para_tercero], [activo]) VALUES (42, N'18', N'Pago de ONP', N' ', N'4030201', N' ', 0, 1, 1, 0, 1)
INSERT [dbo].[vsj_configuractacajabanco] ([id], [cod_tipocuenta], [txt_tipocuenta], [cod_ctacontablecaja], [cod_ctacontablegasto], [cod_ctaespecial], [para_caja], [para_banco], [para_proyecto], [para_tercero], [activo]) VALUES (43, N'19', N'Pago de AFP', N' ', N'4070101', N' ', 0, 1, 1, 0, 1)
INSERT [dbo].[vsj_configuractacajabanco] ([id], [cod_tipocuenta], [txt_tipocuenta], [cod_ctacontablecaja], [cod_ctacontablegasto], [cod_ctaespecial], [para_caja], [para_banco], [para_proyecto], [para_tercero], [activo]) VALUES (44, N'20', N'Ingresos - Intereses bancarios', N' ', N'4620301', N'I.01.09', 0, 1, 1, 0, 1)
INSERT [dbo].[vsj_configuractacajabanco] ([id], [cod_tipocuenta], [txt_tipocuenta], [cod_ctacontablecaja], [cod_ctacontablegasto], [cod_ctaespecial], [para_caja], [para_banco], [para_proyecto], [para_tercero], [activo]) VALUES (45, N'21', N'Gasto - Intenciones de misas', N' ', N'6590901', N'B.04', 0, 1, 1, 0, 1)
INSERT [dbo].[vsj_configuractacajabanco] ([id], [cod_tipocuenta], [txt_tipocuenta], [cod_ctacontablecaja], [cod_ctacontablegasto], [cod_ctaespecial], [para_caja], [para_banco], [para_proyecto], [para_tercero], [activo]) VALUES (46, N'22', N'Ingresos - Donativos y proyectos', N' ', N'4620301', N'I.02', 0, 1, 1, 0, 1)
INSERT [dbo].[vsj_configuractacajabanco] ([id], [cod_tipocuenta], [txt_tipocuenta], [cod_ctacontablecaja], [cod_ctacontablegasto], [cod_ctaespecial], [para_caja], [para_banco], [para_proyecto], [para_tercero], [activo]) VALUES (47, N'23', N'Pensión de misioneros', N' ', N'6220201', N'B.01.01', 0, 1, 1, 0, 1)
INSERT [dbo].[vsj_configuractacajabanco] ([id], [cod_tipocuenta], [txt_tipocuenta], [cod_ctacontablecaja], [cod_ctacontablegasto], [cod_ctaespecial], [para_caja], [para_banco], [para_proyecto], [para_tercero], [activo]) VALUES (48, N'01', N'Movimiento de tercero', N' ', N'4630201', N' ', 0, 1, 0, 1, 1)
INSERT [dbo].[vsj_configuractacajabanco] ([id], [cod_tipocuenta], [txt_tipocuenta], [cod_ctacontablecaja], [cod_ctacontablegasto], [cod_ctaespecial], [para_caja], [para_banco], [para_proyecto], [para_tercero], [activo]) VALUES (49, N'02', N'Reembolso de caja terceros', N' ', N'1030102', N' ', 0, 1, 0, 1, 1)
INSERT [dbo].[vsj_configuractacajabanco] ([id], [cod_tipocuenta], [txt_tipocuenta], [cod_ctacontablecaja], [cod_ctacontablegasto], [cod_ctaespecial], [para_caja], [para_banco], [para_proyecto], [para_tercero], [activo]) VALUES (50, N'03', N'Cuentas por Cobrar Terceros', N' ', N'1620201', N' ', 0, 1, 0, 1, 1)
INSERT [dbo].[vsj_configuractacajabanco] ([id], [cod_tipocuenta], [txt_tipocuenta], [cod_ctacontablecaja], [cod_ctacontablegasto], [cod_ctaespecial], [para_caja], [para_banco], [para_proyecto], [para_tercero], [activo]) VALUES (51, N'09', N'test', N' ', N'623', N'A.01.01.01', 0, 1, 0, 1, 1)
SET IDENTITY_INSERT [dbo].[vsj_configuractacajabanco] OFF
USE [SCP]
GO
SET IDENTITY_INSERT [dbo].[vsj_propiedad] ON 

INSERT [dbo].[vsj_propiedad] ([cod_propiedad], [nombre], [valor]) VALUES (1, N'REPORTE_CAJA_REVISADOR_POR', N'Jorge Rojas Flores')
INSERT [dbo].[vsj_propiedad] ([cod_propiedad], [nombre], [valor]) VALUES (2, N'REPORTE_BANCOS_REVISADOR_POR', N'Jorge Rojas Flores')
INSERT [dbo].[vsj_propiedad] ([cod_propiedad], [nombre], [valor]) VALUES (3, N'PRINTER_LIST_SHOW', N'FALSE')
INSERT [dbo].[vsj_propiedad] ([cod_propiedad], [nombre], [valor]) VALUES (4, N'REPORTS_COMPROBANTE_PRINT', N'FALSE')
INSERT [dbo].[vsj_propiedad] ([cod_propiedad], [nombre], [valor]) VALUES (5, N'DEFAULT_DATE_RENDERER_FORMAT', N'%1$td/%1$tm/%1$ty')
INSERT [dbo].[vsj_propiedad] ([cod_propiedad], [nombre], [valor]) VALUES (6, N'REPORTS_SOURCE_URL', N'reports/')
INSERT [dbo].[vsj_propiedad] ([cod_propiedad], [nombre], [valor]) VALUES (7, N'DEFAULT_DATE_FORMAT', N'yyyy/MM/dd')
INSERT [dbo].[vsj_propiedad] ([cod_propiedad], [nombre], [valor]) VALUES (8, N'DEV_MODE', N'1')
INSERT [dbo].[vsj_propiedad] ([cod_propiedad], [nombre], [valor]) VALUES (9, N'DEFAULT_PRINTER_test', N'PDF')
INSERT [dbo].[vsj_propiedad] ([cod_propiedad], [nombre], [valor]) VALUES (10, N'REPORTS_DIARIO_CAJA_TYPE', N'PDF')
INSERT [dbo].[vsj_propiedad] ([cod_propiedad], [nombre], [valor]) VALUES (11, N'DEFAULT_FILTER_WIDTH', N'5')
INSERT [dbo].[vsj_propiedad] ([cod_propiedad], [nombre], [valor]) VALUES (12, N'REPORTE_CAJA_PREPARADO_POR', N'Gilmer Alvina Gómez Ochoa')
INSERT [dbo].[vsj_propiedad] ([cod_propiedad], [nombre], [valor]) VALUES (13, N'COMMON_FIELD_WIDTH', N'12em')
INSERT [dbo].[vsj_propiedad] ([cod_propiedad], [nombre], [valor]) VALUES (14, N'REPORTS_DIARIO_BANCARIA_TYPE', N'PDF')
INSERT [dbo].[vsj_propiedad] ([cod_propiedad], [nombre], [valor]) VALUES (15, N'REPORTS_SOURCE_FOLDER_WIN', N'C:\ab\dev\nvasja\vasja-reports\reports')
INSERT [dbo].[vsj_propiedad] ([cod_propiedad], [nombre], [valor]) VALUES (16, N'REPORTS_IMAGE_SERVLET', N'../../servlets/image?image=')
INSERT [dbo].[vsj_propiedad] ([cod_propiedad], [nombre], [valor]) VALUES (17, N'DECIMAL_FORMAT', N'#,##0.00')
INSERT [dbo].[vsj_propiedad] ([cod_propiedad], [nombre], [valor]) VALUES (18, N'REPORTS_SOURCE_FOLDER', N'')
INSERT [dbo].[vsj_propiedad] ([cod_propiedad], [nombre], [valor]) VALUES (19, N'REPORTS_COMPROBANTE_TYPE', N'PDF')
INSERT [dbo].[vsj_propiedad] ([cod_propiedad], [nombre], [valor]) VALUES (20, N'REPORTE_BANCOS_PREPARADO_POR', N'Verónica Angelica Salazar Sosa')
INSERT [dbo].[vsj_propiedad] ([cod_propiedad], [nombre], [valor]) VALUES (21, N'LOCALE', N'es_PE')
INSERT [dbo].[vsj_propiedad] ([cod_propiedad], [nombre], [valor]) VALUES (22, N'SHORT_DATE_FORMAT', N'MM/dd')
INSERT [dbo].[vsj_propiedad] ([cod_propiedad], [nombre], [valor]) VALUES (23, N'ALLOW_OVERDRAW', N'TRUE')
INSERT [dbo].[vsj_propiedad] ([cod_propiedad], [nombre], [valor]) VALUES (24, N'REPORTS_SOURCE_FOLDER_UNIX', N'/pol/dev/nvasja/vasja-reports/reports')
INSERT [dbo].[vsj_propiedad] ([cod_propiedad], [nombre], [valor]) VALUES (25, N'REPORTS_COMPROBANTE_OPEN', N'TRUE')
SET IDENTITY_INSERT [dbo].[vsj_propiedad] OFF
/****** Script for SelectTopNRows command from SSMS  ******/
delete from [dbo].[vsj_configuractacajabanco]
insert into [dbo].[vsj_configuractacajabanco]
(      [cod_tipocuenta]
      ,[txt_tipocuenta]
      ,[cod_ctacontablecaja]
      ,[cod_ctacontablegasto]
      ,[cod_ctaespecial]
      ,[para_caja]
      ,[para_banco]
      ,[para_proyecto]
      ,[para_tercero]
      ,[activo])
SELECT [cod_tipocuenta]
      ,[txt_tipocuenta]
      ,[cod_ctacontablecaja]
      ,[cod_ctacontablegasto]
      ,[cod_ctaespecial]
, case [ind_tipocuenta] --para caja
			when 0 then 1
			when 1 then 1
			when 2 then 0
			when 3 then 0
end
, case [ind_tipocuenta] --[para_banco]
			when 0 then 0
			when 1 then 0
			when 2 then 1
			when 3 then 1
end
, case [ind_tipocuenta] --[para_proyecto]
			when 0 then 1
			when 1 then 0
			when 2 then 1
			when 3 then 0
end
, case [ind_tipocuenta] --[para_tercero]
			when 0 then 0
			when 1 then 1
			when 2 then 0
			when 3 then 1
end
,1
FROM [dbo].[scp_configuractacajabanco]
where txt_anoproceso='2018'
PRINT '8. Cr vsj'
PRINT '9. FIXING DATA'
--UPDATE [dbo].[scp_bancocabecera] SET [cod_destino] ='00000000' WHERE [cod_destino] IN ('V128', 'V032', '45604121', '291', 'V90')
UPDATE [dbo].[scp_bancodetalle] SET cod_tipomov='' WHERE cod_tipomov IS NULL
UPDATE [dbo].[scp_plancontable] SET [ind_tipomoneda] = 'E' WHERE txt_anoproceso IN ('2014','2015','2016','2017','2018') AND flg_movimiento='N' AND cod_ctacontable='1060103'
UPDATE [dbo].[scp_plancontable] SET [ind_tipomoneda] = 'N' WHERE txt_anoproceso IN ('2014','2015','2016','2017','2018') AND flg_movimiento='N' AND cod_ctacontable='1060101'
update  [SCP].[dbo].[scp_proyecto]  set fec_final=CAST('2018-12-31' AS date) where cod_proyecto in ('005028','005029','005031','005035','005051','005054','005055','005056','005057','005058','005059','005060','005061','005064','005065','005066','005069','005070','005071','005072','005073','005074','005076','005077','023631','023211','023081','023020','023019','023012','023112','050012','006066','005002','005022');
update [SCP].[dbo].[scp_cajabanco]   set cod_destino='05344434'	where cod_destino='V057';
update [SCP].[dbo].[scp_cajabanco]    set cod_destino='10053714841' where cod_destino='V046';
update [SCP].[dbo].[scp_cajabanco] set cod_destino='V090' where cod_destino='V90';
update [SCP].[dbo].[scp_cajabanco]  set cod_destino='10053444348' where cod_destino='1005344434';
update [SCP].[dbo].[scp_cajabanco] set cod_destino='V090' FROM [SCP].[dbo].[scp_cajabanco]  where cod_destino in (SELECT sc.cod_destino FROM scp_cajabanco sc LEFT JOIN scp_destino sd on sc.cod_destino = sd.cod_destino WHERE sd.cod_destino IS NULL);


update [SCP].[dbo].[scp_bancodetalle] set cod_destino='V090'  where cod_destino='45604121';
update [SCP].[dbo].[scp_bancodetalle] set cod_destino='05344434'  where cod_destino='V057';
update [SCP].[dbo].[scp_bancodetalle] set cod_destino='10053714841'  where cod_destino='V046';
update [SCP].[dbo].[scp_bancodetalle] set cod_destino='V022'  where cod_destino='179';
update [SCP].[dbo].[scp_bancodetalle] set cod_destino='20100017491'  where cod_destino='2010017491';
update [SCP].[dbo].[scp_bancodetalle] set cod_destino='V345'  where cod_destino='125';
update [SCP].[dbo].[scp_bancodetalle] set cod_destino='V268'  where cod_destino='V90';
update [SCP].[dbo].[scp_bancodetalle] set cod_destino='05311887'  where cod_destino='291';
update [SCP].[dbo].[scp_bancodetalle] set cod_destino='20100043140'  where cod_destino='40519799';
update [SCP].[dbo].[scp_bancodetalle] set cod_destino='20100043140'  where cod_destino='';
update [SCP].[dbo].[scp_bancodetalle] set cod_destino='20100043140'  where cod_destino in ('167','20493573643','V128');

update [SCP].[dbo].[scp_bancocabecera] set cod_destino='V090'  where cod_destino='45604121';
update [SCP].[dbo].[scp_bancocabecera] set cod_destino='05344434'  where cod_destino='V057';
update [SCP].[dbo].[scp_bancocabecera] set cod_destino='10053714841'  where cod_destino='V046';
update [SCP].[dbo].[scp_bancocabecera] set cod_destino='V022'  where cod_destino='179';
update [SCP].[dbo].[scp_bancocabecera] set cod_destino='20100017491'  where cod_destino='2010017491';
update [SCP].[dbo].[scp_bancocabecera] set cod_destino='V345'  where cod_destino='125';
update [SCP].[dbo].[scp_bancocabecera] set cod_destino='V268'  where cod_destino='V90';
update [SCP].[dbo].[scp_bancocabecera] set cod_destino='05311887'  where cod_destino='291';
update [SCP].[dbo].[scp_bancocabecera] set cod_destino='20100043140'  where cod_destino='40519799';
update [SCP].[dbo].[scp_bancocabecera] set cod_destino='20100043140'  where cod_destino='';
update [SCP].[dbo].[scp_bancocabecera] set cod_destino='20100043140'  where cod_destino in ('167','20493573643','V128');

update [SCP].[dbo].[scp_plancontable] set ind_tipomoneda='N' where cod_ctacontable in ('1040101','1040102','1040103','1040104','1040201') and txt_anoproceso='2014';
update [SCP].[dbo].[scp_plancontable] set ind_tipomoneda='D' where cod_ctacontable in ('1040105','1040202') and txt_anoproceso='2014';
update [SCP].[dbo].[scp_plancontable] set ind_tipomoneda='E'  where cod_ctacontable in ('1060104','1060105') and txt_anoproceso='2014';

  --------------------------------------CONFIGURACION ----------------------
   update [SCP].[dbo].[scp_plancontable]
    set flg_estadocuenta='1' where txt_anoproceso='2019'
    and cod_ctacontable in ('1040101','1040102','1040104','1040105','1040106','1040201','1040202','1040203','1060101','1060102','1060103','1060108','1060109');
PRINT '10. DATA FIXED'

USE [SCP]
GO

/****** Object:  Table [dbo].[vsj_cajabanco]    Script Date: 09/12/2016 10:02:24 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[vsj_cajabanco](
	[cod_cajabanco] [int] IDENTITY(1,1) NOT NULL,
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
	[cod_contraparte] [varchar](6) NULL,
	[cod_financiera] [varchar](6) NULL,
	[cod_ctacontable] [varchar](14) NOT NULL,
	[cod_contracta] [varchar](14) NULL,
	[num_debesol] [decimal](12, 2) NULL,
	[num_habersol] [decimal](12, 2) NULL,
	[num_debedolar] [decimal](12, 2) NULL,
	[num_haberdolar] [decimal](12, 2) NULL,
	[flg_Anula] [char](1) NULL,
	[flg_enviado] [char](1) NULL,
	[cod_origenenlace] [varchar](6) NULL,
	[cod_comprobanteenlace] [varchar](6) NULL,
	[cod_destinoitem] [varchar](11) NOT NULL,
	[cod_tipocomprobantepago] [varchar](2) NULL,
	[txt_seriecomprobantepago] [varchar](5) NULL,
	[txt_comprobantepago] [varchar](20) NULL,
	[fec_comprobantepago] [datetime] NULL,
	[fec_fregistro] [datetime] NULL,
	[cod_uregistro] [varchar](15) NULL,
	[fec_factualiza] [datetime] NULL,
	[cod_uactualiza] [varchar](15) NULL,
PRIMARY KEY CLUSTERED 
(
	[cod_cajabanco] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO


USE [SCP]
GO

/****** Object:  Table [dbo].[scp_bancodetalle]    Script Date: 09/24/2016 13:25:46 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[vsj_bancodetalle](
	[num_item] int NOT NULL,
	[cod_bancocabecera] int NOT NULL,
	[txt_anoproceso] [varchar](4) NOT NULL,
	[ind_tipocuenta] [char](1) NOT NULL,
	[flg_saldo] [char](1) NOT NULL,
	[txt_correlativo] [varchar](8) NULL,
	[cod_mes] [varchar](2) NULL,
	[fec_fecha] [datetime] NULL,
	[cod_proyecto] [varchar](6) NOT NULL,
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
CONSTRAINT [FK_vsj_bancocabecera] FOREIGN KEY 
(cod_bancocabecera) REFERENCES vsj_bancocabecera(cod_bancocabecera) 
ON UPDATE CASCADE,
 CONSTRAINT [PK_vsj_bancodetalle] PRIMARY KEY CLUSTERED 
(
	[num_item] ASC,
	[cod_bancocabecera] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO



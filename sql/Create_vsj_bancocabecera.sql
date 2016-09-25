USE [SCP]
GO

/****** Object:  Table [dbo].[scp_bancocabecera]    Script Date: 09/24/2016 13:25:50 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING OFF
GO

CREATE TABLE [dbo].[vsj_bancocabecera](
	[cod_bancocabecera] int IDENTITY(1,1),
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
 CONSTRAINT [PK_vsj_bancocabecera] PRIMARY KEY CLUSTERED 
(
	[cod_bancocabecera]
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO


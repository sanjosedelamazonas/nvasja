USE [SCP]
GO

/****** Object:  Table [dbo].[vsj_configuractacajabanco]    Script Date: 10/26/2016 14:35:05 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[vsj_configuractacajabanco](
	[id] [int] IDENTITY(1,1)  NOT NULL,
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



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


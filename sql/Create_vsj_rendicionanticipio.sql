SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO
drop table if exists [dbo].[vsj_rendicionanticipio];
go

CREATE TABLE [dbo].[vsj_rendicionanticipio](
  [id] [int] IDENTITY(1,1) NOT NULL,
	[cod_comprobante] [varchar](6) NOT NULL,
	[fec_anticipio] [datetime] NOT NULL,
	[txt_glosa] [varchar](70),
	[ind_tipomoneda] [char](1),
	[num_anticipio] decimal(12,2) NOT NULL,
	[cod_uregistro] [varchar](15),
	[cod_uactualiza] [varchar](15),
	[fec_factualiza] [datetime],
	[fec_fregistro] [datetime]
 CONSTRAINT [PK_vsj_rendicionanticipio] PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

SET ANSI_PADDING OFF
GO

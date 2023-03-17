/****** Object:  Table [dbo].[vsj_propiedad]    Script Date: 09/08/2016 18:48:05 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO
drop table if exists [dbo].[vsj_passwordresettoken];
go
CREATE TABLE [dbo].[vsj_passwordresettoken](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[token] [varchar](300) NOT NULL,
	[cod_usuario] [varchar](250) NOT NULL,
	[expiry_date] [datetime] NOT NULL,
 CONSTRAINT [PK_vsj_passwordresettoken] PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO



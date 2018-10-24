USE [SCP]
GO

/****** Object:  Table [dbo].[scp_cajabanco]    Script Date: 09/08/2016 12:47:56 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO


CREATE TABLE [dbo].[vsj_cajabanco](
	[cod_cajabanco] [int] IDENTITY(1,1) NOT NULL,
	[cod_mes] [varchar](255) NULL,
	[cod_tipomoneda] [char](1) NOT NULL,
	[cod_uactualiza] [varchar](255) NULL,
	[cod_uregistro] [varchar](255) NULL,
	[fec_factualiza] [datetime] NULL,
	[fec_fecha] [datetime] NOT NULL,
	[fec_fregistro] [datetime] NULL,
	[ind_tipocuenta] [char](1) NULL,
	[txt_anoproceso] [varchar](255) NULL,
	[txt_correlativo] [varchar](255) NULL,
	[cod_comprobanteenlace] [varchar](255) NULL,
	[cod_contracta] [varchar](255) NOT NULL,
	[cod_contraparte] [varchar](255) NULL,
	[cod_ctacontable] [varchar](255) NOT NULL,
	[cod_ctaespecial] [varchar](255) NULL,
	[cod_ctaproyecto] [varchar](255) NULL,
	[cod_destino] [varchar](255) NOT NULL,
	[cod_destinoitem] [varchar](255) NOT NULL,
	[cod_financiera] [varchar](255) NULL,
	[cod_origenenlace] [varchar](255) NULL,
	[cod_proyecto] [varchar](255) NULL,
	[cod_tercero] [varchar](255) NULL,
	[cod_tipocomprobantepago] [varchar](255) NULL,
	[cod_tipomov] [int] NOT NULL,
	[cod_transcorrelativo] [varchar](255) NULL,
	[fec_comprobantepago] [datetime] NULL,
	[flg_enviado] [char](1) NULL,
	[flg_anula] [char](1) NULL,
	[num_debedolar] [decimal](12, 2) NULL,
	[num_debesol] [decimal](12, 2) NULL,
	[num_haberdolar] [decimal](12, 2) NULL,
	[num_habersol] [decimal](12, 2) NULL,
	[num_debemo] [decimal](12, 2) NULL,
	[num_habermo] [decimal](12, 2) NULL,
	[txt_comprobantepago] [varchar](255) NULL,
	[txt_glosaitem] [varchar](255) NOT NULL,
	[txt_seriecomprobantepago] [varchar](255) NULL,
PRIMARY KEY CLUSTERED
(
	[cod_cajabanco] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

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




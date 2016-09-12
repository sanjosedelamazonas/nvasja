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




USE [SCP]
GO

/****** Object:  Trigger [dbo].[scp_cajabanco_insert]    Script Date: 09/12/2016 10:03:34 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO


--alter [dbo].[scp_cajabanco] add usp_scp_cajabanco_gen_correlativo varchar(10)
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


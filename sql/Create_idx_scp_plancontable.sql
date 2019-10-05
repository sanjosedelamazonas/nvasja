CREATE NONCLUSTERED INDEX [FindCuentasInPlanContable] ON [dbo].[scp_plancontable]
(
	[txt_anoproceso] ASC,
	[cod_ctacontable] ASC,
	[flg_movimiento] ASC,
	[flg_estadocuenta] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)

GO

CREATE NONCLUSTERED INDEX [FindCuentasByAnoAndCtaContable] ON [dbo].[scp_plancontable]
    (
     [txt_anoproceso] ASC,
     [cod_ctacontable] ASC
        )WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)

GO

USE [SCP]
GO
SET IDENTITY_INSERT [dbo].[vsj_configuracioncaja] ON 

INSERT [dbo].[vsj_configuracioncaja] ([cod_configuracion], [txt_configuracion], [cod_ctacontable], [cod_categoriaproyecto], [cod_proyecto], [cod_destino], [ind_tipomoneda]) VALUES (1, N'todo soles', N'1011101', NULL, NULL, NULL, N'0')
INSERT [dbo].[vsj_configuracioncaja] ([cod_configuracion], [txt_configuracion], [cod_ctacontable], [cod_categoriaproyecto], [cod_proyecto], [cod_destino], [ind_tipomoneda]) VALUES (2, N'Caja Misereor', N'1011107', NULL, N'023116', NULL, N'0')
INSERT [dbo].[vsj_configuracioncaja] ([cod_configuracion], [txt_configuracion], [cod_ctacontable], [cod_categoriaproyecto], [cod_proyecto], [cod_destino], [ind_tipomoneda]) VALUES (3, N'todo dolares', N'1011201', NULL, NULL, NULL, N'1')
INSERT [dbo].[vsj_configuracioncaja] ([cod_configuracion], [txt_configuracion], [cod_ctacontable], [cod_categoriaproyecto], [cod_proyecto], [cod_destino], [ind_tipomoneda]) VALUES (4, N'tod eur', N'1011203', NULL, NULL, NULL, N'2')
SET IDENTITY_INSERT [dbo].[vsj_configuracioncaja] OFF

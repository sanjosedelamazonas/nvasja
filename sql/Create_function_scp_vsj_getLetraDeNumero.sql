SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

IF EXISTS (SELECT *
            FROM   sysobjects
            WHERE  id = object_id(N'[dbo].[fun_scp_vsj_getLetraDeNumero]'))
BEGIN
    DROP FUNCTION [dbo].[fun_scp_vsj_getLetraDeNumero]
END
GO

CREATE FUNCTION [fun_scp_vsj_getLetraDeNumero] (@Numero NUMERIC(20,2)) RETURNS Varchar(200) AS
BEGIN
--SET NOCOUNT ON
DECLARE @lnEntero INT,
@lcRetorno VARCHAR(512),
@lnTerna INT,
@lcMiles VARCHAR(512),
@lcCadena VARCHAR(512),
@lnUnidades INT,
@lnDecenas INT,
@lnCentenas INT,
@lnFraccion INT
SELECT @lnEntero = CAST(@Numero AS INT),
@lnFraccion = (@Numero - @lnEntero) * 100,
@lcRetorno = '',
@lnTerna = 1
WHILE @lnEntero > 0
BEGIN /* WHILE */
-- Recorro terna por terna
SELECT @lcCadena = ''
SELECT @lnUnidades = @lnEntero % 10
SELECT @lnEntero = CAST(@lnEntero/10 AS INT)
SELECT @lnDecenas = @lnEntero % 10
SELECT @lnEntero = CAST(@lnEntero/10 AS INT)
SELECT @lnCentenas = @lnEntero % 10
SELECT @lnEntero = CAST(@lnEntero/10 AS INT)
-- Analizo las unidades
SELECT @lcCadena =
CASE /* UNIDADES */
WHEN @lnUnidades = 1 AND @lnTerna = 1 THEN 'uno ' + @lcCadena
WHEN @lnUnidades = 1 AND @lnTerna <> 1 THEN 'un ' + @lcCadena
WHEN @lnUnidades = 2 THEN 'dos ' + @lcCadena
WHEN @lnUnidades = 3 THEN 'tres ' + @lcCadena
WHEN @lnUnidades = 4 THEN 'cuatro ' + @lcCadena
WHEN @lnUnidades = 5 THEN 'cinco ' + @lcCadena
WHEN @lnUnidades = 6 THEN 'seis ' + @lcCadena
WHEN @lnUnidades = 7 THEN 'siete ' + @lcCadena
WHEN @lnUnidades = 8 THEN 'ocho ' + @lcCadena
WHEN @lnUnidades = 9 THEN 'nueve ' + @lcCadena
ELSE @lcCadena
END /* UNIDADES */
-- Analizo las decenas
SELECT @lcCadena =
CASE /* DECENAS */
WHEN @lnDecenas = 1 THEN
CASE @lnUnidades
WHEN 0 THEN 'diez '
WHEN 1 THEN 'once '
WHEN 2 THEN 'doce '
WHEN 3 THEN 'trece '
WHEN 4 THEN 'catorce '
WHEN 5 THEN 'quince '
ELSE 'dieci' + @lcCadena
END
WHEN @lnDecenas = 2 AND @lnUnidades = 0 THEN 'veinte ' + @lcCadena
WHEN @lnDecenas = 2 AND @lnUnidades <> 0 THEN 'veinti' + @lcCadena
WHEN @lnDecenas = 3 AND @lnUnidades = 0 THEN 'treinta ' + @lcCadena
WHEN @lnDecenas = 3 AND @lnUnidades <> 0 THEN 'treinta y ' + @lcCadena
WHEN @lnDecenas = 4 AND @lnUnidades = 0 THEN 'cuarenta ' + @lcCadena
WHEN @lnDecenas = 4 AND @lnUnidades <> 0 THEN 'cuarenta y ' + @lcCadena
WHEN @lnDecenas = 5 AND @lnUnidades = 0 THEN 'cincuenta ' + @lcCadena
WHEN @lnDecenas = 5 AND @lnUnidades <> 0 THEN 'cincuenta y ' + @lcCadena
WHEN @lnDecenas = 6 AND @lnUnidades = 0 THEN 'sesenta ' + @lcCadena
WHEN @lnDecenas = 6 AND @lnUnidades <> 0 THEN 'sesenta y ' + @lcCadena
WHEN @lnDecenas = 7 AND @lnUnidades = 0 THEN 'setenta ' + @lcCadena
WHEN @lnDecenas = 7 AND @lnUnidades <> 0 THEN 'setenta y ' + @lcCadena
WHEN @lnDecenas = 8 AND @lnUnidades = 0 THEN 'ochenta ' + @lcCadena
WHEN @lnDecenas = 8 AND @lnUnidades <> 0 THEN 'ochenta y ' + @lcCadena
WHEN @lnDecenas = 9 AND @lnUnidades = 0 THEN 'noventa ' + @lcCadena
WHEN @lnDecenas = 9 AND @lnUnidades <> 0 THEN 'noventa Y ' + @lcCadena
ELSE @lcCadena
END /* DECENAS */

-- Analizo las centenas
SELECT @lcCadena =
CASE /* CENTENAS */
WHEN @lnCentenas = 1 AND @lnUnidades = 0 AND @lnDecenas = 0 THEN 'cien ' +
@lcCadena
WHEN @lnCentenas = 1 AND NOT(@lnUnidades = 0 AND @lnDecenas = 0) THEN
'ciento ' + @lcCadena
WHEN @lnCentenas = 2 THEN 'doscientos ' + @lcCadena
WHEN @lnCentenas = 3 THEN 'trescientos ' + @lcCadena
WHEN @lnCentenas = 4 THEN 'cuatrocientos ' + @lcCadena
WHEN @lnCentenas = 5 THEN 'quinientos ' + @lcCadena
WHEN @lnCentenas = 6 THEN 'seiscientos ' + @lcCadena
WHEN @lnCentenas = 7 THEN 'setecientos ' + @lcCadena
WHEN @lnCentenas = 8 THEN 'ochocientos ' + @lcCadena
WHEN @lnCentenas = 9 THEN 'novecientos ' + @lcCadena
ELSE @lcCadena
END /* CENTENAS */
-- Analizo la terna
SELECT @lcCadena =
CASE /* TERNA */
WHEN @lnTerna = 1 THEN @lcCadena
WHEN @lnTerna = 2 AND (@lnUnidades + @lnDecenas + @lnCentenas <> 0) THEN
@lcCadena + ' mil '
WHEN @lnTerna = 3 AND (@lnUnidades + @lnDecenas + @lnCentenas <> 0) AND
@lnUnidades = 1 AND @lnDecenas = 0 AND @lnCentenas = 0 THEN @lcCadena + '
millon '
WHEN @lnTerna = 3 AND (@lnUnidades + @lnDecenas + @lnCentenas <> 0) AND
NOT (@lnUnidades = 1 AND @lnDecenas = 0 AND @lnCentenas = 0) THEN @lcCadena
+ ' millones '
WHEN @lnTerna = 4 AND (@lnUnidades + @lnDecenas + @lnCentenas <> 0) THEN
@lcCadena + ' mil millones '
ELSE ''
END /* TERNA */
-- Armo el retorno terna a terna
SELECT @lcRetorno = @lcCadena + @lcRetorno
SELECT @lnTerna = @lnTerna + 1
END /* WHILE */
IF @lnTerna = 1
SELECT @lcRetorno = 'cero'
RETURN RTRIM(@lcRetorno) + ' con ' + LTRIM(STR(@lnFraccion,2)) + '/100'
END

GO

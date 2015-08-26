@echo off
REM ============================================================================
REM Startup Script for the Metadata Extraction Tool v3.1 - Command Line Extract Tool
REM ============================================================================

REM To set the METAHOME directly, set it in the variable below.

SET METAHOME=

REM Check whether the METAHOME variable is set.
IF NOT "%METAHOME%" == "" GOTO HOMESET

REM Guess the METAHOME directory.
for %%i in ("%CD%") do (call :resolve %%~si)
set METAHOME=%RESOLVED_DIR%

REM METAHOME is set, so we can start the tool.

:HOMESET

IF EXIST %METAHOME%\config.xml GOTO HOMEFOUND
echo Home directory could not be guessed. Please set METAHOME in the startup
echo script.
GOTO END

:HOMEFOUND
call %METAHOME%\setenv.bat
java -Xmx128m -Dmetahome=%METAHOME% -Djava.system.class.loader=nz.govt.natlib.meta.config.Loader -cp %META_CLASSPATH% nz.govt.natlib.meta.ui.CmdLine %*
goto END

:resolve
set RESOLVED_DIR=%1
goto end

:END


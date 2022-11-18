SETLOCAL EnableDelayedExpansion
SET Target=C:\Program Files (x86)\Fractal Softworks\Starsector
REM Create target.txt in the folder of this script to change the destination
IF EXIST "target.txt" (
    FOR /f "delims=" %%x IN (target.txt) DO set Target=%%x
)
robocopy ..\mod\EnableTranspONder "%Target%\mods\EnableTranspONder" /e /v /mir
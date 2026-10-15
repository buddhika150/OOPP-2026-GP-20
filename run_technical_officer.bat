@echo off
echo ==========================================================
echo Starting Faculty of Technology Management System
echo Member 3: Technical Officer Dashboard
echo ==========================================================
javac -cp "lib/*;." -d bin src/ftms/db/*.java src/ftms/model/*.java src/ftms/gui/*.java
if %ERRORLEVEL% NEQ 0 (
    echo Compilation failed.
    pause
    exit /b %ERRORLEVEL%
)
java -cp "lib/*;bin" ftms.gui.TechnicalOfficerDashboard
pause

@echo off
echo Starting React frontend...

:: Change to the frontend directory
cd /d "%~dp0\src\main\webapp\static"

:: Install dependencies and start the server
call npm install
call npm run dev

pause

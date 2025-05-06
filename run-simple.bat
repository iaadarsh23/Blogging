@echo off
echo Starting Blogging Platform with simplified approach...

:: Start the React frontend
cd /d "%~dp0\src\main\webapp\static"
start cmd /k "echo Starting React frontend... && npm install && npm run dev"

:: Wait a bit for React to start
timeout /t 5 /nobreak

:: Start a simple HTTP server for testing
cd /d "%~dp0"
echo To view your application, open http://localhost:3000
echo The backend API will be mocked by the React application for testing purposes.
echo This is a simplified mode - some features may be limited.

pause

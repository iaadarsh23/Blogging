@echo off
echo Starting Blogging Platform...

:: Change to the project directory
cd /d "%~dp0"

:: Start the React frontend
cd src\main\webapp\static
start cmd /k "echo Starting React frontend... && npm install && npm run dev"

:: Wait a bit for React to start
timeout /t 5 /nobreak

:: Start the Java backend (Tomcat)
cd /d "%~dp0"
start cmd /k "echo Starting Java backend... && mvn tomcat7:run"

echo Blogging Platform is starting up! 
echo To access the application, open http://localhost:3000 in your browser.
echo The backend API will be available at http://localhost:8080/blog

pause

@echo off
echo Setting up database for Blogging Platform...

:: Change these values according to your MySQL setup
set MYSQL_USER=root
set MYSQL_PASSWORD=root
set SQL_FILE=src\main\resources\database.sql

:: Run the SQL script
mysql -u %MYSQL_USER% -p%MYSQL_PASSWORD% < %SQL_FILE%

if %ERRORLEVEL% == 0 (
    echo Database setup completed successfully!
) else (
    echo Error setting up database. Please check your MySQL installation and credentials.
)

pause

@echo off
echo Starting Java backend with embedded Jetty server...

:: Path to Java
set JAVA_HOME=C:\Program Files\java-1.8.0-openjdk-1.8.0.392-1.b08.redhat.windows.x86_64
set PATH=%JAVA_HOME%\bin;%PATH%

:: Define classpath (add all required jar files)
set CLASSPATH=.
set CLASSPATH=%CLASSPATH%;lib\javax.servlet-api-4.0.1.jar
set CLASSPATH=%CLASSPATH%;lib\jetty-server-9.4.48.v20220622.jar
set CLASSPATH=%CLASSPATH%;lib\jetty-servlet-9.4.48.v20220622.jar
set CLASSPATH=%CLASSPATH%;lib\jetty-util-9.4.48.v20220622.jar
set CLASSPATH=%CLASSPATH%;lib\mysql-connector-java-8.0.29.jar
set CLASSPATH=%CLASSPATH%;lib\json-simple-1.1.1.jar
set CLASSPATH=%CLASSPATH%;target\classes

:: Create target directories
mkdir target\classes 2>nul

:: Compile the Java code
echo Compiling Java code...
javac -d target\classes -cp %CLASSPATH% src\main\java\com\blog\utils\*.java
javac -d target\classes -cp %CLASSPATH% src\main\java\com\blog\models\*.java
javac -d target\classes -cp %CLASSPATH% src\main\java\com\blog\services\*.java
javac -d target\classes -cp %CLASSPATH% src\main\java\com\blog\controllers\*.java

:: Run the embedded server
echo Starting server...
java -cp %CLASSPATH% com.blog.EmbeddedServer

pause

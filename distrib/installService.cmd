set JVM=C:\Program Files\Java\JVM1.6.0_21
JavaService.exe -install gitblit "%JVM%\jre\bin\server\jvm.dll" -Xmx1024M -Djava.class.path=%CD%\gitblit.jar;"%JVM%\lib\tools.jar" -start com.gitblit.Launcher -params --storePassword dosomegit -stop com.gitblit.Launcher -params --stop -out %CD%\logs\stdout.log -err %CD%\logs\stderr.log -current %CD%
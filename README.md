## To set up with IntelliJ IDE

1. Download the project.
2. Place the project under "Tomcat/webapps/"
3. (In IntelliJ) Under "WEB-INF" directory, right click directory "classes", select "Mark Directory as", then "excluded".
4. Go to File -> Project Structure -> Modules Tab -> Paths -> Compiler Output -> select "Use module compile output path" -> then, under Output path, browse for the "classes" folder marked in step 3.
5. Apply changes.
6. Anytime you make changes to the Java code that you want reflected in the web app, right click the project root folder (in IntelliJ), and select "Build Module '[PROJECT NAME]".
7. Once this completes, the compiled java .class files should be under "WEB-INF"/classes, which is where Tomcat wants them (may need to restart Tomcat for changes to take effect).
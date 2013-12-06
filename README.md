# REPL Shell using Spring Boot

_NOTE: This is very experimental code! It may be in a broken state at any point since it's in heavy development._

To try out the Spring Boot shell, check out the source code and run it like a normal Spring Boot app:

		git clone https://github.com/jbrisbin/spring-boot-repl.git
		cd spring-boot-repl
		mvn compile package

This creates an executable jar that you can run from the command line using `java -jar`:

		java -jar target/spring-boot-repl-1.0.0.BUILD-SNAPSHOT.jar

You can quit shell by issuing the `quit` command.

### Using the shell

To use the shell, you type the commands for the Spring Boot CLI like you would if you were running it with the `spring` command:

		$ run --watch myapp.groovy

The above will run the Spring Boot app "myapp.groovy" (assumed to be in the working directory) and return you to the prompt. The app will continue to run in the background. To stop the app, just issue the `stop` command.

Hit <TAB> to have commands and their options completed for you, just like you're familiar with in a BASH shell.

The Spring Boot shell supports multiple lines of text. Use the double left-caret character `<<` to start a continuation. Enter text into the shell spanning multiple lines and then complete the text with a carriage return on an empty line. While you're in continuation mode, the prompt will change to `...`.

You can run commands in the shell by using the bang `!` command. For example, to list the files in the current working directory, do:

		$ ! ls -la

Useful for catting log files and interrogating output produced by your application.

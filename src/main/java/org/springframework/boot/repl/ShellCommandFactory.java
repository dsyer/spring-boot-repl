/*
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.repl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ServiceLoader;

import org.springframework.boot.cli.Command;
import org.springframework.boot.cli.CommandFactory;
import org.springframework.boot.cli.SpringCli;
import org.springframework.boot.cli.command.RunCommand;

/**
 * @author Dave Syer
 * 
 */
public class ShellCommandFactory implements CommandFactory {

	@Override
	public Collection<Command> getCommands(SpringCli cli) {
		List<Command> commands = new ArrayList<Command>();
		for (CommandFactory factory : ServiceLoader.load(CommandFactory.class,
				Shell.class.getClassLoader())) {
			if (!(factory instanceof ShellCommandFactory)) {
				for (Command command : factory.getCommands(cli)) {
					if (command instanceof RunCommand) {
						commands.add(new StopCommand((RunCommand) command));
					}
				}
			}
		}
		commands.add(new ShellCommand(cli));
		return commands;
	}

}

package org.springframework.boot.repl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import jline.console.ConsoleReader;
import jline.console.completer.CandidateListCompletionHandler;

import org.springframework.boot.cli.SpringCli;
import org.springframework.boot.cli.command.AbstractCommand;
import org.springframework.util.StringUtils;

/**
 * @author Jon Brisbin
 * @author Dave Syer
 */
public class ShellCommand extends AbstractCommand {

	private SpringCli springCli;

	public ShellCommand(SpringCli springCli) {
		super("shell", "Start a nested shell (REPL).");
		this.springCli = springCli;
	}

	@Override
	public void run(String... args) throws Exception {

		final ConsoleReader console = new ConsoleReader();
		console.addCompleter(new CommandCompleter(console, springCli));
		console.setHistoryEnabled(true);
		console.setCompletionHandler(new CandidateListCompletionHandler());

		final InputStream sysin = System.in;
		final PrintStream sysout = System.out;
		final PrintStream syserr = System.err;

		System.setIn(console.getInput());
		PrintStream out = new PrintStream(new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				console.getOutput().write(b);
			}
		});
		System.setOut(out);
		System.setErr(out);

		String line;
		StringBuffer data = new StringBuffer();

		try {

			while (null != (line = console.readLine("$ "))) {
				if ("quit".equals(line.trim())) {
					break;
				} else if ("clear".equals(line.trim())) {
					console.clearScreen();
					continue;
				}
				List<String> parts = new ArrayList<>();

				if (line.contains("<<")) {
					int startMultiline = line.indexOf("<<");
					data.append(line.substring(startMultiline + 2));
					String contLine;
					while (null != (contLine = console.readLine("... "))) {
						if ("".equals(contLine.trim())) {
							break;
						}
						data.append(contLine);
					}
					line = line.substring(0, startMultiline);
				}

				String lineToParse = line.trim();
				if (lineToParse.startsWith("!")) {
					lineToParse = lineToParse.substring(1).trim();
				}
				String[] segments = StringUtils.delimitedListToStringArray(lineToParse,
						" ");
				StringBuffer sb = new StringBuffer();
				boolean swallowWhitespace = false;
				for (String s : segments) {
					if ("".equals(s)) {
						continue;
					}
					if (s.startsWith("\"")) {
						swallowWhitespace = true;
						sb.append(s.substring(1));
					} else if (s.endsWith("\"")) {
						swallowWhitespace = false;
						sb.append(" ").append(s.substring(0, s.length() - 1));
						parts.add(sb.toString());
						sb = new StringBuffer();
					} else {
						if (!swallowWhitespace) {
							parts.add(s);
						} else {
							sb.append(" ").append(s);
						}
					}
				}
				if (sb.length() > 0) {
					parts.add(sb.toString());
				}
				if (data.length() > 0) {
					parts.add(data.toString());
					data = new StringBuffer();
				}

				if (parts.size() > 0) {
					if (line.trim().startsWith("!")) {
						try {
							ProcessBuilder pb = new ProcessBuilder(parts);
							pb.inheritIO();
							pb.environment().putAll(System.getenv());
							pb.start().waitFor();
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						springCli.runAndHandleErrors(parts.toArray(new String[parts
								.size()]));
					}
				}
			}

		} finally {

			System.setIn(sysin);
			System.setOut(sysout);
			System.setErr(syserr);

			console.shutdown();

		}
	}

}

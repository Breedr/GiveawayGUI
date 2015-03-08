/**
 * Logger.java
 * GiveawayGrowl
 *
 * Created by Ed George on 2 Jan 2015
 *
 */
package uk.breedr.notifier.giveaway.gui;

/**
 * @author edgeorge
 *
 */
public class Logger {

	public enum Level{
		INFO, ERROR
	}

	private Console console;

	Logger(Console console){
		this.console = console;
	}

	public void write(String message){
		write(message, Level.INFO);
	}

	public void write(String message, Level level) {
		switch(level){
		case ERROR:
			System.err.println(message);
			message = "ERR: " + message;
			break;
		default:
			System.out.println(message);
			break;
		}
		console.append(message);
	}
}

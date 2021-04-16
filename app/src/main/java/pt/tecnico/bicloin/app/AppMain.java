package pt.tecnico.bicloin.app;

import java.util.Scanner;

import io.grpc.StatusRuntimeException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.lang.Thread;

public class AppMain {

	private static App app;

	private static final String help =
		"--ping Format is 'ping %message%'\n" +
		"--balance Format is 'balance'\n"+
		"--top-up Format is 'top-up %int%'\n"+
		"--tag Format is 'tag %latitude% %longitude% %name%'\n"+
		"--move Format is 'move %name%'\n"+
		"--move Format is 'move %latitude% %longitude%'\n"+
		"--at Format is 'at'\n"+
		"--scan Format is 'scan %int%'\n"+
		"--info Format is 'info %id%'\n"+
		"--bike-up Format is 'bike-up %id%'\n"+
		"--bike-down Format is 'bike-down %id%'\n"+
		"--sys_status Format is 'sys_status'\n"+
		"--quit Format is 'quit'\n";

	public static void main(String[] args) throws FileNotFoundException, InterruptedException {
		System.out.println(AppMain.class.getSimpleName());

		// receive and print arguments
		System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}

		if (!(args.length == 6 || args.length == 8)) {
			System.err.println("Argument(s) missing!");
			return;
		}

		final String zooHost = args[0];
		final String zooPort = args[1];
		final String userId = args[2];
		final String userPhone = args[3];
		final double latitude = Double.parseDouble(args[4]);
		final double longitude = Double.parseDouble(args[5]);

		app = new App(zooHost, zooPort, userId, userPhone, latitude, longitude);

		String input;
		boolean close = false;

		if (args.length == 8){
			// Read commands from file

			try (Scanner scanner = new Scanner(new File(args[7]))) {
					while (scanner.hasNextLine()) {
						processCommands(scanner.nextLine());
					}
					scanner.close();
					System.exit(0);
			} catch (FileNotFoundException fife) {
					System.out.println(String.format("Could not find file '%s'", args[7]));
					throw fife;
			}
		}

		else {
			// Read commands from stdin
			Scanner scanner = new Scanner(System.in);

			try{
				while(true){
					System.out.print("> ");
					System.out.flush();
					input = scanner.nextLine();
					if (input.equals("")){ continue; }

					processCommands(input);
				}
			} catch(StatusRuntimeException e) {
				System.out.println("Caught exception with description: " + e.getStatus().getDescription());
			} finally{
				scanner.close();
				System.exit(0);
			}
		}
	}

	public static void processCommands(String input){
		String[] tokens;

		tokens = input.split(" ");
		switch (tokens[0]) {
			case "help":
				System.out.println(help);
				break;
			case "ping":
				if(tokens.length != 2) {
					System.out.println("ping Format is 'ping %message%'");
					break;
				}
				app.ping(tokens[1]);
				break;
			case "balance":
				if(tokens.length != 1) {
					System.out.println("--balance Format is 'balance'\n");
					break;
				}
				app.balance();
				break;
			case "top-up":
				if(tokens.length != 2) {
					System.out.println("--top-up Format is 'top-up %int%'\n");
					break;
				}
				app.top(Integer.parseInt(tokens[1]));
				break;
			case "tag":
				if(tokens.length != 4) {
					System.out.println("--tag Format is 'tag %latitude% %longitude% %name%'\n");
				}
				app.tag(Double.parseDouble(tokens[1]), Double.parseDouble(tokens[2]),tokens[3]);
				break;
			case "move":
				if(!(tokens.length != 2 || tokens.length != 3)) {
					System.out.println("--move Format is 'move %name%' or 'move %latitude% %longitude%'\n" );
					break;
				} else if (tokens.length == 2){ //providing tag
					app.move(-1, -1 , tokens[1]);
				} else { //providing coords
					app.move(Double.parseDouble(tokens[1]), Double.parseDouble(tokens[2]), "-1");
				}
				break;
			case "at":
				if(tokens.length != 1) {
					System.out.println("--at Format is 'at'\n");
					break;
				}
				app.at();
				break;
			case "scan":
				if(tokens.length != 2) {
					System.out.println("--scan Format is 'scan %int%'\n");
					break;
				}
				app.scan(Integer.parseInt(tokens[1]));
				break;
			case "info":
				if(tokens.length != 2) {
					System.out.println("--info Format is 'info %id%'\n");
					break;
				}
				app.info(tokens[1]);
				break;
			case "bike-up":
				if(tokens.length != 2) {
					System.out.println("--bike-up Format is 'bike-up %id%'\n");
					break;
				}
				app.bike_up(tokens[1]);
				break;
			case "bike-down":
				if(tokens.length != 2) {
					System.out.println("--bike-down Format is 'bike-down %id%'\n");
					break;
				}
				app.bike_down(tokens[1]);
				break;
			case "sys_status":
				if(tokens.length != 1) {
					System.out.println("--sys_status Format is 'sys_status'\n");
					break;
				}
				app.sys_status();
				break;
			case "#":
				System.out.println("Read a comment: " + input);
				break;
			case "zzz":
				try {
				    Thread.sleep(Integer.parseInt(tokens[1]));
					}
				catch(InterruptedException e){
				    System.out.println("Interrupted Exception!");
				} finally {
					System.out.println("Slept for: " + tokens[1] + " ms");
				}
				break;
			case "quit":
				System.exit(0);
				break;
			default:
				System.out.println("Command not recognized. Use --help for a list of commands.");
				break;
		}
	}

}

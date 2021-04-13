package pt.tecnico.bicloin.app;

import java.util.Scanner;

import io.grpc.StatusRuntimeException;

public class AppMain {

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
		"--sys_status Format is 'sys_status'\n";

	public static void main(String[] args) {
		System.out.println(AppMain.class.getSimpleName());

		// receive and print arguments
		System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}

		/*
		// check arguments
		if (args.length < 6) {
			System.err.println("Argument(s) missing!");
			//TODO
			return;
		}
		*/

		final String zooHost = args[0];
		final String zooPort = args[1];
		final String userId = args[2];
		final int userPhone = Integer.parseInt(args[3]);
		final double latitude = Double.parseDouble(args[4]);
		final double longitude = Double.parseDouble(args[5]);


		App app = new App(zooHost, zooPort, userId, latitude, longitude);

		Scanner scanner = new Scanner(System.in);
		String input;
		String[] tokens;
		boolean close = false;


		try{
			while(!close){
				System.out.print("> ");
				System.out.flush();
				input = scanner.nextLine();
				if (input.equals("")){ continue; }

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
						System.out.println(help);
						break;
					case "top-up":
						if(tokens.length != 2) {
							System.out.println("--top-up Format is 'top-up %int%'\n");
							break;
						}
						System.out.println(help);
						break;
					case "tag":
						if(tokens.length != 3) {
							System.out.println("--tag Format is 'tag %latitude% %longitude% %name%'\n");
						}
						break;
					case "move":
						if(tokens.length != 2 || tokens.length != 3) {
							System.out.println("--move Format is 'move %name%' or 'move %latitude% %longitude%'\n" );
							break;
						}
						System.out.println(help);
						break;
					case "at":
						if(tokens.length != 1) {
							System.out.println("--at Format is 'at'\n");
							break;
						}
						System.out.println(help);
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
						System.out.println(help);
						break;
					case "bike-down":
						if(tokens.length != 2) {
							System.out.println("--bike-down Format is 'bike-down %id%'\n");
							break;
						}
						System.out.println(help);
						break;
					case "sys_status":
						if(tokens.length != 1) {
							System.out.println("--sys_status Format is 'sys_status'\n");
							break;
						}
						System.out.println("Not implemented in this state");
						break;
					default:
						System.out.println("Command not recognized. Use --help for a list of commands.");
						break;
				}
			}
		} catch(StatusRuntimeException e) {
			System.out.println("Caught exception with description: " + e.getStatus().getDescription());
		} finally{
			System.exit(0);
		}
	}
}

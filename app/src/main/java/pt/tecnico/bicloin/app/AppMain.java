package pt.tecnico.bicloin.app;

import java.util.Scanner;
import pt.tecnico.bicloin.hub.HubFrontend;

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
		final float lat = Float.parseFloat(args[4]);
		final float longi = Float.parseFloat(args[5]);

		HubFrontend frontend = new HubFrontend(zooHost, zooPort);
		// HubFronted(zooHost, zooPort, userId, userPhone, lat, longi);

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
						System.out.println(help);
						break;
					case "balance":
						System.out.println(help);
						break;
					case "top-up":
						System.out.println(help);
						break;
					case "tag":
						System.out.println(help);
						break;
					case "move":
						System.out.println(help);
						break;
					case "at":
						System.out.println(help);
						break;
					case "scan":
						System.out.println(help);
						break;
					case "info":
						System.out.println(help);
						break;
					case "bike-up":
						System.out.println(help);
						break;
					case "bike-down":
						System.out.println(help);
						break;
					case "sys_status":
						System.out.println(help);
						break;
					default:
						System.out.println("Command not recognized. Use --help for a list of commands.");
						break;
				}
			}
		} catch(StatusRuntimeException e) {
			System.out.println("Caught exception with description: " + e.getStatus().getDescription());
		} finally{
			frontend.closeChannel();
			System.exit(0);
		}
	}

	private static void ping(){
		//TODO
	}
	private static void balance(){
		//TODO
	}
	private static void top(){
		//TODO
	}
	private static void tag(){
		//TODO
	}
	private static void move(){
		//TODO
	}
	private static void at(){
		//TODO
	}
	private static void scan(){
		//TODO
	}
	private static void info(){
		//TODO
	}
	private static void bike_up(){
		//TODO
	}
	private static void bike_down(){
		//TODO
	}
	private static void sys_status(){
		//TODO
	}

}

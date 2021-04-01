package pt.tecnico.bicloin.app;

import java.util.Scanner;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;


public class AppMain {

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

		final String zooHost = args[0];
		final String zooPort = args[1];
		final String userId = args[2];
		final int userPhone = Integer.parseInt(args[3]);
		//final float lat = Float.parseFloat(args[4]);
		//final float long = Float.parseFloat(args[5]);

		App frontend;

		frontend = new App(args[0], args[1], args[2], args[3], args[4], args[5]);

		try {
			System.out.println(frontend.ctrl_ping("friend"));
		} catch (StatusRuntimeException e) {
			System.out.println("Caught exception with description: " + e.getStatus().getDescription());
		}
		*/
	}

}

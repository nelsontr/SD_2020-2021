package pt.tecnico.hub;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import pt.tecnico.hub.*;

import pt.tecnico.hub.HubServiceImpl;
import java.io.IOException;
import java.lang.InterruptedException;

public class HubMain {

	public static void main(String[] args) throws IOException , InterruptedException {
		System.out.println(HubMain.class.getSimpleName()); //

		// receive and print arguments
		System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}

		//Check arguments
		if (args.length < 7) {
		System.err.println("Argument(s) missing!");
		//TODO EXCEPTION
		return;
		}

		final String zooHost = args[0];
		final int zooPort = Integer.parseInt(args[1]);
		final String host = args[2];
		final String port = args[3];
		final int numberInstances = Integer.parseInt(args[4]);
		final String users = args[5];
		final String stations = args[6];
		if (args.length == 8) {
			final String initOption = args[7];
		}

		final BindableService impl = new HubServiceImpl();

		Server server = ServerBuilder.forPort(zooPort).addService(impl).build();

		// Start the server
		server.start();

		System.out.println("Server started");

		// Do not exit the main thread. Wait until server is terminated.
		server.awaitTermination();
	}
}

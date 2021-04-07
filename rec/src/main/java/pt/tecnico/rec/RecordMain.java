package pt.tecnico.rec;

/*import pt.ulisboa.tecnico.sdis.zk.ZKNaming;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;*/
import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import pt.tecnico.rec.*;

import pt.tecnico.rec.RecordServiceImpl;
import java.io.IOException;
import java.lang.InterruptedException;

public class RecordMain  {

	public static void main(String[] args) throws IOException , InterruptedException{
		System.out.println(RecordMain.class.getSimpleName());

		// receive and print arguments
		System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}

		//Check arguments
		/*if (args.length < 5) {
		System.err.println("Argument(s) missing!");
		//TODO EXCEPTION
		return;
	}*/

		//final String zooHost = args[0];
		final int zooPort = Integer.parseInt(args[0]);
		//final String host = args[2];
		//final int port = Integer.parseInt(args[3]);
		//final int numberInstances = Integer.parseInt(args[4]);

		final BindableService impl = new RecordServiceImpl();

		Server server = ServerBuilder.forPort(zooPort).addService(impl).build();

		// Start the server
		server.start();

		System.out.println("Server started");

		// Do not exit the main thread. Wait until server is terminated.
		server.awaitTermination();

	}
}

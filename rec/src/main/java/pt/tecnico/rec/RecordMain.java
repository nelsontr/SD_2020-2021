package pt.tecnico.rec;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import pt.ulisboa.tecnico.sdis.zk.ZKNaming;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;

import java.io.IOException;
import java.util.Properties;

public class RecordMain {

	public static void main(String[] args) throws IOException, InterruptedException {
		System.out.println(RecordMain.class.getSimpleName());

		// receive and print arguments
		System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}

		if (args.length == 1) {
			int port = Integer.parseInt(args[0]);

			final BindableService impl = new RecordServiceImpl();

			Server server = ServerBuilder.forPort(port).addService(impl).build();

			// Start the server
			server.start();

			System.out.println("Server started");

			// Do not exit the main thread. Wait until server is terminated.
			server.awaitTermination();
		} else {
			//Check arguments
			if (args.length < 5) {
				System.err.println("Argument(s) missing!");
				return;
			}

			java.io.InputStream is = RecordMain.class.getResourceAsStream("/rec.properties");
			java.util.Properties p = new Properties();
			p.load(is);

			final String zooHost = args[0];
			final String zooPort = args[1];
			final String host = args[2];
			final String port = args[3];
			final String numberInstances = args[4];


			String path = "/grpc/bicloin/rec/" + numberInstances;

			ZKNaming zkNaming = null;

			try {

				zkNaming = new ZKNaming(zooHost, zooPort);
				// publish
				zkNaming.rebind(path, host, port);

				final BindableService impl = new RecordServiceImpl();

				Server server = ServerBuilder.forPort(Integer.parseInt(port)).addService(impl).build();

				// Start the server
				server.start();

				System.out.println("Server started");

				// Do not exit the main thread. Wait until server is terminated.
				server.awaitTermination();
			} catch (Exception e) {
				System.out.println("Internal Server Error: " + e.getMessage());
			} finally {
				try {
					if (zkNaming != null) {
						// remove
						zkNaming.unbind(path, host, port);
					}
				} catch (ZKNamingException zkne) {
					System.out.println("ERROR : Unbind zknaming SiloServerApp");
				}
				System.exit(0);

			}
		}
	}
}

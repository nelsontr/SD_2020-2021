package pt.tecnico.rec;


public class RecordMain {

	public static void main(String[] args) {
		System.out.println(RecordMain.class.getSimpleName());

		// receive and print arguments
		System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}

		//Check arguments
		if (args.length < 5) {
		System.err.println("Argument(s) missing!");
		//TODO EXCEPTION
		return;
		}

		final String zooHost = args[0];
		final String zooPort = args[1];
		final String host = args[2];
		final String port = args[3];
		final int numberInstances = Integer.parseInt(args[4]);

		ZKNaming zkNaming = null;

		try {
			zkNaming = new ZKNaming(zooHost, zooPort);
			zkNaming.rebind(path, host, port);


			final BindableService impl = new HubServiceImpl();

			Server server = ServerBuilder.forPort(port).addService(impl).build();

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
					zkNaming.unbind(path, host, port);
				}
			} catch (ZKNamingException zkne) {
				System.out.println("ERROR : Unbind zknaming SiloServerApp");
			}
			System.exit(0);
		}

	}

}

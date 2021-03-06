package pt.tecnico.bicloin.hub;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import pt.ulisboa.tecnico.sdis.zk.ZKNaming;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;
import java.util.Scanner;

public class HubMain {

    public static void main(String[] args) throws IOException, InterruptedException, FileNotFoundException {
        System.out.println(HubMain.class.getSimpleName()); //
        // receive and print arguments
        System.out.printf("Received %d arguments%n", args.length);
        for (int i = 0; i < args.length; i++) {
            System.out.printf("arg[%d] = %s%n", i, args[i]);
        }


        java.io.InputStream is = HubMain.class.getResourceAsStream("/hub.properties");
        java.util.Properties p = new Properties();
        p.load(is);

        String hostZoo = p.getProperty("zoo.host");
        String portZoo = p.getProperty("zoo.port");
        String instance = p.getProperty("instance");

        if (args.length == 6) {

            String port = p.getProperty("server.port");

            final HubServiceImpl impl = new HubServiceImpl(hostZoo, portZoo, instance);

            Server server = ServerBuilder.forPort(Integer.parseInt(port)).addService((BindableService) impl).build();

            // Start the server
            server.start();

            System.out.println("Server started");

            // Do not exit the main thread. Wait until server is terminated.
            server.awaitTermination();
        } else {
            //Check arguments
            if (args.length > 8 || args.length < 6) {
                System.err.println("Invalid length of arguments!");
                return;
            }


            final String zooHost = args[0];
            final String zooPort = args[1];
            final String host = args[2];
            final String port = args[3];
            final String numberInstances = args[4];
            final String users = args[5];
            final String stations = args[6];
            boolean initOption = (args.length == 8);

            String path = "/grpc/bicloin/hub/" + numberInstances;

            ZKNaming zkNaming = null;

            try {

                zkNaming = new ZKNaming(zooHost, zooPort);
                // publish
                zkNaming.rebind(path, host, port);

                final HubServiceImpl impl = new HubServiceImpl(zooHost, zooPort, numberInstances);

                Server server = ServerBuilder.forPort(Integer.parseInt(port)).addService((BindableService) impl).build();

                // Start the server
                server.start();

                // Init with users + stations
                //  Users
                Scanner fileScanner;
                String initialData = "";

                if (!users.isBlank()) {
                    fileScanner = new Scanner(Objects.requireNonNull(HubMain.class.getResourceAsStream("/" + users)));
                    while (fileScanner.hasNextLine()) {
                        initialData = initialData.concat(fileScanner.nextLine() + "\n");
                    }
                }
                //  Stations
                if (!stations.isBlank()) {
                    fileScanner = new Scanner(Objects.requireNonNull(HubMain.class.getResourceAsStream("/" + stations)));
                    while (fileScanner.hasNextLine()) {
                        initialData = initialData.concat(fileScanner.nextLine() + "\n");
                    }
                }

                impl.initData(initialData, initOption);

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

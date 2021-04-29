package pt.tecnico.bicloin.hub;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

import pt.tecnico.bicloin.hub.grpc.*;
import pt.tecnico.bicloin.hub.HubServiceImpl;

public class HubMain {

    public static void main(String[] args) throws IOException, InterruptedException, FileNotFoundException {
        System.out.println(HubMain.class.getSimpleName()); //
        // receive and print arguments
        System.out.printf("Received %d arguments%n", args.length);
        for (int i = 0; i < args.length; i++) {
            System.out.printf("arg[%d] = %s%n", i, args[i]);
        }

        if (args.length == 1) {

            int port = Integer.parseInt(args[0]);

            final HubServiceImpl impl = new HubServiceImpl();

            Server server = ServerBuilder.forPort(port).addService((BindableService) impl).build();

            // Start the server
            server.start();

            System.out.println("Server started");

            // Do not exit the main thread. Wait until server is terminated.
            server.awaitTermination();
        } else {
            //Check arguments
            if (args.length > 8 || args.length < 7) {
                System.err.println("Argument(s) missing!");
                return;
            }

            final String zooHost = args[0];
            final String host = args[2];
            final int port = Integer.parseInt(args[3]);
            final int numberInstances = Integer.parseInt(args[4]);
            final String users = args[5];
            final String stations = args[6];
            boolean initOption = (args.length == 8);

            final int zooPort = Integer.parseInt(args[1]);
            final HubServiceImpl impl = new HubServiceImpl();

            Server server = ServerBuilder.forPort(zooPort).addService((BindableService) impl).build();

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
        }
    }
}

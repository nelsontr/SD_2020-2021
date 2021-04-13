package pt.tecnico.rec;

import pt.tecnico.rec.grpc.*;
import io.grpc.StatusRuntimeException;

public class RecordTester {

	public static void main(String[] args) {
		System.out.println(RecordTester.class.getSimpleName());

		// receive and print arguments
		System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}

		final String host = args[0];
		final String port = args[1];

		RecFrontend frontend = new RecFrontend(host, port);

		try {
			PingRequest request = PingRequest.newBuilder().setInput("friend").build();
			PingResponse response = frontend.ping(request);
			System.out.println(response);
		} catch (StatusRuntimeException e) {
			System.out.println("Caught exception with description: " +
			e.getStatus().getDescription());
		}


		frontend.closeChannel();

	}

}

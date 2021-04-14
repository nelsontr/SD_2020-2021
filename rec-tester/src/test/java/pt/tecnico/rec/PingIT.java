package pt.tecnico.rec;

import java.io.IOException;
import java.util.Properties;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import pt.tecnico.rec.grpc.*;
import io.grpc.StatusRuntimeException;

import static io.grpc.Status.INVALID_ARGUMENT;

public class PingIT extends BaseIT {

  @Test
	public void pingOKTest() {
		PingRequest request = PingRequest.newBuilder().setInput("friend").build();
		PingResponse response = frontend.ping(request);
		System.out.println(response.getOutput());
		assertEquals("Hello friend!", response.getOutput());
	}

  @Test
	public void emptyPingTest() {
		PingRequest request = PingRequest.newBuilder().setInput("").build();
		assertEquals(
		INVALID_ARGUMENT.getCode(),
		assertThrows(
		StatusRuntimeException.class, () -> frontend.ping(request))
		.getStatus()
		.getCode());
	}
}

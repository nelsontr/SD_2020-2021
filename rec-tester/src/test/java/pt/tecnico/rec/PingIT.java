package pt.tecnico.rec;

import pt.tecnico.rec.grpc.*;
import org.junit.jupiter.api.Test;
import io.grpc.StatusRuntimeException;

import static io.grpc.Status.INVALID_ARGUMENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PingIT extends BaseIT {

	@Test
	public void pingOKTest() {
		PingRequest request = PingRequest.newBuilder().setInput("friend").build();
		PingResponse response = frontend.ping(request);
		assertEquals("friend", response.getOutput());
	}

	@Test
	public void emptyPingTest() {
		PingRequest request = PingRequest.newBuilder().setInput("").build();
		assertEquals(INVALID_ARGUMENT.getCode(),
			assertThrows(StatusRuntimeException.class, () -> frontend.ping(request))
						.getStatus()
						.getCode());
	}
}

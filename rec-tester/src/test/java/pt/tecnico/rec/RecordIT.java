package pt.tecnico.rec;

import pt.tecnico.rec.grpc.*;
import org.junit.jupiter.api.Test;
import io.grpc.StatusRuntimeException;

import static io.grpc.Status.NOT_FOUND;
import static io.grpc.Status.INVALID_ARGUMENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RecordIT extends BaseIT {

	@Test
	public void creatingFirstBalanceTest() {
		WriteRequest request = WriteRequest.newBuilder().setName(NAME_1 + REQUEST_1).setIntValue(BALANCE_1).build();
		WriteResponse response = Qfrontend.write(request);
		assertEquals("OK", response.getResponse());

		ReadRequest request2 = ReadRequest.newBuilder().setName(NAME_1 + REQUEST_1).build();
		ReadResponse response2 = Qfrontend.read(request2);
		assertEquals(BALANCE_1, response2.getValue());
	}

	@Test
	public void writingBalanceBelowZeroTest() {
		WriteRequest request = WriteRequest.newBuilder().setName(NAME_1 + REQUEST_1)
			.setIntValue(BALANCE_BELLOW_ZERO).build();
		WriteResponse response = Qfrontend.write(request);
		ReadRequest request2 = ReadRequest.newBuilder().setName(NAME_1 + REQUEST_1).build();
		ReadResponse response2 = Qfrontend.read(request2);
		assertEquals(-1, response2.getValue());

		/*WriteRequest request = WriteRequest.newBuilder().setName(NAME_1 + REQUEST_1)
				.setIntValue(BALANCE_BELLOW_ZERO).build();

		assertEquals(INVALID_ARGUMENT.getCode(), assertThrows(
				StatusRuntimeException.class, () -> Qfrontend.write(request))
				.getStatus()
				.getCode());*/
	}

	@Test
	public void readingBalanceWithoutUserTest() {
		ReadResponse response = Qfrontend.read(
			ReadRequest.newBuilder().setName(NAME_1 + REQUEST_1).build());

		assertEquals(-1, response.getValue());
	}
}

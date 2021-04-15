package pt.tecnico.bicloin.hub;

import pt.tecnico.bicloin.hub.grpc.*;

import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.Test;

import static io.grpc.Status.INVALID_ARGUMENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PingIT extends BaseIT {

    @Test
    public void pingOKTest() {
        CtrlPingRequest request = CtrlPingRequest.newBuilder().setInput("friend").build();
        CtrlPingResponse response = frontend.ping(request);
        assertEquals("Hello friend!", response.getOutput());
    }

    @Test
    public void emptyPingTest() {
        CtrlPingRequest request = CtrlPingRequest.newBuilder().setInput("").build();
        assertEquals(INVALID_ARGUMENT.getCode(),
        assertThrows(StatusRuntimeException.class, () -> frontend.ping(request))
                        .getStatus()
                        .getCode());
    }
}

package pt.tecnico.bicloin.hub;

import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.Test;
import pt.tecnico.bicloin.hub.grpc.CtrlPingRequest;
import pt.tecnico.bicloin.hub.grpc.CtrlPingResponse;

import static io.grpc.Status.INVALID_ARGUMENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PingIT extends BaseIT {

    @Test
    public void pingOKTest() {
        CtrlPingRequest request = CtrlPingRequest.newBuilder().setInput("friend").build();
        CtrlPingResponse response = frontend.ping(request);
        assertEquals("Hub says hello friend!\nRec says hello friend!\n", response.getOutput());
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

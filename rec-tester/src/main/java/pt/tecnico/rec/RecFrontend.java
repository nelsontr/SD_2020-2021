package pt.tecnico.rec;

import pt.tecnico.rec.grpc.*;

import io.grpc.Status;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public class RecFrontend {

    private ManagedChannel channel;
    private RecordGrpc.RecordBlockingStub stub;
    private static final int BEST_EFFORT = 3;

    public RecFrontend(String host, String port) {
        try {
            final String target = host + ":" + port;
            this.channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
            this.stub = RecordGrpc.newBlockingStub(this.channel);
        } catch (StatusRuntimeException sre) {
            System.out.println("ERROR : Frontend createNewChannel : Could not create channel\n"
                    + sre.getStatus().getDescription());
        }
    }

    public void errorHandling(StatusRuntimeException sre, String function, int tries) {
        if (tries == BEST_EFFORT) {
            System.out.println("WARN <" + function + " " + tries + "> : Max tries reached!");
            throw sre;
        }

        if (sre.getStatus().getCode() == Status.Code.UNAVAILABLE) {
            System.out.println("WARN <" + function + " " + tries + "> : Cant connect to server!");
        } else {
            System.out.println(sre);
            throw sre;
        }
    }

    public PingResponse ping(PingRequest request) {
        int tries = 0;

        while (true) {
            try {
                return stub.ctrlPing(request);
            } catch (StatusRuntimeException sre) {
                errorHandling(sre, "ping", ++tries);
            }
        }
    }

    public WriteResponse write(WriteRequest request) {
        int tries = 0;

        while (true) {
            try {
                return stub.write(request);
            } catch (StatusRuntimeException sre) {
                errorHandling(sre, "write", ++tries);
            }
        }
    }

    public ReadResponse read(ReadRequest request) {
        int tries = 0;

        while (true) {
            try {
                return stub.read(request);
            } catch (StatusRuntimeException sre) {
                errorHandling(sre, "read", ++tries);
            }
        }
    }

    public ClearResponse clear(ClearRequest request) {
        int tries = 0;

        while (true) {
            try {
                return stub.clearRecords(request);
            } catch (StatusRuntimeException sre) {
                errorHandling(sre, "clear", ++tries);
            }
        }
    }

    public SysStatResponse sysStat(SysStatRequest request) {
        int tries = 0;

        while (true) {
            try {
                return stub.sysStat(request);
            } catch (StatusRuntimeException sre) {
                errorHandling(sre, "clear", ++tries);
            }
        }
    }

    public void closeChannel() {
        this.channel.shutdownNow();
    }
}

package pt.tecnico.rec;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import com.google.protobuf.GeneratedMessageV3;

import pt.tecnico.rec.grpc.*;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;

import java.util.Scanner;

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

  public CtrlPingResponse ping(CtrlPingRequest request) {
        int tries = 0;

        while (true) {
            try {
                System.out.println("Ping " + (tries + 1) + "...");
                return stub.ctrlPing(request);
            } catch (StatusRuntimeException sre) {
                if (sre.getStatus().getCode() == Status.Code.INVALID_ARGUMENT || ++tries == BEST_EFFORT) {
                    System.out.println("WARN : Cant connect to server!");
                    throw sre;
                }
            }
        }
    }

    public void closeChannel(){
        this.channel.shutdownNow();
    }

}

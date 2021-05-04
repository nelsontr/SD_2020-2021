package pt.tecnico.rec;

import pt.tecnico.rec.grpc.*;

import io.grpc.Status;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import pt.ulisboa.tecnico.sdis.zk.ZKNaming;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;
import pt.ulisboa.tecnico.sdis.zk.ZKRecord;


public class RecFrontend {

    private ManagedChannel channel;
    private RecordGrpc.RecordBlockingStub stub;
    private static final int BEST_EFFORT = 3;

    private final ZKNaming zkNaming;
    private final String path = "/grpc/bicloin/rec";

    public RecFrontend(String zooHost, String zooPort, String instance) {
        this.zkNaming = new ZKNaming(zooHost, zooPort);
        try {
            ZKRecord record = this.zkNaming.lookup(this.path + "/" + instance);
            createNewChannel(record.getURI());
            createRecFrontend();
        } catch (ZKNamingException zkne) {
            throw new IllegalArgumentException("Instance " + instance + " does not exist!");
        }
    }

    public RecFrontend(String zooHost, String zooPort) {
        this.zkNaming = new ZKNaming(zooHost, zooPort);

        try {
            createNewChannel(findRecInstance());
            createRecFrontend();
        } catch (IllegalArgumentException | ZKNamingException zkne) {
            throw new IllegalArgumentException("No servers available!");
        }
    }


    private String findRecInstance() throws ZKNamingException {

        List<ZKRecord> records = new ArrayList<>(this.zkNaming.listRecords(this.path));

        int rnd = new Random().nextInt(records.size());
        System.out.println("Found target: " + records.get(rnd).getURI());
        return records.get(rnd).getURI();
    }


    private void createNewChannel(String target) {
        try {
            this.channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
            this.stub = RecordGrpc.newBlockingStub(this.channel);
        } catch (StatusRuntimeException sre) {
            System.out.println("ERROR : Frontend createNewChannel : Could not create channel\n"
                    + sre.getStatus().getDescription());
        }
    }

    private void createRecFrontend() {
        //Nothing for now
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

    String sysStatusIndividual(ZKRecord record, SysStatRequest request){
      int tries = 0;

      while(true){
        try {
          createNewChannel(record.getURI());
          return "\n"+record.getPath()+": "+
                    stub.sysStat(request).getStatus();
        } catch (StatusRuntimeException sre) {
            if (++tries == BEST_EFFORT) return "\n"+record.getPath()+": DOWN";
            else errorHandling(sre, "sysStatusInd", tries);
        }
      }
    }


    public SysStatResponse sysStat(SysStatRequest request) {
        String responseString = "";
        RecordGrpc.RecordBlockingStub oldStub = this.stub;

        while (true) {
            try {
              List<ZKRecord> records = new ArrayList<>(this.zkNaming.listRecords(this.path));

              for(ZKRecord record: records){
                responseString += sysStatusIndividual(record, request);
              }

              this.stub = oldStub;
              return SysStatResponse.newBuilder().setStatus(responseString).build();
            } catch (ZKNamingException sre) {
                System.out.println("WARN <sysStatus> : Cant connect to server!");
            }
        }
    }

    public void closeChannel() {
        this.channel.shutdownNow();
    }
}

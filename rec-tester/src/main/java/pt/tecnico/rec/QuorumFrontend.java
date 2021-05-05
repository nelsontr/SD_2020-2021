package pt.tecnico.rec;

import pt.tecnico.rec.grpc.*;

import io.grpc.Status;
import static io.grpc.Status.UNKNOWN;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import pt.ulisboa.tecnico.sdis.zk.ZKNaming;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;
import pt.ulisboa.tecnico.sdis.zk.ZKRecord;


public class QuorumFrontend {

    private final String OK_RESPONSE = "OK";

    private static class Replica {
        private ZKRecord _zkRecord;
        private ManagedChannel _rChannel;
        private RecordGrpc.RecordStub _rStub;

        private Replica(ZKRecord record, ManagedChannel channel, RecordGrpc.RecordStub stub) {
            _zkRecord = record;
            _rChannel = channel;
            _rStub = stub;
        }
    }
    
    private final ZKNaming zkNaming;
    private Map<String, Replica> _replicas = new HashMap<>();
    private String _generalPath = "/grpc/bicloin/rec";
    private int _minAcks;
    private int _maxRecDown;

/*     private ManagedChannel channel;
    private RecordGrpc.RecordBlockingStub stub;
    private static final int BEST_EFFORT = 3; */

    

    public QuorumFrontend(String zooHost, String zooPort) throws ZKNamingException {
        ManagedChannel channel;
        RecordGrpc.RecordStub stub;
        this.zkNaming = new ZKNaming(zooHost, zooPort);

        try {
            List<ZKRecord> records = new ArrayList<>(this.zkNaming.listRecords(this._generalPath));

            for(ZKRecord record : records) {
            
                channel = ManagedChannelBuilder.forTarget(record.getURI()).usePlaintext().build();
                stub  = RecordGrpc.newStub(channel);

                String response = "";
                SysStatRequest sysRequest = SysStatRequest.newBuilder().build();

                response += sysStatusIndividual(record, sysRequest);

                if (!response.equals("")) {
                    Replica replica = new Replica(record, channel, stub);
                    _replicas.put(record.getPath(), replica);
                }
            }

            _maxRecDown = records.size() / 2;
            _minAcks = records.size() / 2  + 1;

            if (this._replicas.size() == 0) {
                System.out.println("NO REC SERVER AVAILABLE");
                System.exit(-1);
            }


        } catch(ZKNamingException zkne) {
            throw new IllegalArgumentException("No servers available!");
        }
    }

    /* public QuorumFrontend(String zooHost, String zooPort) {
        this.zkNaming = new ZKNaming(zooHost, zooPort);

        try {
            createNewChannel(findRecInstance());
            createRecFrontend();
        } catch (IllegalArgumentException | ZKNamingException zkne) {
            throw new IllegalArgumentException("No servers available!");
        }
    }
 */

    /* private String findRecInstance() throws ZKNamingException {

        List<ZKRecord> records = new ArrayList<>(this.zkNaming.listRecords(this.path));

        int rnd = new Random().nextInt(records.size());
        System.out.println("Found target: " + records.get(rnd).getURI());
        return records.get(rnd).getURI();
    }
 */

    private void createNewChannel(String target) {
        try {
            this.channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
            this.stub = RecordGrpc.newBlockingStub(this.channel);
        } catch (StatusRuntimeException sre) {
            System.out.println("ERROR : Frontend createNewChannel : Could not create channel\n"
                    + sre.getStatus().getDescription());
        }
    }

   /*  private void createRecFrontend() {
        //Nothing for now
    } */

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

    public WriteResponse write(WriteRequest request) throws StatusRuntimeException{
        
        ResponseCollector<WriteResponse> responseCollector = new ResponseCollector<>();
        final Observer<WriteResponse> observer = new Observer<>(responseCollector);

        RecThread<WriteResponse> thread = new RecThread<>(_minAcks, _maxRecDown, responseCollector);
        synchronized (thread) {
            thread.start();
            _replicas.values().forEach(replica -> replica._rStub.write(request, observer));

            try {
                thread.wait();
                final WriteResponse response = WriteResponse.newBuilder().setResponse(OK_RESPONSE).build();
                return response;
            } catch (InterruptedException ie) {
                throw UNKNOWN.withDescription("Unkown procedure error on Write").asRuntimeException();
            }
        }
    }

    public ReadResponse read(ReadRequest request) throws StatusRuntimeException{
        
        ResponseCollector<ReadResponse> responseCollector = new ResponseCollector<>();
        final Observer<ReadResponse> observer = new Observer<>(responseCollector);

        RecThread<ReadResponse> thread = new RecThread<>(_minAcks, _maxRecDown, responseCollector);

        synchronized(thread) {
            thread.start();
            _replicas.values().forEach(replica -> replica._rStub.read(request, observer));

            try {
                thread.wait();
                List<ReadResponse> readResponses = new ArrayList<>(thread.getResponseCollector().getOKResponses());
                int bestIndex = this.readFindIndex(readResponses);
                return readResponses.get(bestIndex);

            } catch (InterruptedException ie) {
                throw UNKNOWN.withDescription("Unkown procedure error on Read").asRuntimeException();
            }
        }
    }

    private int readFindIndex(List<ReadResponse> responses){
        int bestIndex = -1;
        int maxSequence = -1;

        for(int i = 0 ; i < responses.size(); i++) {
            if(responses.get(i).getSequence() > maxSequence) {
                bestIndex = i;
                maxSequence = responses.get(i).getSequence();
            }
        }
        return bestIndex;
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

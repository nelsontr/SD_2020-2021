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
    private RecordGrpc.RecordBlockingStub stub;*/
    private static final int BEST_EFFORT = 3;



    public QuorumFrontend(String zooHost, String zooPort) throws ZKNamingException {
        ManagedChannel channel;
        RecordGrpc.RecordStub stub;
        this.zkNaming = new ZKNaming(zooHost, zooPort);

        try {
            List<ZKRecord> records = new ArrayList<>(this.zkNaming.listRecords(this._generalPath));

            for(ZKRecord record : records) {

                channel = ManagedChannelBuilder.forTarget(record.getURI()).usePlaintext().build();
                stub  = RecordGrpc.newStub(channel);


                Replica replica = new Replica(record, channel, stub);
                _replicas.put(record.getPath(), replica);

            }

            _maxRecDown = (records.size() - 1) / 2;
            _minAcks = _maxRecDown + 1;

            if (this._replicas.size() == 0) {
                System.out.println("NO REC SERVER AVAILABLE");
                System.exit(-1);
            }


        } catch(ZKNamingException zkne) {
            throw new IllegalArgumentException("No servers available!");
        } catch (StatusRuntimeException sre) {
            /**/
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
      ResponseCollector<PingResponse> responseCollector = new ResponseCollector<>();
      final Observer<PingResponse> observer = new Observer<>(responseCollector);

      RecThread<PingResponse> thread = new RecThread<>(_minAcks, _maxRecDown, responseCollector);
      synchronized (thread) {
          thread.start();
          _replicas.values().forEach(replica -> replica._rStub.ctrlPing(request, observer));

          try {
              thread.wait();

              if (thread.getException() != null) {
                    throw UNKNOWN.withDescription("Unkown procedure error on Clear Thread").asRuntimeException();
              }

              List<PingResponse> pingResponses = new ArrayList<>(thread.getResponseCollector().getOKResponses());

              int bestIndex = this.pingFindIndex(pingResponses);

              return pingResponses.get(bestIndex);
          } catch (InterruptedException ie) {
              throw UNKNOWN.withDescription("Unkown procedure error on Ping").asRuntimeException();
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

                if (thread.getException() != null) {
                      throw UNKNOWN.withDescription("Unkown procedure error on Clear Thread").asRuntimeException();
                }

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

                if (thread.getException() != null) {
                      throw UNKNOWN.withDescription("Unkown procedure error on Clear Thread").asRuntimeException();
                }

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

    private int pingFindIndex(List<PingResponse> responses){
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
      ResponseCollector<ClearResponse> responseCollector = new ResponseCollector<>();
      final Observer<ClearResponse> observer = new Observer<>(responseCollector);

      RecThread<ClearResponse> thread = new RecThread<>(_minAcks, _maxRecDown, responseCollector);
      synchronized (thread) {
          thread.start();
          _replicas.values().forEach(replica -> replica._rStub.clearRecords(request, observer));

          try {
              thread.wait();

              if (thread.getException() != null) {
                    throw UNKNOWN.withDescription("Unkown procedure error on Clear Thread").asRuntimeException();
              }

              final ClearResponse response = ClearResponse.newBuilder().build();
              return response;
          } catch (InterruptedException ie) {
              throw UNKNOWN.withDescription("Unkown procedure error on Clear").asRuntimeException();
          }
      }
    }
/*
    String sysStatusIndividual(ZKRecord record, SysStatRequest request, RecordGrpc.RecordStub stub){
      int tries = 0;

      while(true){
        try {
          return "\n"+record.getPath()+": "+
                    stub.sysStat(request).getStatus();
        } catch (StatusRuntimeException sre) {
            if (++tries == BEST_EFFORT) return "\n"+record.getPath()+": DOWN";
            else errorHandling(sre, "sysStatusInd", tries);
        }
      }
    }
*/

    public SysStatResponse sysStat(SysStatRequest request) {
        String responseString = "";

        ResponseCollector<SysStatResponse> responseCollector = new ResponseCollector<>();
        final Observer<SysStatResponse> observer = new Observer<>(responseCollector);

        RecThread<SysStatResponse> thread = new RecThread<>(_minAcks, _maxRecDown, responseCollector);

        synchronized(thread) {
            thread.start();
            _replicas.values().forEach(replica -> replica._rStub.sysStat(request, observer));

            try {
                thread.wait();

                if (thread.getException() != null) {
                      throw UNKNOWN.withDescription("Unkown procedure error on SysStat Thread").asRuntimeException();
                }

                List<SysStatResponse> sysStatResponses = new ArrayList<>(thread.getResponseCollector().getOKResponses());

                for(SysStatResponse response : sysStatResponses){
                  responseString += response.getStatus();
                }

                SysStatResponse response = SysStatResponse.newBuilder().setStatus(responseString).build();

                return response;

            } catch (InterruptedException ie) {
                throw UNKNOWN.withDescription("Unkown procedure error on Read").asRuntimeException();
            }
        }
    }

    public void closeChannel() {
        /**/
    }
}

package pt.tecnico.rec;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import pt.tecnico.rec.grpc.*;
import pt.ulisboa.tecnico.sdis.zk.ZKNaming;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;
import pt.ulisboa.tecnico.sdis.zk.ZKRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.grpc.Status.UNKNOWN;


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

    private int _minAcks;
    private int _maxRecDown;
    private String _zooHost;
    private String _zooPort;
    private final ZKNaming zkNaming;
    private Map<String, Replica> _replicas;
    private String _generalPath = "/grpc/bicloin/rec";

    private static final int BEST_EFFORT = 3;


    public QuorumFrontend(String zooHost, String zooPort) {
        _zooHost = zooHost;
        _zooPort = zooPort;
        _replicas = new HashMap<>();

        this.zkNaming = new ZKNaming(_zooHost, _zooPort);
        _replicas = this.findReplicas();
    }

    private Map<String, Replica> findReplicas() {
        ManagedChannel channel;
        RecordGrpc.RecordStub stub;
        Map<String, Replica> replicas = new HashMap<>();

        try {
            List<ZKRecord> records = new ArrayList<>(this.zkNaming.listRecords(this._generalPath));

            for (ZKRecord record : records) {
                channel = ManagedChannelBuilder.forTarget(record.getURI()).usePlaintext().build();
                stub = RecordGrpc.newStub(channel);

                Replica replica = new Replica(record, channel, stub);
                replicas.put(record.getPath(), replica);
            }

            _maxRecDown = (records.size() - 1) / 2;
            _minAcks = _maxRecDown + 1;

            if (replicas.size() == 0) {
                System.out.println("NO REC SERVER AVAILABLE");
                System.exit(-1);
            } else if (replicas.size() % 2 == 0) {
                System.out.println("REPLICAS SIZE NOT A ODD NUMBER");
                System.exit(-1);
            }

        } catch (StatusRuntimeException | ZKNamingException zkne) {
            throw new IllegalArgumentException("No servers available!");
        }

        return replicas;
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


    public WriteResponse write(WriteRequest request) throws StatusRuntimeException {
        _replicas.values().forEach(replica -> replica._rChannel.shutdown());
        _replicas = this.findReplicas();

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

    public ReadResponse read(ReadRequest request) throws StatusRuntimeException {

        _replicas.values().forEach(replica -> replica._rChannel.shutdown());
        _replicas = this.findReplicas();

        ResponseCollector<ReadResponse> responseCollector = new ResponseCollector<>();
        final Observer<ReadResponse> observer = new Observer<>(responseCollector);

        RecThread<ReadResponse> thread = new RecThread<>(_minAcks, _maxRecDown, responseCollector);

        synchronized (thread) {
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

    private int readFindIndex(List<ReadResponse> responses) {
        int bestIndex = -1;
        int maxSequence = -1;

        for (int i = 0; i < responses.size(); i++) {
            if (responses.get(i).getSequence() > maxSequence) {
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


    public void closeChannel() {
        _replicas.values().forEach(replica -> replica._rChannel.shutdown());
    }
}

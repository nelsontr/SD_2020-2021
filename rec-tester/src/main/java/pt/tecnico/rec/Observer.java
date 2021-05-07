package pt.tecnico.rec;

import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

import static io.grpc.Status.UNKNOWN;


public class Observer<ResponseType> implements StreamObserver<ResponseType> {

    private ResponseCollector<ResponseType> _responseCollector;

    Observer(ResponseCollector<ResponseType> responses) {
        _responseCollector = responses;
    }

    @Override
    public void onNext(ResponseType response) {
        synchronized (_responseCollector) {
            _responseCollector.addOKResponse(response);
        }
    }

    @Override
    public void onError(Throwable throwable) {
        StatusRuntimeException sre;
        try {
            sre = (StatusRuntimeException) throwable;
        } catch (ClassCastException cce) {
            sre = UNKNOWN.asRuntimeException();
        }
        synchronized (_responseCollector) {
            _responseCollector.addFAILResponse(sre);
        }
    }


    @Override
    public void onCompleted() {
        
    }

}

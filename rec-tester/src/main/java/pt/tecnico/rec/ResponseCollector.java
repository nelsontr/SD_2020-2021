package pt.tecnico.rec;

import java.util.ArrayList;
import java.util.List;

import io.grpc.StatusRuntimeException;

public class ResponseCollector<ResponseType> {

    private volatile int _acks;
    private volatile int _fails;
    private List<ResponseType> _okResponses = new ArrayList<>();
    private List<StatusRuntimeException> _failResponses = new ArrayList<>();

    void addOKResponse(ResponseType response) {
        _okResponses.add(response);
        _acks++;
        this.notifyAll();
    }

    void addFAILResponse(StatusRuntimeException errorResponse) {
        _failResponses.add(errorResponse);
        _fails++;
        this.notifyAll();
    }

    int getAcks() {
        return _acks;
    }

    int getFails() {
        return _fails;
    }

    List<ResponseType> getOKResponses() {
        return _okResponses;
    }

    List<StatusRuntimeException> getFAILResponses() {
        return _failResponses;
    }
}

package pt.tecnico.rec;


public class RecThread<ResponseType> extends Thread {
    private int _failsAllowed;
    private int _acksNeeded;
    Exception _exception;
    ResponseCollector<ResponseType> _responseCollector;


    RecThread(int maxFails, int minAcks, ResponseCollector<ResponseType> results) {
        _acksNeeded = minAcks;
        _failsAllowed = maxFails;
        _responseCollector = results;
    }

    public ResponseCollector<ResponseType> getResponseCollector() {
        return _responseCollector;
    }

    public Exception getException() {
        synchronized (this) {
            return this._exception;
        }
    }

    @Override
    public void run() {
        try {
            waitNeededAcks();

            synchronized (this) {
                this.notifyAll();
            }
        } catch (InterruptedException e) {
            _exception = e;
        }
    }

    private void waitNeededAcks() throws InterruptedException {
        synchronized (_responseCollector) {
            while (_responseCollector.getFails() <= _failsAllowed && _responseCollector.getAcks() < _acksNeeded)
                _responseCollector.wait();
        }
    }

}

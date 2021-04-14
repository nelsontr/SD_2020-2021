package pt.tecnico.bicloin.hub;

import java.io.IOException;
import java.util.Properties;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import pt.tecnico.bicloin.hub.grpc.*;
import io.grpc.StatusRuntimeException;

import static io.grpc.Status.INVALID_ARGUMENT;

public class InfoIt extends BaseIT {
    
    @Test
    public void InfoOKTest() {

    }

    @Test
    public void unexistentStationInfo() {

    }
}

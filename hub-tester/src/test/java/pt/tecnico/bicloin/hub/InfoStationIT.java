package pt.tecnico.bicloin.hub;

import org.junit.jupiter.api.Test;
import pt.tecnico.bicloin.hub.grpc.*;
import io.grpc.StatusRuntimeException;

import static io.grpc.Status.NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class InfoStationIT extends BaseIT {

    @Test
    public void infoStationRightId() {
        InfoStationRequest request1 = InfoStationRequest.newBuilder().setStationId(STATION_ID_1).build();
        InfoStationResponse response1 = frontend.infoStation(request1);

        assertEquals(STATION_NAME_1, response1.getName());
        assertEquals(STATION_LAT_1, response1.getLat());
        assertEquals(STATION_LONG_1, response1.getLong());
        assertEquals(STATION_DOCK_CAPACITY_1, response1.getDockCapacity());
        assertEquals(STATION_COMPENSATION_1, response1.getPrize());
        assertEquals(STATION_AVAILABLEBIKES_1, response1.getAvailableBikes());
    }

    @Test
    public void infoStationNoStationFound() {
        InfoStationRequest request1 = InfoStationRequest.newBuilder().setStationId(STATION_ID_0).build();

        StatusRuntimeException sre = assertThrows(
                StatusRuntimeException.class, () -> frontend.infoStation(request1));

        assertEquals(NOT_FOUND.getCode(), sre.getStatus().getCode());
        assertEquals("Specified station doesn't exist", sre.getStatus().getDescription());
    }
}

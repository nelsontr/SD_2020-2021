package pt.tecnico.bicloin.hub;

import org.junit.jupiter.api.Test;
import pt.tecnico.bicloin.hub.grpc.*;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LocateStationIT extends BaseIT {

    @Test
    public void locateStationSameSpot() {
        LocateStationRequest request1 = LocateStationRequest.newBuilder()
                .setLat(STATION_LAT_1).setLong(STATION_LONG_1).setNumStations(1).build();

        Scan scan1 = frontend.locateStation(request1).getScan(0);
        assertEquals(STATION_ID_1, scan1.getStationId());
        assertEquals(STATION_LAT_1, scan1.getLat());
        assertEquals(STATION_LONG_1, scan1.getLong());
        assertEquals(STATION_DOCK_CAPACITY_1, scan1.getDockCapacity());
        assertEquals(STATION_COMPENSATION_1, scan1.getPrize());
        assertEquals(STATION_AVAILABLEBIKES_1 + 1, scan1.getAvailableBikes()); //another test
        assertEquals(0, scan1.getDistance());
    }

    @Test
    public void locateStationTwoSpots() {
        LocateStationRequest request1 = LocateStationRequest.newBuilder()
                .setLat(STATION_LAT_1).setLong(STATION_LONG_1).setNumStations(2).build();
        LocateStationResponse response = frontend.locateStation(request1);

        Scan scan1 = response.getScan(0);
        assertEquals(STATION_ID_1, scan1.getStationId());
        assertEquals(STATION_LAT_1, scan1.getLat());
        assertEquals(STATION_LONG_1, scan1.getLong());
        assertEquals(STATION_DOCK_CAPACITY_1, scan1.getDockCapacity());
        assertEquals(STATION_COMPENSATION_1, scan1.getPrize());
        assertEquals(STATION_AVAILABLEBIKES_1 + 1, scan1.getAvailableBikes()); //another test
        assertEquals(0, scan1.getDistance());

        Scan scan2 = response.getScan(1);
        assertEquals(STATION_ID_2, scan2.getStationId());
        assertEquals(STATION_LAT_2, scan2.getLat());
        assertEquals(STATION_LONG_2, scan2.getLong());
        assertEquals(STATION_DOCK_CAPACITY_2, scan2.getDockCapacity());
        assertEquals(STATION_COMPENSATION_2, scan2.getPrize());
        assertEquals(STATION_AVAILABLEBIKES_2, scan2.getAvailableBikes());
        assertTrue(scan2.getDistance() > 5630);
        assertTrue(scan2.getDistance() < 5690);
    }
}

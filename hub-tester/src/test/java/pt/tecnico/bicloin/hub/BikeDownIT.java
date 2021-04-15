package pt.tecnico.bicloin.hub;

import org.junit.jupiter.api.Test;
import pt.tecnico.bicloin.hub.grpc.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BikeDownIT extends BaseIT {

    @Test
    public void bikeDownInTheSameLocation() {
        BalanceRequest balanceRequest = BalanceRequest.newBuilder().setUserName(USER_ID_1).build();
        int balanceBefore = frontend.balance(balanceRequest).getBalance();
        BikeRequest bikeRequest1 = BikeRequest.newBuilder().setUserName(USER_ID_1)
                .setLat(USER_LAT_1).setLong(USER_LONG_1).setStationId(STATION_ID_1).build();

        assertEquals("OK", frontend.bikeDown(bikeRequest1).getStatus());

        int balanceAfter = frontend.balance(balanceRequest).getBalance();
        assertEquals(balanceBefore + STATION_COMPENSATION_1, balanceAfter);
    }

    @Test
    public void bikeDownInAFarLocation() {
        BalanceRequest balanceRequest = BalanceRequest.newBuilder().setUserName(USER_ID_1).build();
        BikeRequest bikeRequest1 = BikeRequest.newBuilder().setUserName(USER_ID_1)
                .setLat(USER_LAT_1).setLong(USER_LONG_1).setStationId(STATION_ID_2).build();

        int balanceBefore = frontend.balance(balanceRequest).getBalance();
        String response = frontend.bikeDown(bikeRequest1).getStatus();
        int balanceAfter = frontend.balance(balanceRequest).getBalance();

        assertEquals("ERRO Out of Reach", response);
        assertEquals(balanceAfter, balanceBefore);
    }

    @Test
    public void bikeUpWithoutMoney() {
        BalanceRequest balanceRequest = BalanceRequest.newBuilder().setUserName(USER_ID_1).build();
        BikeRequest bikeRequest1 = BikeRequest.newBuilder().setUserName(USER_ID_1)
                .setLat(USER_LAT_1).setLong(USER_LONG_1).setStationId(STATION_ID_1).build();

        int balanceBefore = frontend.balance(balanceRequest).getBalance();
        String response = frontend.bikeDown(bikeRequest1).getStatus();
        int balanceAfter = frontend.balance(balanceRequest).getBalance();

        assertEquals("OK", response);
        assertEquals(balanceBefore + STATION_COMPENSATION_1, balanceAfter);
    }
}

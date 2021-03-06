package pt.tecnico.bicloin.hub;

import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.Test;
import pt.tecnico.bicloin.hub.grpc.BalanceRequest;
import pt.tecnico.bicloin.hub.grpc.BikeRequest;
import pt.tecnico.bicloin.hub.grpc.BikeResponse;
import pt.tecnico.bicloin.hub.grpc.TopUpRequest;

import static io.grpc.Status.INVALID_ARGUMENT;
import static io.grpc.Status.NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;

public class BalanceIT extends BaseIT {

    @Test
    public void getInicialBalance() {
        BalanceRequest request1 = BalanceRequest.newBuilder().setUserName(USER_ID_1).build();
        BalanceRequest request2 = BalanceRequest.newBuilder().setUserName(USER_ID_2).build();
        BalanceRequest request3 = BalanceRequest.newBuilder().setUserName(USER_ID_3).build();

        assertNotNull(frontend.balance(request1).getBalance());
        assertNotNull(frontend.balance(request2).getBalance());
        assertNotNull(frontend.balance(request3).getBalance());
    }

    @Test
    public void getBalanceFromUnregisteredUser() {
        BalanceRequest request1 = BalanceRequest.newBuilder().setUserName(USER_ID_NOT_REGISTED).build();

        StatusRuntimeException sre = assertThrows(
                StatusRuntimeException.class, () -> frontend.balance(request1));

        assertEquals(NOT_FOUND.getCode(), sre.getStatus().getCode());
        assertEquals("User does not exist in records",
                sre.getStatus().getDescription());
    }

    @Test
    public void getBalanceFromEmptyUser() {
        BalanceRequest request1 = BalanceRequest.newBuilder().setUserName("").build();

        StatusRuntimeException sre = assertThrows(
                StatusRuntimeException.class, () -> frontend.balance(request1));

        assertEquals(INVALID_ARGUMENT.getCode(), sre.getStatus().getCode());
        assertEquals("UserName cannot be empty!", sre.getStatus().getDescription());
    }

    @Test
    public void userAddsMoneyToAccount() {
        BalanceRequest request1 = BalanceRequest.newBuilder().setUserName(USER_ID_1).build();
        TopUpRequest topUpRequest = TopUpRequest.newBuilder().setUserName(USER_ID_1)
                .setStake(10).setPhoneNumber(USER_PHONE_1).build();

        int topUpResponse = frontend.topUp(topUpRequest).getBalance();
        int balanceAfter = frontend.balance(request1).getBalance();

        assertEquals(balanceAfter, topUpResponse);
    }

    @Test
    public void userDeliversBike() {
        BalanceRequest requestBalance = BalanceRequest.newBuilder().setUserName(USER_ID_1).build();
        int balanceBefore = frontend.balance(requestBalance).getBalance();

        TopUpRequest topUpRequest = TopUpRequest.newBuilder().setUserName(USER_ID_1)
                .setStake(10).setPhoneNumber(USER_PHONE_1).build();
        int topUpResponse = frontend.topUp(topUpRequest).getBalance();

        assertEquals(balanceBefore + 10 * 10, topUpResponse);

        BikeRequest request1 = BikeRequest.newBuilder().setUserName(USER_ID_1).
                setLat(USER_LAT_1).setLong(USER_LONG_1).setStationId(STATION_ID_1).build();
        BikeResponse response = frontend.bikeUp(request1);
        balanceBefore = frontend.balance(requestBalance).getBalance();

        request1 = BikeRequest.newBuilder().setUserName(USER_ID_1).
                setLat(USER_LAT_1).setLong(USER_LONG_1).setStationId(STATION_ID_1).build();
        frontend.bikeDown(request1);
        int balanceAfter = frontend.balance(requestBalance).getBalance();

        assertEquals(balanceBefore + STATION_COMPENSATION_1, balanceAfter);
    }
}

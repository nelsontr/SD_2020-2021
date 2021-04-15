package pt.tecnico.bicloin.hub;

import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.*;
import pt.tecnico.bicloin.hub.grpc.*;

import static io.grpc.Status.INVALID_ARGUMENT;
import static io.grpc.Status.NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BalanceIT extends BaseIT {

	@Test
	public void getInicialBalance() {
		BalanceRequest request1 = BalanceRequest.newBuilder().setUserName(USER_ID_1).build();
		BalanceRequest request2 = BalanceRequest.newBuilder().setUserName(USER_ID_2).build();
		BalanceRequest request3 = BalanceRequest.newBuilder().setUserName(USER_ID_3).build();

		int balance1 = frontend.balance(request1).getBalance();
		assertEquals(0, balance1);

		int balance2 = frontend.balance(request2).getBalance();
		assertEquals(0, balance2);

		int balance3 = frontend.balance(request3).getBalance();
		assertEquals(0, balance3);
	}

	/*	@Test
        public void getBalanceFromUnregisteredUser() {
            BalanceRequest request1 = BalanceRequest.newBuilder().setUserName(USER_ID_NOT_REGISTED).build();

            StatusRuntimeException sre = assertThrows(
                        StatusRuntimeException.class, () -> frontend.balance(request1));

            assertEquals(NOT_FOUND.getCode(), sre.getStatus().getCode());
            assertEquals("No " + request1.getUserName() + " was found!",
                    sre.getStatus().getDescription());
        }

        @Test
        public void getBalanceFromEmptyUser() {
            BalanceRequest request1 = BalanceRequest.newBuilder().setUserName(USER_ID_EMPTY).build();

            StatusRuntimeException sre = assertThrows(
                    StatusRuntimeException.class, () -> frontend.balance(request1));

            assertEquals(INVALID_ARGUMENT.getCode(), sre.getStatus().getCode());
            assertEquals("UserName cannot be empty!",sre.getStatus().getDescription());
        }
*/
        @Test
        public void userAddsMoneyToAccount() {
            BalanceRequest request1 = BalanceRequest.newBuilder().setUserName(USER_ID_1).build();
            TopUpRequest topUpRequest = TopUpRequest.newBuilder().setUserName(USER_ID_1)
					.setStake(10).setPhoneNumber(USER_PHONE_1).build();

            int topUpResponse = frontend.topUp(topUpRequest).getBalance();
            int balanceAfter = frontend.balance(request1).getBalance();

            assertEquals(balanceAfter,topUpResponse);
        }

	@Test
	public void userDeliversBike() {
		BalanceRequest requestBalance = BalanceRequest.newBuilder().setUserName(USER_ID_1).build();

		BikeRequest request1 = BikeRequest.newBuilder().setUserName(USER_ID_1).
				setLat(USER_LAT_1).setLong(USER_LONG_1).setStationId(STATION_ID_1).build();
		BikeResponse response = frontend.bikeUp(request1);
		int balanceBefore = frontend.balance(requestBalance).getBalance();

		request1 = BikeRequest.newBuilder().setUserName(USER_ID_1).
				setLat(USER_LAT_1).setLong(USER_LONG_1).setStationId(STATION_ID_1).build();
		response = frontend.bikeDown(request1);

		int balanceAfter = frontend.balance(requestBalance).getBalance();

		assertEquals(balanceBefore+COMPENSATION_1, balanceAfter);
	}

}

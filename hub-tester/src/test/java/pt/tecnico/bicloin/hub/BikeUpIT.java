package pt.tecnico.bicloin.hub;

import org.junit.jupiter.api.Test;
import pt.tecnico.bicloin.hub.grpc.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BikeUpIT extends BaseIT {

	@Test
	public void bikeUpInTheSameLocation() {
		TopUpRequest topUp1 = TopUpRequest.newBuilder().setUserName(USER_ID_1)
				.setStake(10).setPhoneNumber(USER_PHONE_1).build();
		frontend.topUp(topUp1);

		BalanceRequest balanceRequest = BalanceRequest.newBuilder().setUserName(USER_ID_1).build();
		int balanceBefore = frontend.balance(balanceRequest).getBalance();

		BikeRequest bikeRequest1 = BikeRequest.newBuilder().setUserName(USER_ID_1)
				.setLat(USER_LAT_1).setLong(USER_LONG_1).setStationId(STATION_ID_1).build();

		String response = frontend.bikeUp(bikeRequest1).getStatus();
		assertEquals("OK", response);
		int balanceAfter = frontend.balance(balanceRequest).getBalance();
		assertEquals(balanceBefore, balanceAfter + 10);
	}

	@Test
	public void bikeUpInAFarLocation() {
		BalanceRequest balanceRequest = BalanceRequest.newBuilder().setUserName(USER_ID_1).build();
		int balanceBefore = frontend.balance(balanceRequest).getBalance();
		BikeRequest bikeRequest1 = BikeRequest.newBuilder().setUserName(USER_ID_1)
				.setLat(USER_LAT_1).setLong(USER_LONG_1).setStationId(STATION_ID_2).build();
		int balanceAfter = frontend.balance(balanceRequest).getBalance();
		String response = frontend.bikeUp(bikeRequest1).getStatus();

		assertEquals("ERRO Out of Reach", response);
		assertEquals(balanceAfter, balanceBefore);
	}

	@Test
	public void bikeUpWithoutMoney() {
		BalanceRequest balanceRequest = BalanceRequest.newBuilder().setUserName(USER_ID_1).build();
		BikeRequest bikeRequest1 = BikeRequest.newBuilder().setUserName(USER_ID_1)
				.setLat(USER_LAT_1).setLong(USER_LONG_1).setStationId(STATION_ID_1).build();

		int balanceBefore = frontend.balance(balanceRequest).getBalance();
		String response = frontend.bikeUp(bikeRequest1).getStatus();
		int balanceAfter = frontend.balance(balanceRequest).getBalance();

		assertEquals("ERRO No Money Available", response);
		assertEquals(balanceAfter, balanceBefore);
	}
}

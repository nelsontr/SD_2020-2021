package pt.tecnico.bicloin.hub;

import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.*;

import pt.tecnico.bicloin.hub.grpc.*;

import static io.grpc.Status.INVALID_ARGUMENT;
import static io.grpc.Status.NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TopUpIT extends BaseIT {

    @Test
    public void addingTenBTCS() {
        int MONEY = 10; //EURO

        BalanceRequest request1 = BalanceRequest.newBuilder().setUserName(USER_ID_1).build();
        int balanceBefore = frontend.balance(request1).getBalance();

        TopUpRequest topUp1 = TopUpRequest.newBuilder().setUserName(USER_ID_1)
                .setStake(MONEY).setPhoneNumber(USER_PHONE_1).build();
        int balance = frontend.topUp(topUp1).getBalance();
        int balanceAfter = frontend.balance(request1).getBalance();

        assertEquals(balance, balanceAfter);
        assertEquals((balanceBefore + MONEY * 10), balanceAfter);
    }

    @Test
    public void addingTwentyBTCS() {
        int MONEY = 20; //EURO

        BalanceRequest request1 = BalanceRequest.newBuilder().setUserName(USER_ID_1).build();
        int balanceBefore = frontend.balance(request1).getBalance();

        TopUpRequest topUp1 = TopUpRequest.newBuilder().setUserName(USER_ID_1).
                setStake(MONEY).setPhoneNumber(USER_PHONE_1).build();
        int balance = frontend.topUp(topUp1).getBalance();
        int balanceAfter = frontend.balance(request1).getBalance();

        assertEquals(balance, balanceAfter);
        assertEquals(balanceBefore + MONEY * 10, balanceAfter);
    }

    @Test
    public void addingTenBTCSForUnregisteredUser() {
        int MONEY = 10;
        TopUpRequest topUp1 = TopUpRequest.newBuilder().setUserName(USER_ID_NOT_REGISTED).
                setStake(MONEY).setPhoneNumber(USER_PHONE_1).build();
        StatusRuntimeException sre = assertThrows(StatusRuntimeException.class, () -> frontend.topUp(topUp1));

        assertEquals(NOT_FOUND.getCode(), sre.getStatus().getCode());
        assertEquals("UserName not registered!",sre.getStatus().getDescription());
    }

    @Test
    public void addingTenBTCSForEmptyUser() {
        int MONEY = 10;
        TopUpRequest topUp1 = TopUpRequest.newBuilder().setUserName("").
                setStake(MONEY).setPhoneNumber(USER_PHONE_1).build();
        StatusRuntimeException sre = assertThrows(StatusRuntimeException.class, () -> frontend.topUp(topUp1));

        assertEquals(INVALID_ARGUMENT.getCode(), sre.getStatus().getCode());
        assertEquals("UserName cannot be empty!", sre.getStatus().getDescription());
    }

    @Test
    public void addingMoreThanTwentyBTCS() {
        int MONEY = 25;
        TopUpRequest topUp1 = TopUpRequest.newBuilder().setUserName(USER_ID_NOT_REGISTED).
                setStake(MONEY).setPhoneNumber(USER_PHONE_1).build();
        StatusRuntimeException sre = assertThrows(StatusRuntimeException.class, () -> frontend.topUp(topUp1));

        assertEquals(INVALID_ARGUMENT.getCode(), sre.getStatus().getCode());
        assertEquals("Stake has to be in range [1, 20]!", sre.getStatus().getDescription());
    }

    @Test
    public void addingTenBTCSWithWrongPhoneNumber() {
        BalanceRequest request1 = BalanceRequest.newBuilder().setUserName(USER_ID_1).build();
        TopUpRequest topUp1 = TopUpRequest.newBuilder().setUserName(USER_ID_1).setStake(10)
                .setPhoneNumber(USER_PHONE_2).build();

        StatusRuntimeException sre = assertThrows(StatusRuntimeException.class, () -> frontend.topUp(topUp1));

        assertEquals(INVALID_ARGUMENT.getCode(), sre.getStatus().getCode());
        assertEquals("UserName has a different PhoneNumber linked than the one provided!",
                sre.getStatus().getDescription());
    }

}

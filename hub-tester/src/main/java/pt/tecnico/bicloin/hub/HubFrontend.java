package pt.tecnico.bicloin.hub;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import com.google.protobuf.GeneratedMessageV3;

import pt.tecnico.bicloin.hub.grpc.*;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;

public class HubFrontend {
	private ManagedChannel channel;
	private HubGrpc.HubBlockingStub stub;

	private static final int BEST_EFFORT = 3;

	public HubFrontend(String host, String port) {
		try {
				final String target = host + ":" + port;
				this.channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
				this.stub = HubGrpc.newBlockingStub(this.channel);
		} catch (StatusRuntimeException sre) {
				System.out.println("ERROR : Frontend createNewChannel : Could not create channel\n"
						+ sre.getStatus().getDescription());
		}
	}

	public CtrlPingResponse ping(CtrlPingRequest request) {
			int tries = 0;

			while (true) {
				try {
					System.out.println("Ping " + (tries + 1) + "...");
					return stub.ping(request);
				} catch (StatusRuntimeException sre) {
					if (sre.getStatus().getCode() == Status.Code.INVALID_ARGUMENT || ++tries == BEST_EFFORT) {
							System.out.println("WARN : Cant connect to server!");
							throw sre;
					}
				}
			}
		}

	public BalanceResponse balance(BalanceRequest request) {
		return stub.balance(request);
	}	

	public TopUpResponse topUp(TopUpRequest request) {
		return stub.topUp(request);
	}

	public InfoStationResponse infoStation(InfoStationRequest request) {
		//Uncertain about exceptions
		return stub.infoStation(request);
	}

	public LocateStationResponse locateStation(LocateStationRequest request) {
		//Still Uncertain about exceptions
		return stub.locateStation(request);
	}



		public void closeChannel(){
			this.channel.shutdownNow();
		}


}

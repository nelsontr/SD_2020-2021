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

	public void errorHandling(StatusRuntimeException sre, String function, int tries) {
		if (tries==BEST_EFFORT) {
			System.out.println("WARN <"+ function + " " + tries +"> : Max tries reached!");
			throw sre;
		}

		if (sre.getStatus().getCode() == Status.Code.UNAVAILABLE) {
			System.out.println("WARN <"+ function + " " + tries +"> : Cant connect to server!");
		} else{
			System.out.println(sre);
			throw sre;
		}
	}

	public CtrlPingResponse ping(CtrlPingRequest request) {
		int tries = 0;

		while (true) {
			try {
				return stub.ping(request);
			} catch (StatusRuntimeException sre) {
				errorHandling(sre, "ping", ++tries);
			}
		}
	}

	public BalanceResponse balance(BalanceRequest request) {
		int tries = 0;

		while (true) {
			try {
				return stub.balance(request);
			} catch (StatusRuntimeException sre) {
				errorHandling(sre, "balance", ++tries);
			}
		}
	}

	public TopUpResponse topUp(TopUpRequest request) {
		int tries = 0;

		while (true) {
			try {
				return stub.topUp(request);
			} catch (StatusRuntimeException sre) {
				errorHandling(sre, "topUp", ++tries);
			}
		}
	}

	public InfoStationResponse infoStation(InfoStationRequest request) {
		int tries = 0;

		while (true) {
			try {
				return stub.infoStation(request);
			} catch (StatusRuntimeException sre) {
				errorHandling(sre, "infoStation", ++tries);
			}
		}
	}

	public LocateStationResponse locateStation(LocateStationRequest request) {
		int tries = 0;

		while (true) {
			try {
				return stub.locateStation(request);
			} catch (StatusRuntimeException sre) {
				errorHandling(sre, "locateStation", ++tries);
			}
		}
	}

	public BikeResponse bikeUp(BikeRequest request) {
		int tries = 0;

		while (true) {
			try {
				return stub.bikeUp(request);
			} catch (StatusRuntimeException sre) {
				errorHandling(sre, "bikeUp", ++tries);
			}
		}
	}

	public BikeResponse bikeDown(BikeRequest request) {
		int tries = 0;

		while (true) {
			try {
				return stub.bikeDown(request);
			} catch (StatusRuntimeException sre) {
				errorHandling(sre, "bikeDown", ++tries);
			}
		}
	}

	public SysStatusResponse sysStatus(SysStatusRequest request) {
		int tries = 0;

		while (true) {
			try {
				return stub.sysStatus(request);
			} catch (StatusRuntimeException sre) {
				errorHandling(sre, "sysStatus", ++tries);
			}
		}
	}

	public CtrlInitResponse ctrlInit(CtrlInitRequest request){
		int tries = 0;

		while (true) {
			try {
				return stub.ctrlInit(request);
			} catch (StatusRuntimeException sre) {
				errorHandling(sre, "sysStatus", ++tries);
			}
		}
	}
	public CtrlClearResponse ctrlClear(CtrlClearRequest request){
		int tries = 0;

		while (true) {
			try {
				return stub.ctrlClear(request);
			} catch (StatusRuntimeException sre) {
				errorHandling(sre, "sysStatus", ++tries);
			}
		}
	}

	public void closeChannel(){
		this.channel.shutdownNow();
	}


}

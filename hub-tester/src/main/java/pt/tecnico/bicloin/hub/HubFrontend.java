package pt.tecnico.bicloin.hub;

import pt.tecnico.bicloin.hub.grpc.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.grpc.Status;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import pt.ulisboa.tecnico.sdis.zk.ZKNaming;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;
import pt.ulisboa.tecnico.sdis.zk.ZKRecord;

public class HubFrontend {

	private ManagedChannel channel;
	private HubGrpc.HubBlockingStub stub;
	private static final int BEST_EFFORT = 3;

	private final ZKNaming zkNaming;
	private final String path = "/grpc/bicloin/hub";

	public HubFrontend(String zooHost, String zooPort, String instance) {
			this.zkNaming = new ZKNaming(zooHost, zooPort);
			try {
					ZKRecord record = this.zkNaming.lookup(this.path + "/" + instance);
					createNewChannel(record.getURI());
					createHubFrontend();
			} catch (ZKNamingException zkne) {
					throw new IllegalArgumentException("Instance " + instance + " does not exist!");
			}
	}

	public HubFrontend(String zooHost, String zooPort) {
			this.zkNaming = new ZKNaming(zooHost, zooPort);

			try {
					createNewChannel(findHubInstance());
					createHubFrontend();
			} catch (IllegalArgumentException | ZKNamingException zkne) {
					throw new IllegalArgumentException("No servers available!");
			}
	}


	private String findHubInstance() throws ZKNamingException {

			List<ZKRecord> records = new ArrayList<>(this.zkNaming.listRecords(this.path));

			int rnd = new Random().nextInt(records.size());
			System.out.println("Found target: " + records.get(rnd).getURI());
			return records.get(rnd).getURI();
	}


	private void createNewChannel(String target) {
			try {
					this.channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
					this.stub = HubGrpc.newBlockingStub(this.channel);
			} catch (StatusRuntimeException sre) {
					System.out.println("ERROR : Frontend createNewChannel : Could not create channel\n"
									+ sre.getStatus().getDescription());
			}
	}

	private void createHubFrontend() {
			//Nothing for now
	}

	public void errorHandling(StatusRuntimeException sre, String function, int tries) {
		if (tries == BEST_EFFORT) {
			System.out.println("WARN <" + function + " " + tries + "> : Max tries reached!");
			throw sre;
		}

		if (sre.getStatus().getCode() == Status.Code.UNAVAILABLE) {
			System.out.println("WARN <" + function + " " + tries + "> : Cant connect to server!");
		} else {
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

	String sysStatusIndividual(ZKRecord record, SysStatusRequest request){
		int tries = 0;

		while(true){
			try {
				createNewChannel(record.getURI());
				return "\n"+record.getPath()+": UP";
			} catch (StatusRuntimeException sre) {
					if (++tries == BEST_EFFORT) return "\n"+record.getPath()+": DOWN";
					else errorHandling(sre, "sysStatusInd", tries);
			}
		}
	}


	public SysStatusResponse sysStatus(SysStatusRequest request) {
			String responseString = "";
			HubGrpc.HubBlockingStub oldStub = this.stub;

			while (true) {
					try {
						List<ZKRecord> records = new ArrayList<>(this.zkNaming.listRecords(this.path));

						for(ZKRecord record: records){
							responseString += sysStatusIndividual(record, request);
						}

						this.stub = oldStub;
						return SysStatusResponse.newBuilder().setHubStatus(responseString).setRecStatus(
						stub.sysStatus(request).getRecStatus()).build();
					} catch (ZKNamingException sre) {
							System.out.println("WARN <sysStatus> : Cant connect to server!");
					}
			}
	}

	public CtrlInitResponse ctrlInit(CtrlInitRequest request) {
		int tries = 0;

		while (true) {
			try {
				return stub.ctrlInit(request);
			} catch (StatusRuntimeException sre) {
				errorHandling(sre, "ctrInit", ++tries);
			}
		}
	}

	public CtrlClearResponse ctrlClear(CtrlClearRequest request) {
		int tries = 0;

		while (true) {
			try {
				return stub.ctrlClear(request);
			} catch (StatusRuntimeException sre) {
				errorHandling(sre, "ctrlClear", ++tries);
			}
		}
	}

	public void closeChannel() {
		this.channel.shutdownNow();
	}
}

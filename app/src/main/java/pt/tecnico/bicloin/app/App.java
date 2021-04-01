package pt.tecnico.bicloin.app;

import com.google.protobuf.GeneratedMessageV3;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;



public class App {
  /*
  private final ZKNaming zkNaming;

  private final String hubPath = "/grpc/hub";
  private final String recPath = "/grpc/rec";

  private ManagedChannel channel;
  private SiloGrpc.SiloBlockingStub stub;

  public App(String zooHost, String zooPort, String Id, int phoneNumber, float latitude, float longitude) {
    this.zkNaming = new ZKNaming(zooHost, zooPort);

    try {
            ZKRecord record = this.zkNaming.lookup(this.path + "/" + instance);
            createNewChannel(record.getURI());
            //create the actual frontend
        } catch (ZKNamingException zkne) {
            throw new IllegalArgumentException("Instance " + instance + " does not exist!");
        }
  }

  private String findHubInstance() throws ZKNamingException {

      List<ZKRecord> records = new ArrayList<>(this.zkNaming.listRecords(this.hubPath));

      int rnd = new Random().nextInt(records.size());
      System.out.println("Found target: " + records.get(rnd).getURI());
      return records.get(rnd).getURI();
  }

  private String findRecInstance() throws ZKNamingException {

      List<ZKRecord> records = new ArrayList<>(this.zkNaming.listRecords(this.recPath));

      int rnd = new Random().nextInt(records.size());
      System.out.println("Found target: " + records.get(rnd).getURI());
      return records.get(rnd).getURI();
  }

  private void createNewChannel(String target) {
    try {
        this.channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
        this.stub = BicloinGrpc.newBlockingStub(this.channel);
    } catch (StatusRuntimeException sre) {
        System.out.println("ERROR : Frontend createNewChannel : Could not create channel\n"
                + sre.getStatus().getDescription());
    }
  }

  /* * * * CTRL_PING services * * * */

/*
  public String ctrl_ping(String msg) {
      PingResponse response = ctrl_ping(PingRequest.newBuilder()
              .setText(msg)
              .build());

      return response.getOutput();
  }

  public PingResponse ctrl_ping(PingRequest request) {
      int tries = 0;

      while (true) {
          try {
              System.out.println("Ping " + (tries + 1) + "...");
              return stub.ctrlPing(request);
          } catch (StatusRuntimeException sre) {
              if (sre.getStatus().getCode() == Status.Code.INVALID_ARGUMENT || ++tries == BEST_EFFORT) {
                  System.out.println("WARN : Cant connect to server!");
                  throw sre;
              }
          }
      }
  }

  private boolean tryPinging() {
      try {
          ctrl_ping("PING");
          return true;
      } catch (StatusRuntimeException sre) {
          try {
              System.out.println("Fetching new connection...");
              createNewChannel(findHubInstance());
              createNewChannel(findRecInstance());
              return false;
          } catch (ZKNamingException e) {
              throw new StatusRuntimeException(Status.Code
                      .ABORTED.toStatus()
                      .augmentDescription("FATAL : No servers were found!"));
          }
      }
  }


*/

}

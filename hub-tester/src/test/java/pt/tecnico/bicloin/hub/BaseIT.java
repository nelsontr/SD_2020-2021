package pt.tecnico.bicloin.hub;

import pt.tecnico.bicloin.hub.grpc.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;


public class BaseIT {

    private static String initialData = "";
    private static final String USER_DATA_FILE = "src/test/resources/users.csv";
    private static final String STATION_DATA_FILE = "src/test/resources/stations.csv";

    static HubFrontend frontend;
    protected static Properties testProps;
    private static final String TEST_PROP_FILE = "/test.properties";

    //STRING VARIABLES FOR TEST
    public static final String USER_ID_1 = "joao";
    public static final Double USER_LAT_1 = 38.7372;
    public static final Double USER_LONG_1 = -9.3023;
    public static final String USER_PHONE_1 = "+35191102030";
    public static final String USER_ID_2 = "maria";
    public static final String USER_PHONE_2 = "+35191102031";
    public static final String USER_ID_3 = "nelson";
    public static final String USER_ID_NOT_REGISTED = "bela";

    public static final String STATION_ID_1 = "istt";
    public static final String STATION_NAME_1 = "IST Taguspark";
    final static double STATION_LAT_1 = 38.7372;
    final static double STATION_LONG_1 = -9.3023;
    final static int STATION_DOCK_CAPACITY_1 = 20;
    final static int STATION_AVAILABLEBIKES_1 = 12;
    public static final int STATION_COMPENSATION_1 = 4;

    public static final String STATION_ID_2 = "stao";

    public static final String STATION_NAME_2 = "Sto. Amaro Oeiras";
    final static double STATION_LAT_2 = 38.6867;
    final static double STATION_LONG_2 = -9.3124;
    final static int STATION_DOCK_CAPACITY_2 = 30;
    final static int STATION_AVAILABLEBIKES_2 = 20;
    public static final int STATION_COMPENSATION_2 = 3;

    public static final String STATION_ID_0 = "sdis";
    public static final String STATION_ID_3 = "ista";
    public static final String STATION_NAME_3 = "Oceanário";

    @BeforeEach
    public void setUp() {
        /* EMPTY */
    }

    @BeforeAll
    public static void oneTimeSetup() throws IOException {
        testProps = new Properties();

        try {
            testProps.load(BaseIT.class.getResourceAsStream(TEST_PROP_FILE));
            System.out.println("Test properties:");
            System.out.println(testProps);

            final String zoohost = testProps.getProperty("zoo.host");
            final String zooport = testProps.getProperty("zoo.port");
            frontend = new HubFrontend(zoohost, zooport, "1");

        } catch (IOException e) {
            final String msg = String.format("Could not load properties file {}", TEST_PROP_FILE);
            System.out.println(msg);
            throw e;
        }

        //  Users
        try (Scanner fileScanner = new Scanner(new File(USER_DATA_FILE))) {
            while (fileScanner.hasNextLine()) {
                initialData = initialData.concat(fileScanner.nextLine() + "\n");
            }
        } catch (FileNotFoundException fife) {
            System.out.println(String.format("Could not find file '%s'", USER_DATA_FILE));
            throw fife;
        }

        //  Stations
        try (Scanner fileScanner = new Scanner(new File(STATION_DATA_FILE))) {
            while (fileScanner.hasNextLine()) {
                initialData = initialData.concat(fileScanner.nextLine() + "\n");
            }
        } catch (FileNotFoundException fife) {
            System.out.println(String.format("Could not find file '%s'", STATION_DATA_FILE));
            throw fife;
        }

        CtrlInitRequest request = CtrlInitRequest.newBuilder().setInput(initialData).setRecInitOption(true).build();
        frontend.ctrlInit(request);
    }

    @AfterEach
    public void tearDown() {
        /* EMPTY */
    }

    @AfterAll
    public static void cleanup() {
        frontend.closeChannel();
    }
}

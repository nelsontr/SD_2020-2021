package pt.tecnico.bicloin.hub;

import java.io.IOException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import pt.tecnico.bicloin.hub.grpc.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.junit.jupiter.api.*;


public class BaseIT {

    private static final String USER_DATA_FILE = "src/test/resources/users.csv";
    private static final String STATION_DATA_FILE = "src/test/resources/stations.csv";
    private static String initialData = "";

    //STRING VARIABLES FOR TEST
    public static final String USER_ID_1 = "joao";
    public static final String USER_ID_2 = "maria";
    public static final String USER_ID_3 = "nelson";
    public static final String USER_ID_NOT_REGISTED = "bela";
    public static final String USER_ID_EMPTY = "";

    public static final String STATION_ID_1 = "istt";
    public static final String STATION_ID_2 = "ista";
    public static final String STATION_ID_3 = "ocea";
    public static final String STATION_NAME_1 = "IST-TagusPark";
    public static final String STATION_NAME_2 = "IST-Alameda";
    public static final String STATION_NAME_3 = "Oceanário";

    public static final String USER_PHONE_1 = "+35191102030";
    public static final String USER_PHONE_2 = "+35191102031";
    public static final Double USER_LAT_1 = 38.7372;
    public static final Double USER_LONG_1 = -9.3023;
    public static final int COMPENSATION_1 = 4;


    private static final String TEST_PROP_FILE = "/test.properties";
    protected static Properties testProps;
    static HubFrontend frontend;

    @BeforeEach
    public void setUp() {
        CtrlInitRequest request = CtrlInitRequest.newBuilder().setInput(initialData).setRecInitOption(true).build();

        try {
            frontend.ctrlInit(request);
        } catch (Exception e) {
            System.out.println(String.format("Exception<ctrl_init>: %s", e.getMessage()));
            throw e;
        }
    }

    @BeforeAll
    public static void oneTimeSetup() throws IOException {
        testProps = new Properties();

        try {
            testProps.load(BaseIT.class.getResourceAsStream(TEST_PROP_FILE));
            System.out.println("Test properties:");
            System.out.println(testProps);

            final String host = testProps.getProperty("server.host");
            final String port = testProps.getProperty("server.port");
            frontend = new HubFrontend(host, port);

        } catch (IOException e) {
            final String msg = String.format("Could not load properties file {}", TEST_PROP_FILE);
            System.out.println(msg);
            throw e;
        }

        //users
        try (Scanner fileScanner = new Scanner(new File(USER_DATA_FILE))) {
            while (fileScanner.hasNextLine()) {
                initialData = initialData.concat(fileScanner.nextLine() + "\n");
            }
        } catch (FileNotFoundException fife) {
            System.out.println(String.format("Could not find file '%s'", USER_DATA_FILE));
            throw fife;
        }

        //stations
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
        CtrlClearRequest request = CtrlClearRequest.newBuilder().build();
        frontend.ctrlClear(request);
    }

    @AfterAll
    public static void cleanup() {
        frontend.closeChannel();
    }

}

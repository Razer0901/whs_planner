package WHS_planner;


//import WHS_planner.Core.MeetingFileHandler;

import WHS_planner.Calendar.CalendarBox;
import WHS_planner.UI.MainPane;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Random;

public class Main extends Application {

    public static final String SAVE_FOLDER = System.getenv("HOME") + File.separator + "Library" + File.separator + "Application Support" + File.separator + "WHS Planner";

    public static final String VERSION_NUMBER = "1.5.1";

    public static final String UPDATE_NOTES =
            "========== CHANGES ==========\n" +
            " - eSchoolPlus integration\n"+
            " - Allows printing of the eSchoolPlus schedule in GRID FORM!\n" +
            "       Find it in the options menu!";

    public static boolean isFirstStartup = false;
    public static boolean isFirstTimeOnVersion = false;

    //ON first run move jfoenix to a place it can be referenced on a remote system
    private static String readKey = null;
    private static MainPane mainPane;

    public static String getXorKey() {
        if (readKey != null) {
            return readKey;
        }
        return null;
    }

    /**
     * The main method of the program.
     * It initializes and runs the application!
     */

    public static void main(String[] args) {
        try {

            File saveFolder = new File(SAVE_FOLDER);
            if (!saveFolder.exists()) {
                saveFolder.mkdir();
            }

            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                public void run() {
                    mainPane.saveCalendar();
                    File file = new File(Main.SAVE_FOLDER + File.separator + "Keys/ipass.key");
                    if (!file.exists()) {
                        file.delete();
                    }

                }
            }, "Shutdown-thread"));

            PrintStream console = System.err;

            File file = new File(Main.SAVE_FOLDER + File.separator + "err.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            PrintStream ps = new PrintStream(fos);
            System.setErr(ps);

            System.setProperty("http.agent", "Chrome");
//        PropertyConfigurator.configure("log4j.properties");


            File keys = new File(Main.SAVE_FOLDER + File.separator + "Keys");
            if (!keys.exists()) {
                keys.mkdir();
            }

            File CalendarFolder = new File(Main.SAVE_FOLDER + File.separator + "Calendar");
            if (!CalendarFolder.exists()) {
                CalendarFolder.mkdir();
            }

            File runVersionFolder = new File(Main.SAVE_FOLDER + File.separator + "version");
            if (!runVersionFolder.exists()) {
                runVersionFolder.mkdir();
            }

            File versionFile = new File(Main.SAVE_FOLDER + File.separator + "version" + File.separator + VERSION_NUMBER);
            if (!versionFile.exists()) {
                isFirstTimeOnVersion = true;
                versionFile.createNewFile();
            }

            boolean needToPasteDayArray = false;
            //Stupid way to do Day Array
            File dayArrayFile = new File(Main.SAVE_FOLDER + File.separator + "DayArray.json");
            if(dayArrayFile.exists()){
                BufferedReader br = new BufferedReader(new FileReader(dayArrayFile));
//                System.out.println(!br.readLine().contains("flag"));
                boolean flagCheck = !br.readLine().contains("flag");
                if (br.readLine() == null || flagCheck) {
//                    System.out.println("in");
                    needToPasteDayArray = true;
                    dayArrayFile.delete();
                }
            }else{
                needToPasteDayArray = true;
            }
            if (needToPasteDayArray) {
                isFirstStartup = true;
                Files.write(dayArrayFile.toPath(), "{\"flag\":[], \"@dates\":[\"dates0: 9/5\",\"dates1: 9/6\",\"dates2: 9/7\",\"dates3: 9/8\",\"dates4: 9/11\",\"dates5: 9/12\",\"dates6: 9/13\",\"dates7: 9/14\",\"dates8: 9/15\",\"dates9: 9/18\",\"dates10: 9/19\",\"dates11: 9/20\",\"dates12: 9/22\",\"dates13: 9/25\",\"dates14: 9/26\",\"dates15: 9/27\",\"dates16: 9/28\",\"dates17: 9/29\",\"dates18: 10/2\",\"dates19: 10/3\",\"dates20: 10/4\",\"dates21: 10/5\",\"dates22: 10/6\",\"dates23: 10/10\",\"dates24: 10/11\",\"dates25: 10/12\",\"dates26: 10/13\",\"dates27: 10/16\",\"dates28: 10/17\",\"dates29: 10/18\",\"dates30: 10/19\",\"dates31: 10/20\",\"dates32: 10/23\",\"dates33: 10/24\",\"dates34: 10/25\",\"dates35: 10/26\",\"dates36: 10/27\",\"dates37: 10/30\",\"dates38: 10/31\",\"dates39: 11/1\",\"dates40: 11/2\",\"dates41: 11/3\",\"dates42: 11/6\",\"dates43: 11/7\",\"dates44: 11/8\",\"dates45: 11/9\",\"dates46: 11/13\",\"dates47: 11/14\",\"dates48: 11/15\",\"dates49: 11/16\",\"dates50: 11/17\",\"dates51: 11/20\",\"dates52: 11/21\",\"dates53: 11/27\",\"dates54: 11/28\",\"dates55: 11/29\",\"dates56: 11/30\",\"dates57: 12/1\",\"dates58: 12/4\",\"dates59: 12/5\",\"dates60: 12/6\",\"dates61: 12/7\",\"dates62: 12/8\",\"dates63: 12/11\",\"dates64: 12/12\",\"dates65: 12/13\",\"dates66: 12/14\",\"dates67: 12/15\",\"dates68: 12/18\",\"dates69: 12/19\",\"dates70: 12/20\",\"dates71: 12/21\",\"dates72: 12/22\",\"dates73: 1/2\",\"dates74: 1/3\",\"dates75: 1/4\",\"dates76: 1/5\",\"dates77: 1/8\",\"dates78: 1/9\",\"dates79: 1/10\",\"dates80: 1/11\",\"dates81: 1/12\",\"dates82: 1/16\",\"dates83: 1/17\",\"dates84: 1/18\",\"dates85: 1/19\",\"dates86: 1/22\",\"dates87: 1/29\",\"dates88: 1/30\",\"dates89: 1/31\",\"dates90: 2/1\",\"dates91: 2/2\",\"dates92: 2/5\",\"dates93: 2/6\",\"dates94: 2/7\",\"dates95: 2/8\",\"dates96: 2/9\",\"dates97: 2/12\",\"dates98: 2/13\",\"dates99: 2/14\",\"dates100: 2/15\",\"dates101: 2/16\",\"dates102: 2/26\",\"dates103: 2/27\",\"dates104: 2/28\",\"dates105: 3/1\",\"dates106: 3/2\",\"dates107: 3/5\",\"dates108: 3/6\",\"dates109: 3/7\",\"dates110: 3/8\",\"dates111: 3/9\",\"dates112: 3/12\",\"dates113: 3/13\",\"dates114: 3/14\",\"dates115: 3/15\",\"dates116: 3/16\",\"dates117: 3/19\",\"dates118: 3/20\",\"dates119: 3/21\",\"dates120: 3/22\",\"dates121: 3/23\",\"dates122: 3/26\",\"dates123: 3/27\",\"dates124: 3/28\",\"dates125: 3/29\",\"dates126: 4/2\",\"dates127: 4/3\",\"dates128: 4/4\",\"dates129: 4/5\",\"dates130: 4/6\",\"dates131: 4/9\",\"dates132: 4/10\",\"dates133: 4/11\",\"dates134: 4/12\",\"dates135: 4/13\",\"dates136: 4/23\",\"dates137: 4/24\",\"dates138: 4/25\",\"dates139: 4/26\",\"dates140: 4/27\",\"dates141: 4/30\",\"dates142: 5/1\",\"dates143: 5/2\",\"dates144: 5/3\",\"dates145: 5/4\",\"dates146: 5/7\",\"dates147: 5/8\",\"dates148: 5/9\",\"dates149: 5/10\",\"dates150: 5/11\",\"dates151: 5/14\",\"dates152: 5/15\",\"dates153: 5/16\",\"dates154: 5/17\",\"dates155: 5/18\",\"dates156: 5/21\",\"dates157: 5/22\",\"dates158: 5/23\",\"dates159: 5/24\",\"dates160: 5/25\",\"dates161: 5/29\",\"dates162: 5/30\",\"dates163: 5/31\",\"dates164: 6/1\",\"dates165: 6/4\",\"dates166: 6/5\",\"dates167: 6/6\",\"dates168: 6/7\",\"dates169: 6/8\",\"dates170: 6/11\",\"dates171: 6/12\",\"dates172: 6/13\",\"dates173: 6/14\",\"dates174: 6/15\"],\"@days\":[\"days0: 0\",\"days1: 1\",\"days2: 2\",\"days3: 3\",\"days4: 4\",\"days5: 5\",\"days6: 6\",\"days7: 7\",\"days8: 8\",\"days9: 9\",\"days10: 10\",\"days11: 11\",\"days12: 12\",\"days13: 13\",\"days14: 14\",\"days15: 15\",\"days16: 16\",\"days17: 17\",\"days18: 18\",\"days19: 19\",\"days20: 20\",\"days21: 21\",\"days22: 22\",\"days23: 23\",\"days24: 24\",\"days25: 25\",\"days26: 26\",\"days27: 27\",\"days28: 28\",\"days29: 29\",\"days30: 30\",\"days31: 31\",\"days32: 32\",\"days33: 33\",\"days34: 34\",\"days35: 35\",\"days36: 36\",\"days37: 37\",\"days38: 38\",\"days39: 39\",\"days40: 40\",\"days41: 41\",\"days42: 42\",\"days43: 43\",\"days44: 44\",\"days45: 45\",\"days46: 46\",\"days47: 47\",\"days48: 48\",\"days49: 49\",\"days50: 50\",\"days51: 51\",\"days52: 52\",\"days53: 53\",\"days54: 54\",\"days55: 55\",\"days56: 56\",\"days57: 57\",\"days58: 58\",\"days59: 59\",\"days60: 60\",\"days61: 61\",\"days62: 62\",\"days63: 63\",\"days64: 64\",\"days65: 65\",\"days66: 66\",\"days67: 67\",\"days68: 68\",\"days69: 69\",\"days70: 70\",\"days71: 71\",\"days72: 72\",\"days73: 73\",\"days74: 74\",\"days75: 75\",\"days76: 76\",\"days77: 77\",\"days78: 78\",\"days79: 79\",\"days80: 80\",\"days81: 81\",\"days82: 82\",\"days83: 83\",\"days84: 84\",\"days85: 85\",\"days86: 86\",\"days87: 87\",\"days88: 88\",\"days89: 89\",\"days90: 90\",\"days91: 91\",\"days92: 92\",\"days93: 93\",\"days94: 94\",\"days95: 95\",\"days96: 96\",\"days97: 97\",\"days98: 98\",\"days99: 99\",\"days100: 100\",\"days101: 101\",\"days102: 102\",\"days103: 103\",\"days104: 104\",\"days105: 105\",\"days106: 106\",\"days107: 107\",\"days108: 108\",\"days109: 109\",\"days110: 110\",\"days111: 111\",\"days112: 112\",\"days113: 113\",\"days114: 114\",\"days115: 115\",\"days116: 116\",\"days117: 117\",\"days118: 118\",\"days119: 119\",\"days120: 120\",\"days121: 121\",\"days122: 122\",\"days123: 123\",\"days124: 124\",\"days125: 125\",\"days126: 126\",\"days127: 127\",\"days128: 128\",\"days129: 129\",\"days130: 130\",\"days131: 131\",\"days132: 132\",\"days133: 133\",\"days134: 134\",\"days135: 135\",\"days136: 136\",\"days137: 137\",\"days138: 138\",\"days139: 139\",\"days140: 140\",\"days141: 141\",\"days142: 142\",\"days143: 143\",\"days144: 144\",\"days145: 145\",\"days146: 146\",\"days147: 147\",\"days148: 148\",\"days149: 149\",\"days150: 150\",\"days151: 151\",\"days152: 152\",\"days153: 153\",\"days154: 154\",\"days155: 155\",\"days156: 156\",\"days157: 157\",\"days158: 158\",\"days159: 159\",\"days160: 160\",\"days161: 161\",\"days162: 162\",\"days163: 163\",\"days164: 164\",\"days165: 165\",\"days166: 166\",\"days167: 167\",\"days168: 168\",\"days169: 169\",\"days170: 170\",\"days171: 171\",\"days172: 172\",\"days173: 173\",\"days174: 174\"],\"@calendarData\":[\"calendarData0: A\",\"calendarData1: B\",\"calendarData2: C\",\"calendarData3: D\",\"calendarData4: E\",\"calendarData5: F\",\"calendarData6: G\",\"calendarData7: H\",\"calendarData8: A\",\"calendarData9: B\",\"calendarData10: C\",\"calendarData11: D\",\"calendarData12: E\",\"calendarData13: F\",\"calendarData14: G\",\"calendarData15: H\",\"calendarData16: A\",\"calendarData17: B\",\"calendarData18: C\",\"calendarData19: D\",\"calendarData20: E\",\"calendarData21: F\",\"calendarData22: G\",\"calendarData23: H\",\"calendarData24: A\",\"calendarData25: B\",\"calendarData26: C\",\"calendarData27: D\",\"calendarData28: E\",\"calendarData29: F\",\"calendarData30: G\",\"calendarData31: H\",\"calendarData32: A\",\"calendarData33: B\",\"calendarData34: C\",\"calendarData35: D\",\"calendarData36: E\",\"calendarData37: F\",\"calendarData38: G\",\"calendarData39: H\",\"calendarData40: A\",\"calendarData41: B\",\"calendarData42: C\",\"calendarData43: D\",\"calendarData44: E\",\"calendarData45: F\",\"calendarData46: G\",\"calendarData47: H\",\"calendarData48: A\",\"calendarData49: B\",\"calendarData50: C\",\"calendarData51: D\",\"calendarData52: E\",\"calendarData53: F\",\"calendarData54: G\",\"calendarData55: H\",\"calendarData56: A\",\"calendarData57: B\",\"calendarData58: C\",\"calendarData59: D\",\"calendarData60: E\",\"calendarData61: F\",\"calendarData62: G\",\"calendarData63: H\",\"calendarData64: A\",\"calendarData65: B\",\"calendarData66: C\",\"calendarData67: D\",\"calendarData68: E\",\"calendarData69: F\",\"calendarData70: G\",\"calendarData71: H\",\"calendarData72: A\",\"calendarData73: B\",\"calendarData74: C\",\"calendarData75: D\",\"calendarData76: E\",\"calendarData77: F\",\"calendarData78: G\",\"calendarData79: H\",\"calendarData80: A\",\"calendarData81: B\",\"calendarData82: C\",\"calendarData83: D\",\"calendarData84: E\",\"calendarData85: F\",\"calendarData86: G\",\"calendarData87: H\",\"calendarData88: A\",\"calendarData89: B\",\"calendarData90: C\",\"calendarData91: D\",\"calendarData92: E\",\"calendarData93: F\",\"calendarData94: G\",\"calendarData95: H\",\"calendarData96: A\",\"calendarData97: B\",\"calendarData98: C\",\"calendarData99: D\",\"calendarData100: E\",\"calendarData101: F\",\"calendarData102: G\",\"calendarData103: H\",\"calendarData104: A\",\"calendarData105: B\",\"calendarData106: C\",\"calendarData107: D\",\"calendarData108: E\",\"calendarData109: F\",\"calendarData110: G\",\"calendarData111: H\",\"calendarData112: A\",\"calendarData113: B\",\"calendarData114: C\",\"calendarData115: D\",\"calendarData116: E\",\"calendarData117: F\",\"calendarData118: G\",\"calendarData119: H\",\"calendarData120: A\",\"calendarData121: B\",\"calendarData122: C\",\"calendarData123: D\",\"calendarData124: E\",\"calendarData125: F\",\"calendarData126: G\",\"calendarData127: H\",\"calendarData128: A\",\"calendarData129: B\",\"calendarData130: C\",\"calendarData131: D\",\"calendarData132: E\",\"calendarData133: F\",\"calendarData134: G\",\"calendarData135: H\",\"calendarData136: A\",\"calendarData137: B\",\"calendarData138: C\",\"calendarData139: D\",\"calendarData140: E\",\"calendarData141: F\",\"calendarData142: G\",\"calendarData143: H\",\"calendarData144: A\",\"calendarData145: B\",\"calendarData146: C\",\"calendarData147: D\",\"calendarData148: E\",\"calendarData149: F\",\"calendarData150: G\",\"calendarData151: H\",\"calendarData152: A\",\"calendarData153: B\",\"calendarData154: C\",\"calendarData155: D\",\"calendarData156: E\",\"calendarData157: F\",\"calendarData158: G\",\"calendarData159: H\",\"calendarData160: A\",\"calendarData161: B\",\"calendarData162: C\",\"calendarData163: D\",\"calendarData164: E\",\"calendarData165: F\",\"calendarData166: G\",\"calendarData167: H\",\"calendarData168: A\",\"calendarData169: B\",\"calendarData170: C\",\"calendarData171: D\",\"calendarData172: E\",\"calendarData173: F\",\"calendarData174: A\",\"calendarData175: There is No School Today\",\"calendarData176: There is No School Today\",\"calendarData177: There is No School Today\",\"calendarData178: There is No School Today\",\"calendarData179: There is No School Today\",\"calendarData180: There is No School Today\",\"calendarData181: There is No School Today\",\"calendarData182: There is No School Today\",\"calendarData183: There is No School Today\",\"calendarData184: There is No School Today\",\"calendarData185: There is No School Today\",\"calendarData186: There is No School Today\",\"calendarData187: There is No School Today\",\"calendarData188: There is No School Today\",\"calendarData189: There is No School Today\",\"calendarData190: There is No School Today\",\"calendarData191: There is No School Today\",\"calendarData192: There is No School Today\",\"calendarData193: There is No School Today\",\"calendarData194: There is No School Today\",\"calendarData195: There is No School Today\",\"calendarData196: There is No School Today\",\"calendarData197: There is No School Today\",\"calendarData198: There is No School Today\",\"calendarData199: There is No School Today\",\"calendarData200: There is No School Today\",\"calendarData201: There is No School Today\",\"calendarData202: There is No School Today\",\"calendarData203: There is No School Today\",\"calendarData204: There is No School Today\",\"calendarData205: There is No School Today\",\"calendarData206: There is No School Today\",\"calendarData207: There is No School Today\",\"calendarData208: There is No School Today\",\"calendarData209: There is No School Today\",\"calendarData210: There is No School Today\",\"calendarData211: There is No School Today\",\"calendarData212: There is No School Today\",\"calendarData213: There is No School Today\",\"calendarData214: There is No School Today\",\"calendarData215: There is No School Today\",\"calendarData216: There is No School Today\",\"calendarData217: There is No School Today\",\"calendarData218: There is No School Today\",\"calendarData219: There is No School Today\",\"calendarData220: There is No School Today\",\"calendarData221: There is No School Today\",\"calendarData222: There is No School Today\",\"calendarData223: There is No School Today\",\"calendarData224: There is No School Today\",\"calendarData225: There is No School Today\",\"calendarData226: There is No School Today\",\"calendarData227: There is No School Today\",\"calendarData228: There is No School Today\",\"calendarData229: There is No School Today\",\"calendarData230: There is No School Today\",\"calendarData231: There is No School Today\",\"calendarData232: There is No School Today\",\"calendarData233: There is No School Today\",\"calendarData234: There is No School Today\",\"calendarData235: There is No School Today\",\"calendarData236: There is No School Today\",\"calendarData237: There is No School Today\",\"calendarData238: There is No School Today\",\"calendarData239: There is No School Today\",\"calendarData240: There is No School Today\",\"calendarData241: There is No School Today\",\"calendarData242: There is No School Today\",\"calendarData243: There is No School Today\",\"calendarData244: There is No School Today\",\"calendarData245: There is No School Today\",\"calendarData246: There is No School Today\",\"calendarData247: There is No School Today\",\"calendarData248: There is No School Today\",\"calendarData249: There is No School Today\",\"calendarData250: There is No School Today\",\"calendarData251: There is No School Today\",\"calendarData252: There is No School Today\",\"calendarData253: There is No School Today\",\"calendarData254: There is No School Today\",\"calendarData255: There is No School Today\",\"calendarData256: There is No School Today\",\"calendarData257: There is No School Today\",\"calendarData258: There is No School Today\",\"calendarData259: There is No School Today\",\"calendarData260: There is No School Today\",\"calendarData261: There is No School Today\",\"calendarData262: There is No School Today\",\"calendarData263: There is No School Today\",\"calendarData264: There is No School Today\",\"calendarData265: There is No School Today\",\"calendarData266: There is No School Today\",\"calendarData267: There is No School Today\",\"calendarData268: There is No School Today\",\"calendarData269: There is No School Today\",\"calendarData270: There is No School Today\",\"calendarData271: There is No School Today\",\"calendarData272: There is No School Today\",\"calendarData273: There is No School Today\",\"calendarData274: There is No School Today\",\"calendarData275: There is No School Today\",\"calendarData276: There is No School Today\",\"calendarData277: There is No School Today\",\"calendarData278: There is No School Today\",\"calendarData279: There is No School Today\",\"calendarData280: There is No School Today\",\"calendarData281: There is No School Today\",\"calendarData282: There is No School Today\",\"calendarData283: There is No School Today\",\"calendarData284: There is No School Today\",\"calendarData285: There is No School Today\",\"calendarData286: There is No School Today\",\"calendarData287: There is No School Today\",\"calendarData288: There is No School Today\",\"calendarData289: There is No School Today\",\"calendarData290: There is No School Today\",\"calendarData291: There is No School Today\",\"calendarData292: There is No School Today\",\"calendarData293: There is No School Today\",\"calendarData294: There is No School Today\",\"calendarData295: There is No School Today\",\"calendarData296: There is No School Today\",\"calendarData297: There is No School Today\",\"calendarData298: There is No School Today\",\"calendarData299: There is No School Today\",\"calendarData300: There is No School Today\",\"calendarData301: There is No School Today\",\"calendarData302: There is No School Today\",\"calendarData303: There is No School Today\",\"calendarData304: There is No School Today\",\"calendarData305: There is No School Today\",\"calendarData306: There is No School Today\",\"calendarData307: There is No School Today\",\"calendarData308: There is No School Today\",\"calendarData309: There is No School Today\",\"calendarData310: There is No School Today\",\"calendarData311: There is No School Today\",\"calendarData312: There is No School Today\",\"calendarData313: There is No School Today\",\"calendarData314: There is No School Today\",\"calendarData315: There is No School Today\",\"calendarData316: There is No School Today\",\"calendarData317: There is No School Today\",\"calendarData318: There is No School Today\",\"calendarData319: There is No School Today\",\"calendarData320: There is No School Today\",\"calendarData321: There is No School Today\",\"calendarData322: There is No School Today\",\"calendarData323: There is No School Today\",\"calendarData324: There is No School Today\",\"calendarData325: There is No School Today\",\"calendarData326: There is No School Today\",\"calendarData327: There is No School Today\",\"calendarData328: There is No School Today\",\"calendarData329: There is No School Today\",\"calendarData330: There is No School Today\",\"calendarData331: There is No School Today\",\"calendarData332: There is No School Today\",\"calendarData333: There is No School Today\",\"calendarData334: There is No School Today\",\"calendarData335: There is No School Today\",\"calendarData336: There is No School Today\",\"calendarData337: There is No School Today\",\"calendarData338: There is No School Today\",\"calendarData339: There is No School Today\",\"calendarData340: There is No School Today\",\"calendarData341: There is No School Today\",\"calendarData342: There is No School Today\",\"calendarData343: There is No School Today\",\"calendarData344: There is No School Today\",\"calendarData345: There is No School Today\",\"calendarData346: There is No School Today\",\"calendarData347: There is No School Today\",\"calendarData348: There is No School Today\",\"calendarData349: There is No School Today\",\"calendarData350: There is No School Today\",\"calendarData351: There is No School Today\",\"calendarData352: There is No School Today\",\"calendarData353: There is No School Today\",\"calendarData354: There is No School Today\",\"calendarData355: There is No School Today\",\"calendarData356: There is No School Today\",\"calendarData357: There is No School Today\",\"calendarData358: There is No School Today\",\"calendarData359: There is No School Today\",\"calendarData360: There is No School Today\",\"calendarData361: There is No School Today\",\"calendarData362: There is No School Today\",\"calendarData363: There is No School Today\",\"calendarData364: There is No School Today\"]}".getBytes(StandardCharsets.UTF_8));
            }

            File encKey = new File(Main.SAVE_FOLDER + File.separator + "Keys" + File.separator + "xor.key");
            if (!encKey.exists()) {
                Random r = new Random();
                int key = r.nextInt();
                readKey = String.valueOf(key);
                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(Main.SAVE_FOLDER + File.separator + "Keys" + File.separator + "xor.key"));
                    writer.write(String.valueOf(key));
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(Main.SAVE_FOLDER + File.separator + "Keys" + File.separator + "xor.key"));
                    readKey = reader.readLine();
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            File saveFile = new File(Main.SAVE_FOLDER + File.separator + "BellTimes.txt");
            try {
                saveFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                URL website = new URL("https://dl.dropboxusercontent.com/s/a8e3qfwgbfbi0qc/BellTimes.txt?dl=0");
                InputStream in = website.openStream();
                if (!(in == null)) {
                    Files.copy(in, Paths.get(Main.SAVE_FOLDER + File.separator + "BellTimes.txt"), StandardCopyOption.REPLACE_EXISTING);
                    in.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                launch(args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {

        }
    }

    public static Object getMainPane() {
        return mainPane;
    }

    /**
     * This method is where JavaFX creates the UI and displays the window.
     */
    @Override
    public void start(Stage stage) throws Exception {

        mainPane = new MainPane(); //Create the mainPane (pane with all the content)

        Scene scene = new Scene(mainPane); //Put the mainPane into a scene
//        mainPane.setCache(true);
//        mainPane.setCacheShape(true);
//        mainPane.setCacheHint(CacheHint.SPEED);


        //Binds the size of the mainPane to be equal to the scene
        mainPane.prefWidthProperty().bind(scene.widthProperty());
        mainPane.prefHeightProperty().bind(scene.heightProperty());

        //Original (without HOME)
//        stage.setMinHeight(CalendarBox.CALENDAR_BOX_MIN_HEIGHT*5+198); //Set the minimum height of the window
//        stage.setMinWidth(CalendarBox.CALENDAR_BOX_MIN_WIDTH*7+90); //Set the minimum width of the window

        //WITH HOME
        stage.setMinHeight(CalendarBox.CALENDAR_BOX_MIN_HEIGHT * 5 + 198 + 110); //Set the minimum height of the window
        stage.setMinWidth(CalendarBox.CALENDAR_BOX_MIN_WIDTH * 7 + 90 + 280); //Set the minimum width of the window
        //Width: 1140
        //Height: 708


        stage.setTitle("WHS Planner"); //Set the title of the window
        stage.setScene(scene); //Set the window (stage) to display things in the scene

        scene.getStylesheets().add("/Calendar/MainUI.css");
        scene.getStylesheets().add("/UI/Main.css");

        stage.show(); //Display the window

    }

    /**
     * Exits the program fully when it is closed. Put save functions here!
     */
    @Override
    public void stop() {
//        mainPane.saveCalendar();
//        new File(Main.SAVE_FOLDER+ File.separator +"Keys/ipass.key").delete();

        System.exit(0);
    }
}
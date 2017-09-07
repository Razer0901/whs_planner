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

    public static final String VERSION_NUMBER = "1.4.2";

    public static final String UPDATE_NOTES =
            "===== CHANGES =====\n " +
                    "- New calendar saving method. NOTE: 1.4 will automatically port your saves from older versions. However, older versions will not be able to read 1.4 saves. \n " +
                    "- Added icons to make the app look better\n " +
                    "- Added a button to copy Err.txt\n " +
                    "===== Implemented Suggestions =====\n " +
                    "- Implemented dropdown menu for applying any of your classes to a homework task (iPass login required). (Michael German) \n " +
                    "- News links now open in the default browser (Steven Russo)\n " +
                    "- Added a refresh button to News if the user started the application offline (Ella Johnson)\n " +
                    "- Escape key now closes the Schedule drawer (Steven Russo).\n " +
                    "- Added task editing support (Vincent Pak)\n " +
                    "- Saves deleted tasks (Uma Paithankar)\n " +
                    "- Show letter days on the calendar (Vincent Pak/Kevin Wang/Talia Leong)\n " +
                    "===== BUG FIXES =====\n " +
                    "- Minor bell 2 error fixed (Found by Thomas Daley) \n " +
                    "- Fixed iPass login for some users \n " +
                    "- Fixed error log \n " +
                    "- Fixed tasks not saving upon shutdown (Found by Kevin Wang)";

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
                if (br.readLine() == null) {
                    needToPasteDayArray = true;
                    dayArrayFile.delete();
                }
            }else{
                needToPasteDayArray = true;
            }
            if (needToPasteDayArray) {
                isFirstStartup = true;
                Files.write(dayArrayFile.toPath(), "{\"@dates\":[\"dates0: 1/3\",\"dates1: 1/4\",\"dates2: 1/5\",\"dates3: 1/6\",\"dates4: 1/9\",\"dates5: 1/10\",\"dates6: 1/11\",\"dates7: 1/12\",\"dates8: 1/13\",\"dates9: 1/17\",\"dates10: 1/18\",\"dates11: 1/19\",\"dates12: 1/20\",\"dates13: 1/23\",\"dates14: 1/30\",\"dates15: 1/31\",\"dates16: 2/1\",\"dates17: 2/2\",\"dates18: 2/3\",\"dates19: 2/6\",\"dates20: 2/7\",\"dates21: 2/8\",\"dates22: 2/9\",\"dates23: 2/10\",\"dates24: 2/13\",\"dates25: 2/14\",\"dates26: 2/15\",\"dates27: 2/16\",\"dates28: 2/17\",\"dates29: 2/27\",\"dates30: 2/28\",\"dates31: 3/1\",\"dates32: 3/2\",\"dates33: 3/3\",\"dates34: 3/6\",\"dates35: 3/7\",\"dates36: 3/8\",\"dates37: 3/9\",\"dates38: 3/10\",\"dates39: 3/13\",\"dates40: 3/14\",\"dates41: 3/15\",\"dates42: 3/16\",\"dates43: 3/17\",\"dates44: 3/20\",\"dates45: 3/21\",\"dates46: 3/22\",\"dates47: 3/23\",\"dates48: 3/24\",\"dates49: 3/27\",\"dates50: 3/28\",\"dates51: 3/29\",\"dates52: 3/30\",\"dates53: 3/31\",\"dates54: 4/3\",\"dates55: 4/4\",\"dates56: 4/5\",\"dates57: 4/6\",\"dates58: 4/7\",\"dates59: 4/10\",\"dates60: 4/11\",\"dates61: 4/12\",\"dates62: 4/13\",\"dates63: 4/24\",\"dates64: 4/25\",\"dates65: 4/26\",\"dates66: 4/27\",\"dates67: 4/28\",\"dates68: 5/1\",\"dates69: 5/2\",\"dates70: 5/3\",\"dates71: 5/4\",\"dates72: 5/5\",\"dates73: 5/8\",\"dates74: 5/9\",\"dates75: 5/10\",\"dates76: 5/11\",\"dates77: 5/12\",\"dates78: 5/15\",\"dates79: 5/16\",\"dates80: 5/17\",\"dates81: 5/18\",\"dates82: 5/19\",\"dates83: 5/22\",\"dates84: 5/23\",\"dates85: 5/24\",\"dates86: 5/25\",\"dates87: 5/26\",\"dates88: 5/30\",\"dates89: 5/31\",\"dates90: 6/1\",\"dates91: 6/2\",\"dates92: 6/5\",\"dates93: 6/6\",\"dates94: 6/7\",\"dates95: 6/8\",\"dates96: 6/9\",\"dates97: 6/12\",\"dates98: 6/13\",\"dates99: 6/14\",\"dates100: 6/15\",\"dates101: 6/16\",\"dates102: 6/19\",\"dates103: 6/20\",\"dates104: 6/21\",\"dates105: 6/22\",\"dates106: 6/23\",\"dates107: 9/6\",\"dates108: 9/7\",\"dates109: 9/8\",\"dates110: 9/11\",\"dates111: 9/12\",\"dates112: 9/13\",\"dates113: 9/14\",\"dates114: 9/15\",\"dates115: 9/18\",\"dates116: 9/19\",\"dates117: 9/20\",\"dates118: 9/22\",\"dates119: 9/25\",\"dates120: 9/26\",\"dates121: 9/27\",\"dates122: 9/28\",\"dates123: 9/29\",\"dates124: 10/2\",\"dates125: 10/3\",\"dates126: 10/4\",\"dates127: 10/5\",\"dates128: 10/6\",\"dates129: 10/10\",\"dates130: 10/11\",\"dates131: 10/12\",\"dates132: 10/13\",\"dates133: 10/16\",\"dates134: 10/17\",\"dates135: 10/18\",\"dates136: 10/19\",\"dates137: 10/20\",\"dates138: 10/23\",\"dates139: 10/24\",\"dates140: 10/25\",\"dates141: 10/26\",\"dates142: 10/27\",\"dates143: 10/30\",\"dates144: 10/31\",\"dates145: 11/1\",\"dates146: 11/2\",\"dates147: 11/3\",\"dates148: 11/6\",\"dates149: 11/7\",\"dates150: 11/8\",\"dates151: 11/9\",\"dates152: 11/13\",\"dates153: 11/14\",\"dates154: 11/15\",\"dates155: 11/16\",\"dates156: 11/17\",\"dates157: 11/20\",\"dates158: 11/21\",\"dates159: 11/27\",\"dates160: 11/28\",\"dates161: 11/29\",\"dates162: 11/30\",\"dates163: 12/1\",\"dates164: 12/4\",\"dates165: 12/5\",\"dates166: 12/6\",\"dates167: 12/7\",\"dates168: 12/8\",\"dates169: 12/11\",\"dates170: 12/12\",\"dates171: 12/13\",\"dates172: 12/14\",\"dates173: 12/15\",\"dates174: 12/18\",\"dates175: 12/19\",\"dates176: 12/20\",\"dates177: 12/21\",\"dates178: 12/22\"],\"@days\":[\"days0: 0\",\"days1: 1\",\"days2: 2\",\"days3: 3\",\"days4: 4\",\"days5: 5\",\"days6: 6\",\"days7: 7\",\"days8: 8\",\"days9: 9\",\"days10: 10\",\"days11: 11\",\"days12: 12\",\"days13: 13\",\"days14: 14\",\"days15: 15\",\"days16: 16\",\"days17: 17\",\"days18: 18\",\"days19: 19\",\"days20: 20\",\"days21: 21\",\"days22: 22\",\"days23: 23\",\"days24: 24\",\"days25: 25\",\"days26: 26\",\"days27: 27\",\"days28: 28\",\"days29: 29\",\"days30: 30\",\"days31: 31\",\"days32: 32\",\"days33: 33\",\"days34: 34\",\"days35: 35\",\"days36: 36\",\"days37: 37\",\"days38: 38\",\"days39: 39\",\"days40: 40\",\"days41: 41\",\"days42: 42\",\"days43: 43\",\"days44: 44\",\"days45: 45\",\"days46: 46\",\"days47: 47\",\"days48: 48\",\"days49: 49\",\"days50: 50\",\"days51: 51\",\"days52: 52\",\"days53: 53\",\"days54: 54\",\"days55: 55\",\"days56: 56\",\"days57: 57\",\"days58: 58\",\"days59: 59\",\"days60: 60\",\"days61: 61\",\"days62: 62\",\"days63: 63\",\"days64: 64\",\"days65: 65\",\"days66: 66\",\"days67: 67\",\"days68: 68\",\"days69: 69\",\"days70: 70\",\"days71: 71\",\"days72: 72\",\"days73: 73\",\"days74: 74\",\"days75: 75\",\"days76: 76\",\"days77: 77\",\"days78: 78\",\"days79: 79\",\"days80: 80\",\"days81: 81\",\"days82: 82\",\"days83: 83\",\"days84: 84\",\"days85: 85\",\"days86: 86\",\"days87: 87\",\"days88: 88\",\"days89: 89\",\"days90: 90\",\"days91: 91\",\"days92: 92\",\"days93: 93\",\"days94: 94\",\"days95: 95\",\"days96: 96\",\"days97: 97\",\"days98: 98\",\"days99: 99\",\"days100: 100\",\"days101: 101\",\"days102: 102\",\"days103: 103\",\"days104: 104\",\"days105: 105\",\"days106: 106\",\"days107: 107\",\"days108: 108\",\"days109: 109\",\"days110: 110\",\"days111: 111\",\"days112: 112\",\"days113: 113\",\"days114: 114\",\"days115: 115\",\"days116: 116\",\"days117: 117\",\"days118: 118\",\"days119: 119\",\"days120: 120\",\"days121: 121\",\"days122: 122\",\"days123: 123\",\"days124: 124\",\"days125: 125\",\"days126: 126\",\"days127: 127\",\"days128: 128\",\"days129: 129\",\"days130: 130\",\"days131: 131\",\"days132: 132\",\"days133: 133\",\"days134: 134\",\"days135: 135\",\"days136: 136\",\"days137: 137\",\"days138: 138\",\"days139: 139\",\"days140: 140\",\"days141: 141\",\"days142: 142\",\"days143: 143\",\"days144: 144\",\"days145: 145\",\"days146: 146\",\"days147: 147\",\"days148: 148\",\"days149: 149\",\"days150: 150\",\"days151: 151\",\"days152: 152\",\"days153: 153\",\"days154: 154\",\"days155: 155\",\"days156: 156\",\"days157: 157\",\"days158: 158\",\"days159: 159\",\"days160: 160\",\"days161: 161\",\"days162: 162\",\"days163: 163\",\"days164: 164\",\"days165: 165\",\"days166: 166\",\"days167: 167\",\"days168: 168\",\"days169: 169\",\"days170: 170\",\"days171: 171\",\"days172: 172\",\"days173: 173\",\"days174: 174\",\"days175: 175\",\"days176: 176\",\"days177: 177\",\"days178: 178\"],\"@calendarData\":[\"calendarData0: A\",\"calendarData1: B\",\"calendarData2: C\",\"calendarData3: D\",\"calendarData4: E\",\"calendarData5: F\",\"calendarData6: G\",\"calendarData7: H\",\"calendarData8: A\",\"calendarData9: B\",\"calendarData10: C\",\"calendarData11: D\",\"calendarData12: E\",\"calendarData13: F\",\"calendarData14: H\",\"calendarData15: A\",\"calendarData16: B\",\"calendarData17: C\",\"calendarData18: D\",\"calendarData19: E\",\"calendarData20: F\",\"calendarData21: G\",\"calendarData22: H\",\"calendarData23: A\",\"calendarData24: B\",\"calendarData25: C\",\"calendarData26: D\",\"calendarData27: E\",\"calendarData28: F\",\"calendarData29: G\",\"calendarData30: H\",\"calendarData31: A\",\"calendarData32: B\",\"calendarData33: C\",\"calendarData34: D\",\"calendarData35: E\",\"calendarData36: F\",\"calendarData37: G\",\"calendarData38: H\",\"calendarData39: A\",\"calendarData40: B\",\"calendarData41: C\",\"calendarData42: D\",\"calendarData43: E\",\"calendarData44: F\",\"calendarData45: G\",\"calendarData46: H\",\"calendarData47: A\",\"calendarData48: B\",\"calendarData49: C\",\"calendarData50: D\",\"calendarData51: E\",\"calendarData52: F\",\"calendarData53: G\",\"calendarData54: H\",\"calendarData55: A\",\"calendarData56: B\",\"calendarData57: C\",\"calendarData58: D\",\"calendarData59: E\",\"calendarData60: F\",\"calendarData61: G\",\"calendarData62: H\",\"calendarData63: A\",\"calendarData64: B\",\"calendarData65: C\",\"calendarData66: D\",\"calendarData67: E\",\"calendarData68: F\",\"calendarData69: G\",\"calendarData70: H\",\"calendarData71: A\",\"calendarData72: B\",\"calendarData73: C\",\"calendarData74: D\",\"calendarData75: E\",\"calendarData76: F\",\"calendarData77: G\",\"calendarData78: H\",\"calendarData79: A\",\"calendarData80: B\",\"calendarData81: C\",\"calendarData82: D\",\"calendarData83: E\",\"calendarData84: F\",\"calendarData85: G\",\"calendarData86: H\",\"calendarData87: A\",\"calendarData88: B\",\"calendarData89: C\",\"calendarData90: D\",\"calendarData91: E\",\"calendarData92: F\",\"calendarData93: G\",\"calendarData94: H\",\"calendarData95: A\",\"calendarData96: B\",\"calendarData97: C\",\"calendarData98: D\",\"calendarData99: E\",\"calendarData100: F\",\"calendarData101: G\",\"calendarData102: H\",\"calendarData103: A\",\"calendarData104: B\",\"calendarData105: C\",\"calendarData106: D\",\"calendarData107: A\",\"calendarData108: B\",\"calendarData109: C\",\"calendarData110: D\",\"calendarData111: E\",\"calendarData112: F\",\"calendarData113: G\",\"calendarData114: H\",\"calendarData115: A\",\"calendarData116: B\",\"calendarData117: C\",\"calendarData118: D\",\"calendarData119: E\",\"calendarData120: F\",\"calendarData121: G\",\"calendarData122: H\",\"calendarData123: A\",\"calendarData124: B\",\"calendarData125: C\",\"calendarData126: D\",\"calendarData127: E\",\"calendarData128: F\",\"calendarData129: G\",\"calendarData130: H\",\"calendarData131: A\",\"calendarData132: B\",\"calendarData133: C\",\"calendarData134: D\",\"calendarData135: E\",\"calendarData136: F\",\"calendarData137: G\",\"calendarData138: H\",\"calendarData139: A\",\"calendarData140: B\",\"calendarData141: C\",\"calendarData142: D\",\"calendarData143: E\",\"calendarData144: F\",\"calendarData145: G\",\"calendarData146: H\",\"calendarData147: A\",\"calendarData148: B\",\"calendarData149: C\",\"calendarData150: D\",\"calendarData151: E\",\"calendarData152: F\",\"calendarData153: G\",\"calendarData154: H\",\"calendarData155: A\",\"calendarData156: B\",\"calendarData157: C\",\"calendarData158: D\",\"calendarData159: E\",\"calendarData160: F\",\"calendarData161: G\",\"calendarData162: H\",\"calendarData163: A\",\"calendarData164: B\",\"calendarData165: C\",\"calendarData166: D\",\"calendarData167: E\",\"calendarData168: F\",\"calendarData169: G\",\"calendarData170: H\",\"calendarData171: A\",\"calendarData172: B\",\"calendarData173: C\",\"calendarData174: D\",\"calendarData175: E\",\"calendarData176: F\",\"calendarData177: G\",\"calendarData178: H\",\"calendarData179: There is No School Today\",\"calendarData180: There is No School Today\",\"calendarData181: There is No School Today\",\"calendarData182: There is No School Today\",\"calendarData183: There is No School Today\",\"calendarData184: There is No School Today\",\"calendarData185: There is No School Today\",\"calendarData186: There is No School Today\",\"calendarData187: There is No School Today\",\"calendarData188: There is No School Today\",\"calendarData189: There is No School Today\",\"calendarData190: There is No School Today\",\"calendarData191: There is No School Today\",\"calendarData192: There is No School Today\",\"calendarData193: There is No School Today\",\"calendarData194: There is No School Today\",\"calendarData195: There is No School Today\",\"calendarData196: There is No School Today\",\"calendarData197: There is No School Today\",\"calendarData198: There is No School Today\",\"calendarData199: There is No School Today\",\"calendarData200: There is No School Today\",\"calendarData201: There is No School Today\",\"calendarData202: There is No School Today\",\"calendarData203: There is No School Today\",\"calendarData204: There is No School Today\",\"calendarData205: There is No School Today\",\"calendarData206: There is No School Today\",\"calendarData207: There is No School Today\",\"calendarData208: There is No School Today\",\"calendarData209: There is No School Today\",\"calendarData210: There is No School Today\",\"calendarData211: There is No School Today\",\"calendarData212: There is No School Today\",\"calendarData213: There is No School Today\",\"calendarData214: There is No School Today\",\"calendarData215: There is No School Today\",\"calendarData216: There is No School Today\",\"calendarData217: There is No School Today\",\"calendarData218: There is No School Today\",\"calendarData219: There is No School Today\",\"calendarData220: There is No School Today\",\"calendarData221: There is No School Today\",\"calendarData222: There is No School Today\",\"calendarData223: There is No School Today\",\"calendarData224: There is No School Today\",\"calendarData225: There is No School Today\",\"calendarData226: There is No School Today\",\"calendarData227: There is No School Today\",\"calendarData228: There is No School Today\",\"calendarData229: There is No School Today\",\"calendarData230: There is No School Today\",\"calendarData231: There is No School Today\",\"calendarData232: There is No School Today\",\"calendarData233: There is No School Today\",\"calendarData234: There is No School Today\",\"calendarData235: There is No School Today\",\"calendarData236: There is No School Today\",\"calendarData237: There is No School Today\",\"calendarData238: There is No School Today\",\"calendarData239: There is No School Today\",\"calendarData240: There is No School Today\",\"calendarData241: There is No School Today\",\"calendarData242: There is No School Today\",\"calendarData243: There is No School Today\",\"calendarData244: There is No School Today\",\"calendarData245: There is No School Today\",\"calendarData246: There is No School Today\",\"calendarData247: There is No School Today\",\"calendarData248: There is No School Today\",\"calendarData249: There is No School Today\",\"calendarData250: There is No School Today\",\"calendarData251: There is No School Today\",\"calendarData252: There is No School Today\",\"calendarData253: There is No School Today\",\"calendarData254: There is No School Today\",\"calendarData255: There is No School Today\",\"calendarData256: There is No School Today\",\"calendarData257: There is No School Today\",\"calendarData258: There is No School Today\",\"calendarData259: There is No School Today\",\"calendarData260: There is No School Today\",\"calendarData261: There is No School Today\",\"calendarData262: There is No School Today\",\"calendarData263: There is No School Today\",\"calendarData264: There is No School Today\",\"calendarData265: There is No School Today\",\"calendarData266: There is No School Today\",\"calendarData267: There is No School Today\",\"calendarData268: There is No School Today\",\"calendarData269: There is No School Today\",\"calendarData270: There is No School Today\",\"calendarData271: There is No School Today\",\"calendarData272: There is No School Today\",\"calendarData273: There is No School Today\",\"calendarData274: There is No School Today\",\"calendarData275: There is No School Today\",\"calendarData276: There is No School Today\",\"calendarData277: There is No School Today\",\"calendarData278: There is No School Today\",\"calendarData279: There is No School Today\",\"calendarData280: There is No School Today\",\"calendarData281: There is No School Today\",\"calendarData282: There is No School Today\",\"calendarData283: There is No School Today\",\"calendarData284: There is No School Today\",\"calendarData285: There is No School Today\",\"calendarData286: There is No School Today\",\"calendarData287: There is No School Today\",\"calendarData288: There is No School Today\",\"calendarData289: There is No School Today\",\"calendarData290: There is No School Today\",\"calendarData291: There is No School Today\",\"calendarData292: There is No School Today\",\"calendarData293: There is No School Today\",\"calendarData294: There is No School Today\",\"calendarData295: There is No School Today\",\"calendarData296: There is No School Today\",\"calendarData297: There is No School Today\",\"calendarData298: There is No School Today\",\"calendarData299: There is No School Today\",\"calendarData300: There is No School Today\",\"calendarData301: There is No School Today\",\"calendarData302: There is No School Today\",\"calendarData303: There is No School Today\",\"calendarData304: There is No School Today\",\"calendarData305: There is No School Today\",\"calendarData306: There is No School Today\",\"calendarData307: There is No School Today\",\"calendarData308: There is No School Today\",\"calendarData309: There is No School Today\",\"calendarData310: There is No School Today\",\"calendarData311: There is No School Today\",\"calendarData312: There is No School Today\",\"calendarData313: There is No School Today\",\"calendarData314: There is No School Today\",\"calendarData315: There is No School Today\",\"calendarData316: There is No School Today\",\"calendarData317: There is No School Today\",\"calendarData318: There is No School Today\",\"calendarData319: There is No School Today\",\"calendarData320: There is No School Today\",\"calendarData321: There is No School Today\",\"calendarData322: There is No School Today\",\"calendarData323: There is No School Today\",\"calendarData324: There is No School Today\",\"calendarData325: There is No School Today\",\"calendarData326: There is No School Today\",\"calendarData327: There is No School Today\",\"calendarData328: There is No School Today\",\"calendarData329: There is No School Today\",\"calendarData330: There is No School Today\",\"calendarData331: There is No School Today\",\"calendarData332: There is No School Today\",\"calendarData333: There is No School Today\",\"calendarData334: There is No School Today\",\"calendarData335: There is No School Today\",\"calendarData336: There is No School Today\",\"calendarData337: There is No School Today\",\"calendarData338: There is No School Today\",\"calendarData339: There is No School Today\",\"calendarData340: There is No School Today\",\"calendarData341: There is No School Today\",\"calendarData342: There is No School Today\",\"calendarData343: There is No School Today\",\"calendarData344: There is No School Today\",\"calendarData345: There is No School Today\",\"calendarData346: There is No School Today\",\"calendarData347: There is No School Today\",\"calendarData348: There is No School Today\",\"calendarData349: There is No School Today\",\"calendarData350: There is No School Today\",\"calendarData351: There is No School Today\",\"calendarData352: There is No School Today\",\"calendarData353: There is No School Today\",\"calendarData354: There is No School Today\",\"calendarData355: There is No School Today\",\"calendarData356: There is No School Today\",\"calendarData357: There is No School Today\",\"calendarData358: There is No School Today\",\"calendarData359: There is No School Today\",\"calendarData360: There is No School Today\",\"calendarData361: There is No School Today\",\"calendarData362: There is No School Today\",\"calendarData363: There is No School Today\",\"calendarData364: There is No School Today\"]}".getBytes(StandardCharsets.UTF_8));
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
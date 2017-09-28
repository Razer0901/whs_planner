package WHS_planner.Schedule;

import WHS_planner.Core.IO;
import WHS_planner.Main;
import WHS_planner.UI.ESchoolHandler;
import WHS_planner.UI.LoginException;
import WHS_planner.UI.ScheduleBox;
import WHS_planner.UI.ScheduleTutorialController;
import WHS_planner.Util.XorTool;
import com.jfoenix.controls.JFXCheckBox;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.util.*;

public class Schedule
{

    public static Scene schedule;
    private final String[] start = {"7:30", "8:32", "9:39", "10:41", "12:16", "1:18", "0:00"};
    private final String[] end = {"8:27", "9:29", "10:36", "12:11", "1:13", "2:15", "0:00"};
    private final String[] wens = {"7:30", "8:15", "9:35", "10:20", "11:40", "12:25", "0:00"};
    private final String[] wene = {"8:10", "8:55", "10:15", "11:35", "12:20", "1:05", "0:00"};
    private final String[] bells = {"7:30", "8:26", "9:58", "10:55", "12:26", "1:23", "0:00"};
    private final String[] belle = {"8:21", "9:18", "10:50", "12:21", "1:18", "2:15", "0:00"};
    public static String SCHEDULE_PATH = Main.SAVE_FOLDER+ File.separator +"Schedule.json";

    private Integer[][] periodGrid = {
            {1,2,3,5,6,7}, //A & E
            {2,3,4,6,7,8}, //B & F
            {3,4,1,7,8,5}, //C & G
            {4,1,2,8,5,6}, //D & H
    };

    @FXML
    private Pane rootLayout;
    @FXML
    private Pane login;
    private Map<String, Object> labels;
    private ScheduleBlock[] blocks;
    private ScheduleController scheduleControl;
    private JFXCheckBox checkBox;

    public Schedule(JFXCheckBox checkBox)
    {

        this.checkBox = checkBox;
        this.checkBox.setOnAction(e -> Platform.runLater(this::resetLabels));

        try
        {
            //replaced with threading...
            buildSchedule();

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }


    public JFXCheckBox getCheck()
    {
        return checkBox;
    }

    public void setTimeLabels(String[] startTimes, String[] endTimes){ //Takes in start and end times and updates the labels on the side, Format should be like the times above.
        for (int i = 1; i <= 7; i++) {
            Label l = (Label) labels.get("Time"+i);
            String s;
            s = "Period " + i + "\nStart: " + startTimes[i - 1] + "\nEnd: " + endTimes[i - 1];
            l.setText(s);
            l.setWrapText(true);
        }
    }

    public void resetLabels()
    {
        for (int i = 1; i <= 7; i++) {
            Label l = (Label) labels.get("Time"+i);
            String s;
            if(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == 4) {
                s = "Period " + i + "\nStart: " + wens[i - 1] + "\nEnd: " + wene[i - 1];
            } else if (checkBox.isSelected()) {
                s = "Period " + i + "\nStart: " + bells[i - 1] + "\nEnd: " + belle[i - 1];
            } else {
                s = "Period " + i + "\nStart: " + start[i - 1] + "\nEnd: " + end[i - 1];
            }
            l.setText(s);
            l.setWrapText(true);
        }
    }

    private void buildSchedule() throws Exception
    {
        FXMLLoader loader = new FXMLLoader();

        loader.setLocation(getClass().getResource("/Schedule/schedule_layout.fxml"));

        rootLayout = loader.load();
        scheduleControl = loader.getController();

        FXMLLoader loader2 = new FXMLLoader();


//      ++++++++++++++++++++ NEW ++++++++++++++++++++
//        loader2.setLocation(getClass().getResource("/UI/ScheduleTutorial.fxml"));
//        login = loader2.load();
//        ScheduleTutorialController control2 = loader2.getController();
//      ++++++++++++++++++++ NEW ++++++++++++++++++++

//      ++++++++++++++++++++ OLD ++++++++++++++++++++
        loader2.setLocation(getClass().getResource("/Schedule/material_login.fxml"));
        login = loader2.load();
        loginController control2 = loader2.getController();

        login.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {

                try
                {
                    control2.submit();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
//      ++++++++++++++++++++ OLD ++++++++++++++++++++

        File g = new File(SCHEDULE_PATH);

        if(!g.exists()) {
            schedule = new Scene(login);
        } else {
            generateSchedule(loader);
            schedule = new Scene(rootLayout);
        }
    }


    private void generateSchedule(FXMLLoader loader)
    {
        int incr = 0;
        int incr2 = 1;

        labels = loader.getNamespace();

        try {
            JSONArray courses = loadClasslist();
            for(Object rawCourse: courses){
                JSONObject course = (JSONObject) rawCourse;
                String[] days = ((String) course.get("days")).split(", ");
                ArrayList<Integer> letterDays = new ArrayList<>();
                for (String day : days) {
                    char[] ch = day.toCharArray();
                    for (char c : ch) {
                        int temp = (int) c;
                        int temp_integer = 64; //for upper case
                        if (temp <= 90 & temp >= 65)
                            letterDays.add(temp - temp_integer - 1);
                    }
                }
                Date[] quarterStartDates = {new Date(2017, 9, 5), new Date(2017, 11, 13), new Date(2018, 1, 29), new Date(2018, 4, 9)}; //Hey bitch boys who are maintaining this program. Fix this next year. -Autism #4
                Date today = Calendar.getInstance().getTime();
                String quarter = "";
                if ((quarterStartDates[0].before(today) && quarterStartDates[1].after(today)) || quarterStartDates[0].equals(today)){
                    quarter = "Q1";
                } else if ((quarterStartDates[1].before(today) && quarterStartDates[2].after(today)) || quarterStartDates[1].equals(today)) {
                    quarter = "Q2";
                } else if ((quarterStartDates[2].before(today) && quarterStartDates[3].after(today)) || quarterStartDates[2].equals(today)) {
                    quarter = "Q3";
                } else if (quarterStartDates[3].before(today) || quarterStartDates[3].equals(today)){
                    quarter = "Q4";
                }
//                System.out.println(course.get("quarters") + " q " + quarter);
                if (((String)course.get("quarters")).contains(quarter)) {
                    for (int letterDay : letterDays) {
//                    int tempBlock = Arrays.asList(periodGrid[letterDay%4]).indexOf(Integer.parseInt((String)course.get("period")));
//
//                    ScheduleBox tempBox = (ScheduleBox) getNodeByRowColumnIndex(tempBlock,letterDay,this);
//                    tempBox.setName((String)course.get("name"));
//                    tempBox.setTeacher(((String)course.get("teacher")).split(",")[0]);
//                    tempBox.setPeriod((String)course.get("period"));
//                    tempBox.setRoom((String)course.get("room"));
                        String s = (String) course.get("name") + "\n" + ((String) course.get("teacher")).split(",")[0] + "\n" + (String) course.get("room") + "\n" + "Period:" + (String) course.get("period");

                        String letter;

                        switch (letterDay) {
                            case 0:
                                letter = "A";
                                break;
                            case 1:
                                letter = "B";
                                break;
                            case 2:
                                letter = "C";
                                break;
                            case 3:
                                letter = "D";
                                break;
                            case 4:
                                letter = "E";
                                break;
                            case 5:
                                letter = "F";
                                break;
                            case 6:
                                letter = "G";
                                break;
                            case 7:
                                letter = "H";
                                break;
                            default:
                                letter = "Time";
                                break;
                        }

                        int tempBlock = Arrays.asList(periodGrid[letterDay % 4]).indexOf(Integer.parseInt((String) course.get("period")));

//                    System.out.println("letterDay: " + letterDay + " letter: " + letter + " tempBLock: " + tempBlock + " incr2: " + incr2);

                        int block = tempBlock + 1;

                        Label l = (Label) labels.get(letter + (tempBlock + 1));
                        l.setText(s);
                        l.setWrapText(true);

                        incr++;
                        if (incr == 8) {
                            incr = 0;
                            incr2++;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 1; i <= 6; i++)
        {
            Label l = (Label) labels.get("Time"+i);


            String s;

            if(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == 4)
            {
//                s = "Period "+i+"\nStart: "+"\n"+wens[i-1]+"\nEnd:\n"+wene[i-1];
                s = "Period " + i + "\nStart: " + wens[i - 1] + "\nEnd: " + wene[i - 1];

            } else if (checkBox.isSelected()) {
                s = "Period " + i + "\nStart: " + bells[i - 1] + "\nEnd: " + belle[i - 1];
            }

            else
            {
//                s = "Period "+i+"\nStart: "+"\n"+start[i-1]+"\nEnd:\n"+end[i-1];
                s = "Period " + i + "\nStart: " + start[i - 1] + "\nEnd: " + end[i - 1];

            }

            l.setText(s);
            l.setWrapText(true);
        }
 //
//        for (int i = 0; i < 56; i++)
//        {
//            String s;
//
//            currentClass = blocks[i].getClassName();
//            currentTeacher = blocks[i].getTeacher();
//            currentTeacher = currentTeacher.replace("<br>", " & ");
//            currentClass = currentClass.replace("&amp;", "&");
//            currentPeriod = blocks[i].getPeriodNumber();
//            currentRoom = blocks[i].getRoomNumber();
//
//            if(blocks[i].getClassName().trim().equals("Free"))
//            {
//                s = "Free";
//            }
//            else
//            {
//                s = currentClass+"\n"+currentTeacher+"\n"+currentRoom+"\n"+ "Block:" + currentPeriod;
//            }
//
//            //System.out.println(s);
//
//            String letter;
//
//            switch(incr)
//            {
//                case 0: letter = "A";
//                    break;
//                case 1: letter = "B";
//                    break;
//                case 2: letter = "C";
//                    break;
//                case 3: letter = "D";
//                    break;
//                case 4: letter = "E";
//                    break;
//                case 5: letter = "F";
//                    break;
//                case 6: letter = "G";
//                    break;
//                case 7: letter = "H";
//                    break;
//                default: letter = "Time";
//                    break;
//            }
//
//            try
//            {
//                Label l = (Label) labels.get(letter+incr2);
//                l.setText(s);
//                l.setWrapText(true);
//            }
//            catch(Exception e)
//            {
//                //e.printStackTrace();
//            }
//
//            incr++;
//            if(incr == 8)
//            {
//                incr = 0;
//                incr2++;
//            }
//        }
//
//        for (int i = 1; i <= 7; i++)
//        {
//            Label l = (Label) labels.get("Time"+i);
//
//
//            String s;
//
//            if(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == 4)
//            {
////                s = "Period "+i+"\nStart: "+"\n"+wens[i-1]+"\nEnd:\n"+wene[i-1];
//                s = "Period " + i + "\nStart: " + wens[i - 1] + "\nEnd: " + wene[i - 1];
//
//            } else if (checkBox.isSelected()) {
//                s = "Period " + i + "\nStart: " + bells[i - 1] + "\nEnd: " + belle[i - 1];
//            }
//
//            else
//            {
////                s = "Period "+i+"\nStart: "+"\n"+start[i-1]+"\nEnd:\n"+end[i-1];
//                s = "Period " + i + "\nStart: " + start[i - 1] + "\nEnd: " + end[i - 1];
//
//            }
//
//            l.setText(s);
//            l.setWrapText(true);
//        }
    }

    public ScheduleBlock[] getData()
    {
        File schedulefile = new File(Main.SAVE_FOLDER+ File.separator +"Schedule.json");

        if(!schedulefile.exists())
        {
            try {
                schedulefile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new ScheduleBlock[0];
        }

        ScheduleBlock[][] tempGrid = new ScheduleBlock[6][8];

        JSONArray courses = loadClasslist();
        for(Object rawCourse: courses){
            JSONObject course = (JSONObject) rawCourse;
            String[] days = ((String) course.get("days")).split(", ");
            ArrayList<Integer> letterDays = new ArrayList<>();
            for(String day: days){
                char[] ch  = day.toCharArray();
                for(char c : ch)
                {
                    int temp = (int)c;
                    int temp_integer = 64; //for upper case
                    if(temp<=90 & temp>=65)
                        letterDays.add(temp-temp_integer-1);
                }
            }

            for(int letterDay: letterDays){
                int tempBlock = Arrays.asList(periodGrid[letterDay%4]).indexOf(Integer.parseInt((String)course.get("period")));
                tempGrid[tempBlock][letterDay] = new ScheduleBlock((String)course.get("name"),((String)course.get("teacher")).split(",")[0],(String)course.get("room"),(String)course.get("period"));
            }
        }

        blocks = new ScheduleBlock[48];
        int counter = 0;
        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 8; c++) {
                ScheduleBlock temp = tempGrid[r][c];
                if(temp != null){
                    blocks[counter] = temp;
                }else{
                    blocks[counter] = new ScheduleBlock("Free", "Free", "Free", "Free");
                }
                counter ++;
            }
        }

        return blocks;

    }

    public Node getPane()
    {
        return schedule.getRoot();
    }



    public ScheduleBlock[] getToday(String letter)
    {
        ScheduleBlock b[] = new ScheduleBlock[6];
        int x;
        switch(letter)
        {
            case "A": x = 0;
                break;
            case "B": x = 1;
                break;
            case "C": x = 2;
                break;
            case "D": x = 3;
                break;
            case "E": x = 4;
                break;
            case "F": x = 5;
                break;
            case "G": x = 6;
                break;
            case "H": x = 7;
                break;
            default:  x = -1;
                break;
        }

        for (int i = 0; i < 6; i++)
        {
            b[i] = blocks[x+(i*8)];
        }

        return b;
    }

    public ScheduleController getScheduleControl() {
        return scheduleControl;
    }

    public boolean isLoggedIn() {
        File g = new File(Main.SAVE_FOLDER+ File.separator +"Schedule.json");

        return g.exists();
    }

    public JSONArray loadClasslist(){
        JSONParser parser = new JSONParser();
        Object obj = null;
        try {
            obj = parser.parse(new FileReader(SCHEDULE_PATH));
            return (JSONArray) obj;
        }catch (Exception e){
            e.printStackTrace();
        }
        return (JSONArray) obj;
    }
}

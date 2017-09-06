package WHS_planner.UI;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by geoffreywang on 9/6/17.
 */
public class ScheduleGrid extends GridPane {

    private Integer[][] periodGrid = {
            {1,2,3,5,6,7}, //A & E
            {2,3,4,6,7,8}, //B & F
            {3,4,1,7,8,5}, //C & G
            {4,1,2,8,5,6}, //D & H
    };

    public ScheduleGrid(){
        this.setStyle("-fx-background-color: #F1F1F1; -fx-font-size: 12px;");
        this.setGridLinesVisible(false);

        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 8; c++) {
                ScheduleBox temp = new ScheduleBox();
                temp.setMinSize(130,100);
                this.add(temp,c,r);
            }
        }
//
        try {
            JSONArray courses = ESchoolHandler.getCourses("","");
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

                    ScheduleBox tempBox = (ScheduleBox) getNodeByRowColumnIndex(tempBlock,letterDay,this);
                    tempBox.setName((String)course.get("name"));
                    String[] teacherNames = ((String)course.get("teacher")).split(", ");
                    tempBox.setTeacher(teacherNames[1] + " " + teacherNames[0]);
                    tempBox.setPeriod("Period: " + (String)course.get("period"));
                    tempBox.setRoom((String)course.get("room"));
                }

            }
        } catch (LoginException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Node getNodeByRowColumnIndex (final int row, final int column, GridPane gridPane) {
        Node result = null;
        ObservableList<Node> childrens = gridPane.getChildren();

        for (Node node : childrens) {
            if(gridPane.getRowIndex(node) == row && gridPane.getColumnIndex(node) == column) {
                result = node;
                break;
            }
        }

        return result;
    }
}

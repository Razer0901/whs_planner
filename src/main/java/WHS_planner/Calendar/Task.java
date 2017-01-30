package WHS_planner.Calendar;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;

import java.io.File;

/**
 * Created by geoffrey_wang on 9/20/16.
 */
public class Task {
    String Class, Title, Description;
    private Boolean doesExist = true;

    public Task(String class1,String title1, String description1){
        Class = class1;
        Title = title1;
        Description = description1;
    }

    public void changeClass(String class1){
        Class = class1;
    }
    public void changeTitle(String title1){
        Title = title1;
    }
    public void changeDescription(String description1){
        Description = description1;
    }

    public Pane getPane(CalendarBox box){
        HBox pane = new HBox();
//        StackPane pane = new StackPane();

        pane.setMinHeight(30);
        pane.getStylesheets().add("Calendar" + File.separator + "MainUI.css");
        pane.getStyleClass().add("task-pane");
        pane.setAlignment(Pos.CENTER_LEFT);

        Text label;

        Text spaces = new Text("  ");


        if (Class.isEmpty()) //If there is no class
        {
            label = new Text(Description);
            label.setBoundsType(TextBoundsType.VISUAL);

            pane.getChildren().add(spaces);
            pane.getChildren().add(label);
        }
        else //If there is a class
        {
            label = new Text(Class + ": " + Description);
            //TODO ^^ This is the problem code that is causing the bug
            label.setBoundsType(TextBoundsType.VISUAL);

            pane.getChildren().add(spaces);
            pane.getChildren().add(label);
        }

        pane.setOnMouseClicked((event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (doesExist) {
                    doesExist = false;
                    label.setStrikethrough(true);
                }else{
                    doesExist = true;
                    label.setStrikethrough(false);
                }
            }
            box.update();
        }));

//        pane.setOnMouseEntered((event -> {
//            if (event.getButton() == MouseButton.PRIMARY) {
//                label.setStrikethrough(true);
//            }
//        }));
//        pane.setOnMouseExited((event -> {
//            if (event.getButton() == MouseButton.PRIMARY) {
//                label.setStrikethrough(true);
//            }
//        }));

        return pane;
    }

    Boolean doesExist() {
        return doesExist;
    }
}

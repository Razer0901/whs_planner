package WHS_planner.UI;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;


/**
 * Created by geoffreywang on 9/6/17.
 */
public class ScheduleBox extends VBox {

    private Label nameLabel, periodLabel, teacherLabel, roomLabel;

    public ScheduleBox(){
        this.setStyle("-fx-background-color: #ffffff");
        this.setAlignment(Pos.CENTER);
        this.toBack();
        this.setBorder(new Border(new BorderStroke(Color.rgb(241,241,241,1),
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY,BorderStroke.THIN)));


        nameLabel = new Label();
        nameLabel.prefWidthProperty().bind(this.widthProperty());
        nameLabel.setTextAlignment(TextAlignment.CENTER);
        nameLabel.setAlignment(Pos.CENTER);
        nameLabel.setWrapText(true);

        periodLabel = new Label();
        periodLabel.prefWidthProperty().bind(this.widthProperty());
        periodLabel.setTextAlignment(TextAlignment.CENTER);
        periodLabel.setAlignment(Pos.CENTER);

        teacherLabel = new Label();
        teacherLabel.prefWidthProperty().bind(this.widthProperty());
        teacherLabel.setTextAlignment(TextAlignment.CENTER);
        teacherLabel.setAlignment(Pos.CENTER);

        roomLabel = new Label();
        roomLabel.prefWidthProperty().bind(this.widthProperty());
        roomLabel.setTextAlignment(TextAlignment.CENTER);
        roomLabel.setAlignment(Pos.CENTER);

        this.getChildren().addAll(nameLabel,teacherLabel,roomLabel,periodLabel);
    }

    public void setName(String text){
        nameLabel.setText(text);
    }

    public void setPeriod(String text){
        periodLabel.setText("Period: " + text);
    }

    public void setTeacher(String text){
        teacherLabel.setText(text);
    }

    public void setRoom(String text){
        roomLabel.setText(text);
    }

}

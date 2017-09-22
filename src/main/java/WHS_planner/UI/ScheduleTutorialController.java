package WHS_planner.UI;

import WHS_planner.Main;
import WHS_planner.Schedule.Schedule;
import WHS_planner.Util.UserLoggedIn;
import com.jfoenix.controls.*;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.*;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.json.simple.JSONArray;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.*;
import java.util.ResourceBundle;
import java.net.URL;

public class ScheduleTutorialController implements Initializable {
    @FXML
    private Button buttonSafari, buttonChrome, buttonFirefox;
    @FXML
    private ImageView imageView;
    @FXML
    private StackPane tutorialPane;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tutorialPane.setOnDragOver(event -> {
            if (event.getGestureSource() != tutorialPane && event.getDragboard().hasFiles()) {
                /* allow for both copying and moving, whatever user chooses */
                event.acceptTransferModes(TransferMode.LINK);
            }
            event.consume();
        });

        tutorialPane.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {

                System.out.println(db.getFiles());
                try {
                    boolean loginSuccess = false;

                    try {
                        saveClasslist(ScheduleHTMLHandler.getCoursesStudent(db.getFiles().get(0)));
                        loginSuccess = true;
                    }catch (LoginException ex){

                    }

                    if(!loginSuccess) {
                        try {
                            saveClasslist(ScheduleHTMLHandler.getCoursesTeacher(db.getFiles().get(0)));
                            loginSuccess = true;
                        } catch (LoginException ex) {

                        }
                    }

                    try {
                        MainPane mp = (MainPane) Main.getMainPane();
                        if(loginSuccess){
                            mp.resetSchedule();
                            UserLoggedIn.logIn();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        PauseTransition ps = new PauseTransition(Duration.seconds(1));
                        ps.setOnFinished(event1 -> {

                        });
                        ps.play();
                    } catch (Exception e) {
                        System.out.println("Error in refreshing schedule pane...");
                    }

                    if(loginSuccess == false){
                        Alert alert = new Alert(Alert.AlertType.ERROR, "File doesn't look right... please follow the directions!\nContact the devs if the problem persists!", ButtonType.OK);
                        alert.showAndWait();
                    }

                }catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "File doesn't look right... please follow the directions!\nContact the devs if the problem persists!", ButtonType.OK);
                    alert.showAndWait();
                    e.printStackTrace();
                    System.out.println("Wrong HTML");
                }
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    @FXML
    public void safari(){
        imageView.setImage(new Image("UI/Safari.png"));
        buttonSafari.setStyle("-fx-background-color: #EA660F;");
        buttonChrome.setStyle("-fx-background-color: #0079F5;");
        buttonFirefox.setStyle("-fx-background-color: #0079F5;");
    }

    @FXML
    public void chrome(){
        imageView.setImage(new Image("UI/Chrome.png"));
        buttonSafari.setStyle("-fx-background-color: #0079F5;");
        buttonChrome.setStyle("-fx-background-color: #EA660F;");
        buttonFirefox.setStyle("-fx-background-color: #0079F5;");
    }

    @FXML
    public void firefox(){
        imageView.setImage(new Image("UI/Firefox.png"));
        buttonSafari.setStyle("-fx-background-color: #0079F5;");
        buttonChrome.setStyle("-fx-background-color: #0079F5;");
        buttonFirefox.setStyle("-fx-background-color: #EA660F;");
    }

    @FXML
    public void teacherLink(){
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(ScheduleHTMLHandler.TEACHER_URL));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void studentLink(){
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(ScheduleHTMLHandler.STUDENT_URL));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void why(){
        Label info = new Label(
                "With the new feature to be able to show/print teacher schedules, account security has been reworked." +
                        " Since every time a user types in their account credentials into a 3rd party software, the software has" +
                        " full access to the account and could potentially store sensitive information, we want to avoid passwords all together." +
                        " While our implementation and source code is/has been all PUBLICLY available on" +
                        " Github, we do not want to take any responsibility in handling teacher account credentials (or" +
                        " any credentials for that matter).");
        info.setPadding(new Insets(10));
        info.setPrefSize(400,175);
        info.setWrapText(true);
        Pane temp = new Pane(info);
        JFXDialog dialog = new JFXDialog(tutorialPane, temp, JFXDialog.DialogTransition.CENTER, true);
        dialog.show();
        dialog.setMinWidth(400);
        dialog.setMinHeight(0);
    }

    public void saveClasslist(JSONArray data){
        File scheduleFile = new File(Schedule.SCHEDULE_PATH);
        if(scheduleFile.exists()){
            scheduleFile.delete();
        }
        try {
            scheduleFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (FileWriter file = new FileWriter(Schedule.SCHEDULE_PATH)) {
            file.write(data.toJSONString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}


package WHS_planner.Schedule;

import WHS_planner.Main;
import WHS_planner.UI.ESchoolHandler;
import WHS_planner.UI.LoginException;
import WHS_planner.UI.MainPane;
import WHS_planner.Util.UserLoggedIn;
import WHS_planner.Util.XorTool;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.json.simple.JSONArray;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ResourceBundle;

public class loginController implements Initializable
{
    @FXML
    private Button button;
    @FXML
    private JFXPasswordField password;
    @FXML
    private JFXTextField user;
    @FXML
    private Label error;
    @FXML
    private Pane loginPane;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //Initializes the "submit" button style
//        button.setButtonType(JFXButton.ButtonType.RAISED);
//        button.getStyleClass().setAll("button-raised");
//        button.getStylesheets().add("Schedule" + File.separator + "ButtonStyle.css");

//        loginPane.getStyleClass().add("-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.25), 15, 0, 1, 2, 0);");
    }

    public void submit() throws Exception {
        String username = user.getText();
        String pass = password.getText();
        error.setVisible(true);

        if(username.equals("") || pass.equals("")) {
            error.setTextFill(Color.BLACK);
            error.setText("Please enter your eSchoolPlus information");
        } else {
            try {
                error.setTextFill(Color.GREEN);
                error.setText("Logging in, please wait...");
                loginPane.requestLayout();
                button.setDisable(true);
                saveClasslist(ESchoolHandler.getCourses(username,pass));

                try {
                    PauseTransition ps = new PauseTransition(Duration.seconds(1));
                    ps.setOnFinished(event -> {
                        try {
                            MainPane mp = (MainPane) Main.getMainPane();
                            mp.resetSchedule();
                            UserLoggedIn.logIn();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    ps.play();
                } catch (Exception e) {
                    System.out.println("Error in refreshing schedule pane...");
                }
            }catch (LoginException e){
                error.setTextFill(Color.RED);
                error.setText("Incorrect username or password. Please try again.");
                button.setDisable(false);
                password.clear();
            }catch (SocketTimeoutException e) {
                error.setTextFill(Color.RED);
                error.setText("Server timed out. Please try again later.");
            }catch (Exception e) {
                error.setTextFill(Color.RED);
                error.setText("Hmmm... something went REALLY wrong!\nPlease contact the devs ASAP!");
                e.printStackTrace();
                System.out.println("Error occurred during login");
            }
        }
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


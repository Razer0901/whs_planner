package WHS_planner.Calendar;

import WHS_planner.UI.ScheduleBox;
import WHS_planner.UI.ScheduleGrid;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by geoffrey_wang on 9/17/16.
 */
public class MainUI extends Application{

    @Override
    public void start(Stage stage) throws Exception {
        try {
            ScheduleBox temp = new ScheduleBox();
            temp.setName("HI");
            Scene scene = new Scene(temp);
            stage.setScene(scene);
        }catch (Exception e){
            e.printStackTrace();
        }

        stage.show();
    }
}
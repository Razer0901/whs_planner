package WHS_planner.UI;

import WHS_planner.Calendar.CalendarYear;
import WHS_planner.Main;
import WHS_planner.News.ui.NewsUI;
import WHS_planner.Schedule.Schedule;
import WHS_planner.Util.UserLoggedIn;
import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.*;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.transform.Scale;
import javafx.util.Duration;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.stream.Stream;


public class MainPane extends StackPane {

    private final static String ICON_BELL = "\uf0f3";
    private final static String ICON_SCHEDULE = "\uf073";
    private final static String ICON_NEWS = "\uf1ea";
    private final static String ICON_FEEDBACK = "\uf044";
    private final static String ICON_ABOUT = "\uf05a";
    private final static String ICON_PRINT = "\uf02f";

    //    private final int HOME = 0;
//    private final int SCHEDULE = 1;
//    private final int CALENDAR = 2;
//    private final int NEWS = 3;
//    private final int MEETING = 4;
    private JFXCheckBox bell2Check = new JFXCheckBox();
    private Pane navBar;
    private Pane content;
    private ArrayList<Pane> contentPanes;
    private JFXDrawer drawer;
    private JFXDrawer newsDrawer = new JFXDrawer();


    private VBox mainPane;
//    private VBox cardView = new NewsUI().getCardView();
    private Schedule schedule;
    private CalendarYear calendar;
    private NewsUI news;
    private boolean isHamburgerPressed = false;

//    private Home homePane;

    public MainPane(){
        navBar = loadNavBar(); //Loads the navBar from the FXML
        navBar.getStyleClass().setAll("navBar");
        navBar.getChildren().get(0).getStyleClass().setAll("jfx-hamburger-icon");
        navBar.setStyle("-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.25), 15, 0, 1, 2, 0);");
        content = new Pane(); //Creates an empty main content pane
        contentPanes = new ArrayList<>(); //Makes an empty list for all the content panes
        generatePanes(); //Loads in all the different panes
        mainPane = createPane();
        mainPane.prefWidthProperty().bind(this.widthProperty());
        mainPane.prefHeightProperty().bind(this.heightProperty());
        this.getChildren().setAll(mainPane); //Set the main pane as the pane generated

        //Initialize update note panel
        if (Main.isFirstTimeOnVersion) {
            final StackPane outsidePane = this;
            VBox info = new VBox();
            info.setAlignment(Pos.CENTER_LEFT);
            info.setSpacing(10);
            info.getStylesheets().addAll("/UI/dropDown.css");
            info.getStyleClass().setAll("roboto");
            Label title = new Label("What's new in " + Main.VERSION_NUMBER);
            title.setMaxWidth(335);
            title.setWrapText(true);
            title.getStyleClass().setAll("title-text");
            title.setPadding(new Insets(0, 0, 10, 0));
            Label agreement = new Label();
            agreement.setText(Main.UPDATE_NOTES);
            agreement.setPadding(new Insets(0, 0, 10, 0));
            agreement.setMaxWidth(335);
            agreement.setWrapText(true);

            JFXButton continueButton = new JFXButton("CONTINUE");
            continueButton.getStyleClass().addAll("continue-button");
            continueButton.setRipplerFill(Color.DARKSLATEGRAY);
            continueButton.setCursor(Cursor.HAND);
            HBox continueContainer = new HBox(continueButton);
            continueContainer.setAlignment(Pos.BOTTOM_RIGHT);
            info.getChildren().add(title);
            info.getChildren().add(agreement);
            info.getChildren().add(continueContainer);
            JFXDialog dialog = new JFXDialog(outsidePane, info, JFXDialog.DialogTransition.CENTER, true);
            dialog.show();
            dialog.setMinWidth(400);
            dialog.setMinHeight(0);
            info.setPadding(new Insets(25, 25, 15, 25));
            info.setMinWidth(0);
            info.setMinHeight(0);
            continueButton.setOnMouseClicked(event -> dialog.close());
        }

        //Initialize agreement panel
        if (Main.isFirstStartup) {
            final StackPane outsidePane = this;
            VBox info = new VBox();
            info.setAlignment(Pos.CENTER_LEFT);
            info.setSpacing(10);
            info.getStylesheets().addAll("/UI/dropDown.css");
            info.getStyleClass().setAll("roboto");
            Label title = new Label("Agreement");
            title.setMaxWidth(335);
            title.setWrapText(true);
            title.getStyleClass().setAll("title-text");
            title.setPadding(new Insets(0, 0, 10, 0));
            Label agreement = new Label();
            if (System.getenv("HOME").contains("ryan_swanke")) {
                agreement.setText("Hi Ryan! Your kind! \n\n\nThis application is intended for school use only. Do not use it to store sensitive personal information.");
            } else {
                agreement.setText("This application is intended for school use only. Do not use it to store sensitive personal information.");
            }
//        agreement.setText("I agree to not store sensitive personal information in this application.");
//            agreement.setTextAlignment(TextAlignment.JUSTIFY);
            agreement.setPadding(new Insets(0, 0, 10, 0));
            agreement.setMaxWidth(335);
            agreement.setWrapText(true);
            final JFXCheckBox agreeCheckbox = new JFXCheckBox("I agree");
            agreeCheckbox.setCursor(Cursor.HAND);
//        agreeCheckbox.setText("I agree!");
            agreeCheckbox.setCheckedColor(Paint.valueOf("#009688"));
            JFXButton continueButton = new JFXButton("CONTINUE");
//        continueButton.setText("Continue");
            continueButton.getStyleClass().addAll("continue-button");
            continueButton.setRipplerFill(Color.DARKSLATEGRAY);
            continueButton.setCursor(Cursor.HAND);
            continueButton.setDisable(true);
            HBox continueContainer = new HBox(continueButton);
            continueContainer.setAlignment(Pos.BOTTOM_RIGHT);
            info.getChildren().add(title);
            info.getChildren().add(agreement);
            info.getChildren().add(agreeCheckbox);
            info.getChildren().add(continueContainer);
            JFXDialog dialog = new JFXDialog(outsidePane, info, JFXDialog.DialogTransition.CENTER, true);
            dialog.show();
            dialog.setOverlayClose(false);
            dialog.setMinWidth(400);
            dialog.setMinHeight(0);
            info.setPadding(new Insets(25, 25, 15, 25));
            info.setMinWidth(0);
            info.setMinHeight(0);
            agreeCheckbox.setOnAction(event -> {
                if (agreeCheckbox.isSelected()) {
                    continueButton.setDisable(false);
                } else {
                    continueButton.setDisable(true);
                }
            });
            continueButton.setOnMouseClicked(event -> dialog.close());
        }

    }

    /**
     * Loads in the NavBar from the FXML file.
     * @return NavBarPane
     */
    private Pane loadNavBar() {
        String location = "/UI/NavBar.fxml"; //Location of the FXML in the resources folder //TODO make absolute


        //Tries to load in the FXML and if it fails it returns an error message
        try {
            return FXMLLoader.load(getClass().getResource(location));
        } catch (IOException e) {
            System.out.println("Exception on FXMLLoader.load()");
            System.out.println("  * location: " + location);
            System.out.println("  * " + e);
            System.out.println("----------------------------------------\n");
        }

        return null; //Return nothing if it errors out
    }

    /**
     * Generates the Pane from all the data given
     * @return vBox
     */
    private VBox createPane() {
        VBox vBox = new VBox(); //Create a vBox for the base pane

//        JFXButton fab = new JFXButton("NEWS");
//        fab.setButtonType(JFXButton.ButtonType.RAISED);



        //Make a stack pane with the drawer and content in it
//        StackPane stackPane = new StackPane(content,createDrawer((JFXHamburger)navBar.getChildren().get(0),1440,48), createNewsDrawer((Button)navBar.getChildren().get(1)));
        StackPane stackPane = new StackPane(content,createDrawer((JFXHamburger)navBar.getChildren().get(0),1440,48));


//        StackPane stackPane = new StackPane(content,fab,createDrawer((JFXHamburger)navBar.getChildren().get(0),1440,48), createNewsDrawer(fab));


//        createNewsDrawer((Button)navBar.getChildren().get(1));
        initiateDropDown((Button)navBar.getChildren().get(1));

        //Set the content the base pane to have the nav bar on top and content under it
        vBox.getChildren().setAll(navBar,stackPane);
        VBox.setVgrow(stackPane, Priority.ALWAYS);
        VBox.setVgrow(navBar, Priority.NEVER);

        return vBox;
    }

//    public JFXCheckBox getCheck() {
//        return bell2Check;
//    }

    private void initiateDropDown(Button bigButton) {
        /*
        \
         |   START Jesus code
          \
         */
        bigButton.setText("\uf142");
//        bigButton.setPrefSize(50,50);
        bigButton.getStylesheets().setAll("UI" + File.separator + "dropDown.css");
        bigButton.getStyleClass().addAll("big-button");
//        bigButton.setStyle("-fx-font-family: 'FontAwesome Regular'; -fx-font-size: 28px; -fx-text-fill: #FFFFFF;");
        Pane parent = (Pane)(bigButton.getParent());
        bigButton.prefHeightProperty().bind(parent.heightProperty());
        final StackPane backmanISGay = this;
        bigButton.setOnMouseClicked(event -> {
//            System.out.println(schedule.isLoggedIn());
            VBox info = new VBox();

            JFXButton buttonPrint = new JFXButton();
            SplitPane printContainer = new SplitPane(buttonPrint);
            printContainer.setStyle("-fx-box-border: transparent; -fx-background-color: 000000;");


            buttonPrint.setText("      " + ICON_PRINT + "  Print Schedule");
            buttonPrint.getStylesheets().addAll("UI" + File.separator + "dropDown.css");
            buttonPrint.setDisable(false);
            buttonPrint.getStyleClass().setAll("list-button");


            Tooltip tooltip = new Tooltip();
            printContainer.setTooltip(tooltip);
            hackTooltipStartTiming(tooltip);

            if(schedule.isLoggedIn()) {
//                System.out.println("schedule logged");
                printContainer.setTooltip(null);
                buttonPrint.setDisable(false);
                buttonPrint.getStyleClass().addAll("ungrayed-out");
                buttonPrint.setOnMouseClicked(event1 -> {
                    if(drawer.isShown()) {
                        PrinterJob job = PrinterJob.createPrinterJob();
                        PageLayout pageLayout = job.getPrinter().createPageLayout(Paper.NA_LETTER, PageOrientation.LANDSCAPE, Printer.MarginType.HARDWARE_MINIMUM);


                        //ungray
                        schedule.getScheduleControl().clearGrayBox();

                        double scaleX
                                = pageLayout.getPrintableWidth() / schedule.getPane().getBoundsInParent().getWidth();
                        double scaleY
                                = pageLayout.getPrintableHeight() / schedule.getPane().getBoundsInParent().getHeight();
                        Scale scale = new Scale(scaleX, scaleX); //Uniform Scaling

                        schedule.getPane().getTransforms().add(scale);


                        Label titleLable = ((Label) schedule.getPane().lookup("#Title3"));  // NEVER DO THIS!!!
                        String tempText = titleLable.getText();                             // Instead...
                        titleLable.setText("");                                             // do as I say...

                        if (job != null && job.showPrintDialog(this.getScene().getWindow())) {
                            boolean success = job.printPage(pageLayout, schedule.getPane());
                            if (success) {
                                job.endJob();
                                //gray
                                schedule.getScheduleControl().setClass();
                            }
                        }

                        titleLable.setText(tempText);                                       // not as I do!
                        schedule.getPane().getTransforms().remove(scale);
                    }else {
                        JFXSnackbar snackbar = new JFXSnackbar(mainPane);
                        snackbar.show("Open the Schedule tab first!", 2000);
                    }

                });


            } else {
//                System.out.println("schedule not logged");
                tooltip.setText("Log into Schedule first!");
                buttonPrint.getStyleClass().addAll("grayed-out");
//                System.out.println(buttonPrint.getStyleClass());

                buttonPrint.setDisable(true);

            }



            JFXButton button0 = new JFXButton();
            button0.setText("      " + ICON_BELL + "  Show Bell Schedule");
            button0.setOnMouseClicked(event1 -> {
                info.setMinSize(0, 0);
                info.getChildren().clear();
                info.getChildren().add(getBellSchedulePane());
                info.getStyleClass().setAll("large-text");
            });
            JFXButton button1 = new JFXButton();
            button1.setText("      " + ICON_SCHEDULE + "  Reload Schedule");
            button1.setOnMouseClicked(event12 -> {
                try {
                    schedule.getScheduleControl().logout();
                    UserLoggedIn.logOut();

                    printContainer.setTooltip(tooltip);
                    tooltip.setText("Log into Schedule first!");
                    buttonPrint.getStyleClass().removeAll("ungrayed-out");
                    buttonPrint.getStyleClass().addAll("grayed-out");
//                    buttonPrint.setStyle("-fx-text-fill: 757575;");

                    buttonPrint.setDisable(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            JFXButton button2 = new JFXButton();
            button2.setText("      " + ICON_NEWS + "  Refresh News");
            button2.setOnMouseClicked(eventNews -> {
                news.refresh();
                news.requestFocus();
                news.requestLayout();
                news.layout();
            });
            JFXButton button3 = new JFXButton();
            button3.setText("      " + ICON_FEEDBACK + "  Send Feedback");
            button3.setOnMouseClicked(event13 -> {
                if(Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().browse(new URI("https://goo.gl/forms/KSCFGXldhE4EBytp1"));
                    } catch (IOException | URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
            });
            JFXButton button4 = new JFXButton();
            button4.setText("      " + ICON_ABOUT + "   About");
            button4.setOnMouseClicked(event14 -> {
                info.getChildren().clear();
                Label versionLabel = new Label("Version:");
                versionLabel.setUnderline(true);
                Label peopleLabel = new Label("Collaborators:");
                peopleLabel.setUnderline(true);
                info.getChildren().add(new Label("Created in HACS under the guidance of Mr. Hopps!\n "));
                info.getChildren().add(versionLabel);
                info.getChildren().add(new Label(Main.VERSION_NUMBER+" \n "));
                info.getChildren().add(peopleLabel);
                String[] names = new String[]{
                        "Tyler Brient - UI Master, Bug Squasher",
                        "Andrew Eggleston - Group Motivator",
                        "Geoffrey Wang - UI Master, Calendar, T-Rex :)",
                        "George Jiang - UX, News, Bug Finder",
                        "Jack Bachman - Backend, Github",
                        "Matthew Elbing - Backend, Project Lead",
                        "Will Robison - HTML, Piano Tiles 2",
                        "John Broderick - Schedule, Bug Creator",
                        "Tzur Almog - Calendar",
                        "Alex Bell - News",
                };
                for (String name : names) {
                    info.getChildren().add(new Label(name));
                }
                info.getChildren().add(new Label("\n"));
                HBox buttonContainer = new HBox();
                buttonContainer.setSpacing(40);
                JFXButton licenses = new JFXButton("Licenses");
                JFXButton copyError = new JFXButton("Copy Error Log");

                copyError.getStyleClass().setAll("gray-button");
                copyError.setButtonType(JFXButton.ButtonType.FLAT);
                copyError.setAlignment(Pos.CENTER);
                copyError.setPrefSize(120,30);

                licenses.getStyleClass().setAll("gray-button");
                licenses.setButtonType(JFXButton.ButtonType.FLAT);
                licenses.setAlignment(Pos.CENTER);
                licenses.setPrefSize(120,30);

                buttonContainer.getChildren().addAll(licenses,copyError);
                buttonContainer.setAlignment(Pos.CENTER);

                licenses.setOnMouseClicked(showLicences -> {
                    info.getChildren().clear();
                    Label licenseText = new Label("We used the JSOUP library (https://jsoup.org/) which is licensed under the MIT license:\nThe MIT License \nCopyright © 2009 - 2016 Jonathan Hedley (jonathan@hedley.net) \nPermission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the \"Software\"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions: \nThe above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software. \nTHE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. \n\nWe also used federkasten's appbundler library (https://github.com/federkasten/appbundle-maven-plugin), as well as the JFoenix UI library (http://www.jfoenix.com/). \nBoth are avaiable under the Apache License 2.0(https://www.apache.org/licenses/LICENSE-2.0). \nCredit goes to Stack Overflow users: Chui Tey and jewelsea for their work on the Layout Animator class. \nhttps://gist.github.com/jewelsea/5683558 \n\nFont Awesome by Dave Gandy - http://fontawesome.io");
                    licenseText.setWrapText(true);
                    licenseText.getStyleClass().addAll("times");
                    info.getChildren().add(licenseText);
                });
                copyError.setOnMouseClicked(addToClipboard -> {
                    StringSelection selection = null;
                    JFXSnackbar snackbar = new JFXSnackbar(mainPane);
                    try {
                        String errorLog = new String(Files.readAllBytes(Paths.get(System.getenv("HOME") + File.separator + "Library" + File.separator + "Application Support" + File.separator + "WHS Planner" + File.separator + "err.txt"))/*,"UTF-8"*/);
                        selection = new StringSelection(errorLog);

                        if(errorLog == null || errorLog.isEmpty()){
                            snackbar.show("Error Log Empty!", 2000);
                            //Don't override user's existing clipboard if err.txt is empty
                        } else {
                            snackbar.show("Error Log Copied!",2000);
                            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                            clipboard.setContents(selection, selection);
                        }

                    } catch (IOException e) {
                        snackbar.show("Error when copying!",2000);
                        e.printStackTrace();
                    }


                });
                info.getChildren().add(buttonContainer);
                info.getStyleClass().setAll("large-text");
                info.setPadding(new Insets(10));
            });
            bell2Check.setCursor(Cursor.HAND);
            bell2Check.setText("Bell 2");
            bell2Check.setTranslateX(10);
            bell2Check.setAlignment(Pos.CENTER_LEFT);
            info.getChildren().addAll(button0, button1, printContainer, button2, button3, button4, bell2Check);
            info.setAlignment(Pos.TOP_LEFT);
            info.getStylesheets().addAll("UI" + File.separator + "dropDown.css");
            info.setPadding(new Insets(10,0,10,0));

            button0.getStyleClass().setAll("list-button");
            button1.getStyleClass().setAll("list-button");
            button2.getStyleClass().setAll("list-button");
            button3.getStyleClass().setAll("list-button");
            button4.getStyleClass().setAll("list-button");

            bell2Check.getStyleClass().setAll("label-button");
            bell2Check.setPrefSize(200,50);
            info.setSpacing(0);
            info.setMinSize(200, info.getChildren().toArray().length*50); //Sets the height of the window assuming each button is 50px tall
            JFXDialog dialog = new JFXDialog(backmanISGay, info, JFXDialog.DialogTransition.CENTER, true);

            dialog.show();
            /*
            \
             |    END Jesus code
              \
             */
        });
    }


//    private JFXDrawer createNewsDrawer(Button button){
//        Pane parent = (Pane)(button.getParent());
//        button.prefHeightProperty().bind(parent.heightProperty());
//        button.setText(ICON_NEWS);
//        button.setCursor(Cursor.HAND);
////        button.getStylesheets().setAll("UI" + File.separator + "dropDown.css");
////        button.getStyleClass().addAll("big-button");
//        button.setStyle("-fx-font-family: 'FontAwesome Regular'; -fx-font-size: 24; -fx-text-fill: #FFFFFF;");
//        button.setTextOverrun(OverrunStyle.CLIP);
//
//        javafx.scene.control.ScrollPane newsScroll = new javafx.scene.control.ScrollPane();
//        newsScroll.setContent(news.getCardView());
//        newsScroll.setVbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.AS_NEEDED);
//        newsScroll.setHbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);
//        //NEWS Style
//        newsScroll.setStyle("-fx-background-color: transparent;");
//        newsScroll.getStylesheets().add("News" + File.separator + "NewsUI.css");
//        newsScroll.getStyleClass().setAll("scroll-bar");
//        //NEWS Scaling
//        newsScroll.setFitToWidth(true);
//        newsScroll.setMinWidth(280);
//        newsScroll.setMaxWidth(280);
//        newsScroll.setPrefHeight(this.getPrefHeight());
//
//        newsDrawer = new JFXDrawer();
//        newsDrawer.setDirection(JFXDrawer.DrawerDirection.RIGHT);
//        newsDrawer.setStyle("-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.25), 15, 0, 1, 5, 0);");
//        newsDrawer.setDefaultDrawerSize(280);
//        newsDrawer.setSidePane(newsScroll);
//        newsDrawer.setPickOnBounds(false);
//        newsDrawer.setMouseTransparent(true);
//
//
//        button.setOnMouseClicked(event -> {
//            if (newsDrawer.isShown()) {
//                newsDrawer.setMouseTransparent(true);
//
//                newsDrawer.close();
//            } else {
//                newsDrawer.setMouseTransparent(false);
////                newsDrawer.setMouseTransparent(true);
//
//                newsDrawer.open();
//            }
//        });
//
//        this.setOnKeyPressed(new EventHandler<KeyEvent>() {
//            @Override
//            public void handle(KeyEvent event) {
//                if (event.getCode() == KeyCode.ESCAPE) {
//                    if (newsDrawer.isShown()) {
//                        newsDrawer.setMouseTransparent(true);
//                        newsDrawer.close();
//                    }
//                }
//            }
//        });
//
//        newsDrawer.setOnDrawerClosing(event -> {
//            newsDrawer.setMouseTransparent(true);
//
//        });
//
//        return newsDrawer;
//    }

    /**
     * Generates a drawer initiated by the hamburger, defined by the width and height
     * @param hamburger
     * @param width
     * @param buttonHeight
     * @return drawer
     */
    private JFXDrawer createDrawer(JFXHamburger hamburger, double width, double buttonHeight) {
        //Put the buttons generated into a vBox
//        ScheduleBlock[] scheduleBlocks = new ScheduleBlock[7]; //TODO: Get from schedule
//        VBox tabsVBox = new VBox();
//        for (int i = 0; i < scheduleBlocks.length; i++) {
//            Label l = new Label();
//            l.setPadding(new Insets(20, 5, 20, 5));
//            l.setText(scheduleBlocks[i].getClassName());
//            tabsVBox.getChildren().add(l);
//        }
//        generateButtons(new String[]{"Home", "Schedule"}, width, buttonHeight);

        //Set drawer preferences
        drawer = new JFXDrawer();
        drawer.setDefaultDrawerSize(width);
        drawer.setSidePane(contentPanes.get(1));
//        drawer.setSidePane(tabsVBox);
        drawer.setPickOnBounds(false);
        drawer.setMouseTransparent(true);                                                //vertical, higher = lower
        drawer.setStyle("-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.25), 15, 0, 1, 5, 0);");

        drawer.setOnDrawerClosing(event -> {
            if (!isHamburgerPressed) {
                drawer.setMouseTransparent(true);
                hamburger.getAnimation().setRate(-1);
                hamburger.getAnimation().play();
            }
        });

        //Hamburger animation
        hamburger.setAnimation(new HamburgerBackArrowBasicTransition(hamburger));
        hamburger.getAnimation().setRate(-1);

        //Hamburger function to open an close the drawer
        hamburger.setOnMouseClicked(event -> {
            if (drawer.isShown()) {
                isHamburgerPressed = true;
                drawer.setMouseTransparent(true);
                hamburger.getAnimation().setRate(-1); //Switches the transition between forward and backwards.
                drawer.close();
            } else {
                drawer.setMouseTransparent(false);
                hamburger.getAnimation().setRate(1);
                drawer.open();
            }
            hamburger.getAnimation().play(); //Plays the transition
            isHamburgerPressed = false;
        });

        this.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ESCAPE) {
                    if (drawer.isShown()) {
                        isHamburgerPressed = true;
                        drawer.setMouseTransparent(true);
                        hamburger.getAnimation().setRate(-1); //Switches the transition between forward and backwards.
                        drawer.close();
                        hamburger.getAnimation().play(); //Plays the transition
                        isHamburgerPressed = false;
                    }
                }
            }
        });

        //More functions to open and close the drawer

        return drawer;
    }

    private void generatePanes() {
        schedule = new Schedule(bell2Check);
        calendar = new CalendarYear(schedule);
        //calender has schedule and bell2check

//        NewsUI news = null;
//        try {
        news = new NewsUI();
//        }catch(Exception e){
//            e.printStackTrace();
//        }
        //Pane meeting = new MeetingPane();
        Home homePane = new Home(calendar, news.getCardView());
        addPane(homePane);
        addPane((Pane) schedule.getPane());
//        addPane(new ScheduleGrid());
        //addPane(meeting);

        content.getChildren().add(contentPanes.get(0)); //Sets home tab as default

    }


    public void resetSchedule() throws Exception {
        remPane((Pane)schedule.getPane());
        schedule = new Schedule(bell2Check);
        addPane((Pane) schedule.getPane(), 1);
        drawer.getContent().clear();
        drawer.setSidePane(contentPanes.get(1));
        calendar.setSchedule(schedule);

    }

    private Node[] generateButtons(String[] text, double width, double buttonHeight) {
        JFXButton[] buttonArray = new JFXButton[text.length];
        for (int i = 0; i < text.length; i++) {
            JFXButton button = new JFXButton(text[i].toUpperCase());
            button.setPrefSize(width, buttonHeight);
            buttonArray[i] = button;
            setMouseClickedEvent(button, i);
        }
        return buttonArray;
    }

    private void setMouseClickedEvent(JFXButton button, final int id) {
        button.setOnMouseClicked(event -> {
            if(!content.getChildren().contains(contentPanes.get(id))) {
                content.getChildren().clear();
                content.getChildren().add(contentPanes.get(id));
            }
            JFXDrawer drawer = (JFXDrawer)button.getParent().getParent().getParent();
            JFXHamburger hamburger =  (JFXHamburger)navBar.getChildren().get(0);

            closeDrawer(drawer, hamburger);
        });
    }

    private void addPane(Pane pane) {
        pane.prefHeightProperty().bind(content.heightProperty());
        pane.prefWidthProperty().bind(content.widthProperty());
        contentPanes.add(pane);
    }

    private void addPane(Pane pane, int i) {
        pane.prefHeightProperty().bind(content.heightProperty());
        pane.prefWidthProperty().bind(content.widthProperty());
        contentPanes.add(i, pane);
    }

    private void remPane(Pane pane)
    {
        contentPanes.remove(pane);
    }

    private void closeDrawer(JFXDrawer drawer, JFXHamburger hamburg) {
        drawer.setMouseTransparent(true);
        hamburg.getAnimation().setRate(-1); //Switches the transition between forward and backwards.
        hamburg.getAnimation().play(); //Plays the transition
        drawer.close();
    }

    public Schedule getSchedule()
    {
        return schedule;
    }

    public void saveCalendar(){
        calendar.saveCalendar();
    }

    private Pane getBellSchedulePane(){
        Calendar now = Calendar.getInstance();
//        //Testing
//        now.set(Calendar.DATE, 3);
//        now.set(Calendar.MONTH, 1);
//        now.set(Calendar.YEAR, 2017);

        String[] times;
        String[] blocks;

        if(now.get(Calendar.DAY_OF_WEEK)==Calendar.WEDNESDAY){
            times = new String[]{"7:30-8:10","8:15-8:55","9:05-9:30","9:35-10:15", "10:20-10:50","10:42-11:12","11:05-11:35","11:40-12:20","12:25-1:05"};
            blocks = new String[]{"Block 1: ","Block 2: ","Advisory: ","Block 3: ","1st Lunch: ","2nd Lunch: ","3rd Lunch: ","Block 5: ","Block 6: "};

        } else if (bell2Check.isSelected()) {
            times = new String[]{"7:30-8:21", "8:26-9:18", "9:28-9:53", "9:58-10:50", "10:55-11:25", "11:22-11:52", "11:51-12:21", "12:26-1:18", "1:23-2:15"};
            blocks = new String[]{"Block 1: ", "Block 2: ", "Class Meeting: ", "Block 3: ", "1st Lunch: ", "2nd Lunch: ", "3rd Lunch: ", "Block 5: ", "Block 6: "};
        } else {
            times = new String[]{"7:30-8:26","8:31-9:28","9:38-10:35","10:40-11:10","11:10-11:40","11:41-12:11","12:16-1:13","1:18-2:15"};
            blocks = new String[]{"Block 1: ","Block 2: ","Block 3: ","1st Lunch: ","2nd Lunch: ","3rd Lunch: ","Block 5: ","Block 6: "};
        }

        ArrayList<String> bellTimesFile = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get(Main.SAVE_FOLDER + File.separator + "BellTimes.txt"))) {
            stream.forEachOrdered(bellTimesFile::add);
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            while(bellTimesFile.size()>4) {
                if (now.get(Calendar.MONTH) == Integer.parseInt(bellTimesFile.get(0)) - 1 && now.get(Calendar.DAY_OF_MONTH) == Integer.parseInt(bellTimesFile.get(1)) && now.get(Calendar.YEAR) == Integer.parseInt(bellTimesFile.get(2))) {
                    int numberOfRows = Integer.parseInt(bellTimesFile.get(3));
                    blocks = new String[numberOfRows];
                    times = new String[numberOfRows];
                    for (int i = 0; i < numberOfRows; i++) {
                        blocks[i] = bellTimesFile.get(i + 4);
                    }
                    for (int i = 0; i < numberOfRows; i++) {
                        times[i] = bellTimesFile.get(i + 4 + numberOfRows);
                    }
                    break;
                }else{
                    int numberOfRows = Integer.parseInt(bellTimesFile.get(3));
                    for (int i = 0; i < numberOfRows*2+4; i++) {
                        bellTimesFile.remove(0);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        GridPane returnPane = new GridPane();
        for (int i = 0; i < times.length; i++) {
            returnPane.add(new Label(blocks[i]),0,i);
            returnPane.add(new Label(times[i]),1,i);
        }
        returnPane.setHgap(10);
        returnPane.setPadding(new Insets(10));
        return returnPane;
    }

    private static void hackTooltipStartTiming(Tooltip tooltip) {
        try {
            Field fieldBehavior = tooltip.getClass().getDeclaredField("BEHAVIOR");
            fieldBehavior.setAccessible(true);
            Object objBehavior = fieldBehavior.get(tooltip);
            Field fieldTimer = objBehavior.getClass().getDeclaredField("activationTimer");
            fieldTimer.setAccessible(true);
            Timeline objTimer = (Timeline) fieldTimer.get(objBehavior);
            objTimer.getKeyFrames().clear();
            objTimer.getKeyFrames().add(new KeyFrame(new Duration(0)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

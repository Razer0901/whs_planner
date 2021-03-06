package WHS_planner.News.ui;

import WHS_planner.News.model.Feed;
import WHS_planner.News.model.FeedMessage;
import WHS_planner.News.read.RSSFeedParser;
import WHS_planner.Trex.TrexPane;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.jsoup.Jsoup;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class NewsUI extends Pane {

    private static final double BOX_WIDTH = 250;
    private static final double IMAGE_WIDTH = 250;

    private RSSFeedParser parser = new RSSFeedParser("https://waylandstudentpress.com/feed/");
    private Feed feed = parser.readFeed();
    private List<FeedMessage> feedArray = feed.getMessages();
    private List<FeedMessage> onScreenMessages = new ArrayList<>();
    private VBox cardView = new VBox();

    public NewsUI() {
        cardView.getStylesheets().add("News" + File.separator + "NewsUI.css");

        //Checks if feed sends back a connection error. If it doesn't, initialize cards as normal.
        if (feed.getTitle().equals("badNet")) {
            cardView.setAlignment(Pos.CENTER);
            Hyperlink badNetLink = new Hyperlink("https://www.google.com/");
            badNetLink.setText("Test Connection (google.com)");
            //Refresh button to test connection after offline app launch
            JFXButton offlineRefresh = new JFXButton("Refresh");
            offlineRefresh.getStylesheets().addAll("UI" + File.separator + "NewsUI.css");
            offlineRefresh.getStyleClass().addAll("refresh-button");
            VBox refreshContainer = new VBox(offlineRefresh);
            VBox.setMargin(refreshContainer, new Insets(10,0,10,0));
            refreshContainer.setAlignment(Pos.CENTER);
            Label refreshError = new Label("Failed to refresh News.");
//            refreshError.setTextOverrun(OverrunStyle);
            refreshError.setTextOverrun(OverrunStyle.CLIP);

            refreshError.setTextFill(Color.RED);
            int[] fontSize = {14};
            offlineRefresh.setOnMouseClicked(event -> {
                feed = parser.readFeed();
                if (feed.getTitle().equals("badNet")) {
                    if(!(cardView.getChildren().get(1) == refreshError)){
                        cardView.getChildren().add(1,refreshError);
                    } else {
                        fontSize[0]++;
                        refreshError.setStyle("-fx-font-size: " + fontSize[0]+ "px;");
                    }
                } else {
                    cardView.getChildren().clear();
                    init();
                }
            });
            cardView.getChildren().add(refreshContainer);
            addCard(badNetLink, new Label("Error with Connection!"));
            addCard(new TrexPane());
        } else {
            init();
        }
    }

    //OLD hyperlink method - uses Chrome only
//    private void openLink(int index) {
//        try {
////            Runtime.getRuntime().exec(new String[]{"open", "-a", "Google Chrome", parser.readFeed().getMessages().get(index).getLink()});
//            Runtime.getRuntime().exec(new String[]{"open", "-a", "Google Chrome", onScreenMessages.get(index).getLink()});
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    private void openLink(int index) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(onScreenMessages.get(index).getLink()));
            }
        } catch (URISyntaxException e) {
            System.out.println("URI Syntax Exception!");
            System.out.println("----------------------");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("I/O Exception!");
            System.out.println("----------------------");
            e.printStackTrace();
        }
    }

    private void init() {
        cardView.getChildren().clear();

        feedArray = feed.getMessages();

        //Loop through all articles
        for (int i = 0; i < feedArray.size(); i++) {

            //Add Title (Hyperlink)
            final int eye = i;
            Hyperlink hpl = new Hyperlink(escapeHTML(feedArray.get(i).getTitle()));
//            hpl.setOnAction((event) -> openLink(eye));
            hpl.setWrapText(true);
            hpl.setMaxWidth(BOX_WIDTH);
            hpl.setPadding(new Insets(0, 0, 0, 4));

            //Add Description (Label)
            Label description = new Label(escapeHTML(feedArray.get(i).getDescription()));
            description.setWrapText(true);
            description.setMaxWidth(BOX_WIDTH);
            description.setPadding(new Insets(0, 0, 0, 6));

            //Add Image
            try {
                String urlString = scanDescription(feedArray.get(i).getDescription());
                if (urlString != null) {
                    URL url = new URL(urlString);
                    BufferedImage bf;
                    try {
                        bf = ImageIO.read(url);
                    } catch (Exception ex) {
                        addCard(description, hpl, null, feedArray.get(i).getLink());
                        continue;
                    }
                    WritableImage wr = convertImg(bf);
                    ImageView img = new ImageView(wr);

                    img.setFitWidth(IMAGE_WIDTH);
                    img.setFitHeight(wr.getHeight() / (wr.getWidth() / (IMAGE_WIDTH)));
                    img.setImage(wr);

                    //Add article to list WITH image
                    addCard(description, hpl, img, feedArray.get(i).getLink());
                } else {
                    //Add article to list WITHOUT image
                    addCard(description, hpl, null, feedArray.get(i).getLink());
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            onScreenMessages.add(feedArray.get(i));
        }
    }

    public void refresh() {
        feedArray.clear();
        feedArray = parser.getNewArticles(onScreenMessages);


        //Loop through all articles
        for (int i = 0; i < feedArray.size(); i++) {

            //Add Title (Hyperlink)
            final int eye = i;
            Hyperlink hpl = new Hyperlink(escapeHTML(feedArray.get(i).getTitle()));
//            hpl.setOnAction((event) -> openLink(eye));
            hpl.setWrapText(true);
            hpl.setMaxWidth(BOX_WIDTH);
            hpl.setPadding(new Insets(0, 0, 0, 4));

            //Add Description (Label)
            Label description = new Label(escapeHTML(feedArray.get(i).getDescription()));
            description.setWrapText(true);
            description.setMaxWidth(BOX_WIDTH);
            description.setPadding(new Insets(0, 0, 0, 6));

            //Add Image
            try {
                String urlString = scanDescription(feedArray.get(i).getDescription());
                if (urlString != null) {
                    URL url = new URL(urlString);
                    BufferedImage bf;
                    try {
                        bf = ImageIO.read(url);
                    } catch (Exception ex) {
                        addCard(description, hpl, null, feedArray.get(i).getLink());
                        continue;
                    }
                    WritableImage wr = convertImg(bf);
                    ImageView img = new ImageView(wr);

                    img.setFitWidth(IMAGE_WIDTH);
                    img.setFitHeight(wr.getHeight() / (wr.getWidth() / (IMAGE_WIDTH)));
                    img.setImage(wr);

                    //Add article to list WITH image
                    Platform.runLater(()-> addCard(description, hpl, img, feedArray.get(eye).getLink()));
                } else {
                    //Add article to list WITHOUT image
                    Platform.runLater(()-> addCard(description, hpl, null, feedArray.get(eye).getLink()));
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            onScreenMessages.add(0,feedArray.get(i));
        }
    }

    //Normal news article
    private void addCard(Label description, Hyperlink hyperlink, ImageView image, String link) {
        hyperlink.getStyleClass().add("roboto");
        description.getStyleClass().add("roboto");
        hyperlink.setOnAction((event) -> {
            try {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().browse(new URI(link));
                }
            } catch (URISyntaxException e) {
                System.out.println("URI Syntax Exception!");
                System.out.println("---------------------");
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("I/O Exception in News!");
                System.out.println("----------------------");
                e.printStackTrace();
            }
        });

        VBox textVBox;
        VBox newsCard;
        if (image == null) {
            textVBox = new VBox(hyperlink, description);
            textVBox.getStyleClass().setAll("text-padding");
            newsCard = new VBox(textVBox);
        } else {
            textVBox = new VBox(hyperlink, description);
            textVBox.getStyleClass().setAll("text-padding");
            newsCard = new VBox(image,textVBox);
        }
        newsCard.setAlignment(Pos.TOP_CENTER);
        newsCard.setPrefWidth(BOX_WIDTH);
        newsCard.getStyleClass().setAll("news-card");
        VBox.setMargin(newsCard, new Insets(10, 10, 10, 10));
//        Platform.runLater(()-> cardView.getChildren().add(newsCard));
        cardView.getChildren().add(newsCard);
    }

    //Normal news article for refresh
//    private void addCard(Label description, Hyperlink hyperlink, ImageView image, boolean isRefresh) {
//        hyperlink.getStyleClass().add("roboto");
//        description.getStyleClass().add("roboto");
//        VBox textVBox;
//        VBox newsCard;
//        if (image == null) {
//            textVBox = new VBox(hyperlink, description);
//            textVBox.getStyleClass().setAll("text-padding");
//            newsCard = new VBox(textVBox);
//        } else {
//            textVBox = new VBox(hyperlink, description);
//            textVBox.getStyleClass().setAll("text-padding");
//            newsCard = new VBox(image, textVBox);
//        }
//        newsCard.setAlignment(Pos.TOP_CENTER);
////        description.setTextAlignment(TextAlignment.JUSTIFY);
//        newsCard.setPrefWidth(BOX_WIDTH);
//        newsCard.getStyleClass().setAll("news-card");
//        VBox.setMargin(newsCard, new Insets(10, 10, 10, 10));
//        Platform.runLater(() -> cardView.getChildren().add(0, newsCard));
////        cardView.getChildren().add(0,newsCard);
//    }

    //T-Rex :)
    private void addCard(Pane pane) {
        HBox hBox;
        hBox = new HBox(pane);
        hBox.setAlignment(Pos.CENTER);
        hBox.setPrefWidth(BOX_WIDTH);
        hBox.getStyleClass().setAll("news-card");
        VBox.setMargin(hBox, new Insets(10, 10, 10, 10));
//        Platform.runLater(()-> cardView.getChildren().add(hBox));
        cardView.getChildren().add(hBox);
    }

    //This one adds the label first - for offline
    private void addCard(Hyperlink hyperlink, Label description) {
        hyperlink.getStyleClass().add("roboto");
        description.getStyleClass().add("roboto");

        VBox vBox = new VBox(description, hyperlink);
        vBox.setAlignment(Pos.TOP_CENTER);
        vBox.setPrefWidth(BOX_WIDTH);
        vBox.getStyleClass().setAll("offline-news-card");

        VBox.setMargin(vBox, new Insets(10, 10, 10, 10));
        vBox.setPadding(new Insets(10));
//        Platform.runLater(()-> cardView.getChildren().add(vBox));
        cardView.getChildren().add(vBox);
    }

    private WritableImage convertImg(BufferedImage bf) {
        WritableImage wr = null;
        if (bf != null) {
            wr = new WritableImage(bf.getWidth(), bf.getHeight());
            PixelWriter pw = wr.getPixelWriter();
            for (int x = 0; x < bf.getWidth(); x++) {
                for (int y = 0; y < bf.getHeight(); y++) {
                    pw.setArgb(x, y, bf.getRGB(x, y));
                }
            }
        }
        return wr;
    }

    private String escapeHTML(String string) {
        return Jsoup.parse(string).text();
    }

    private String scanDescription(String content) {
        String link;
        if (content.contains("src")) {
            content = content.substring(content.indexOf("src=") + 5, content.length());
            link = content.substring(0, content.indexOf("\""));
            return link;
        } else {
            return null;
        }
    }

    public VBox getCardView() {
        return cardView;
    }


}

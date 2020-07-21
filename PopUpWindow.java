package sample;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import static javafx.scene.paint.Color.*;

public class PopUpWindow extends Thread {
    private Stage stage2;

    public PopUpWindow(PlayingField playingField){
       //Platform.runLater(()->{
            stage2 = new Stage();
            HBox hBox = new HBox();
            BorderPane borderPane = new BorderPane();
            borderPane.setTop(new Label("Click on the color you want!"));
            Button red = new Button();
            red.setStyle("-fx-background-color: #ff0000");  //red
            red.setPrefSize(50, 50);
            Button yellow = new Button();
            yellow.setStyle("-fx-background-color: #FFFF00"); //yellow
            yellow.setPrefSize(50, 50);
            Button blue = new Button();
            blue.setStyle("-fx-background-color: #0000FF"); //blue
            blue.setPrefSize(50, 50);
            Button green = new Button();
            green.setStyle("-fx-background-color: #008000"); //green
            green.setPrefSize(50, 50);

            //event handling
            red.setOnMouseClicked(mouseEvent -> {
                //rectangle aka topColor is index 0
                //label aka cardtext is index 1
                ModelGUI.updateDiscardPileStackPane(RED, "RED");
                stage2.close();
                playingField.setBlackCardPlayed(0);
            });
            yellow.setOnMouseClicked(mouseEvent -> {
                ModelGUI.updateDiscardPileStackPane(YELLOW, "YELLOW");
                stage2.close();
                playingField.setBlackCardPlayed(0);
            });
            green.setOnMouseClicked(mouseEvent -> {
                ModelGUI.updateDiscardPileStackPane(GREEN, "GREEN");
                stage2.close();
                playingField.setBlackCardPlayed(0);
            });
            blue.setOnMouseClicked(mouseEvent -> {
                ModelGUI.updateDiscardPileStackPane(BLUE, "BLUE");
                stage2.close();
                playingField.setBlackCardPlayed(0);
            });

            //prevent stage2 from being closed if the RED X button is clicked!
            Platform.runLater(() -> {
                stage2.setOnCloseRequest(evt -> {
                    // prevent window from closing
                    evt.consume();
                });
            });

            hBox.getChildren().addAll(red, yellow, blue, green);
            borderPane.setCenter(hBox);
            stage2.setScene(new Scene(borderPane));
        //});
    }

    @Override
    public void run() {
        //keep the platform.runlater!!!
        Platform.runLater(()-> stage2.show());
    }
}

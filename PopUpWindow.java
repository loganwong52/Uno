package sample;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.concurrent.ConcurrentLinkedQueue;

import static javafx.scene.paint.Color.*;

public class PopUpWindow extends Thread {
    private Stage stage2;
    private PlayingField playingField;

    public PopUpWindow(PlayingField pF){
        stage2 = new Stage();
        playingField = pF;
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
            ModelGUI.updateDiscardPileStackPane(RED, "RED");
            //playingField.setBlackCardPlayed(0);
            ModelGUI.turnOrder.peek().setMyTurn(false);
            playingField.prepNextPlayer();  //P1 made their move.
            synchronized (ModelGUI.turnOrder.peek()){
                try {
                    ModelGUI.turnOrder.peek().wait();      //now, player 1 waits for playingField to update the turnOrder
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            stage2.close();
        });

        yellow.setOnMouseClicked(mouseEvent -> {
            ModelGUI.updateDiscardPileStackPane(YELLOW, "YELLOW");
            //playingField.setBlackCardPlayed(0);
            ModelGUI.turnOrder.peek().setMyTurn(false);
            playingField.prepNextPlayer();  //P1 made their move.
            synchronized (ModelGUI.turnOrder.peek()){
                try {
                    ModelGUI.turnOrder.peek().wait();      //now, player 1 waits for playingField to update the turnOrder
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            stage2.close();
        });

        green.setOnMouseClicked(mouseEvent -> {
            ModelGUI.updateDiscardPileStackPane(GREEN, "GREEN");
            //playingField.setBlackCardPlayed(0);
            ModelGUI.turnOrder.peek().setMyTurn(false);
            playingField.prepNextPlayer();  //P1 made their move.
            synchronized (ModelGUI.turnOrder.peek()){
                try {
                    ModelGUI.turnOrder.peek().wait();      //now, player 1 waits for playingField to update the turnOrder
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            stage2.close();
        });

        blue.setOnMouseClicked(mouseEvent -> {
            ModelGUI.updateDiscardPileStackPane(BLUE, "BLUE");
            //playingField.setBlackCardPlayed(0);
            ModelGUI.turnOrder.peek().setMyTurn(false);
            playingField.prepNextPlayer();  //P1 made their move.
            synchronized (ModelGUI.turnOrder.peek()){
                try {
                    ModelGUI.turnOrder.peek().wait();      //now, player 1 waits for playingField to update the turnOrder
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            stage2.close();
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
    }

    @Override
    public void run() {
        //keep the platform.runlater!!!
        Platform.runLater(()-> {
            stage2.show();
            //add this later: if playerNumber not equal to 1, then disable buttons; only AI can "fire" them.

            //Pause the current turn player's thread while they choose a color
            try {
                ModelGUI.turnOrder.peek().wait();
            } catch (InterruptedException | IllegalMonitorStateException e) {
                System.out.println("illegal monitor state exception occurred, but ignored");
            }
            if(ModelGUI.turnOrder.peek().getHand().getSize() == 0){
                synchronized (this) {
                    /**the other threads are waiting for it to be THEIR turn,
                     * so tell them to stop, and then they'll see that
                     * the turnPlayer has 0 cards in hand, so they leave this
                     * method and exit their while loop in Player. Then they
                     * print their win/lose messages.
                     */
                    notifyAll();
                }
            }
        });
    }
}

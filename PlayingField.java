package sample;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.ObjectInputStream;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;

import static javafx.scene.paint.Color.*;
import static javafx.scene.paint.Color.BLUE;

public class PlayingField{
    //the critical region AKA the monitor

    private int numOfPlayers;
    private Player player1;
    private Player player2;
    private Player player3;
    private Player player4;
    private boolean ready;
    private int blackCardPlayed;
    private Stage stage2;

    public PlayingField(int players, Player p1, Player p2, Player p3, Player p4){
        numOfPlayers = players;
        ready = false;
        player1 = p1;
        player2 = p2;
        if(numOfPlayers >=3){
            player3 = p3;
        }
        if(players == 4){
            player4 = p4;
        }
        ready = false;
        blackCardPlayed = 0;
    }

    public synchronized void enableCards(Player turnPlayer) {
        System.out.println("-------------------");
        System.out.println("Turn Player: p-l-a-y-e-r #" + turnPlayer.getPlayerNumber());
        turnPlayer.printPlayer();
        /**for (Player p : ModelGUI.turnOrder) {
            System.out.println("Player #" + p.getPlayerNumber());
        }*/
        System.out.println("turnOrder.peek: Player #" + ModelGUI.turnOrder.peek().getPlayerNumber());
        System.out.println("It is the TOP player in turnOrder's turn:   " + ModelGUI.turnOrder.peek().getTurn());
        System.out.println(ready);
        while (blackCardPlayed != 1 &&
                (!ModelGUI.turnOrder.peek().equals(turnPlayer) || ModelGUI.turnOrder.peek().getTurn() == false || ready == false) ) {
            try {
                wait();     //players are waiting for it to be THEIR turn
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        ready = false;
        //turnPlayer.getHandGrid().setClickable(true);
        //System.out.println("turn player can click their buttons.   " + turnPlayer.getHandGrid().isClickable());
        turnPlayer.getHandGrid().enableAll(turnPlayer);
        try {
            wait();     //waiting for the player to click a button.
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        System.out.println("Is a black card played? :  " + turnPlayer.getHand().getBlackCardPlayed());
        if(turnPlayer.getHand().getBlackCardPlayed() == 1){
            blackCardPlayed = 1;
            System.out.println("Player is choosing a card: ");
            for(Player p : ModelGUI.turnOrder){
                if(!p.equals(turnPlayer)){
                    try{
                        synchronized (p){
                            p.wait();       //make the non-turnPlayer threads wait until color is picked
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            /*Platform.runLater(()->{
                Stage stage2 = new Stage();
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
                red.setOnMouseClicked(mouseEvent2 -> {
                    //rectangle aka topColor is index 0
                    //label aka cardtext is index 1
                    ModelGUI.updateDiscardPileStackPane(RED, "RED");
                    stage2.close();
                    this.setBlackCardPlayed(0);
                    ModelGUI.turnOrder.remove();
                    ModelGUI.turnOrder.add(turnPlayer);  //re-add turnplayer to the end of the Queue
                    System.out.println("CHECKING of the turnOrder: ");
                    for (Player p : ModelGUI.turnOrder) {
                        System.out.println("Player #" + p.getPlayerNumber());
                    }
                    ModelGUI.turnOrder.peek().setMyTurn(true);
                    System.out.println("Next player: Player #" + ModelGUI.turnOrder.peek().getPlayerNumber());
                    System.out.println("Is it Player " + ModelGUI.turnOrder.peek().getPlayerNumber() + "'s turn?   " + ModelGUI.turnOrder.peek().getTurn());
                    //turnPlayer.stopWaiting();    //this is to tell ModelGUI that the turnOrder has finished updating.
                    synchronized (this) {
                        notifyAll();
                    }
                });
                yellow.setOnMouseClicked(mouseEvent2 -> {
                    ModelGUI.updateDiscardPileStackPane(YELLOW, "YELLOW");
                    stage2.close();
                    this.setBlackCardPlayed(0);
                    ModelGUI.turnOrder.remove();
                    ModelGUI.turnOrder.add(turnPlayer);  //re-add turnplayer to the end of the Queue
                    System.out.println("CHECKING of the turnOrder: ");
                    for (Player p : ModelGUI.turnOrder) {
                        System.out.println("Player #" + p.getPlayerNumber());
                    }
                    ModelGUI.turnOrder.peek().setMyTurn(true);
                    System.out.println("Next player: Player #" + ModelGUI.turnOrder.peek().getPlayerNumber());
                    System.out.println("Is it Player " + ModelGUI.turnOrder.peek().getPlayerNumber() + "'s turn?   " + ModelGUI.turnOrder.peek().getTurn());
                    //turnPlayer.stopWaiting();    //this is to tell ModelGUI that the turnOrder has finished updating.
                    synchronized (this) {
                        notifyAll();
                    }
                });
                green.setOnMouseClicked(mouseEvent2 -> {
                    ModelGUI.updateDiscardPileStackPane(GREEN, "GREEN");
                    stage2.close();
                    this.setBlackCardPlayed(0);
                    ModelGUI.turnOrder.remove();
                    ModelGUI.turnOrder.add(turnPlayer);  //re-add turnplayer to the end of the Queue
                    System.out.println("CHECKING of the turnOrder: ");
                    for (Player p : ModelGUI.turnOrder) {
                        System.out.println("Player #" + p.getPlayerNumber());
                    }
                    ModelGUI.turnOrder.peek().setMyTurn(true);
                    System.out.println("Next player: Player #" + ModelGUI.turnOrder.peek().getPlayerNumber());
                    System.out.println("Is it Player " + ModelGUI.turnOrder.peek().getPlayerNumber() + "'s turn?   " + ModelGUI.turnOrder.peek().getTurn());
                    //turnPlayer.stopWaiting();    //this is to tell ModelGUI that the turnOrder has finished updating.
                    synchronized (this) {
                        notifyAll();
                    }
                });
                blue.setOnMouseClicked(mouseEvent2 -> {
                    ModelGUI.updateDiscardPileStackPane(BLUE, "BLUE");
                    stage2.close();
                    this.setBlackCardPlayed(0);
                    ModelGUI.turnOrder.remove();
                    ModelGUI.turnOrder.add(turnPlayer);  //re-add turnplayer to the end of the Queue
                    System.out.println("CHECKING of the turnOrder: ");
                    for (Player p : ModelGUI.turnOrder) {
                        System.out.println("Player #" + p.getPlayerNumber());
                    }
                    ModelGUI.turnOrder.peek().setMyTurn(true);
                    System.out.println("Next player: Player #" + ModelGUI.turnOrder.peek().getPlayerNumber());
                    System.out.println("Is it Player " + ModelGUI.turnOrder.peek().getPlayerNumber() + "'s turn?   " + ModelGUI.turnOrder.peek().getTurn());
                    //turnPlayer.stopWaiting();    //this is to tell ModelGUI that the turnOrder has finished updating.
                    synchronized (this) {
                        notifyAll();
                    }
                });

                //prevent stage2 from being closed if the RED X button is clicked!
                Platform.runLater(() -> stage2.setOnCloseRequest(evt -> {
                    evt.consume();
                }));

                hBox.getChildren().addAll(red, yellow, blue, green);
                borderPane.setCenter(hBox);
                stage2.setScene(new Scene(borderPane));
                stage2.show();

            });*/
            turnPlayer.getHand().setBlackCardPlayed(0);     //ends the while looop in ModelGUI in p1's button event handler
        }else{
            ModelGUI.turnOrder.remove();
            ModelGUI.turnOrder.add(turnPlayer);  //re-add turnplayer to the end of the Queue
            System.out.println("CHECKING of the turnOrder: ");
            for (Player p : ModelGUI.turnOrder) {
                System.out.println("Player #" + p.getPlayerNumber());
            }
            ModelGUI.turnOrder.peek().setMyTurn(true);
            System.out.println("Next player: Player #" + ModelGUI.turnOrder.peek().getPlayerNumber());
            System.out.println("Is it Player " + ModelGUI.turnOrder.peek().getPlayerNumber() + "'s turn?   " + ModelGUI.turnOrder.peek().getTurn());
            turnPlayer.stopWaiting();    //this is to tell ModelGUI that the turnOrder has finished updating.
        }
    }

    public synchronized void prepNextPlayer(){
        notifyAll();
        ready = true;
    }

    public void setBlackCardPlayed(int tF) {
        //1 is true;
        //0 is false;
        blackCardPlayed = tF;
    }

    public int getBlackCardPlayed() {
        return blackCardPlayed;
    }
}

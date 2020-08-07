package sample;

import javafx.animation.Animation;
import javafx.animation.FillTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Player extends Thread{
    private Hand hand;
    private int playerNumber;
    private boolean myTurn;
    private boolean lost;
    private HandGrid handGrid;
    private Player nextPlayer;
    private PlayingField playingField;

    public Player(Hand h, int num){
        hand = h;
        playerNumber = num;
        myTurn = false;
        lost = false;
    }

    public Hand getHand() {
        return hand;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public int getNextPlayerNumber(){
        return nextPlayer.getPlayerNumber();
    }

    public void setMyTurn(boolean tf){
        myTurn = tf;
    }

    public boolean getTurn() {
        return myTurn;
    }

    public void setHandGrid(HandGrid h) {
        this.handGrid = h;
    }

    public HandGrid getHandGrid() {
        return handGrid;
    }

    public void setNextPlayer(Player next) {
        nextPlayer = next;
    }

    public Player getNextPlayer() {
        return nextPlayer;
    }

    public void printPlayer(){
        System.out.println("I am the turn Player: PLAYER #" + playerNumber);
    }

    public void setPlayingField(PlayingField p){
        playingField = p;
    }

    public void run() {
        //System.out.println("Player " + playerNumber + "'s thread has started!");
        while(hand.getSize() > 0 && !lost){
                playingField.enableCards(ModelGUI.turnOrder.peek());
        }
        stopWaiting();
        //System.out.println("Player #" + playerNumber + " has exited the while loop.");
        if(hand.getSize() == 0){
            System.out.println("PLAYER " + playerNumber + " has won the game!");
            Platform.runLater(()-> {
                Label winner = new Label();
                Label youLost = new Label("YOU LOST!");
                Stage victoryWindow = new Stage();
                Button okay = new Button("OK");
                okay.setOnMouseClicked(mouseEvent->{
                    victoryWindow.close();
                    ModelGUI.endGame();
                });
                VBox vBox = new VBox();
                StackPane stack = new StackPane();
                Rectangle flash = new Rectangle(200, 100);      //length, height
                flash.setFill(Color.WHITE);

                if(playerNumber != 1){
                    winner.setText("PLAYER " + playerNumber + " won the game!");
                    vBox.getChildren().addAll(winner, youLost, okay);
                    vBox.setAlignment(Pos.CENTER);
                    victoryWindow.setScene(new Scene(vBox));
                }else{
                    winner.setText(ModelGUI.getName() + " won the game!");
                    vBox.getChildren().addAll(winner, okay);
                    vBox.setAlignment(Pos.CENTER);

                    stack.getChildren().addAll(flash, vBox);
                    stack.setAlignment(Pos.CENTER);
                    victoryWindow.setScene(new Scene(stack));
                }
                //a winning animation:
                FillTransition fillTransition = new FillTransition();
                fillTransition.setDuration(Duration.seconds(1));
                fillTransition.setShape(flash);
                fillTransition.setFromValue(Color.WHITE);
                fillTransition.setToValue(Color.TRANSPARENT);
                fillTransition.setAutoReverse(true);
                fillTransition.setCycleCount(Animation.INDEFINITE);
                fillTransition.play();

                victoryWindow.show();
            });
        }else{
            System.out.println("PLAYER " + playerNumber + " has lost the game!");
        }
    }

    public boolean hasLost() {
        return lost;
    }

    public void setLost(boolean l) {
        lost = l;
    }

    public void stopWaiting(){
        synchronized (this){
            notifyAll();
        }
    }
}

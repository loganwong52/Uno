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

/**
 * A Player has a hand and a playerNumber: 1,2,3, or 4.
 * A player hasn't lost until lost becomes true.
 * The variable lost becomes true when one player's
 * hand reaches zero cards, and then in the
 * playingField, the other player's lost variable
 * becomes true.
 * A player keeps track of who the next player is and if
 * it's their turn. A player enters the critical region
 * in the PlayingField class via the enableCards() method.
 * If it's not their turn, they wait for it to be their turn.
 *
 * @author Logan Wong
 */
public class Player extends Thread{
    private Hand hand;
    private int playerNumber;
    private boolean myTurn;
    private boolean lost;
    private HandGrid handGrid;
    private Player nextPlayer;
    private PlayingField playingField;

    /**
     * Initializes the fields
     * @param h  a player's hand
     * @param num  the player's number (1, 2, 3 or 4)
     */
    public Player(Hand h, int num){
        hand = h;
        playerNumber = num;
        myTurn = false;
        lost = false;
    }

    /**
     * Gets a player's hand
     * @return  hand
     */
    public Hand getHand() {
        return hand;
    }

    /**
     * Gets a player's number
     * @return  playerNumber
     */
    public int getPlayerNumber() {
        return playerNumber;
    }

    /**
     * Gets the next player's number
     * @return  the next player's player number
     */
    public int getNextPlayerNumber(){
        return nextPlayer.getPlayerNumber();
    }

    /**
     * Sets the player's myTurn value to true or false
     * @param tf  true or false
     */
    public void setMyTurn(boolean tf){
        myTurn = tf;
    }

    /**
     * Checks if it's the player's turn now.
     * @return  true if it's the player's turn, false otherwise
     */
    public boolean getTurn() {
        return myTurn;
    }

    /**
     * Assigns a player's handGrid
     * @param h  the handGrid that shall become this player's handGrid
     */
    public void setHandGrid(HandGrid h) {
        this.handGrid = h;
    }

    /**
     * Gets a player's handGrid
     * @return  handGrid
     */
    public HandGrid getHandGrid() {
        return handGrid;
    }

    /**
     * Sets the player's nextPlayer to the player whose turn is
     * after this player's turn.
     * @param next  the next Player
     */
    public void setNextPlayer(Player next) {
        nextPlayer = next;
    }

    /**
     * Gets the player who is going to go after this player
     * @return  the nextPlayer
     */
    public Player getNextPlayer() {
        return nextPlayer;
    }

    /**
     * Prints a message to confirm that the playerNumber is correct.
     */
    public void printPlayer(){
        System.out.println("I am the turn Player: PLAYER #" + playerNumber);
    }

    /**
     * Assigns the playingField field to the main playingField, which is
     * the critical region for all the player threads.
     * @param p  the main playingField
     */
    public void setPlayingField(PlayingField p){
        playingField = p;
    }

    /**
     * While the player's hand size is greater than 0 and they
     * haven'y lost, they enter the critical region and wait
     * for it to be their turn, and once it's their turn,
     * they play the game. But once a player reaches zero
     * cards, all the other players' lose and they
     * exit the while loop and print out a losing message.
     * The winner prints out a win message and a small
     * window appears saying that they won. If player 1
     * won, the window will flash.
     */
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

    /**
     * Sets a player's lost field to true or false
     * @param l  true or false
     */
    public void setLost(boolean l) {
        lost = l;
    }

    /**
     * I'm not exactly sure, but I think
     * it tells all waiting player threads to stop waiting.
     */
    public void stopWaiting(){
        synchronized (this){
            notifyAll();
        }
    }
}
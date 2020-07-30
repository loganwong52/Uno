package sample;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

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

    public PlayingField getPlayingField(){
        return playingField;
    }

    public void run() {
        System.out.println("Player " + playerNumber + "'s thread has started!");
        while(hand.getSize() > 0 && !lost){
                playingField.enableCards(ModelGUI.turnOrder.peek());
        }
        synchronized (this){
            notifyAll();        //I dunno... get all waiting threads to stop waiting?
        }
        System.out.println("Player #" + playerNumber + " has exited the while loop.");
        if(hand.getSize() == 0){
            System.out.println("PLAYER " + playerNumber + " has won the game!");
            Platform.runLater(()-> {
                Label winner = new Label();
                Stage victoryWindow = new Stage();
                if(playerNumber != 1){
                    winner.setText("PLAYER " + playerNumber + " won the game!\nYOU LOST!");
                }else{
                    winner.setText("PLAYER " + playerNumber + " won the game!");        //basically player 1, you, won the game
                }
                victoryWindow.setScene(new Scene(winner));
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

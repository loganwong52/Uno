package sample;

import javafx.scene.layout.GridPane;

import java.io.*;


public class Player extends Thread{
    private Hand hand;
    private int playerNumber;
    private boolean myTurn;
    private boolean won;
    private HandGrid handGrid;
    private Player nextPlayer;
    private PlayingField playingField;

    public Player(Hand h, int num){
        hand = h;
        playerNumber = num;
        myTurn = false;
        won = false;
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
        while(hand.getSize() > 0 && !won){
                playingField.enableCards(ModelGUI.turnOrder.peek());
        }

        if(won) {
            System.out.println("PLAYER " + playerNumber + " won the game!");
        }else{
            System.out.println("You Lost!");
        }
    }

    public void stopWaiting(){
        synchronized (this){
            notifyAll();
        }
    }
}

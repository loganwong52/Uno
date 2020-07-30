package sample;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.ObjectInputStream;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;

import static javafx.scene.paint.Color.*;
import static javafx.scene.paint.Color.BLUE;

public class PlayingField{
    //the critical region AKA the monitor

    private int numOfPlayers;
    private boolean ready;
    //private int blackCardPlayed;
    private Color topColor;
    private int topNum;
    private boolean skip1Player;
    private boolean invalidCardDrawn;
    private boolean previousPlayerHasUno;

    public PlayingField(int players){//, Color color, int n){//Player p1, Player p2, Player p3, Player p4){
        numOfPlayers = players;
        ready = false;
        //blackCardPlayed = 0;
        topColor = RED;
        topNum = 52;
        skip1Player = false;
        invalidCardDrawn = false;
        previousPlayerHasUno = false;
    }

    public void updateColor(Color c){
        topColor = c;
    }
    public void updateNum(int n){
        topNum = n;
    }

    public int getTopNum() {
        return topNum;
    }
    public Color getTopColor() {
        return topColor;
    }

    public void setSkip1Player(boolean skip1Player) {
        this.skip1Player = skip1Player;
    }
    public void setInvalidCardDrawn(boolean tf) {
        invalidCardDrawn = tf;
    }
    public void setPreviousPlayerHasUno(boolean tf){previousPlayerHasUno = tf;}

    public synchronized void enableCards(Player turnPlayer) {
        System.out.println("-------------------");
        System.out.println("Turn Player: p-l-a-y-e-r #" + turnPlayer.getPlayerNumber());
        while ( (!ModelGUI.turnOrder.peek().equals(turnPlayer) || ModelGUI.turnOrder.peek().getTurn() == false || ready == false) ) {
            try {
                wait();     //players are waiting for it to be THEIR turn
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(turnPlayer.getHand().getSize() == 0){
                return;
            }
        }
        if(previousPlayerHasUno){
            try{
                turnPlayer.sleep(3000);
                /*to prevent turnPlayer from going before previous
                player presses the UNO! button (or forgets to press it)*/
            }catch(InterruptedException e){
                e.printStackTrace();
            }
            previousPlayerHasUno = false;
        }else{
            try{
                turnPlayer.sleep(500);      //to allow the HUMAN player to not be overwhelmed by speed.
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
        ready = false;
        turnPlayer.getHandGrid().enableAll(turnPlayer);

        //If the player doesn't have any valid cards, they get to draw 1 card.
        if(!topColor.equals(BLACK) && ModelGUI.turnOrder.peek().getHand().needToDraw(topColor, topNum)){
            ModelGUI.setUnoDeckButton(false);
            ModelGUI.unoDeckButtonAction(ModelGUI.turnOrder.peek());
        }
        try {
            wait();     //waiting for the player to click a button.
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        /**if the turn player had no valid cards, they drew 1 card
         * if that card they drew was also invalid, then their turn is
         * over. So to avoid accidentally activating the card that
         * was played LAST turn AGAIN, there's an if-statement.
         */
        if(invalidCardDrawn == false) {
            if (turnPlayer.getHand().getBlackCardPlayed() == 1) {
                //blackCardPlayed = 1;
                turnPlayer.getHand().setBlackCardPlayed(0);     //ends the while loop in ModelGUI in p1's button event handler
            }
            if (turnPlayer.getHand().getSize() != 0) {    //if player hasn't won yet
                //Update the turnOrder.
                if (numOfPlayers > 2 && topNum == 11) {       //REVERSE
                    Stack<Player> tempHolderStack = new Stack<>();
                    for (int i = 0; i < numOfPlayers; ++i) {
                        tempHolderStack.push(ModelGUI.turnOrder.remove());
                    }
                    ModelGUI.turnOrder.clear();
                    for (int i = 0; i < numOfPlayers; ++i) {
                        ModelGUI.turnOrder.add(tempHolderStack.pop());
                    }
                    ModelGUI.reverseTurnOrder();

                } else {
                    ModelGUI.turnOrder.remove();
                    ModelGUI.turnOrder.add(turnPlayer);
                }
                if ((skip1Player && topNum == 10) || (numOfPlayers == 2 && topNum == 11)) {       //SKIP
                    Player next = ModelGUI.turnOrder.remove();
                    ModelGUI.turnOrder.add(next);
                    skip1Player = false;
                }

                System.out.println("CHECKING of the turnOrder: ");
                for (Player p : ModelGUI.turnOrder) {
                    System.out.println("Player #" + p.getPlayerNumber());
                }
                ModelGUI.turnOrder.peek().setMyTurn(true);
                //System.out.println("Next player: Player #" + ModelGUI.turnOrder.peek().getPlayerNumber());
                //System.out.println("Is it Player " + ModelGUI.turnOrder.peek().getPlayerNumber() + "'s turn?   " + ModelGUI.turnOrder.peek().getTurn());
                turnPlayer.stopWaiting();    //this is to tell ModelGUI that the turnOrder has finished updating.
            } else {
                System.out.println("Player #" + turnPlayer.getPlayerNumber() + " has reached 0 cards!!!");
                for (Player p : ModelGUI.turnOrder) {
                    p.setLost(true);
                }
            /*synchronized (this) {
                /**the other threads are waiting for it to be THEIR turn,
                 * so tell them to stop, and then they'll see that
                 * the turnPlayer has 0 cards in hand, so they leave this
                 * method and exit their while loop in Player. Then they
                 * print their win/lose messages.
                 */
                //notifyAll();
                //}
                this.prepNextPlayer();
                turnPlayer.stopWaiting();
            }
        }else{
            invalidCardDrawn = false;       //reset the boolean
            ModelGUI.turnOrder.remove();
            ModelGUI.turnOrder.add(turnPlayer);
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

    /*public void setBlackCardPlayed(int tF) {
        //1 is true;
        //0 is false;
        blackCardPlayed = tF;
    }

    public int getBlackCardPlayed() {
        return blackCardPlayed;
    }*/
}

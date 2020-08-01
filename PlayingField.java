package sample;

import javafx.scene.paint.Color;
import java.util.Stack;
import static javafx.scene.paint.Color.*;

public class PlayingField{
    //the critical region AKA the monitor
    private int numOfPlayers;
    private boolean ready;
    private Color topColor;
    private int topNum;
    private boolean skip1Player;
    private boolean invalidCardDrawn;
    private boolean previousPlayerHasUno;
    private boolean firstTopCardIsBlack;

    public PlayingField(int players){
        numOfPlayers = players;
        ready = false;
        topColor = RED;     //default value
        topNum = 52;        //default value
        skip1Player = false;
        invalidCardDrawn = false;
        previousPlayerHasUno = false;
        firstTopCardIsBlack = false;
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
    public void setFirstTopCardIsBlack(boolean t) {
        firstTopCardIsBlack = t;
    }

    /**
     * The critical region for all the Player Threads.
     * It makes them wait when it isn't their turn.
     * It updates the turn order. If a special card is
     * played, it activates their effects. It checks if
     * the turn player needs to draw a card. It checks
     * if they have UNO. It checks if a player has won yet.
     * @param turnPlayer
     */
    public synchronized void enableCards(Player turnPlayer) {
        while ( (!ModelGUI.turnOrder.peek().equals(turnPlayer) || ModelGUI.turnOrder.peek().getTurn() == false || ready == false) ) {
            try {
                wait();     //players are waiting for it to be THEIR turn
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally{
                if(ModelGUI.turnOrder.peek().getHand().getSize() == 0){
                    return;
                }
            }
        }
        System.out.println("-------------------");
        System.out.println("Turn Player: player #" + turnPlayer.getPlayerNumber());
        if(previousPlayerHasUno){
            try{
                turnPlayer.sleep(2510);
                /*to prevent turnPlayer from going before previous
                player presses the UNO! button (or forgets to press it)*/
            }catch(InterruptedException e){
                e.printStackTrace();
            }
            previousPlayerHasUno = false;
        }
        else{
            try{
                turnPlayer.sleep(250);      //to prevent the HUMAN player from being overwhelmed by speed.
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
        ready = false;
        if(!firstTopCardIsBlack) {
            turnPlayer.getHandGrid().enableAll(turnPlayer);
        }
        //If the player doesn't have any valid cards, they get to draw 1 card.
        if(!topColor.equals(BLACK) && ModelGUI.turnOrder.peek().getHand().needToDraw(topColor, topNum)){
            ModelGUI.setUnoDeckButton(false);
            ModelGUI.unoDeckButtonAction(ModelGUI.turnOrder.peek());
        }
        ModelGUI.turnOrder.peek().getHand().canPlayDraw4(topColor, topNum);         //this checks if the turnPlayer can play a Draw 4

        try {
            /*waiting for the player to click a card-button in the hand OR
            in the case of a wild card, once the color is chosen.
            this stops waiting when prepNextPlayer() is called. */
            wait();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        /**If the turn player drew 1 card because their hand was invalid
         * and the card they drew was also invalid, their turn ends.
         * To avoid activating the card that was played LAST turn
         * AGAIN, the if-statement below is used.
         */
        if(invalidCardDrawn == false) {
            if (turnPlayer.getHand().getBlackCardPlayed() == 1) {
                turnPlayer.getHand().setBlackCardPlayed(0);
            }
            if (turnPlayer.getHand().getSize() != 0) {    //if player hasn't won yet
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
                    //Update the turnOrder.
                    ModelGUI.turnOrder.remove();
                    ModelGUI.turnOrder.add(turnPlayer);
                }
                if ((skip1Player && topNum == 10) ||
                        topNum == 100 ||
                        (numOfPlayers == 2 && topNum == 11 ) ||
                        (skip1Player && topNum == 12) ) {       //SKIP
                    /*skip the next player's turn IF a skip is played (10)
                      OR if the top card is a Wild Draw 4
                      OR if there are only 2 players and a reverse is played
                      OR if a "draw 2" is played.*/
                    Player next = ModelGUI.turnOrder.remove();
                    ModelGUI.turnOrder.add(next);
                    skip1Player = false;
                }

                System.out.println("CHECKING of the turnOrder: ");
                for (Player p : ModelGUI.turnOrder) {
                    System.out.println("Player #" + p.getPlayerNumber());
                }
                ModelGUI.turnOrder.peek().setMyTurn(true);
                turnPlayer.stopWaiting();    //tells ModelGUI that the turnOrder has finished updating.
            } else {
                System.out.println("Player #" + turnPlayer.getPlayerNumber() + " has reached 0 cards!!!");
                for (Player p : ModelGUI.turnOrder) {
                    if(p.getPlayerNumber() != turnPlayer.getPlayerNumber()) {
                        p.setLost(true);
                    }
                }
                synchronized (this) {       //make other threads stop waiting
                    notifyAll();
                }
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
            turnPlayer.stopWaiting();    //tells ModelGUI that the turnOrder has finished updating.
        }
    }

    public synchronized void prepNextPlayer(){
        notifyAll();
        ready = true;
    }
}
package sample;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import java.util.Random;
import java.util.Stack;
import static javafx.scene.paint.Color.*;

/**
 * This class is the critical region for the player threads.
 * It controls when a player has to wait for their turn.
 * It enables/disables a player's buttons. It checks
 * if the player needs to draw 1 card, it checks if a black
 * was played, and it notifies the other threads if a player
 * has won. MOST IMPORTANTLY OF ALL, this is where the
 * ACTION cards' effects go off. So if a skip, reverse, or
 * draw 2 card is played, their effects activate in the
 * this class.
 *
 * There is also a subclass that acts as the AI.
 *
 * @author Logan Wong
 */
public class PlayingField{
    private int numOfPlayers;
    private boolean ready;
    private Color topColor;
    private int topNum;
    private boolean skip1Player;
    private boolean invalidCardDrawn;
    private boolean previousPlayerHasUno;
    private boolean firstTopCardIsBlack;
    private boolean firstTopCardIsSpecial;

    /**
     * Initializes all the fields.
     * @param players  the total number of players
     */
    public PlayingField(int players){
        numOfPlayers = players;
        ready = false;
        topColor = RED;     //default value
        topNum = 52;        //default value
        skip1Player = false;
        invalidCardDrawn = false;
        previousPlayerHasUno = false;
        firstTopCardIsBlack = false;
        firstTopCardIsSpecial = false;
    }

    /**
     * Updates the topColor
     * @param c  the new color
     */
    public void updateColor(Color c){
        topColor = c;
    }

    /**
     * Updates the top number
     * @param n  the new number
     */
    public void updateNum(int n){
        topNum = n;
    }

    /**
     * Gets the top number
     * @return  topNum
     */
    public int getTopNum() {
        return topNum;
    }

    /**
     * Gets the top color
     * @return  topColor
     */
    public Color getTopColor() {
        return topColor;
    }

    /**
     * Tells playingField when to skip a player
     * @param skip1Player  a True/false value
     */
    public void setSkip1Player(boolean skip1Player) {
        this.skip1Player = skip1Player;
    }

    /**
     * Sets invalidCardDrawn true or false
     * @param tf  true or false
     */
    public void setInvalidCardDrawn(boolean tf) {
        invalidCardDrawn = tf;
    }

    /**
     * Sets the field "previousPlayerHasUno" to true or false
     * @param tf  true or false
     */
    public void setPreviousPlayerHasUno(boolean tf){previousPlayerHasUno = tf;}

    /**
     * Sets the field "firstTopCardIsBlack" to true or false.
     * @param t  true or false.
     */
    public void setFirstTopCardIsBlack(boolean t) {
        firstTopCardIsBlack = t;
    }

    /**
     * If the first card on the discard pile is a 10, 11, or 12,
     * firstTopCardIsSpecial becomes true. Else, false.
     * @param t  true or false
     */
    public void setFirstTopCardIsSpecial(boolean t){firstTopCardIsSpecial = t;}

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
        ModelGUI.redifyLabel(turnPlayer.getPlayerNumber());
        if(previousPlayerHasUno){
            try{
                turnPlayer.sleep(5200);
                /*to prevent turnPlayer from going before previous
                player presses the UNO! button (or forgets to press it)*/
            }catch(InterruptedException e){
                e.printStackTrace();
            }
            previousPlayerHasUno = false;
        }
        else{
            try{
                turnPlayer.sleep(1500);      //to prevent the HUMAN player from being overwhelmed by speed.
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
        ready = false;
        Platform.runLater(()->{
            ModelGUI.clearUnoTrackerLabel();
        });
        if(!firstTopCardIsBlack) {
            turnPlayer.getHandGrid().enableAll();
        }
        //If the player doesn't have any valid cards, they get to draw 1 card.
        if(!topColor.equals(BLACK) && ModelGUI.turnOrder.peek().getHand().needToDraw(topColor, topNum)){
            if(!firstTopCardIsSpecial) {        //when the firstTopCard ISN'T special
                ModelGUI.setUnoDeckButton(false);
                ModelGUI.unoDeckButtonAction(ModelGUI.turnOrder.peek());
                //in the unoDeckButtonAction method, once they draw a card, their hand is checked to see if they can play a draw 4 or not
                firstTopCardIsSpecial = false;
            }
        }else {
            ModelGUI.turnOrder.peek().getHand().canPlayDraw4(topColor, topNum);         //check if the turnPlayer can play a Draw 4
        }

        if(turnPlayer.getPlayerNumber() != 1){
            AiThread ai = new AiThread();
            ai.setTurnPlayer(turnPlayer);
            ai.start();

        }
        ModelGUI.updateLastPlayedValues(topColor, topNum);
        //no matter the player, make the player thread wait...?
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

                /*System.out.println("CHECKING of the turnOrder: ");
                for (Player p : ModelGUI.turnOrder) {
                    System.out.println("Player #" + p.getPlayerNumber());
                }*/
                ModelGUI.turnOrder.peek().setMyTurn(true);
                ModelGUI.blackifyLabel(turnPlayer.getPlayerNumber());
                turnPlayer.stopWaiting();    //tells ModelGUI that the turnOrder has finished updating.
            } else {
                System.out.println("Player #" + turnPlayer.getPlayerNumber() + " has reached 0 cards!!!");
                ModelGUI.blackifyLabel(turnPlayer.getPlayerNumber());
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
            /*System.out.println("CHECKING of the turnOrder: ");
            for (Player p : ModelGUI.turnOrder) {
                System.out.println("Player #" + p.getPlayerNumber());
            }*/
            ModelGUI.turnOrder.peek().setMyTurn(true);
            ModelGUI.blackifyLabel(turnPlayer.getPlayerNumber());
            turnPlayer.stopWaiting();    //tells ModelGUI that the turnOrder has finished updating.
        }
    }

    /**
     * Notifies all the waiting player threads
     * (in the while loop in enableCards)
     * and turns ready to true.
     */
    public synchronized void prepNextPlayer(){
        notifyAll();
        ready = true;
    }

    //------------------------------------------------------------------------------------------------------------------
    /**
     * This class acts as the AI players. When it's a player's
     * turn and they aren't player 1, this thread goes through
     * their hand and sees what can be played, and then plays
     * a valid card.
     *
     * @author Logan Wong
     */
    private class AiThread extends Thread{
        Player turnPlayer;
        public AiThread(){

        }

        /**
         * Assigns turnPlayer a value.
         * @param p  the player whose turn it is
         */
        public void setTurnPlayer(Player p){
            turnPlayer = p;
        }

        /**
         * Checks the AI player's hand to see if they have
         * a skip card that they can play.
         * @return  true if they have a skip card, false otherwise
         */
        private boolean hasValidSkip(){
            CardButton temp;
            for(Node card : turnPlayer.getHandGrid().getGridKids()) {
                temp = (CardButton)card;
                //card in hand is a 10 AND it's color matches the topColor
                if((temp.getNumber() == 10 && (temp.getColorColor().equals(topColor) || topNum == 10))){
                    return true;
                }
            }
            return false;
        }
        /**
         * Checks the AI player's hand to see if they have
         * a reverse card that they can play.
         * @return  true if they have a reverse card, false otherwise
         */
        private boolean hasValidReverse(){
            CardButton temp;
            for(Node card : turnPlayer.getHandGrid().getGridKids()) {
                temp = (CardButton)card;
                //card in hand is a 11 AND it's color matches the topColor
                if((temp.getNumber() == 11 && (temp.getColorColor().equals(topColor) || topNum == 11))){
                    return true;
                }
            }
            return false;
        }
        /**
         * Checks the AI player's hand to see if they have
         * a draw 2 card that they can play.
         * @return  true if they have a draw 2 card, false otherwise
         */
        private boolean hasValidDraw2(){
            CardButton temp;
            for(Node card : turnPlayer.getHandGrid().getGridKids()) {
                temp = (CardButton)card;
                //card in hand is a 12 AND it's color matches the topColor
                if((temp.getNumber() == 12 && (temp.getColorColor().equals(topColor) || topNum == 12))){
                    return true;
                }
            }
            return false;
        }

        /**
         * The AI player plays a card.
         * They might play a wild draw 4 illegally
         * Otherwise, they might try to sabatoge the next
         * player if the nextPlayer has 2 cards or less in
         * their hand.
         * The AI tries to save zeroes for last.
         * Otherwise, the AI loops through the hand
         * and plays the first valid card it sees.
         * @param nextPlayer
         */
        public void playACard(Player nextPlayer){
            Platform.runLater(()->{
                //int playerTracker = turnPlayer.getPlayerNumber();
                int validSameColor = 0;
                int validSameNum = 0;
                int totalValidNormalCards = 0;
                int wildCards = 0;
                int wildDrawFours = 0;
                //int validZeroes = 0;
                int validNonZeroes = 0;
                CardButton temp;

                /*Smart AI: sees that the next player is close to winning
                  so the AI tries to sabotage them (if possible)*/
                if(nextPlayer.getHand().getSize() <= 2){
                    CardButton card;
                    if(hasValidDraw2()){
                        for(Node c : turnPlayer.getHandGrid().getGridKids()) {
                            card = (CardButton)c;
                            //card in hand is a 12 AND it's color matches the topColor
                            if((card.getNumber() == 12 && (card.getColorColor().equals(topColor) || topNum == 12))){
                                ((CardButton) c).fire();
                                return;
                            }
                        }
                    }
                    else if(hasValidSkip()){
                        for(Node c : turnPlayer.getHandGrid().getGridKids()) {
                            card = (CardButton)c;
                            //card in hand is a 10 AND it's color matches the topColor
                            if((card.getNumber() == 10 && (card.getColorColor().equals(topColor) || topNum == 10))){
                                ((CardButton) c).fire();
                                return;
                            }
                        }
                    }
                    else if(hasValidReverse()){
                        for(Node c : turnPlayer.getHandGrid().getGridKids()) {
                            card = (CardButton)c;
                            //card in hand is a 11 AND it's color matches the topColor
                            if((card.getNumber() == 11 && (card.getColorColor().equals(topColor) || topNum == 11))){
                                ((CardButton) c).fire();
                                return;
                            }
                        }
                    }
                }

                for(Node card : turnPlayer.getHandGrid().getGridKids()){
                    temp = (CardButton)card;
                    //count number of valid colors or numbers
                    if(temp.getColorColor().equals(topColor)){
                        ++validSameColor;
                        ++totalValidNormalCards;
                    }else if(temp.getNumber() == topNum){
                        ++validSameNum;
                        ++totalValidNormalCards;
                    }
                    //count number of wild cards and wild Draw 4's
                    if(temp.getNumber() == 99){
                        ++wildCards;
                    }else if(temp.getNumber() == 100) {
                        ++wildDrawFours;
                    }
                    //count number of valid non zero cards
                    if(temp.getNumber() != 0){
                        ++validNonZeroes;
                    }
                }
                /*System.out.println("AI THREAD STATS for player #" + playerTracker + ":");
                System.out.println("Total valid normal cards: " + totalValidNormalCards);
                System.out.println("valid same color: " + validSameColor);
                System.out.println("valid same num: " + validSameNum);
                System.out.println("wild cards: " + wildCards);
                System.out.println("wild draw 4's: " + wildDrawFours);
                System.out.println("valid zeroes: " + validZeroes);*/

                CardButton placeHolder;
                //Try to play a wild draw 4 ILLEGALLY:
                //has valid cards AND a wild draw 4 AND their hand is small enough to bluff
                if( validSameColor > 0 && validSameNum > 0 && wildCards > 0 && wildDrawFours > 0 && turnPlayer.getHandGrid().getGridKids().size() < 5){
                    //there's a 20% chance AI will play it illegaly.
                    if(chooseToPlayWildDraw4() == 1){
                        //play wild draw 4.
                        for(Node c : turnPlayer.getHandGrid().getGridKids()) {
                            placeHolder = (CardButton)c;
                            if(placeHolder.getNumber() == 100){
                                ((CardButton) c).fire();
                                return;
                            }
                        }
                    }
                }

                //otherwise, THE AI PLAYER ALWAYS SAVES WILD CARDS FOR LAST
                if(validSameColor == 0 && validSameNum == 0 && wildCards == 0 && wildDrawFours > 0){
                    //play a wild draw 4
                    for(Node c : turnPlayer.getHandGrid().getGridKids()) {
                        placeHolder = (CardButton)c;
                        if(placeHolder.getNumber() == 100){
                            ((CardButton) c).fire();
                            return;
                        }
                    }
                }else if(validSameColor == 0 && validSameNum == 0 && wildCards > 0){
                    //play a wild card
                    for(Node c : turnPlayer.getHandGrid().getGridKids()) {
                        placeHolder = (CardButton)c;
                        if(placeHolder.getNumber() == 99){
                            ((CardButton) c).fire();
                            return;
                        }
                    }
                }else if(validSameColor > 0 || validSameNum > 0){
                    //player doesn't have ANY wild cards; play a normal card
                    for(Node c : turnPlayer.getHandGrid().getGridKids()) {
                        placeHolder = (CardButton)c;
                        //IF the card "c" is on valid (AI typically plays the left-most or top-most card and then exits)
                        if(placeHolder.getNumber() == topNum || placeHolder.getColorColor().equals(topColor)){
                            if(placeHolder.getNumber() == 100 && totalValidNormalCards == 0 && wildCards == 0) {
                                //Only play a wild draw 4 if there are no other options!
                                ((CardButton) c).fire();
                                return;
                            }else if(placeHolder.getNumber() == 99 && totalValidNormalCards == 0) {
                                //Only play a wild card if there are no other options!
                                ((CardButton) c).fire();
                                return;
                            }else if(placeHolder.getNumber() == 0 && validNonZeroes == 0){
                                //try to save any Zero cards it has FOR LAST
                                ((CardButton) c).fire();
                                return;
                            } else{
                                //so the card is EITHER the same color or the same number AND it's not a zero card AND it's not a wild card
                                ((CardButton) c).fire();
                                return;
                            }
                        }
                    }
                }
            });
        }

        /**
         * there's a 20% chance a 0 will be returned.
         * @return
         */
        private int chooseToPlayWildDraw4(){
            int[] array = {1,1,0,0,0,0,0,0,0,0};
            int rnd = new Random().nextInt(array.length);
            return array[rnd];
        }

        /**
         * This is a thread, so first thing it does
         * is wait for 2 seconds to not overwhelm
         * the human player. Then, the AI plays a
         * card.
         */
        @Override
        public void run() {
            try{
                synchronized (turnPlayer) {
                    turnPlayer.wait(2000);
                }
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            playACard(turnPlayer.getNextPlayer());
        }
    }
}
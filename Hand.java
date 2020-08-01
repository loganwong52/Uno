package sample;

import javafx.scene.paint.Color;
import java.util.ArrayList;

public class Hand{

    private ArrayList<Card> hand;
    private int blackCardPlayed;       //1 is true, 0 is false
    private boolean canPlayDraw4;

    public Hand(){
        hand = new ArrayList<Card>(50);
        blackCardPlayed = 0;
        canPlayDraw4 = false;
    }

    public ArrayList<Card> getHand() {
        return hand;
    }

    public int getSize(){
        return hand.size();
    }

    public void add(Card drawedCard){
        hand.add(drawedCard);
    }

    public void remove(Card cardBeingPlayed){
        hand.remove(cardBeingPlayed);
        if(cardBeingPlayed.getColor().equals("BLACK")){
            setBlackCardPlayed(1);
        }
    }

    public int getBlackCardPlayed() {
        return blackCardPlayed;
    }

    public void setBlackCardPlayed(int tF) {
        blackCardPlayed = tF;
    }

    /**
     * Finds a card in your hand.
     * @param color
     * @param number
     * @return the target if found, null Otherwise
     */
    public Card playCard(String color, int number){
        Card target;
        for(int i = 0; i < hand.size();++i){
            target = hand.get(i);
            if(target.getColor().equals(color) && target.getNumber() == number){
                return target;
            }
        }
        return null;
    }

    public void printHand(){
        System.out.println("YOUR CURRENT HAND:");
        for(int i = 0; i < hand.size(); ++i){
            hand.get(i).printCard();
        }
    }

    /**
     * Checks if there are INVALID cards in the hand.
     * If there aren't, it returns true, that the player
     * needs to draw 1 new card. Otherwise, the player
     * doesn't need to draw 1 new card.
     * @param topColor
     * @param topNum
     * @return true/false
     */
    public boolean needToDraw(Color topColor, int topNum){
        int numOfInvalidCards = 0;
        String topColorStr;
        if(topColor.equals(Color.RED)){
            topColorStr = "RED";
        }else if(topColor.equals(Color.YELLOW)){
            topColorStr = "YELLOW";
        }else if(topColor.equals(Color.BLUE)){
            topColorStr = "BLUE";
        }else if(topColor.equals(Color.GREEN)){
            topColorStr = "GREEN";
        }else {
            topColorStr = "BLACK";
        }

        for(int i = 0; i < hand.size(); ++i){
            if(!hand.get(i).getColor().equals(topColorStr) &&
                    !hand.get(i).getColor().equals("BLACK") &&
                    hand.get(i).getNumber() != topNum ){
                ++numOfInvalidCards;
            }
        }
        if(numOfInvalidCards == hand.size()){
            return true;
        }
        return false;
    }

    private boolean hasDraw4(){
        for(int i = 0; i < hand.size(); ++i){
            if(hand.get(i).getNumber() == 100){
                return true;
            }
        }
        return false;
    }

    /**
     * Counts the number of VALID cards in the turnPlayer's hand.
     * If the amount is greater than 0, that means the turnPlayer
     * shouldn't play a Wild Draw 4.
     * @param topColor
     * @param topNum
     * @return  true if player can play a Wild Draw 4, otherwise, false.
     */
    public void canPlayDraw4(Color topColor, int topNum){
        if(!hasDraw4()){
            return;
        }
        //a valid card is any card that's valid AND it's not a Wild Draw 4
        int numOfValidCards = 0;
        String topColorStr;
        if(topColor.equals(Color.RED)){
            topColorStr = "RED";
        }else if(topColor.equals(Color.YELLOW)){
            topColorStr = "YELLOW";
        }else if(topColor.equals(Color.BLUE)){
            topColorStr = "BLUE";
        }else if(topColor.equals(Color.GREEN)){
            topColorStr = "GREEN";
        }else {
            topColorStr = "BLACK";
        }
        for(int i = 0; i < hand.size(); ++i){
            if(hand.get(i).getNumber() == 99 ||
                    (hand.get(i).getColor().equals(topColorStr) && !topColorStr.equals("BLACK")) ||
                    (hand.get(i).getNumber() == topNum && topNum != 100) ){
                ++numOfValidCards;
            }
        }
        //System.out.println("The turn player has " + numOfValidCards + " valid cards.");

        if(numOfValidCards == 0){
            canPlayDraw4 = true;
        }
    }

    public void falsifyCanPlayDraw4(boolean f){
        canPlayDraw4 = f;
    }

    public boolean getCanPlayDraw4() {
        return canPlayDraw4;
    }

    public String getMostCommonColor(){
        int redCounter = 0;
        int yellowCounter = 0;
        int blueCounter = 0;
        int greenCounter = 0;
        for(Card c : hand){
            if(c.getColor().equals("RED")){
                ++redCounter;
            }else if(c.getColor().equals("YELLOW")){
                ++yellowCounter;
            }else if(c.getColor().equals("BLUE")){
                ++blueCounter;
            }else if(c.getColor().equals("GREEN")){
                ++greenCounter;
            }
        }
        int greatest = Math.max(Math.max(redCounter, yellowCounter), Math.max(blueCounter, greenCounter));
        if(greatest == redCounter){
            return "RED";
        }else if(greatest == yellowCounter){
            return "YELLOW";
        }else if(greatest == blueCounter){
            return "BLUE";
        }else{
            return "GREEN";
        }
    }
}

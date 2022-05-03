package sample;

import javafx.scene.paint.Color;
import java.util.ArrayList;

/**
 * This class represents the model of a hand, whose
 * View is the HandGrid class. It's a glorified
 * ArrayList of cards. So it's not sorted by
 * color or number or anything.
 *
 * @author Logan Wong
 */
public class Hand{
    private ArrayList<Card> hand;
    private int blackCardPlayed;       //1 is true, 0 is false
    private boolean canPlayDraw4;

    /**
     * Initializes the fields
     */
    public Hand(){
        hand = new ArrayList<Card>(50);
        blackCardPlayed = 0;
        canPlayDraw4 = false;
    }

    /**
     * Gets the ArrayList of Cards, which is the hand
     * @return  hand
     */
    public ArrayList<Card> getHand() {
        return hand;
    }

    /**
     * Gets the amount of cards in a player's hand
     * @return  the size of hand
     */
    public int getSize(){
        return hand.size();
    }

    /**
     * Adds a card to the hand.
     * @param drawedCard  the card that was drawn
     */
    public void add(Card drawedCard){
        hand.add(drawedCard);
    }

    /**
     * Finds a card in your hand.
     * @param color     the color you're looking for
     * @param number    the number you're looking for
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

    /**
     * Actually REMOVES a card from the hand arraylist.
     * @param cardBeingPlayed  the card to be removed from the hand
     */
    public void remove(Card cardBeingPlayed){
        hand.remove(cardBeingPlayed);
        if(cardBeingPlayed.getColor().equals("BLACK")){
            setBlackCardPlayed(1);
        }
    }

    /**
     * Checks if a Wild card was played
     * @return  true if a wild card was played, false otherwise
     */
    public int getBlackCardPlayed() {
        return blackCardPlayed;
    }

    /**
     * Changes the field "blackCardPlayed" to true or false
     * @param tF  true or false
     */
    public void setBlackCardPlayed(int tF) {
        blackCardPlayed = tF;
    }

    /**
     * Checks if there are INVALID cards in the hand.
     * If there aren't, it returns true, that the player
     * needs to draw 1 new card. Otherwise, the player
     * doesn't need to draw 1 new card.
     * @param topColor  the color of the top card on the discard pile
     * @param topNum  the number on the top card in the discard pile
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

    /**
     * Checks if player's hand has A wild draw 4.
     * The MOMENT it finds it, it returns true
     * @return  true if there's a wild draw 4. False otherwise.
     */
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
     * @param topColor  the color of the top card in the discard pile
     * @param topNum  the number of the top card in the discard pile
     * @return  true if player can play a Wild Draw 4, otherwise, false.
     */
    public void canPlayDraw4(Color topColor, int topNum){
        if(!hasDraw4()){
            return;
        }

        //If the turnPlayer JUST drew a card (b/c their whole hand was invalid), check that they indeed have a 100 card
        //System.out.println("The turnPlayer has a draw 4. Check their hand:");
        //printHand();

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

    /**
     * Turns canPlayDraw4 true or false
     * @param f true or false
     */
    public void falsifyCanPlayDraw4(boolean f){
        canPlayDraw4 = f;
    }

    /**
     * Gets the field "canPlayDraw4"
     * @return  canPlayDraw4
     */
    public boolean getCanPlayDraw4() {
        return canPlayDraw4;
    }

    /**
     * Loops through a player's hand and counts the amount of
     * each color in their hand. Then, the most abundant
     * color is returned as a string.
     * @return  the most common color in a hand as a string
     */
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
        //get the value that's the greatest
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
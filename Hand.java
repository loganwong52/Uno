package sample;

import javafx.scene.paint.Color;
import java.util.ArrayList;

public class Hand{

    private ArrayList<Card> hand;
    private int blackCardPlayed;       //1 is true, 0 is false

    public Hand(){
        hand = new ArrayList<Card>(50);
        blackCardPlayed = 0;
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
     * Checks if there are not VALID cards in the hand.
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

}

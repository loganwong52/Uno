package sample;

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

}

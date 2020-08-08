package sample;

import java.util.EmptyStackException;
import java.util.Stack;

/**
 * The discardPile is a Stack of cards played.
 * The topColor is kept track of. The top
 * numer is accessible by peeking at the
 * top card and then getting that card's
 * number. The amount of cards in the
 * discardPile is kept track of (size).
 * Cards can be added to the discardPile
 * using the add() method.
 *
 * @uthor Logan Wong
 */
public class DiscardPile {
    private Stack<Card> discardPile;
    private String topColor;

    /**
     * Initalizes the discardPile
     */
    public DiscardPile(){
        discardPile = new Stack<>();
    }

    /**
     * Sets the topColor
     * @param c  the topColor as a String
     */
    public void setTopColor(String c) {
        topColor = c;
    }

    /**
     * Gets the top color
     * @return  topColor
     */
    public String getTopColor() {
        return topColor;
    }

    /**
     * Gets the top number
     * @return  the number of the first card in the discardPile
     */
    public int getTopNumber(){
        return discardPile.peek().getNumber();
    }

    /**
     * Gets the info of the top card in the discard pile
     * @return  the top card's info as a String
     */
    public String getTopCardInfo(){
        return discardPile.peek().cardInfo();
    }

    /**
     * Adds a card to the top of the discard pile
     * @param placedOnTop  the card to be added
     * @return  true if the card added is valid, false otherwise
     */
    public boolean add(Card placedOnTop){
        // if player types in a color/number that doesn't exist in uno Deck (for ModelPTUI)
        if(placedOnTop == null){
            System.out.println("You did not enter a REAL card!!!");
            return false;
        }

        //when you begin the game:
        if(discardPile.size() == 0){
            discardPile.push(placedOnTop);
            topColor = placedOnTop.getColor();
            return true;
        }

        //For the rest of the game:
        Card lastPlayed = discardPile.peek();
        if(placedOnTop.getColor().equals(topColor) ||
            placedOnTop.getNumber() == lastPlayed.getNumber() ||
            placedOnTop.getColor().equals("BLACK") ){
            //valid card played
            discardPile.push(placedOnTop);
            topColor = placedOnTop.getColor();

            System.out.println("Card played: " + placedOnTop.cardInfo());
            return true;

        }else{
            //INVALID card played
            System.out.println(placedOnTop.getColor() + " " + placedOnTop.getNumber() + " was an INVALID move!!!");
            seeTopCard();
            //in case a black card was played, the topColor != discardPile.peek().getColor()
            System.out.println("Top color: " + topColor);
            return false;
        }
    }

    /**
     * removes the top card of the discard pile
     */
    public void remove(){
        discardPile.pop();
    }

    /**
     * See the information of the last card that was played,
     * which should be the top cad.
     */
    public void seeTopCard(){
        System.out.print("Last Played Card: ");
        try{
            System.out.println(discardPile.peek().getColor() + " " + discardPile.peek().getNumber());
        }catch(EmptyStackException ese){
            System.out.println("The Stack is still empty! Play a valid card next time!");
        }
    }

    /**
     * Empty the discard pile of all of its cards
     */
    public void clear(){
        discardPile.clear();
    }

    /**
     * Gets the discard pile
     * @return  discardPile
     */
    public Stack<Card> getDiscardPile() {
        return discardPile;
    }

    /**
     * Gets the amount of cards in the discardPile
     * @return  the discardPile's size
     */
    public int size(){
        return discardPile.size();
    }
}
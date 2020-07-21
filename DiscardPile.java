package sample;

import java.util.EmptyStackException;
import java.util.Stack;

public class DiscardPile {

    private Stack<Card> discardPile;
    private String topColor;

    public DiscardPile(){
        discardPile = new Stack<>();
    }

    public void setTopColor(String c) {
        topColor = c;
    }

    public String getTopColor() {
        return topColor;
    }

    public int getTopNumber(){
        return discardPile.peek().getNumber();
    }

    public boolean add(Card placedOnTop){
        // if player types in a color/number that doesn't exist in uno Deck
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

            //what if the card's color was black
                //handled in the GUI :)

            //what if the card's number was black & DRAW 4 (AKA num = 100)
            //what if the card's number was 10
            // number was 11
            // number was 12

            System.out.println(placedOnTop.cardInfo(placedOnTop) + " was a v-a-l-i-d m-o-v-e :)");
            return true;

        }else{
            //INVALID card played
            System.out.println(placedOnTop.getColor() + " " + placedOnTop.getNumber() + " was an INVALID move!!!");
            seeTopCard();
            System.out.println("Top color: " + topColor);
            return false;
        }
    }

    public void remove(){
        discardPile.pop();
    }

    public void seeTopCard(){
        System.out.print("Last Played Card:");
        try{
            System.out.println(discardPile.peek().getColor() + " " + discardPile.peek().getNumber());
        }catch(EmptyStackException ese){
            System.out.println("The Stack is still empty! Play a valid card next time!");
        }

    }




}

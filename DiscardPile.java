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

            System.out.println(placedOnTop.cardInfo(placedOnTop) + " was a v-a-l-i-d m-o-v-e :)");
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

    public void remove(){
        discardPile.pop();
    }

    public void seeTopCard(){
        System.out.print("Last Played Card: ");
        try{
            System.out.println(discardPile.peek().getColor() + " " + discardPile.peek().getNumber());
        }catch(EmptyStackException ese){
            System.out.println("The Stack is still empty! Play a valid card next time!");
        }
    }

    public void clear(){
        discardPile.clear();
    }

    public Stack<Card> getDiscardPile() {
        return discardPile;
    }

    public int size(){
        return discardPile.size();
    }
}

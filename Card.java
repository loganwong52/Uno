package sample;

/**
 * This is a Card, and the model of a card.
 * A Card has a color that's a String, and a
 * number that's an int.
 * If the card's number is 10, 11, 12, 99, or 100,
 * It has a special effect and is called an
 * Action card.
 * number can only be 0 thru 12
 * there is only 1 "0 card" per color
 * there are 2 of each number 1-12 per color
 * there are 4 99's and 4 100's total
 * 10 = skip
 * 11 = reverse
 * 12 = draw 2
 * 99 = wild card
 * 100 = wild draw 4
 *
 * @author Logan Wong
 */
public class Card {
    private String color;
    private int number;

    /**
     * Initializes the fields
     * @param color  the color the card's going to be
     * @param number  the number the card's going to have
     */
    public Card(String color, int number){
        this.color = color;
        this.number = number;
    }

    /**
     * Gets the color of the card
     * @return  color
     */
    public String getColor() {
        return color;
    }

    /**
     * Gets the number of the card
     * @return  number
     */
    public int getNumber() {
        return number;
    }

    /**
     * Prints out the card's info as color: x | number: x
     */
    public void printCard(){
        System.out.println("color: " + color + " | number: " + number);
    }

    /**
     * Prints the card's info. Note that
     * it's not just number, but if it has an effect,
     * that's printed instead.
     * @return  the card's info as a String, which is "color effect/number"
     */
    public String cardInfo(){
        String effect;
        if(number == 10){
            effect = "SKIP";
        }else if(number == 11){
            effect = "REVERSE";
        }else if(number == 12){
            effect = "DRAW 2";
        }else if(number == 99){
            effect = "WILD CARD";
        }else if(number == 100){
            effect = "WILD DRAW 4";
        }else{
            return color + " " + number;
        }
        return color + " " + effect;
    }
}
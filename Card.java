package sample;

public class Card {
    private String color;
    private int number;
    /*number can only be 0 thru 12
    there is only 1 "0 card" per color
    there are 2 of each number 1-12 per color
    there are 4 99's and 4 100's total
    10 = skip
    11 = reverse
    12 = draw 2
    99 = wild card
    100 = wild draw 4*/

    public Card(String color, int number){
        this.color = color;
        this.number = number;
    }

    public String getColor() {
        return color;
    }

    public int getNumber() {
        return number;
    }

    public void printCard(){
        System.out.println("color: " + color + " | number: " + number);
    }

    public String cardInfo(Card c){
        return c.getColor() + " " + c.getNumber();
    }


}

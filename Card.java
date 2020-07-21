package sample;

public class Card {
    private String color;
    private int number;
    // number can only be 0 thru 12
    // 10 = skip
    // 11 = reverse
    // 12 = draw 2

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

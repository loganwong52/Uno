package sample;

import javafx.scene.control.Button;
import javafx.scene.paint.Color;

public class CardButton extends Button {
    private String colorString;
    private Color colorColor;
    private int number;

    public CardButton(String c, int n){
        colorString = c;
        number = n;

        if(c.equals("RED")){
            colorColor = Color.RED;
        }else if(c.equals("YELLOW")){
            colorColor = Color.YELLOW;
        }else if(c.equals("GREEN")){
            colorColor = Color.GREEN;
        }else if(c.equals("BLUE")){
            colorColor = Color.BLUE;
        }else{
            colorColor = Color.BLACK;
        }
    }

    public int getNumber() {
        return number;
    }

    public String getNumEffect(){
        if(number == 10){
            return "SKIP";
        }else if(number == 11){
            return "REVERSE";
        }else if(number == 12){
            return "DRAW 2";
        }else if(number == 99){
            return "WILD CARD";
        }else if(number == 100){
            return "WILD DRAW 4";
        }else{
            return "no effect";
        }
    }

    public String getColorString() {
        return colorString;
    }

    public Color getColorColor(){
        return colorColor;
    }
}

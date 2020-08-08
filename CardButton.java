package sample;

import javafx.scene.control.Button;
import javafx.scene.paint.Color;

/**
 * Essentially a Button that has three additional features:
 * 1. a number (0 through 12, 99, or 100)
 * 2. A color which is a Color
 * 3. The color as a String
 *
 * @author Logan Wong
 */
public class CardButton extends Button {
    private int number;
    private Color colorColor;
    private String colorString;

    /**
     * Initialzes the fields.
     * @param c  a card's color as a String
     * @param n  a card's numer
     */
    public CardButton(String c, int n){
        colorString = c;
        number = n;

        switch (c) {
            case "RED":
                colorColor = Color.RED;
                break;
            case "YELLOW":
                colorColor = Color.YELLOW;
                break;
            case "GREEN":
                colorColor = Color.GREEN;
                break;
            case "BLUE":
                colorColor = Color.BLUE;
                break;
            default:
                colorColor = Color.BLACK;
                break;
        }
    }

    /**
     * Gets the card's number
     * @return  number
     */
    public int getNumber() {
        return number;
    }

    /**
     * Gets the card's number or effect
     * @return  number or effect as a String
     */
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
            return "" + number;
        }
    }

    /**
     * Gets the card's color as a String
     * @return  colorString
     */
    public String getColorString() {
        return colorString;
    }

    /**
     * Gets the card's color as a Color
     * @return  colorColor
     */
    public Color getColorColor(){
        return colorColor;
    }
}
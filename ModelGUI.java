package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;
import static javafx.scene.paint.Color.*;

/**
 * This class acts as the View and the Controller of the
 * Uno game. It creates the deck, the playingField, and
 * the player threads. Whenever a window pops up, this
 * is where it originates. It has one public Concurrent
 * Linked Queue which holds the turn order of the players
 * which is updated in the Playing Field class.
 *
 * @author Logan Wong
 */
public class ModelGUI extends Application {
    private static final int CARD_LENGTH = 65;
    private static final int CARD_HEIGHT = 78;
    private static Stage mainStage;
    private static DiscardPile discardPile;
    private static PlayingField playingField;
    private static Rectangle cardColor;
    /**
     * cardText is the text on the discard pile GUI Model!
     */
    private static Label cardText;
    /**
     * topColor keeps track of the GUI's color; and it MAY differ
     * from the model's color (AKA the discardPile's topColor)
     * when a black card is played!
     */
    private static Color topColor;
    private static Button unoDeckButton;
    /**
     * "draw2" this tells unoDeckButton which action to do
     */
    private static boolean draw2;
    private static Player p1;
    private static Player p2;
    private static Player p3;
    private static Player p4;
    private static Deck unoDeck;
    private static int numOfPlayers;
    private static Label playByPlayLabel;
    private static String name;
    private static Label p1Label;
    private static Label p2Label;
    private static Label p3Label;
    private static Label p4Label;
    private Stage rulesStage;
    //Uno Button related stuff
    private static Button unoButton;
    private static int playerWithUno;
    private static CriticalRegion criticalRegion;
    private UnoThread u;
    //Challenge Window related stuff
    private static boolean challengerWon;
    private static boolean challengerLost;
    private static boolean cardJustDrawn;
    private static Button yes;
    private static Button no;
    private static Color lastColorPlayed;
    private static int lastNumberPlayed;
    //Choosing Color related stuff
    private static Button red;
    private static Button yellow;
    private static Button blue;
    private static Button green;
    //Uno label related stuff
    private static Label unoTrackerLabel;
    private static boolean someoneHasUno;
    //The ONLY public field in ModelGUI
    public static ConcurrentLinkedQueue<Player> turnOrder;

    /**
     * default constructor which is called before init().
     * It initializes player 1's name and gets the total
     * number of players that the user wants to play with.
     */
    public ModelGUI() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to UNO! Please read the rules if you forget them!");
        try{
            Thread.sleep(500);
        }catch(InterruptedException e){
            e.printStackTrace();
        }
        System.out.println("Please enter your name!");
        name = scanner.nextLine().trim();
        while(name.isEmpty() || name.isBlank()){
            System.out.println("Please enter a valid 'name'!");
            name = scanner.nextLine();
        }
        System.out.println("How many TOTAL players do you want? (2, 3, or 4)");
        String temp = scanner.nextLine();
        while (!temp.equals("2") ^ !temp.equals("3") ^ !temp.equals("4")) {   //exclusive OR
            System.out.println("That was not a valid number of players! Please try again.");
            temp = scanner.nextLine();
        }
        numOfPlayers = Integer.parseInt(temp);
    }

    /**
     * This method is called after the constructor.
     * It initializes most, if not ALL, of the
     * non-GUI related fields.
     */
    @Override
    public void init() {
        unoDeck = new Deck(numOfPlayers);    //1,2,3 or 4 players
        unoDeck.shuffleWell(108);

        // Initialize Players and give them *empty* hands.
        Hand[] hands = unoDeck.distributeHands();
        p1 = new Player(hands[0], 1);
        p2 = new Player(hands[1], 2);
        if (numOfPlayers >= 3) {
            p3 = new Player(hands[2], 3);
        }
        if (numOfPlayers == 4) {
            p4 = new Player(hands[3], 4);
        }

        // make discard pile
        discardPile = new DiscardPile();
        discardPile.add(unoDeck.beginGame(unoDeck.getDeckStack().size()));
        draw2 = false;
        playerWithUno = 0;
        challengerWon = false;
        challengerLost = false;
        cardJustDrawn = false;
        someoneHasUno = false;
    }

    /**
     * Gets the player 1's name.
     * @return  the player 1's name
     */
    public static String getName(){
        return name;
    }

    /**
     * Turns the turnPlayer's label red.
     * @param playerNum  the turnPlayer's player number
     */
    public static void redifyLabel(int playerNum){
        if(playerNum == 1){
            p1Label.setTextFill(RED);
        }else if(playerNum == 2){
            p2Label.setTextFill(RED);
        }else if(playerNum == 3){
            p3Label.setTextFill(RED);
        }else{
            p4Label.setTextFill(RED);
        }
    }

    /**
     * Turns the turnPlayer's label black.
     * @param playerNum  the turnPlayer's player number
     */
    public static void blackifyLabel(int playerNum){
        if(playerNum == 1){
            p1Label.setTextFill(BLACK);
        }else if(playerNum == 2){
            p2Label.setTextFill(BLACK);
        }else if(playerNum == 3){
            p3Label.setTextFill(BLACK);
        }else{
            p4Label.setTextFill(BLACK);
        }
    }

    /**
     * A card is placed on top of the discardPileStack.
     * Takes in that card's color as a string (ALL CAPS!).
     * Assigns topColor to be the new color.
     * The GUI's topColor & the model's topColor in the
     * DiscardPile class are assigned the new value.
     * If the color is BLACK (aka a wild card), then a little
     * window pops up and the player can choose which color
     * they want the BLACK card to be.
     *
     * @param first  the color of the first card on the discard pile as a String
     */
    private static void strToColor(String first) {
        //first should be discardPile.getTopColor();
        switch (first) {
            case "RED":
                topColor = RED;
                discardPile.setTopColor("RED");
                break;
            case "YELLOW":
                topColor = YELLOW;
                discardPile.setTopColor("YELLOW");
                break;
            case "BLUE":
                topColor = BLUE;
                discardPile.setTopColor("BLUE");
                break;
            case "GREEN":
                topColor = GREEN;
                discardPile.setTopColor("GREEN");
                break;
            default:
                topColor = BLACK;
                break;
        }
        cardColor.setFill(topColor);
    }

    /**
     * Updates the discard pile in the View so its color
     * matches the color chosen if a Wild Card was played.
     * It also updates the PlayingField's topColor.
     * @param newColor  the new color as a Color: red, yellow, green, or blue
     * @param newColorStr  the new color as a String
     */
    public static void updateDiscardPileStackPane(Color newColor, String newColorStr) {
        topColor = newColor;
        discardPile.setTopColor(newColorStr);
        if (newColor.equals(YELLOW)) {
            cardText.setTextFill(BLACK);
        } else {
            cardText.setTextFill(WHITE);
        }
        cardColor.setFill(newColor);
        playingField.updateColor(newColor);
    }

    /**
     * This method is called in the PlayingField
     * Update the lastPlayed fields.
     */
    public static void updateLastPlayedValues(Color c, int n){
        lastColorPlayed = c;
        lastNumberPlayed = n;
    }

    /**
     * prepares the text for the window that shows the
     * card info of the card that was on top of the
     * discard pile before the wild draw 4.
     * @return  the card info of the last card played before the wild draw 4
     */
    public static String confirmDraw4Text(){
        String rVString = "";
        if(lastColorPlayed.equals(RED)){
            rVString += "RED\n";
        }else if(lastColorPlayed.equals(YELLOW)){
            rVString += "YELLOW\n";
        }else if(lastColorPlayed.equals(BLUE)){
            rVString += "BLUE\n";
        }else if(lastColorPlayed.equals(GREEN)){
            rVString += "GREEN\n";
        }

        if(lastNumberPlayed == 10){
            rVString += "SKIP";
        }else if(lastNumberPlayed == 11){
            rVString += "REVERSE";
        }else if(lastNumberPlayed == 12){
            rVString += "DRAW 2";
        }else if(lastNumberPlayed == 99){
            rVString += "WILD\nCARD";
        }else if(lastNumberPlayed == 100){
            rVString += "WILD\nDRAW 4";
        }else{
            rVString += lastNumberPlayed;
        }
        return rVString;
    }


    /**
     * makes the buttons in the hands have color!
     * @param b  the button
     * @param color  the color as a String
     */
    private static void setHandCardColor(Button b, String color) {
        if (color.equals("RED")) {
            b.setStyle("-fx-background-color: #ff0000");  //red
        } else if (color.equals("YELLOW")) {
            b.setStyle("-fx-background-color: #FFFF00"); //yellow
        } else if (color.equals("BLUE")) {
            b.setStyle("-fx-background-color: #0000FF"); //blue
        } else if (color.equals("GREEN")) {
            b.setStyle("-fx-background-color: #008000"); //green
        } else {
            b.setStyle("-fx-background-color: #000000"); //black
        }
    }

    /**
     * If someone has 1 card left in their hand,
     * the uno Button is disabled for 2.5 seconds. If it's
     * not clicked within that time frame, the player with
     * UNO is forced to draw 2 cards.
     * @param player    the player with uno.
     */
    private static void checkForUno(Player player) {
        if (player.getHand().getSize() == 1) {
            playerWithUno = player.getPlayerNumber();       //this is where playerWithUno is updated
            System.out.println("SOMEONE HAS UNO...");
            someoneHasUno = true;
            unoTrackerLabel.setText("SOMEONE HAS UNO...");
            unoButton.setDisable(false);
            UnoThread.changeReady(true);
            criticalRegion.stopWaiting();
            playingField.setPreviousPlayerHasUno(true);
            //If the turnPlayer isn't player 1, randomly decide to press UNO!
            if(player.getPlayerNumber() != 1){
                PressUnoButtonThread presserAI = new PressUnoButtonThread();
                presserAI.start();
            }
        }
    }

    /**
     * Randomly chooses a zero (no) or a 1 (yes).
     * There's a 5% chance that a Zero will be chosen. (1/20)
     * @return  a 0 or a 1
     */
    private static int unoResponse(){
        int[] array = {0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
        int rnd = new Random().nextInt(array.length);
        return array[rnd];
    }

    //-------------------------------------------------------------------------------------------------------------------
    /**
     * SUBCLASS #1 of the ModelGUI class.
     * This is the thread that controls when the non human
     * players press the UnoButton.
     *
     * @author Logan Wong
     */
    private static class PressUnoButtonThread extends Thread{
        public PressUnoButtonThread(){

        }

        /**
         * When the thread starts, all the other player threads
         * are waiting/sleeping. This thread waits for 1.1 seconds.
         * Then, there is a 95% chance that the uno button will be
         * pressed.
         */
        @Override
        public void run() {
            System.out.println("SOMEONE OTHER THAN PLAYER 1 HAS UNO; PLAYER 1, PLEASE DO NOT PRESS THE UNO BUTTON!");
            try{
                sleep(1100);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            int pressButton = unoResponse();
            if(pressButton == 1){
                Platform.runLater(()-> unoButton.fire());
                //System.out.println("Sucessfully pressed UNO: " + pressButton);
            }else{
                System.out.println("Oops! UnoResponse (should be 0): " + pressButton);
            }
        }
    }
    //-------------------------------------------------------------------------------------------------------------------

    /**
     * When the Wild Draw 4 card is played, the next player can
     * choose to challenge the turnPlayer if their move was
     * valid or not. A new window appears with buttons that
     * asks this yes or no question.
     * @param nextPlayerNum the next player's number
     * @param turnPlayerNum the turn player's number
     */
    private static void challengeWindow(int nextPlayerNum, int turnPlayerNum){
        Stage stage3 = new Stage();
        Label label = new Label("Player " + nextPlayerNum + ", would you like\nto challenge player " + turnPlayerNum + "?");
        yes = new Button("yes");
        no = new Button("no");
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(label);
        borderPane.setAlignment(label, Pos.CENTER);
        borderPane.setCenter(yes);
        borderPane.setAlignment(yes, Pos.CENTER);
        borderPane.setBottom(no);
        borderPane.setAlignment(no, Pos.CENTER);
        yes.setPrefWidth(150);
        no.setPrefWidth(150);

        //Stage 4 is turnPlayer's hand if their Wild Draw 4 was valid/invalid
        Stage stage4 = new Stage();
        HBox longHbox = new HBox();
        VBox bigVbox = new VBox();
        VBox leftVbox = new VBox();
        VBox rightVBox = new VBox();

        //The previous card's color & info
        Rectangle previousColor = new Rectangle(80, 80);
        previousColor.setFill(lastColorPlayed);
        Label previousCardInfo = new Label(confirmDraw4Text());
        if(!previousColor.getFill().equals(YELLOW)){
            previousCardInfo.setTextFill(WHITE);
        }else{
            previousCardInfo.setTextFill(BLACK);
        }
        StackPane stack = new StackPane();
        stack.getChildren().addAll(previousColor, previousCardInfo);

        //Putting things in the left VBox
        Label leftLabel = new Label("Last card played before\nthe Wild Draw 4:");
        leftVbox.getChildren().addAll(leftLabel, stack);
        leftVbox.setAlignment(Pos.CENTER);

        //Putting things in the gridpane
        GridPane rectangles = new GridPane();
        rectangles.setHgap(3);
        int col = 0;
        for(Node b : turnOrder.peek().getHandGrid().getGridKids()){
            Rectangle r = new Rectangle(80, 80);
            r.setFill(((CardButton)b).getColorColor());
            Label l = new Label(((CardButton) b).getColorString() + "\n" + ((CardButton)b).getNumEffect());
            if(!r.getFill().equals(YELLOW)){
                l.setTextFill(WHITE);
            }else{
                l.setTextFill(BLACK);
            }
            StackPane s = new StackPane();
            s.getChildren().addAll(r, l);
            rectangles.add(s, col, 0);
            ++col;
        }

        //putting things in the right VBox
        Label rightLabel = new Label("Player " + turnPlayerNum + "'s other cards:");
        rightVBox.getChildren().addAll(rightLabel, rectangles);
        longHbox.getChildren().addAll(leftVbox,rightVBox);
        longHbox.setMargin(leftVbox, new Insets(0, 50, 0, 0));

        //putting things in the biggest VBox
        Label confirmLabel = new Label("CONFIRMATION OF PLAYER " + turnPlayerNum +"'S HAND");
        Label resultLabel = new Label();
        Button close = new Button("close");
        close.setOnMouseClicked(mouseEvent -> stage4.close());
        bigVbox.getChildren().addAll(confirmLabel, longHbox, resultLabel, close);
        bigVbox.setAlignment(Pos.CENTER);
        bigVbox.setSpacing(5);
        stage4.setScene(new Scene(bigVbox));

        //event handling
        yes.setOnAction(mouseEvent ->{
            yes.setDisable(true);
            no.setDisable(true);
            if(turnOrder.peek().getHand().getCanPlayDraw4() == false){
                System.out.println("The challenger won! Player " + turnPlayerNum + " must draw 4 cards!");
                playByPlayLabel.setText("Player " + nextPlayerNum + " won the challenge!\nPlayer " + turnPlayerNum + " must draw 4 cards!\nPlayer " +
                        nextPlayerNum + "'s turn is now skipped.");
                challengerWon = true;
                resultLabel.setText("The wild draw 4 was an INVALID move!\nThe challenger, Player " + nextPlayerNum +", won!\nPlayer " + turnPlayerNum + " must draw 4 cards!" +
                        "\nAlso, Player " + nextPlayerNum + " is skipped.");
            }else{
                System.out.println("The challenger lost! They must draw 6 cards!");
                playByPlayLabel.setText("Player " + nextPlayerNum + " lost the challenge!\nPlayer " + nextPlayerNum + " must draw 6 cards!\nPlayer " +
                        nextPlayerNum + "'s turn is now skipped.");
                challengerLost = true;
                resultLabel.setText("The wild draw 4 was a VALID move!\nThe challenger, Player " + nextPlayerNum + ", lost!\nPlayer " + nextPlayerNum + " must draw 6 cards and their turn is skipped!");
            }
            turnOrder.peek().getHand().falsifyCanPlayDraw4(false);
            stage4.showAndWait();
            stage3.close();
        });

        no.setOnAction(mouseEvent -> {
            yes.setDisable(true);
            no.setDisable(true);
            System.out.println("No Challenge occurred.");
            playByPlayLabel.setText("Player " + nextPlayerNum + " did not challenge Player " + turnPlayerNum + ".\nPlayer " +
                    nextPlayerNum + "'s turn is now skipped.");
            stage3.close();
        });

        //prevent stage3 from being closed if the RED X button is clicked!
        Platform.runLater(() -> stage3.setOnCloseRequest(evt -> evt.consume()));
        stage3.setResizable(false);
        stage3.setScene(new Scene(borderPane));
        playByPlayLabel.setText("A Wild Draw 4 card was played!\nPlayer " + nextPlayerNum + " can choose to challenge Player " + turnPlayerNum);
        if(nextPlayerNum != 1){
            yes.setDisable(true);
            no.setDisable(true);
            ChallengeAiThread challengerAI = new ChallengeAiThread();
            challengerAI.start();
        }
        stage3.showAndWait();
    }

    /**
     * Randomly chooses a zero (no) or a 1 (yes)
     * for whether or not to challenge the turn player
     * when a wild draw 4 is played.
     * There's a 90% chance that a Zero will be chosen.
     * @return  a 0 or a 1
     */
    private static int challengeResponse(){
        int[] array = {0,0,0,0,0,0,0,0,0,1};
        int rnd = new Random().nextInt(array.length);
        return array[rnd];
    }

    /**
     * When a wild draw 4 is played, this method updates the
     * playByPlay label to say who played it, what color
     * was chosen, and who's turn is skipped.
     * @param color  the chosen color as a string
     * @param turnPlayer  the player who played the wild draw 4
     */
    private static void wildUpdate(String color, Player turnPlayer){
        if(playingField.getTopNum() == 100) {
            playByPlayLabel.setText("Player " + turnPlayer.getPlayerNumber() + " played a " + discardPile.getTopCardInfo() +
                    "\nColor chosen: " + color + "\nPlayer " + turnPlayer.getNextPlayerNumber() + "'s turn is skipped.");
        }else{
            playByPlayLabel.setText("Player " + turnPlayer.getPlayerNumber() + " played a " + discardPile.getTopCardInfo() +
                    "\nColor chosen: " + color);
        }
    }

    /**
     * When a black card is played, this window pops up and prompts
     * the turn player to choose the color they want. By using
     * showAndWait(), the rest of the code waits until the pop
     * up window is closed.
     * @param turnPlayer  the turn player
     */
    private static void popUpWindow(Player turnPlayer){
        Stage stage2 = new Stage();
        HBox hBox = new HBox();
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(new Label("Click on the color you want!"));
        red = new Button();
        red.setStyle("-fx-background-color: #ff0000");  //red
        red.setPrefSize(50, 50);
        yellow = new Button();
        yellow.setStyle("-fx-background-color: #FFFF00"); //yellow
        yellow.setPrefSize(50, 50);
        blue = new Button();
        blue.setStyle("-fx-background-color: #0000FF"); //blue
        blue.setPrefSize(50, 50);
        green = new Button();
        green.setStyle("-fx-background-color: #008000"); //green
        green.setPrefSize(50, 50);

        //event handling
        red.setOnAction(mouseEvent -> {
            updateDiscardPileStackPane(RED, "RED");
            System.out.println("Color chosen: RED");
            wildUpdate("RED", turnPlayer);
            turnPlayer.setMyTurn(false);
            playingField.prepNextPlayer();  //Player made their move.
            synchronized (turnPlayer){
                try {
                    turnPlayer.wait();      //now, player waits for playingField to update the turnOrder
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            stage2.close();
        });

        yellow.setOnAction(mouseEvent -> {
            updateDiscardPileStackPane(YELLOW, "YELLOW");
            System.out.println("Color chosen: YELLOW");
           wildUpdate("YELLOW", turnPlayer);
            turnPlayer.setMyTurn(false);
            playingField.prepNextPlayer();  //Player made their move.
            synchronized (turnPlayer){
                try {
                    turnPlayer.wait();      //now, player waits for playingField to update the turnOrder
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            stage2.close();
        });

        green.setOnAction(mouseEvent -> {
            updateDiscardPileStackPane(GREEN, "GREEN");
            System.out.println("Color chosen: GREEN");
            wildUpdate("GREEN", turnPlayer);
            turnPlayer.setMyTurn(false);
            playingField.prepNextPlayer();  //Player made their move.
            synchronized (turnPlayer){
                try {
                    turnPlayer.wait();      //now, player waits for playingField to update the turnOrder
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            stage2.close();
        });

        blue.setOnAction(mouseEvent -> {
            updateDiscardPileStackPane(BLUE, "BLUE");
            System.out.println("Color chosen: BLUE");
            wildUpdate("BLUE", turnPlayer);
            turnPlayer.setMyTurn(false);
            playingField.prepNextPlayer();  //Player made their move.
            synchronized (turnPlayer){
                try {
                    turnPlayer.wait();      //now, player waits for playingField to update the turnOrder
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            stage2.close();
        });

        //prevent stage2 from being closed if the RED X button is clicked!
        Platform.runLater(() -> {
            // prevent window from closing
            stage2.setOnCloseRequest(Event::consume);
        });

        hBox.getChildren().addAll(red, yellow, blue, green);
        borderPane.setCenter(hBox);
        stage2.setScene(new Scene(borderPane));
        if(turnPlayer.getPlayerNumber() != 1){
            red.setDisable(true);
            yellow.setDisable(true);
            blue.setDisable(true);
            green.setDisable(true);
            ColorChoosingThread chooserAI = new ColorChoosingThread(turnPlayer);
            chooserAI.start();
        }
        stage2.showAndWait();
    }

    //-------------------------------------------------------------------------------------------------------------------
    /**
     * SUBCLASS #2 OF MODELGUI
     *
     * This thread chooses the color to press if a wild card
     * was played AND the turn player isn't player 1.
     *
     * @author Logan Wong
     */
    private static class ColorChoosingThread extends Thread{
        Player turnPlayer;
        private ColorChoosingThread(Player p){
            turnPlayer = p;
        }

        /**
         * When this thread starts, it sleeps for 1.5 seconds to
         * pretend that the AI is thinking. Then, it gets the
         * color that's most abundant in their hand and chooses
         * that color in the pop-up window with the 4 colors.
         */
        @Override
        public void run() {
            try{
                sleep(1500);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            String mostCommonColor = turnPlayer.getHand().getMostCommonColor();
            switch (mostCommonColor) {
                case "RED":
                    Platform.runLater(() -> {
                        red.setDisable(false);
                        red.fire();
                    });
                    break;
                case "YELLOW":
                    Platform.runLater(() -> {
                        yellow.setDisable(false);
                        yellow.fire();
                    });
                    break;
                case "BLUE":
                    Platform.runLater(() -> {
                        blue.setDisable(false);
                        blue.fire();
                    });
                    break;
                default:
                    Platform.runLater(() -> {
                        green.setDisable(false);
                        green.fire();
                    });
                    break;
            }
        }
    }
    //-------------------------------------------------------------------------------------------------------------------

    /**
     *  Whenever a card-button is clicked, the play-by-play label is updated.
     *  If the card clicked on is invalid, the label will tell the player
     *  that the card they click on is invalid. If it's valid, it will
     *  display that card-button's info and any effect if it has one.
     * @param playerNum     the turn player's number
     * @param valid         the cardButton that was clicked was a valid card to play (or not)
     * @param nextPlayerNum     the next player's number
     */
    private static void updatePlayByPlayLabel(int playerNum, boolean valid, int nextPlayerNum){
        if(valid){
            int num = discardPile.getTopNumber();
            playByPlayLabel.setText("Player " + playerNum + " played a " + discardPile.getTopCardInfo());
            if(num == 10){
                playByPlayLabel.setText(playByPlayLabel.getText() + "\nPlayer " + nextPlayerNum + " is skipped!");
            }else if(num == 12) {
                playByPlayLabel.setText(playByPlayLabel.getText() + "\nPlayer " + nextPlayerNum + " draws 2 cards and is skipped!");
            }
        }else{
            playByPlayLabel.setText("Player " + playerNum + ", that was an INVALID move! Pick a different card!");
        }
    }

    /**
     * this method is called in PlayingField.
     * A player has uno. Label says so. Then, their turn ends
     * and the next player's turn begins. At that point, the
     * message disappears via this method.
     */
    public static void clearUnoTrackerLabel(){
        unoTrackerLabel.setText("");
    }

    /**
     * When a card is played, this method
     * updates the discard pile to show the amount of
     * cards are in the discard pile and the card's
     * effect or number.
     * @param number  the card's number
     */
    private static void changeDiscardPileText(int number){
        if (number == 10) {
            cardText.setText("(discard pile)\n(" + discardPile.size() + ")\nSKIP");
            playingField.setSkip1Player(true);
        } else if (number == 11) {
            cardText.setText("(discard pile)\n(" + discardPile.size() + ")\nREVERSE");
        } else if (number == 12) {
            cardText.setText("(discard pile)\n(" + discardPile.size() + ")\nDRAW 2");
            playingField.setSkip1Player(true);
        } else if (number == 99) {
            cardText.setText("(discard pile)\n(" + discardPile.size() + ")\nWILD");
        } else if (number == 100) {
            cardText.setText("(discard pile)\n(" + discardPile.size() + ")\nWILD DRAW 4");
        } else {
            cardText.setText("(discard pile)\n(" + discardPile.size() + ")\n" + number);
        }
    }

    /**
     * Gives each player their hand and makes it appear in the View.
     * Gives the cards in the player's hands, which are buttons,
     * EventHandlers. When the UNO game starts, the View appears.
     * Also, a second window called the Begin Window appears. It has a
     * Start Game button that starts the game when pressed.
     * Next, the first card in the discard pile is checked. If it's a
     * skip, reverse, draw 2, or a wild card, and then the effect is
     * applied. Finally, Player 1 can go first and the game begins.
     * @param primaryStage  the main stage
     */
    @Override
    public void start(Stage primaryStage){
        primaryStage.setTitle("UNO");
        BorderPane borderPane = new BorderPane();
        ScrollPane scrollPane = new ScrollPane();

        //PLAYER 1
        VBox bottomVBox = new VBox();
        p1Label = new Label();
        p1Label.setText(name);
        p1Label.setFont(new Font("Arial", 15));
        bottomVBox.setAlignment(Pos.CENTER);
        HandGrid p1HandGrid = new HandGrid(1);
        //Putting initial cards in hand... each card is a button!
        for (int i = 0; i < 7; ++i) {
            int number = p1.getHand().getHand().get(i).getNumber();
            String color = p1.getHand().getHand().get(i).getColor();
            CardButton button = new CardButton(color, number);
            button.setDisable(true);            //disable/enable PLAYER 1's cards HERE!
            //prevent the button from changing size
            button.setMinWidth(CARD_LENGTH);
            button.setPrefWidth(CARD_LENGTH);
            button.setMaxWidth(CARD_LENGTH);
            button.setMinHeight(CARD_HEIGHT);
            button.setPrefHeight(CARD_HEIGHT);
            button.setMaxHeight(CARD_HEIGHT);
            setHandCardColor(button, color);

            button.setOnMouseClicked(mouseEvent -> {
                if (discardPile.add(p1.getHand().playCard(color, number))) {
                    p1.getHand().remove(p1.getHand().playCard(color, number));  //remove hand from the hand (the Model)
                    p1.getHandGrid().remove(button);                                 //remove card from handGrid (the View)
                    updatePlayByPlayLabel(1, true, p1.getNextPlayerNumber());       //update label
                    checkForUno(p1);

                    //disable cards in turnplayer's hand to prevent them from playing more than 1 card in a turn
                    p1.getHandGrid().disableAll();
                    strToColor(color);
                    //set the color of the text on the discard pile
                    if (topColor.equals(YELLOW)) {
                        cardText.setTextFill(BLACK);
                    } else {
                        cardText.setTextFill(WHITE);
                    }
                    //change the text on the discard pile stack pane
                    changeDiscardPileText(number);

                    //update the playingField fields
                    playingField.updateColor(topColor);
                    playingField.updateNum(number);

                    //Check if the CARD played has a special effect!
                    if (topColor.equals(BLACK)) {
                        if (number == 100) {      //wild draw 4
                            //the next player can challenge the turnPlayer.
                            System.out.println("challenge window appeared.");
                            challengeWindow(p1.getNextPlayerNumber(), 1);
                            if(challengerWon){
                                //if "yes" was pressed and the move was illegal
                                unoDeckButtonAction2(p1, 4);
                                unoDeckButton.setDisable(false);
                                unoDeckButton.fire();
                                challengerWon = false;

                            }else if(challengerLost){
                                //if "yes" was pressed and the move WAS legal
                                unoDeckButtonAction2(p1.getNextPlayer(), 6);
                                unoDeckButton.setDisable(false);
                                unoDeckButton.fire();
                                challengerLost = false;

                            }else {
                                //if "no" was pressed
                                unoDeckButtonAction2(p1.getNextPlayer(), 4);
                                unoDeckButton.setDisable(false);
                                unoDeckButton.fire();
                            }
                        }
                        popUpWindow(p1);
                        //the unoDeckbutton's topcolor is updated in updateDiscardPileStackPane

                    } else if (number == 12) {     //DRAW 2
                        draw2 = true;
                        unoDeckButtonAction2(p1.getNextPlayer(), 2);
                        unoDeckButton.setDisable(false);
                        unoDeckButton.fire();
                        draw2 = false;
                        p1.setMyTurn(false);
                        playingField.prepNextPlayer();  //P1 made their move.
                        synchronized (p1) {
                            try {
                                p1.wait();      //now, player 1 waits for playingField to update the turnOrder
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        p1.setMyTurn(false);
                        playingField.prepNextPlayer();  //P1 made their move.
                        synchronized (p1) {
                            try {
                                p1.wait();      //now, player 1 waits for playingField to update the turnOrder
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                else{
                    updatePlayByPlayLabel(1, false, p1.getNextPlayerNumber());
                }
            });

            //Set the color of the text on the card buttons in the hand (View)
            if (color.equals("YELLOW")) {
                button.setTextFill(BLACK);
            } else {
                button.setTextFill(WHITE);
            }
            //Set the text of the card buttons
            if (number == 10) {
                button.setText(color + "\nSKIP");
            } else if (number == 11) {
                button.setText(color + "\nREVERSE");
            } else if (number == 12) {
                button.setText(color + "\nDRAW 2");
            } else if (number == 99) {
                button.setText("WILD\nCARD");
            } else if (number == 100) {
                button.setText("WILD\nDRAW 4");
            } else {
                button.setText(color + "\n" + number);
            }
            button.setTextAlignment(TextAlignment.CENTER);
            p1HandGrid.add(button, i, 1);   //hand should just be 1 ROW
        }
        p1.setHandGrid(p1HandGrid);
        bottomVBox.getChildren().addAll(p1Label, p1HandGrid.getGridPane());
        bottomVBox.setSpacing(2);

        //PLAYER 2
        HBox leftHBox = new HBox();
        p2Label = new Label();
        p2Label.setText("PLAYER 2");
        p2Label.setFont(new Font("Arial", 15));
        leftHBox.setAlignment(Pos.CENTER);
        HandGrid p2HandGrid = new HandGrid(2);
        for (int i = 0; i < 7; ++i) {
            int number = p2.getHand().getHand().get(i).getNumber();
            String color = p2.getHand().getHand().get(i).getColor();
            CardButton button = new CardButton(color, number);
            button.setDisable(true);
            button.setPrefSize(CARD_HEIGHT, CARD_LENGTH);
            //setHandCardColor(button, color);            //only player 1 should have colorful buttons

            button.setOnAction(mouseEvent -> {
                if (discardPile.add(p2.getHand().playCard(color, number))) {
                    p2.getHand().remove(p2.getHand().playCard(color, number));  //remove hand from the hand (the Model)
                    p2.getHandGrid().remove(button);                                  //remove card from handGrid (the View)
                    updatePlayByPlayLabel(2, true, p2.getNextPlayerNumber());
                    checkForUno(p2);

                    //disable cards in turnplayer's hand to prevent them from playing more than 1 card in a turn
                    p2.getHandGrid().disableAll();
                    strToColor(color);
                    //set the color of the text on discard pile
                    if (topColor.equals(YELLOW)) {
                        cardText.setTextFill(BLACK);
                    } else {
                        cardText.setTextFill(WHITE);
                    }
                    //set the text of the discard pile
                    if (number == 10) {
                        cardText.setText("(discard pile)\n(" + discardPile.size() + ")\nSKIP");
                        playingField.setSkip1Player(true);
                    } else if (number == 11) {
                        cardText.setText("(discard pile)\n(" + discardPile.size() + ")\nREVERSE");
                    } else if (number == 12) {
                        cardText.setText("(discard pile)\n(" + discardPile.size() + ")\nDRAW 2");
                        playingField.setSkip1Player(true);

                    } else if (number == 99) {
                        cardText.setText("(discard pile)\n(" + discardPile.size() + ")\nWILD");
                    } else if (number == 100) {
                        cardText.setText("(discard pile)\n(" + discardPile.size() + ")\nWILD DRAW 4");
                    } else {
                        cardText.setText("(discard pile)\n(" + discardPile.size() + ")\n" + number);
                    }
                    //update playingField fields
                    playingField.updateColor(topColor);
                    playingField.updateNum(number);

                    //Check if the CARD played has a special effect!
                    if (topColor.equals(BLACK)) {
                        if (number == 100) {      //wild draw 4
                            //the next player can challenge the turnPlayer.
                            System.out.println("challenge window appeared.");
                            challengeWindow(p2.getNextPlayerNumber(), 2);
                            if(challengerWon){
                                //if "yes" was pressed and the move was illegal
                                unoDeckButtonAction2(p2, 4);
                                unoDeckButton.setDisable(false);
                                unoDeckButton.fire();
                                challengerWon = false;

                            }else if(challengerLost){
                                //if "yes" was pressed and the move WAS legal
                                unoDeckButtonAction2(p2.getNextPlayer(), 6);
                                unoDeckButton.setDisable(false);
                                unoDeckButton.fire();
                                challengerLost = false;

                            }else {
                                //if "no" was pressed
                                unoDeckButtonAction2(p2.getNextPlayer(), 4);
                                unoDeckButton.setDisable(false);
                                unoDeckButton.fire();
                            }
                        }
                        popUpWindow(p2);

                    } else if (number == 12) {     //DRAW 2
                        draw2 = true;
                        unoDeckButtonAction2(p2.getNextPlayer(), 2);
                        unoDeckButton.setDisable(false);
                        unoDeckButton.fire();
                        draw2 = false;
                        p2.setMyTurn(false);
                        playingField.prepNextPlayer();  //P2 made their move.
                        synchronized (p2) {
                            try {
                                p2.wait();      //now, player 2 waits for playingField to update the turnOrder
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        p2.setMyTurn(false);
                        playingField.prepNextPlayer();  //P2 made their move.
                        synchronized (p2) {
                            try {
                                p2.wait();      //now, player 2 waits for playingField to update the turnOrder
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                else{
                    updatePlayByPlayLabel(2, false, p2.getNextPlayerNumber());
                }
            });

            //Set color of text on the card button (view)
            /*if (color.equals("YELLOW")) {
                button.setTextFill(BLACK);
            } else {
                button.setTextFill(WHITE);
            }
            //set the text on the card
            if (number == 10) {
                button.setText(color + "\nSKIP");
            } else if (number == 11) {
                button.setText(color + "\nREVERSE");
            } else if (number == 12) {
                button.setText(color + "\nDRAW 2");
            } else if (number == 99) {
                button.setText("WILD\nCARD");
            } else if (number == 100) {
                button.setText("WILD\nDRAW 4");
            } else {
                button.setText(color + "\n" + number);
            }*/
            button.setText("UNO!");     //non-human players have buttons with only UNO
            p2HandGrid.add(button, i, 2);   //hand should just be 1 COL
        }
        p2.setHandGrid(p2HandGrid);
        leftHBox.getChildren().addAll(p2HandGrid.getGridPane(), p2Label);
        leftHBox.setSpacing(2);

        //PLAYER 3
        VBox topVBox = new VBox();
        HandGrid p3HandGrid = new HandGrid(3);
        if (numOfPlayers >= 3) {
            p3Label = new Label();
            p3Label.setText("PLAYER 3");
            p3Label.setFont(new Font("Arial", 15));
            topVBox.setAlignment(Pos.CENTER);
            for (int i = 0; i < 7; ++i) {
                int number = p3.getHand().getHand().get(i).getNumber();
                String color = p3.getHand().getHand().get(i).getColor();
                CardButton button = new CardButton(color, number);
                button.setDisable(true);
                button.setPrefSize(CARD_LENGTH, CARD_HEIGHT);
                //setHandCardColor(button, color);      //only player 1's buttons have color

                button.setOnAction(mouseEvent -> {
                    if (discardPile.add(p3.getHand().playCard(color, number))) {
                        p3.getHand().remove(p3.getHand().playCard(color, number));      //remove hand from the hand (the Model)
                        p3.getHandGrid().remove(button);                                      //remove card from handGrid (the View)
                        updatePlayByPlayLabel(3, true, p3.getNextPlayerNumber());
                        checkForUno(p3);

                        //disable cards in turnplayer's hand to prevent them from playing more than 1 card in a turn
                        p3.getHandGrid().disableAll();
                        strToColor(color);
                        //Set color of text on the discard pile
                        if (topColor.equals(YELLOW)) {
                            cardText.setTextFill(BLACK);
                        } else {
                            cardText.setTextFill(WHITE);
                        }
                        //set the text on the discard pile
                        if (number == 10) {
                            cardText.setText("(discard pile)\n(" + discardPile.size() + ")\nSKIP");
                            playingField.setSkip1Player(true);

                        } else if (number == 11) {
                            cardText.setText("(discard pile)\n(" + discardPile.size() + ")\nREVERSE");
                        } else if (number == 12) {
                            cardText.setText("(discard pile)\n(" + discardPile.size() + ")\nDRAW 2");
                            playingField.setSkip1Player(true);

                        } else if (number == 99) {
                            cardText.setText("(discard pile)\n(" + discardPile.size() + ")\nWILD");
                        } else if (number == 100) {
                            cardText.setText("(discard pile)\n(" + discardPile.size() + ")\nWILD DRAW 4");
                        } else {
                            cardText.setText("(discard pile)\n(" + discardPile.size() + ")\n" + number);
                        }
                        //update playingField fields
                        playingField.updateColor(topColor);
                        playingField.updateNum(number);

                        //Check if the CARD played has a special effect
                        if (topColor.equals(BLACK)) {
                            if (number == 100) {      //wild draw 4
                                //the next player can challenge the turnPlayer.
                                System.out.println("challenge window appeared.");
                                challengeWindow(p3.getNextPlayerNumber(), 3);
                                if(challengerWon){
                                    //if "yes" was pressed and the move was illegal
                                    unoDeckButtonAction2(p3, 4);
                                    unoDeckButton.setDisable(false);
                                    unoDeckButton.fire();
                                    challengerWon = false;

                                }else if(challengerLost){
                                    //if "yes" was pressed and the move WAS legal
                                    unoDeckButtonAction2(p3.getNextPlayer(), 6);
                                    unoDeckButton.setDisable(false);
                                    unoDeckButton.fire();
                                    challengerLost = false;

                                }else {
                                    //if "no" was pressed
                                    unoDeckButtonAction2(p3.getNextPlayer(), 4);
                                    unoDeckButton.setDisable(false);
                                    unoDeckButton.fire();
                                }
                            }
                            popUpWindow(p3);

                        } else if (number == 12) {     //DRAW 2
                            draw2 = true;
                            unoDeckButtonAction2(p3.getNextPlayer(), 2);
                            unoDeckButton.setDisable(false);
                            unoDeckButton.fire();
                            draw2 = false;
                            p3.setMyTurn(false);
                            playingField.prepNextPlayer();  //P3 made their move.
                            synchronized (p3) {
                                try {
                                    p3.wait();      //now, player 3 waits for playingField to update the turnOrder
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            p3.setMyTurn(false);
                            playingField.prepNextPlayer();  //P3 made their move.
                            synchronized (p3) {
                                try {
                                    p3.wait();      //now, player 3 waits for playingField to update the turnOrder
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    else{
                        updatePlayByPlayLabel(3, false, p3.getNextPlayerNumber());
                    }
                });

                //set the color of the text on the card button
                /*if (color.equals("YELLOW")) {
                    button.setTextFill(BLACK);
                } else {
                    button.setTextFill(WHITE);
                }
                //set the text on the card button
                if (number == 10) {
                    button.setText(color + "\nSKIP");
                } else if (number == 11) {
                    button.setText(color + "\nREVERSE");
                } else if (number == 12) {
                    button.setText(color + "\nDRAW 2");
                } else if (number == 99) {
                    button.setText("WILD\nCARD");
                } else if (number == 100) {
                    button.setText("WILD\nDRAW 4");
                } else {
                    button.setText(color + "\n" + number);
                }*/
                button.setText("UNO!");     //for everyone other than player 1, no one can see the text.
                p3HandGrid.add(button, i, 3);   //hand should just be 1 ROW
            }
            p3.setHandGrid(p3HandGrid);
            topVBox.getChildren().addAll(p3HandGrid.getGridPane(), p3Label);
            topVBox.setSpacing(2);
        }

        //PLAYER 4
        HBox rightHBox = new HBox();
        HandGrid p4HandGrid = new HandGrid(4);
        if (numOfPlayers == 4) {
            p4Label = new Label();
            p4Label.setText("PLAYER 4");
            p4Label.setFont(new Font("Arial", 15));
            rightHBox.setAlignment(Pos.CENTER);
            for (int i = 0; i < 7; ++i) {
                int number = p4.getHand().getHand().get(i).getNumber();
                String color = p4.getHand().getHand().get(i).getColor();
                CardButton button = new CardButton(color, number);
                button.setDisable(true);
                button.setPrefSize(CARD_HEIGHT, CARD_LENGTH);
                //setHandCardColor(button, color);      //only player 1 should have colorful buttons

                button.setOnAction(mouseEvent -> {
                    if (discardPile.add(p4.getHand().playCard(color, number))) {
                        p4.getHand().remove(p4.getHand().playCard(color, number));      //remove hand from the hand (the Model)
                        p4.getHandGrid().remove(button);                                      //remove card from handGrid (the View)
                        updatePlayByPlayLabel(4, true, p4.getNextPlayerNumber());
                        checkForUno(p4);

                        //disable cards in turnplayer's hand to prevent them from playing more than 1 card in a turn
                        p4.getHandGrid().disableAll();
                        strToColor(color);
                        //set the color of the text on the discard pile
                        if (topColor.equals(YELLOW)) {
                            cardText.setTextFill(BLACK);
                        } else {
                            cardText.setTextFill(WHITE);
                        }
                        //set the text on the discard pile
                        if (number == 10) {
                            cardText.setText("(discard pile)\n(" + discardPile.size() + ")\nSKIP");
                            playingField.setSkip1Player(true);

                        } else if (number == 11) {
                            cardText.setText("(discard pile)\n(" + discardPile.size() + ")\nREVERSE");
                        } else if (number == 12) {
                            cardText.setText("(discard pile)\n(" + discardPile.size() + ")\nDRAW 2");
                            playingField.setSkip1Player(true);

                        } else if (number == 99) {
                            cardText.setText("(discard pile)\n(" + discardPile.size() + ")\nWILD");
                        } else if (number == 100) {
                            cardText.setText("(discard pile)\n(" + discardPile.size() + ")\nWILD DRAW 4");
                        } else {
                            cardText.setText("(discard pile)\n(" + discardPile.size() + ")\n" + number);
                        }
                        //update the playingField fields
                        playingField.updateColor(topColor);
                        playingField.updateNum(number);

                        //Check if the CARD played has a special effect
                        if (topColor.equals(BLACK)) {
                            if (number == 100) {      //wild draw 4
                                //the next player can challenge the turnPlayer.
                                System.out.println("challenge window appeared.");
                                challengeWindow(p4.getNextPlayerNumber(), 4);
                                if(challengerWon){
                                    //if "yes" was pressed and the move was illegal
                                    unoDeckButtonAction2(p4, 4);
                                    unoDeckButton.setDisable(false);
                                    unoDeckButton.fire();
                                    challengerWon = false;

                                }else if(challengerLost){
                                    //if "yes" was pressed and the move WAS legal
                                    unoDeckButtonAction2(p4.getNextPlayer(), 6);
                                    unoDeckButton.setDisable(false);
                                    unoDeckButton.fire();
                                    challengerLost = false;

                                }else {
                                    //if "no" was pressed
                                    unoDeckButtonAction2(p4.getNextPlayer(), 4);
                                    unoDeckButton.setDisable(false);
                                    unoDeckButton.fire();
                                }
                            }
                            popUpWindow(p4);

                        } else if (number == 12) {     //DRAW 2
                            draw2 = true;
                            unoDeckButtonAction2(p4.getNextPlayer(), 2);
                            unoDeckButton.setDisable(false);
                            unoDeckButton.fire();
                            draw2 = false;
                            p4.setMyTurn(false);
                            playingField.prepNextPlayer();  //P4 made their move.
                            synchronized (p4) {
                                try {
                                    p4.wait();      //now, player 4 waits for playingField to update the turnOrder
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            p4.setMyTurn(false);
                            playingField.prepNextPlayer();  //P4 made their move.
                            synchronized (p4) {
                                try {
                                    p4.wait();      //now, player 4 waits for playingField to update the turnOrder
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    else{
                        updatePlayByPlayLabel(4, false, p4.getNextPlayerNumber());
                    }
                });

                //Set the color of the text on the Card Button
                /*if (color.equals("YELLOW")) {
                    button.setTextFill(BLACK);
                } else {
                    button.setTextFill(WHITE);
                }
                //set the text on the card button
                if (number == 10) {
                    button.setText(color + "\nSKIP");
                } else if (number == 11) {
                    button.setText(color + "\nREVERSE");
                } else if (number == 12) {
                    button.setText(color + "\nDRAW 2");
                } else if (number == 99) {
                    button.setText("WILD\nCARD");
                } else if (number == 100) {
                    button.setText("WILD\nDRAW 4");
                } else {
                    button.setText(color + "\n" + number);
                }*/
                button.setText("UNO!");     //for everyone other than player 1, no one can see the text.
                p4HandGrid.add(button, i, 4);   //hand should just be 1 COL
            }
            p4.setHandGrid(p4HandGrid);
            rightHBox.getChildren().addAll(p4Label, p4HandGrid.getGridPane());
            rightHBox.setSpacing(2);
        }

        //Assign topNumber
        int topNumber = discardPile.getTopNumber();
        //Create turnOrder and assign each player's "nextPlayer"
        turnOrder = new ConcurrentLinkedQueue<>();
        turnOrder.add(p1);
        turnOrder.add(p2);
        p1.setNextPlayer(p2);
        if (numOfPlayers >= 3) {
            turnOrder.add(p3);
            p2.setNextPlayer(p3);
        } else {
            p2.setNextPlayer(p1);
        }
        if (numOfPlayers == 3) {
            p3.setNextPlayer(p1);
        } else if (numOfPlayers == 4) {
            turnOrder.add(p4);
            p3.setNextPlayer(p4);
            p4.setNextPlayer(p1);
        }
        //Start the PLAYER threads and their CRITICAL REGION
        playingField = new PlayingField(numOfPlayers);
        for (Player p : turnOrder) {
            p.setPlayingField(playingField);
            p.start();
        }

        /* centerPane holds discard pile and the deck
        discardpile is a Stackpane w/ rectangle whose color and label change
        deck is a button. When clicked, add a card to hand*/
        FlowPane centerPane = new FlowPane();
        //normal private fields
        StackPane discardPileStackPane = new StackPane();
        cardColor = new Rectangle(100, 100);
        cardText = new Label();
        strToColor(discardPile.getTopColor());      //topColor is assigned a value for the 1st time in strToColor

        playingField.updateColor(topColor);
        playingField.updateNum(topNumber);

        if (discardPile.getTopColor().equals("YELLOW")) {
            cardText.setTextFill(BLACK);
        } else {
            cardText.setTextFill(WHITE);
        }
        //THESE ARE FOR IF THE FIRST CARD IN THE DISCARD PILE IS A SPECIAL CARD!!!
        if (topNumber == 10) {
            cardText.setText("(discard pile)\n(" + discardPile.size() + ")\nSKIP");
            //no need to skip 1 extra player...
        } else if (topNumber == 11) {
            cardText.setText("(discard pile)\n(" + discardPile.size() + ")\nREVERSE");
        } else if (topNumber == 12) {
            cardText.setText("(discard pile)\n(" + discardPile.size() + ")\nDRAW 2");
            //no need to skip 1 extra player... player 1's turn is skipped, and it goes to player 2.
        } else if (topNumber == 99) {
            cardText.setText("(discard pile)\n(" + discardPile.size() + ")\nWILD");
        } else {
            cardText.setText("(discard pile)\n(" + discardPile.size() + ")\n" + topNumber);
        }
        cardText.setTextAlignment(TextAlignment.CENTER);

        //the DECK Button
        unoDeckButton = new Button();
        unoDeckButton.setPrefSize(100, 100);
        unoDeckButton.setText("Deck\n" + unoDeck.getCardsLeft());
        unoDeckButton.setTextAlignment(TextAlignment.CENTER);
        unoDeckButton.setDisable(true);
        //the DECK Button has 2 action events (in 2 separate methods)

        //the UNO Button
        unoButton = new Button();
        unoButton.setText("UNO!");
        unoButton.setDisable(true);
        unoButton.setPrefSize(50, 50);
        unoButton.setOnAction(mouseEvent -> {
            System.out.println("PLAYER #" + playerWithUno + " has UNO!");
            unoTrackerLabel.setText("PLAYER " + playerWithUno + " has UNO!");
            UnoThread.changeReady(false);
            unoButton.setDisable(true);
            someoneHasUno = false;
        });
        criticalRegion = new CriticalRegion();
        u = new UnoThread();
        u.start();

        //The CenterPane's center
        discardPileStackPane.getChildren().addAll(cardColor, cardText);
        HBox bigThree = new HBox();
        bigThree.getChildren().addAll(discardPileStackPane, unoDeckButton, unoButton);
        bigThree.setAlignment(Pos.CENTER);

        //The CenterPane's Labels
        Label unoLabel = new Label("UNO!");
        unoLabel.setStyle("-fx-font: " + 40 + " arial;");
        unoTrackerLabel = new Label("");

        playByPlayLabel = new Label("First card on the Discard Pile:\n" + discardPile.getTopCardInfo());
        playByPlayLabel.setFont(new Font("Arial", 15));
        VBox centerVBox = new VBox();
        centerVBox.getChildren().addAll(unoLabel, unoTrackerLabel, bigThree, playByPlayLabel);
        centerVBox.setAlignment(Pos.CENTER);
        centerVBox.setSpacing(2);

        //position everything I guess
        centerPane.getChildren().addAll(centerVBox);
        centerPane.setAlignment(Pos.CENTER);
        borderPane.setCenter(centerPane);
        borderPane.setBottom(bottomVBox);
        borderPane.setLeft(leftHBox);
        borderPane.setTop(topVBox);
        borderPane.setRight(rightHBox);
        scrollPane.setContent(borderPane);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        primaryStage.setScene(new Scene(scrollPane));
        primaryStage.setWidth(1000);
        primaryStage.setHeight(720);
        // exit program when the red X button is pressed
        primaryStage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(1);
        });
        primaryStage.show();
        mainStage = primaryStage;

        p1.setMyTurn(true);

        //the begin window with a "start game" button
        Stage beginWindow = new Stage();
        Label startText = new Label("Welcome to UNO! Be the first player to\nreach zero cards in your hand!");
        Button begin = new Button("START GAME!");
        Button rules = new Button("Rules");
        VBox vBox = new VBox();
        HBox buttons = new HBox();
        buttons.getChildren().addAll(begin, rules);
        buttons.setSpacing(5);
        buttons.setAlignment(Pos.CENTER);
        vBox.getChildren().addAll(startText, buttons);
        vBox.setAlignment(Pos.CENTER);
        Platform.runLater(() -> beginWindow.setOnCloseRequest(Event::consume));
        begin.setOnMouseClicked(mouseEvent -> beginWindow.close());
        rules();
        rules.setOnMouseClicked(mouseEvent -> {
            rulesStage.showAndWait();
        });
        beginWindow.setScene(new Scene(vBox));
        beginWindow.showAndWait();

        playingField.prepNextPlayer();              //p1 sleeps for 0.25 seconds
        /*if the first card in the discardPile is a wild card (99).
          It'll never be a 100 b/c the rules say so & I coded it to be so.*/
        if (discardPile.getTopColor().equals("BLACK")) {
            playingField.setFirstTopCardIsBlack(true);
            firstCardIsBlack();
        }
        if(topNumber >= 10 && topNumber < 99){
            p1.getHandGrid().disableAll();
            playingField.setFirstTopCardIsSpecial(true);
            if(topNumber == 10) {
                playByPlayLabel.setText(playByPlayLabel.getText() + "\nPlayer 1's turn is skipped!");
            }else if(topNumber == 11){
                playByPlayLabel.setText(playByPlayLabel.getText() + "\nPlayer 4 goes first now instead of Player 1!");
            }else if(topNumber == 12) {
                draw2 = true;
                unoDeckButtonAction2(p1, 2);
                unoDeckButton.setDisable(false);
                unoDeckButton.fire();
                draw2 = false;
                playByPlayLabel.setText(playByPlayLabel.getText() + "\n" + name + " drew 2 cards\nand their turn is skipped.");
            }
            p1.setMyTurn(false);
            playingField.prepNextPlayer();  //P1 made their move.
            p1.getHandGrid().disableAll();
            synchronized (p1) {
                try {
                    p1.wait();      //now, player 1 waits for playingField to update the turnOrder
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            playingField.setFirstTopCardIsSpecial(false);
        }
    }

    /**
     * Creates the window that has the rules.
     */
    private void rules(){
        rulesStage = new Stage();
        ScrollPane rulesScroller = new ScrollPane();
        Label rulesLabel = new Label("THE RULES OF UNO:\nSetup:\n" +
                "-\tThe game is for 2-4 players. \n" +
                "-\tEvery player starts with seven cards. \n" +
                "-\tThe rest of the cards are placed in a Draw Pile face down. \n" +
                "-\tNext to the pile a space should be designated for a Discard Pile.\n" +
                "-\tThe top card should be placed in the Discard Pile, and the game begins!\n" +
                "\n" +
                "Game Play: \n" +
                "-\tPlayer 1 goes first and gameplay follows a clockwise direction. \n" +
                "-\tEvery player views his/her cards and tries to match the card in the Discard Pile.\n" +
                "-\tYou have to match either by the number, color, or the symbol/Action.\n" +
                "\n" +
                "For instance, if the Discard Pile has a red card that is an 8 you have to place either a red card or a card with an 8 on it. You can also play a Wild card (which can alter current color in play).\n" +
                "\n" +
                "If the player has NO matches, they must draw a card from the Draw pile. \n" +
                "-\tIf that card can be played, play it. \n" +
                "-\tOtherwise, keep the card, and the game moves on to the next person in turn.\n" +
                "-\tYou can also play a Wild card or a Wild Draw Four card on your turn.\n" +
                "\n" +
                "Note: If the first card turned up from the Draw Pile (to form the Discard Pile) is an Action card, the Action from that card applies and must be carried out by the first player to go (player 1). The exceptions are if a Wild or Wild Draw Four card is turned up.\n" +
                "-\tIf it is a Wild card, the first player to start (player 1), can choose whatever color to begin play.\n" +
                "-\tIf the first card is a Wild Draw Four card – Return it to the Draw Pile, shuffle the deck, and turn over a new card (this is done automatically by the game).\n" +
                "-\tAt any time during the game, if the Draw Pile becomes depleted and no one has yet won the round, remove the top card of the current Discard Pile, take the rest of the Discard Pile, shuffle it, and turn it over to regenerate a new Draw Pile, and the card you removed earlier becomes the new Discard Pile.\n" +
                "\n" +
                "Note: you can only put down one card at a time; you cannot stack two or more cards together on the same turn.\n" +
                "Ex: you cannot put down a Draw Two on top of another Draw Two, or Wild Draw Four during the same turn, or put down two Wild Draw Four cards together.\n" +
                "\n" +
                "The game continues until a player has one card left. The moment a player has just one card they must click the button that says “UNO!” If they don’t click it, that player must draw two new cards as a penalty.\n" +
                "\n" +
                "Assuming that the player is unable to play/discard their last card and needs to draw, but after drawing, is then able to play/discard that penultimate card, the player has to repeat the action of calling out “Uno”. The bottom line is – Clicking the “Uno” button needs to be repeated every time you are left with one card.\n" +
                "\n" +
                "Once a player has no cards remaining, they win and everyone else loses.\n" +
                "\n" +
                "Action Cards: Besides the number cards, there are several other cards that help mix up the game.\n" +
                "\n" +
                "Reverse\n" +
                "-\tIf going clockwise, switch to counterclockwise or vice versa. \n" +
                "-\tIt can only be played on a card that matches by color, or on another Reverse card. \n" +
                "-\tIf turned up at the beginning of play, the player to player 1’s right goes first and the turn order continues counter-clockwise.\n" +
                "\n" +
                "Skip\n" +
                "-\tWhen a player places this card, the next player has to skip their turn.\n" +
                "-\tIt can only be played on a card that matches by color, or on another Skip card.\n" +
                "-\tIf turned up at the beginning of play, the player 1 loses his/her turn and the next player to that player’s right starts the game instead.\n" +
                "\n" +
                "Draw Two\n" +
                "-\tWhen a person places this card, the next player will have to pick up two cards and forfeit his/her turn.\n" +
                "-\tIt can only be played on a card that matches by color, or on another Draw Two. \n" +
                "-\tIf turned up at the beginning of play, player 1 draws two cards and gets skipped.\n" +
                "\n" +
                "Wild\n" +
                "-\tThis card represents all four colors, and can be placed on any card. \n" +
                "-\tThe player chooses which color it will represent for the next player.\n" +
                "-\tIt can be played regardless of whether another card is available.\n" +
                "-\tIf turned up at the beginning of play, player 1 chooses what color to continue play and then their turn begins\n" +
                "\n" +
                "Wild Draw Four\n" +
                "-\tThis acts just like the wild card but the next player also has to draw four cards as well as forfeit his/her turn. \n" +
                "-\tWith this card, you must have no other alternative cards to play that matches the color of the card previously played.\n" +
                "-\tIf you play this card illegally, you may be challenged by the other player to show your hand to him/her.\n" +
                "-\tIf guilty, you need to draw 4 cards.\n" +
                "-\tIf not, the challenger needs to draw 6 cards instead. \n" +
                "-\tIf turned up at the beginning of play, return this card to the Draw pile, shuffle, and turn up a new one. (this is done automatically by the game)\n" +
                "\n" +
                "Two Player & Four Player Rules\n" +
                "For two players, there is a slight change of rules:\n" +
                "-\tReverse works like Skip\n" +
                "-\tPlay Skip, and you may immediately play another card\n" +
                "-\tIf you play a Draw Two or Wild Draw Four card, your opponent has to draw the number of cards required, and then play immediately resumes back on your turn.\n\n");
        Scene scene = new Scene(rulesScroller);
        rulesLabel.setWrapText(true);
        rulesScroller.setContent(rulesLabel);
        rulesScroller.setPrefSize(1000, 500);
        rulesScroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        rulesScroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        rulesScroller.setFitToWidth(true);      //you can't scroll left to right
        rulesScroller.setFitToHeight(false);    //you CAN scroll up and down
        rulesStage.setScene(scene);
    }

    /**
     * This method is only called once: if the first card
     * in the discard pile is a Wild card (not a wild draw 4)
     */
    private void firstCardIsBlack(){
        System.out.println("The first top card is a wild card; player 1 chooses the starting color!");
        Stage stage2 = new Stage();
        HBox hBox = new HBox();
        VBox vBox = new VBox();
        vBox.getChildren().add(new Label("Click on the color you want!"));
        Button red = new Button();
        red.setPrefSize(50, 50);
        red.setStyle("-fx-background-color: #ff0000");  //red
        Button yellow = new Button();
        yellow.setPrefSize(50, 50);
        yellow.setStyle("-fx-background-color: #FFFF00"); //yellow
        Button blue = new Button();
        blue.setPrefSize(50, 50);
        blue.setStyle("-fx-background-color: #0000FF"); //blue
        Button green = new Button();
        green.setPrefSize(50, 50);
        green.setStyle("-fx-background-color: #008000"); //green

        red.setOnMouseClicked(mouseEvent -> {
            topColor = RED;
            discardPile.setTopColor("RED");
            playByPlayLabel.setText(playByPlayLabel.getText() + "\nColor chosen: RED");
            cardColor.setFill(RED);
            playingField.updateColor(topColor);
            playingField.updateNum(99);
            stage2.close();
            p1.getHandGrid().enableAll();
        });
        yellow.setOnMouseClicked(mouseEvent -> {
            topColor = YELLOW;
            discardPile.setTopColor("YELLOW");
            playByPlayLabel.setText(playByPlayLabel.getText() + "\nColor chosen: YELLOW");
            cardColor.setFill(YELLOW);
            cardText.setTextFill(BLACK);
            playingField.updateColor(topColor);
            playingField.updateNum(99);
            stage2.close();
            p1.getHandGrid().enableAll();
        });
        green.setOnMouseClicked(mouseEvent -> {
            topColor = GREEN;
            discardPile.setTopColor("GREEN");
            playByPlayLabel.setText(playByPlayLabel.getText() + "\nColor chosen: GREEN");
            cardColor.setFill(GREEN);
            playingField.updateColor(topColor);
            playingField.updateNum(99);
            stage2.close();
            p1.getHandGrid().enableAll();
        });
        blue.setOnMouseClicked(mouseEvent -> {
            topColor = BLUE;
            discardPile.setTopColor("BLUE");
            playByPlayLabel.setText(playByPlayLabel.getText() + "\nColor chosen: BLUE");
            cardColor.setFill(BLUE);
            playingField.updateColor(topColor);
            playingField.updateNum(99);
            stage2.close();
            p1.getHandGrid().enableAll();
        });

        hBox.getChildren().addAll(red, yellow, blue, green);
        vBox.getChildren().add(hBox);
        stage2.setScene(new Scene(vBox));
        stage2.showAndWait();
        playingField.setFirstTopCardIsBlack(false);
    }

    /**
     * Reverses the turn order and sets the next player
     * of each player accordingly.
     */
    public static void reverseTurnOrder() {
        /*if numOfPlayers == 2, reverse turn is same as
        the SKIP card. This is done in PlayingField*/
        if (numOfPlayers == 3) {
            if (p1.getNextPlayer().equals(p2)) {
                p1.setNextPlayer(p3);
            } else if (p1.getNextPlayer().equals(p3)) {
                p1.setNextPlayer(p2);
            }
            if (p2.getNextPlayer().equals(p3)) {
                p2.setNextPlayer(p1);
            } else if (p2.getNextPlayer().equals(p1)) {
                p2.setNextPlayer(p3);
            }
            if (p3.getNextPlayer().equals(p1)) {
                p3.setNextPlayer(p2);
            } else if (p3.getNextPlayer().equals(p2)) {
                p3.setNextPlayer(p1);
            }

        } else if (numOfPlayers == 4) {
            if (p1.getNextPlayer().equals(p2)) {
                p1.setNextPlayer(p4);
            } else if (p1.getNextPlayer().equals(p4)) {
                p1.setNextPlayer(p2);
            }
            if (p2.getNextPlayer().equals(p3)) {
                p2.setNextPlayer(p1);
            } else if (p2.getNextPlayer().equals(p1)) {
                p2.setNextPlayer(p3);
            }
            if (p3.getNextPlayer().equals(p4)) {
                p3.setNextPlayer(p2);
            } else if (p3.getNextPlayer().equals(p2)) {
                p3.setNextPlayer(p4);
            }
            if (p4.getNextPlayer().equals(p1)) {
                p4.setNextPlayer(p3);
            } else if (p4.getNextPlayer().equals(p3)) {
                p4.setNextPlayer(p1);
            }

        }
    }

    /**
     * Disables or enables the unoDeckButton.
     * @param tF  boolean value of true or false
     */
    public static void setUnoDeckButton(boolean tF) {
        unoDeckButton.setDisable(tF);
    }

    /**
     * The unoDeckButton has 2 actions-- this is the first one.
     * When the turnplayer has NO valid cards in their hand, this method is
     * called in PlayingField and allows the turnPlayer to draw
     * 1 card. If it's valid, they can play it. Otherwise,
     * their turn ends.
     *
     * @param player the turnPlayer
     */
    public static void unoDeckButtonAction(Player player) {
        unoDeckButton.setOnAction(mouseEvent -> {
            Card drawnCard = unoDeck.drawOne();
            unoDeckButton.setText("Deck\n" + unoDeck.getCardsLeft());
            playByPlayLabel.setText("Player " + player.getPlayerNumber() + " drew 1 card.");
            //If the deck NOW has 0 cards left...
            if (unoDeck.getCardsLeft() == 0) {
                Card temp = unoDeck.replenish(discardPile.getDiscardPile());   //keep the top card; deck is replenished
                discardPile.clear(); //clear the discardPile
                discardPile.getDiscardPile().add(temp);     //make the discardPile the (old) top card
                if (temp.getNumber() == 10) {
                    cardText.setText("(discard pile)\n(" + discardPile.size() + ")\nSKIP");
                } else if (temp.getNumber() == 11) {
                    cardText.setText("(discard pile)\n(" + discardPile.size() + ")\nREVERSE");
                } else if (temp.getNumber() == 12) {
                    cardText.setText("(discard pile)\n(" + discardPile.size() + ")\nDRAW 2");
                } else if (temp.getNumber() == 99) {
                    cardText.setText("(discard pile)\n(" + discardPile.size() + ")\nWILD");
                } else if (temp.getNumber() == 100) {
                    cardText.setText("(discard pile)\n(" + discardPile.size() + ")\nWILD DRAW 4");
                } else {
                    cardText.setText("(discard pile)\n(" + discardPile.size() + ")\n" + temp.getNumber());
                }
                unoDeckButton.setText("Deck\n" + unoDeck.getCardsLeft());
            }
            //Add card to the hand model
            System.out.println("Player #" + player.getPlayerNumber() + " drew 1 card.");
            player.getHand().add(drawnCard);
            if(playerWithUno == player.getPlayerNumber()){
                unoTrackerLabel.setText("");
            }
            cardJustDrawn = true;
            assert turnOrder.peek() != null;
            turnOrder.peek().getHand().canPlayDraw4(playingField.getTopColor(), playingField.getTopNum());         //this checks if the turnPlayer can play a Draw 4
            System.out.println("Player #" + player.getPlayerNumber() + "'s new hand size: " + player.getHand().getSize());
            unoDeckButton.setDisable(true);

            //add card to the view!
            int number = drawnCard.getNumber();
            String color = drawnCard.getColor();
            CardButton button = new CardButton(color, number);
            if (player.getPlayerNumber() % 2 == 0) {  //even
                button.setPrefSize(CARD_HEIGHT, CARD_LENGTH);
            } else {      //odd
                button.setPrefSize(CARD_LENGTH, CARD_HEIGHT);
            }

            if (player.getPlayerNumber() == 1) {
                setHandCardColor(button, color);
            }

            button.setOnAction(mouseEvent2 -> {
                if (discardPile.add(player.getHand().playCard(color, number))) {
                    player.getHand().remove(player.getHand().playCard(color, number));  //remove hand from the hand (the Model)
                    player.getHandGrid().remove(button);                                //remove card from handGrid (the View)
                    updatePlayByPlayLabel(player.getPlayerNumber(), true, player.getNextPlayerNumber());
                    checkForUno(player);

                    //disable cards in turnplayer's hand to prevent them from playing more than 1 card in a turn
                    player.getHandGrid().disableAll();
                    strToColor(color);
                    //Set the color of the text on the discard pile
                    if (topColor.equals(YELLOW)) {
                        cardText.setTextFill(BLACK);
                    } else {
                        cardText.setTextFill(WHITE);
                    }
                    //set the text on the discard pile
                    if (number == 10) {
                        cardText.setText("(discard pile)\n(" + discardPile.size() + ")\nSKIP");
                        playingField.setSkip1Player(true);

                    } else if (number == 11) {
                        cardText.setText("(discard pile)\n(" + discardPile.size() + ")\nREVERSE");
                    } else if (number == 12) {
                        cardText.setText("(discard pile)\n(" + discardPile.size() + ")\nDRAW 2");
                        playingField.setSkip1Player(true);

                    } else if (number == 99) {
                        cardText.setText("(discard pile)\n(" + discardPile.size() + ")\nWILD");
                    } else if (number == 100) {
                        cardText.setText("(discard pile)\n(" + discardPile.size() + ")\nWILD DRAW 4");
                    } else {
                        cardText.setText("(discard pile)\n(" + discardPile.size() + ")\n" + number);
                    }
                    //update the fields of the playingField
                    playingField.updateColor(topColor);
                    playingField.updateNum(number);

                    //Check if the CARD played has any special effects.
                    if (topColor.equals(BLACK)) {
                        if (number == 100) {      //wild draw 4
                            //the next player can challenge the turnPlayer.
                            System.out.println("challenge window appeared.");
                            challengeWindow(player.getNextPlayerNumber(), player.getPlayerNumber());
                            if (challengerWon) {
                                //if "yes" was pressed and the move was illegal
                                unoDeckButtonAction2(player, 4);
                                unoDeckButton.setDisable(false);
                                unoDeckButton.fire();
                                challengerWon = false;

                            } else if (challengerLost) {
                                //if "yes" was pressed and the move WAS legal
                                unoDeckButtonAction2(player.getNextPlayer(), 6);
                                unoDeckButton.setDisable(false);
                                unoDeckButton.fire();
                                challengerLost = false;

                            } else {
                                //if "no" was pressed
                                unoDeckButtonAction2(player.getNextPlayer(), 4);
                                unoDeckButton.setDisable(false);
                                unoDeckButton.fire();
                            }
                        }
                        popUpWindow(player);
                        cardJustDrawn = false;

                    } else if (number == 12) {     //DRAW 2
                        draw2 = true;
                        unoDeckButtonAction2(player.getNextPlayer(), 2);
                        unoDeckButton.setDisable(false);
                        unoDeckButton.fire();
                        draw2 = false;
                        player.setMyTurn(false);
                        cardJustDrawn = false;
                        playingField.prepNextPlayer();  //Player made their move.
                        synchronized (player) {
                            try {
                                player.wait();      //now, player waits for playingField to update the turnOrder
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        player.setMyTurn(false);
                        cardJustDrawn = false;
                        playingField.prepNextPlayer();  //Player made their move.
                        synchronized (player) {
                            try {
                                player.wait();      //now, player waits for playingField to update the turnOrder
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                else{
                    updatePlayByPlayLabel(player.getPlayerNumber(), false, player.getNextPlayerNumber());
                }
            });

            //set the color of the text on the card Button
            if (player.getPlayerNumber() == 1) {
                if (color.equals("YELLOW")) {
                    button.setTextFill(BLACK);
                } else {
                    button.setTextFill(WHITE);
                }
                //set the text on the card Button
                if (number == 10) {
                    button.setText(color + "\nSKIP");
                } else if (number == 11) {
                    button.setText(color + "\nREVERSE");
                } else if (number == 12) {
                    button.setText(color + "\nDRAW 2");
                } else if (number == 99) {
                    button.setText("WILD\nCARD");
                } else if (number == 100) {
                    button.setText("WILD\nDRAW 4");
                } else {
                    button.setText(color + "\n" + number);
                }
                button.setTextAlignment(TextAlignment.CENTER);
            } else {
                button.setText("UNO!");     //for everyone other than player 1, no one can see the image.
            }
            int lastSlot = player.getHandGrid().getSize();
            //System.out.println("The slot to add the card they just drew: slot # " + " lastSlot");
            player.getHandGrid().add(button, lastSlot, player.getPlayerNumber());   //hand should just be 1 ROW

            //If the new card they just drew is ALSO invalid, then their turn is over
            //disable their cards and update the turnOrder in PlayingField
            if (!drawnCard.getColor().equals("BLACK") &&
                    !drawnCard.getColor().equals(discardPile.getTopColor()) &&
                    drawnCard.getNumber() != discardPile.getTopNumber()) {
                player.getHandGrid().disableAll();
                player.setMyTurn(false);
                cardJustDrawn = false;
                playingField.setInvalidCardDrawn(true);
                playingField.prepNextPlayer();  //Player made their move.
                synchronized (player) {
                    try {
                        player.wait();      //now, player waits for playingField to update the turnOrder
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        if(player.getPlayerNumber() != 1) {
            drawOneThread drawAI = new drawOneThread();
            drawAI.start();
        }
    }


    /**
     * SUBCLASS #3 OF MODELGUI
     *
     * If the turnPlayer isn't player 1, this thread
     * acts as an AI and presses the deckButton to
     * allow the turnPlayer to draw 1 new card.
     *
     * @author Logan Wong
     */
    private static class drawOneThread extends Thread{
        private drawOneThread(){ }
        @Override
        public void run() {
            try{
                sleep(1000);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
            Platform.runLater(()-> unoDeckButton.fire());
            try{
                sleep(1500);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * This is the second action for unoDeckButton.
     * When a card that makes the next player draw cards is played,
     * the next player is forced to draw that many cards.
     *
     * @param player the player who's going to draw cards
     * @param amount the amount of times to draw a new card (2 or 4 only!!!)
     */
    private static void unoDeckButtonAction2(Player player, int amount) {
        //this one is "set on action" to allow the .fire() to work.
        unoDeckButton.setOnAction(mouseEvent -> {
            if ((draw2 && playingField.getTopNum() == 12) || (topColor.equals(BLACK) && playingField.getTopNum() == 100) || (draw2 && player.getHand().getSize() == 1)) {
                for (int i = 0; i < amount; ++i) {
                    Card drawnCard = unoDeck.drawOne();
                    unoDeckButton.setText("Deck\n" + unoDeck.getCardsLeft());
                    //If the deck NOW has 0 cards left...
                    if (unoDeck.getCardsLeft() == 0) {
                        Card temp = unoDeck.replenish(discardPile.getDiscardPile());   //keep the top card; deck is replenished
                        discardPile.clear();    //clear the discardPile
                        discardPile.getDiscardPile().add(temp);     //make the discardPile the (old) top card
                        if (temp.getNumber() == 10) {
                            cardText.setText("(discard pile)\n(" + discardPile.size() + ")\nSKIP");
                        } else if (temp.getNumber() == 11) {
                            cardText.setText("(discard pile)\n(" + discardPile.size() + ")\nREVERSE");
                        } else if (temp.getNumber() == 12) {
                            cardText.setText("(discard pile)\n(" + discardPile.size() + ")\nDRAW 2");
                        } else if (temp.getNumber() == 99) {
                            cardText.setText("(discard pile)\n(" + discardPile.size() + ")\nWILD");
                        } else if (temp.getNumber() == 100) {
                            cardText.setText("(discard pile)\n(" + discardPile.size() + ")\nWILD DRAW 4");
                        } else {
                            cardText.setText("(discard pile)\n(" + discardPile.size() + ")\n" + temp.getNumber());
                        }
                        unoDeckButton.setText("Deck\n" + unoDeck.getCardsLeft());
                    }
                    //Add card to the hand model
                    System.out.println("Player #" + player.getPlayerNumber() + " drew a card.");
                    player.getHand().add(drawnCard);
                    if(playerWithUno == player.getPlayerNumber()){
                        unoTrackerLabel.setText("");
                    }

                    //add card to the view!
                    int number = drawnCard.getNumber();
                    String color = drawnCard.getColor();
                    CardButton button = new CardButton(color, number);
                    button.setDisable(true);
                    if (player.getPlayerNumber() % 2 == 0) {  //even
                        button.setPrefSize(CARD_HEIGHT, CARD_LENGTH);
                    } else {      //odd
                        button.setPrefSize(CARD_LENGTH, CARD_HEIGHT);
                    }

                    if(player.getPlayerNumber() == 1) {
                        setHandCardColor(button, color);
                    }

                    button.setOnAction(mouseEvent2 -> {
                        if (discardPile.add(player.getHand().playCard(color, number))) {
                            player.getHand().remove(player.getHand().playCard(color, number));  //remove hand from the hand (the Model)
                            player.getHandGrid().remove(button);                                //remove card from handGrid (the View)
                            updatePlayByPlayLabel(player.getPlayerNumber(), true, player.getNextPlayerNumber());
                            checkForUno(player);

                            //disable cards in turnplayer's hand to prevent them from playing more than 1 card in a turn
                            player.getHandGrid().disableAll();
                            strToColor(color);
                            //set the color of the text on the discard pile
                            if (topColor.equals(YELLOW)) {
                                cardText.setTextFill(BLACK);
                            } else {
                                cardText.setTextFill(WHITE);
                            }
                            //set the text on the discard pile
                            if (number == 10) {
                                cardText.setText("(discard pile)\n(" + discardPile.size() + ")\nSKIP");
                                playingField.setSkip1Player(true);

                            } else if (number == 11) {
                                cardText.setText("(discard pile)\n(" + discardPile.size() + ")\nREVERSE");
                            } else if (number == 12) {
                                cardText.setText("(discard pile)\n(" + discardPile.size() + ")\nDRAW 2");
                                playingField.setSkip1Player(true);

                            } else if (number == 99) {
                                cardText.setText("(discard pile)\n(" + discardPile.size() + ")\nWILD");
                            } else if (number == 100) {
                                cardText.setText("(discard pile)\n(" + discardPile.size() + ")\nWILD DRAW 4");
                            } else {
                                cardText.setText("(discard pile)\n(" + discardPile.size() + ")\n" + number);
                            }
                            //update the playingField's fields
                            playingField.updateColor(topColor);
                            playingField.updateNum(number);

                            //Check if the CARD played has any special effects!
                            if (topColor.equals(BLACK)) {
                                if (number == 100) {      //wild draw 4
                                    //the next player can challenge the turnPlayer.
                                    System.out.println("challenge window appeared.");
                                    challengeWindow(player.getNextPlayerNumber(), player.getPlayerNumber());
                                    if(challengerWon){
                                        //if "yes" was pressed and the move was illegal
                                        unoDeckButtonAction2(player, 4);
                                        unoDeckButton.setDisable(false);
                                        unoDeckButton.fire();
                                        challengerWon = false;

                                    }else if(challengerLost){
                                        //if "yes" was pressed and the move WAS legal
                                        unoDeckButtonAction2(player.getNextPlayer(), 6);
                                        unoDeckButton.setDisable(false);
                                        unoDeckButton.fire();
                                        challengerLost = false;

                                    }else {
                                        //if "no" was pressed
                                        unoDeckButtonAction2(player.getNextPlayer(), 4);
                                        unoDeckButton.setDisable(false);
                                        unoDeckButton.fire();
                                    }
                                }
                                popUpWindow(player);

                            } else if (number == 12) {     //DRAW 2
                                draw2 = true;
                                unoDeckButtonAction2(player.getNextPlayer(), 2);
                                unoDeckButton.setDisable(false);
                                unoDeckButton.fire();
                                draw2 = false;
                                player.setMyTurn(false);
                                playingField.prepNextPlayer();  //Player made their move.
                                synchronized (player) {
                                    try {
                                        player.wait();      //now, player waits for playingField to update the turnOrder
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                player.setMyTurn(false);
                                playingField.prepNextPlayer();  //Player made their move.
                                synchronized (player) {
                                    try {
                                        player.wait();      //now, player waits for playingField to update the turnOrder
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                        else{
                            updatePlayByPlayLabel(player.getPlayerNumber(), false, player.getNextPlayerNumber());
                        }
                    });

                    if(player.getPlayerNumber() == 1) {
                        //set the color of the text on the card Button
                        if (color.equals("YELLOW")) {
                            button.setTextFill(BLACK);
                        } else {
                            button.setTextFill(WHITE);
                        }
                        //set the text on the card Button
                        if (number == 10) {
                            button.setText(color + "\nSKIP");
                        } else if (number == 11) {
                            button.setText(color + "\nREVERSE");
                        } else if (number == 12) {
                            button.setText(color + "\nDRAW 2");
                        } else if (number == 99) {
                            button.setText("WILD\nCARD");
                        } else if (number == 100) {
                            button.setText("WILD\nDRAW 4");
                        } else {
                            button.setText(color + "\n" + number);
                        }
                        button.setTextAlignment(TextAlignment.CENTER);
                    }else{
                        button.setText("UNO!");     //for everyone other than player 1, no one can see the image.
                    }
                    int lastSlot = player.getHandGrid().getSize();
                    player.getHandGrid().add(button, lastSlot, player.getPlayerNumber());   //hand should just be 1 ROW
                }   //curly brace of the for loop
                System.out.println("Player #" + player.getPlayerNumber() + "'s new hand size: " + player.getHand().getSize());
                if(someoneHasUno){
                    unoTrackerLabel.setText("Player " + playerWithUno + " did not press the UNO! button in time!");
                    playByPlayLabel.setText(playByPlayLabel.getText() + "\nPlayer " + playerWithUno + " drew 2 cards.");
                    someoneHasUno = false;
                    playerWithUno = 0;
                }
            }       //curly brace of the if statement.
            unoDeckButton.setDisable(true);
        });
    }

    /**
     * When the start method is finished, this method is called next.
     * It prints out GAME OVER and terminates the program.
     */
    public void stop() {
        System.out.println("GAME OVER");
        System.exit(1);
    }

    /**
     * THIS METHOD IS CALLED IN PLAYER
     * When a player has won, a victoryWindow appears.
     * In the window, a button labeled "OK" is there.
     * If the OK button is pressed, this method is called.
     * It goes to the stop() method and then everything exits.
     */
    public static void endGame(){
        mainStage.close();
    }

    /**
     * Calls the default constructor and starts the program.
     * @param args  command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * SUBCLASS #4 OF MODELGUI
     *
     * The CriticalRegion class is only the critical region for
     * UnoThread objects, which are threads. It regulates
     * when to disable/enable the unoButton, since there
     * is a 2.5 second time frame in which it's enabled
     * and can be pressed when a player has Uno!
     *
     * @author Logan Wong
     */
    public class CriticalRegion {
        public CriticalRegion(){ }

        /**
         * The Critical region for the UnoThread class.
         * It makes the thread wait for 2.5 seconds. If the
         * UNO button isn't pressed in time, it's disabled,
         * and then the UnoDeckButton is fired and
         * unoButtonAction2 is called and the player with
         * UNO is forced to draw 2 cards.
         */
        private synchronized void action(){
            while(UnoThread.getReady() == false) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                u.sleep(2500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(UnoThread.getReady()){
                unoButton.setDisable(true);
                UnoThread.changeReady(false);
                System.out.println("UNO! was not pressed in time! It's now disabled.");
                Platform.runLater(()->{
                    unoTrackerLabel.setText("Player " + playerWithUno + " did not press the UNO! button in time!");
                });

                for(Player p : turnOrder){
                    if(p.getPlayerNumber() == playerWithUno){
                        Platform.runLater(()->{
                            draw2 = true;
                            unoDeckButtonAction2(p, 2);
                            unoDeckButton.setDisable(false);
                            unoDeckButton.fire();
                            draw2 = false;

                            // theDeckButton is disabled after the player who formerly had uno draws 2 cards,
                            // so re-enable it for the next player it IF necessary!
                            assert ModelGUI.turnOrder.peek() != null;
                            if(!topColor.equals(BLACK) &&
                                    ModelGUI.turnOrder.peek().getHand().needToDraw(playingField.getTopColor(), playingField.getTopNum())){
                                ModelGUI.setUnoDeckButton(false);
                                assert ModelGUI.turnOrder.peek() != null;
                                ModelGUI.unoDeckButtonAction(ModelGUI.turnOrder.peek());
                            }
                        });
                        break;
                    }
                }
            }
        }

        /**
         * Notifies the thread waiting in the critical region
         */
        private synchronized void stopWaiting(){
            notifyAll();
        }
    }


    /**
     * SUBCLASS #5 OF MODELGUI
     * The UnoThread subclass is the thread that
     * controls when the unoButton disables after 2.5 seconds.
     *
     * @author Logan Wong
     */
    public static class UnoThread extends Thread{
        private static boolean ready;
        public UnoThread(){
            ready = false;
        }

        /**
         * Gets the ready field
         * @return  ready
         */
        private static boolean getReady(){
            return ready;
        }

        /**
         * Changes ready's value.
         * @param b  true or false
         */
        private static void changeReady(boolean b){
            ready = b;
        }

        /**
         * Calls the critical region's method that
         * acts as the critical region for this thread.
         */
        public void run() {
            while(true) {
                criticalRegion.action();
            }
        }
    }

    /*
     * This thread starts when a Challenge Window
     * pops up AND the player who's challenging the
     * Wild Draw 4 is (an AI player and) NOT player 1.
     */
    public static class ChallengeAiThread extends Thread {
        public ChallengeAiThread() {

        }

        /*
         * Wait 1 sec. Then, if the turn Player's hand size
         * is greater than or equal to 5, challenge.
         * If the turn Player just drew a card, and now they're
         * playing (a wild draw 4), DON'T challenge,
         * OR if the challengeResponse() method randomly
         * generated a 0, DON'T challenge.
         * However, if a 1 was generated, DO challenge.
         */
        public void run() {
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (cardJustDrawn || challengeResponse() == 0 || turnOrder.peek().getHand().getSize() <= 1) {
                Platform.runLater(()->{
                    no.setDisable(false);
                    no.fire();
                });
            } else {
                Platform.runLater(()->{
                    yes.setDisable(false);
                    yes.fire();
                });
            }
            cardJustDrawn = false;
            try{
                sleep(1000);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    }

}//last curly brace
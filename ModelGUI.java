package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Button;
import static javafx.scene.paint.Color.*;

import java.nio.file.StandardOpenOption;
import java.util.Scanner;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;

//This is the VIEW and the CONTROLLER (I think)
public class ModelGUI extends Application implements Observer<Player, Hand> {
    private final int cardLength = 80;
    private final int cardHeight = 90;
    private Player p1;
    private Player p2;
    private Player p3;
    private Player p4;
    private Deck unoDeck;
    private int numOfPlayers;

    //private boolean blackCardPlayed;
    //private static boolean wildCardChosen;
    private static boolean colorPicked;

    private static DiscardPile discardPile;
    private static Rectangle cardColor;
    /**
     * cardText is the text on the discard pile GUI Model!
     */
    private static Label cardText;
    /**topColor keeps track of the GUI's color; and it MAY differ
     * from the model's color (AKA the discardPile's topColor)
     * when a black card is played!
     */
    private static Color topColor;
    private StackPane discardPileStackPane;

    private boolean aGameIsInProgress;
    public static ConcurrentLinkedQueue<Player> turnOrder;
    private Stack<Player> tempHolder;
    public PlayingField playingField;

    public ModelGUI(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("How many TOTAL players do you want? (2, 3, or 4)");
        String temp = scanner.nextLine();
        while(!temp.equals("2") ^ !temp.equals("3") ^ !temp.equals("4")){   //exclusive OR
            System.out.println("That was not a valid number of players! Please try again.");
            temp = scanner.nextLine();
        }
        numOfPlayers = Integer.parseInt(temp);
    }

    @Override
    public void init(){
        unoDeck = new Deck(numOfPlayers);    //1,2,3 or 4 players
        unoDeck.shuffleWell(108);

        // Initialize Players and give them *empty* hands.
        Hand[] hands = unoDeck.distributeHands(unoDeck);
        p1 = new Player(hands[0], 1);
        p2 = new Player(hands[1], 2);
        if(numOfPlayers >= 3){
            p3 = new Player(hands[2], 3);
        }
        if(numOfPlayers == 4){
            p4 = new Player(hands[3], 4);
        }

        // make discard pile
        discardPile = new DiscardPile();
        discardPile.add(unoDeck.beginGame(unoDeck.getDeckStack().size()));
        aGameIsInProgress = true;
        //wildCardChosen = false;
        //blackCardPlayed = false;
        colorPicked = false;
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
     * @param first
     */
    public void strToColor(String first){
        //first should be discardPile.getTopColor();
        if(first.equals("RED")){
            topColor = RED;
            discardPile.setTopColor("RED");
        }else if(first.equals("YELLOW")){
            topColor = YELLOW;
            discardPile.setTopColor("YELLOW");
        }else if(first.equals("BLUE")){
            topColor = BLUE;
            discardPile.setTopColor("BLUE");
        }else if(first.equals("GREEN")){
            topColor = GREEN;
            discardPile.setTopColor("GREEN");
        }else{
            topColor = BLACK;
            //stuff used to be here.
            cardColor.setFill(topColor);
            //PopUpWindow pop = new PopUpWindow(playingField);
            //pop.run();
            Player turnPlayer = turnOrder.peek();
            Stage stage2 = new Stage();
            HBox hBox = new HBox();
            BorderPane borderPane = new BorderPane();
            borderPane.setTop(new Label("Click on the color you want!"));
            Button red = new Button();
            red.setStyle("-fx-background-color: #ff0000");  //red
            red.setPrefSize(50, 50);
            Button yellow = new Button();
            yellow.setStyle("-fx-background-color: #FFFF00"); //yellow
            yellow.setPrefSize(50, 50);
            Button blue = new Button();
            blue.setStyle("-fx-background-color: #0000FF"); //blue
            blue.setPrefSize(50, 50);
            Button green = new Button();
            green.setStyle("-fx-background-color: #008000"); //green
            green.setPrefSize(50, 50);

            //event handling
            red.setOnMouseClicked(mouseEvent2 -> {
                //rectangle aka topColor is index 0
                //label aka cardtext is index 1
                ModelGUI.updateDiscardPileStackPane(RED, "RED");
                stage2.close();
                playingField.setBlackCardPlayed(0);
            });
            yellow.setOnMouseClicked(mouseEvent2 -> {
                ModelGUI.updateDiscardPileStackPane(YELLOW, "YELLOW");
                stage2.close();
                playingField.setBlackCardPlayed(0);
            });
            green.setOnMouseClicked(mouseEvent2 -> {
                ModelGUI.updateDiscardPileStackPane(GREEN, "GREEN");
                stage2.close();
                playingField.setBlackCardPlayed(0);
            });
            blue.setOnMouseClicked(mouseEvent2 -> {
                ModelGUI.updateDiscardPileStackPane(BLUE, "BLUE");
                stage2.close();
                playingField.setBlackCardPlayed(0);
            });

            //prevent stage2 from being closed if the RED X button is clicked!
            Platform.runLater(() -> stage2.setOnCloseRequest(evt -> {
                evt.consume();
            }));

            hBox.getChildren().addAll(red, yellow, blue, green);
            borderPane.setCenter(hBox);
            stage2.setScene(new Scene(borderPane));
            stage2.show();
        }
    }

    public static void updateDiscardPileStackPane(Color newColor, String newColorStr){
        topColor = newColor;
        discardPile.setTopColor(newColorStr);
        if(newColor.equals(YELLOW)){
            cardText.setTextFill(BLACK);
        }else {
            cardText.setTextFill(WHITE);
        }
        cardColor.setFill(newColor);
        //wildCardChosen = true;
        colorPicked = true;
    }

    /**
     * makes the buttons in the hands have color!
     * @param b
     * @param color
     */
    private static void setHandCardColor(Button b, String color){
        if(color.equals("RED")){
            b.setStyle("-fx-background-color: #ff0000");  //red
        }else if(color.equals("YELLOW")){
            b.setStyle("-fx-background-color: #FFFF00"); //yellow
        }else if(color.equals("BLUE")){
            b.setStyle("-fx-background-color: #0000FF"); //blue
        } else if (color.equals("GREEN")){
            b.setStyle("-fx-background-color: #008000"); //green
        }else{
            b.setStyle("-fx-background-color: #000000"); //black
        }
    }

    public boolean isAGameIsInProgress(){
        return aGameIsInProgress;
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("UNO");
        BorderPane borderPane = new BorderPane();

        //PLAYER 1
        VBox bottomVBox = new VBox();
        Label p1Label = new Label();
        p1Label.setText("PLAYER 1");
        p1Label.setAlignment(Pos.CENTER);
        HandGrid p1HandGrid = new HandGrid(1);
        //Putting initial cards in hand... each card is a button!
        for(int i = 0; i < 7; ++i){
            Button button = new Button();

            button.setDisable(true);            //disable/enable PLAYER 1's cards HERE!

            button.setPrefSize(cardLength, cardHeight);
            int number = p1.getHand().getHand().get(i).getNumber();
            String color = p1.getHand().getHand().get(i).getColor();
            setHandCardColor(button, color);

            button.setOnMouseClicked(mouseEvent -> {
                if( discardPile.add(p1.getHand().playCard(color, number)) ){
                    p1.getHand().remove(p1.getHand().playCard(color, number));  //remove hand from the hand (the Model)
                    p1HandGrid.remove(button);        //remove card from handGrid (the View)
                    System.out.println("A Black card was played:  " + p1.getHand().getBlackCardPlayed());
                    //disable cards in turnplayer's hand to prevent them from playing more than 1 card in a turn
                    p1.getHandGrid().disableAll(p1);
                    strToColor(color);
                    cardColor.setFill(topColor);
                    if(topColor.equals(YELLOW)){
                        cardText.setTextFill(BLACK);
                    }else{
                        cardText.setTextFill(WHITE);
                    }
                    cardText.setText("(discard pile)\n       " + number);

                    if(topColor.equals(BLACK)){
                        synchronized (this) {
                            notifyAll();        //gets the p1 waiting in monitor to stop waiting (but also all the others too)
                        }
                        if(p1.getHand().getBlackCardPlayed() == 1) {
                            while(colorPicked != true){
                                System.out.println("waiting to choose a color");
                            }
                            colorPicked = false;
                            p1.setMyTurn(false);
                            playingField.prepNextPlayer();  //P1 made their move.
                            synchronized (p1){
                                try {
                                    p1.wait();      //now, player 1 waits for playingField to update the turnOrder
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                        }

                    }else{
                        p1.setMyTurn(false);
                        playingField.prepNextPlayer();  //P1 made their move.
                        synchronized (p1){
                            try {
                                p1.wait();      //now, player 1 waits for playingField to update the turnOrder
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    System.out.println("Player 1's turn is OVER.");
                }
            });

            if(color.equals("YELLOW")){
                button.setTextFill(BLACK);
            }else{
                button.setTextFill(WHITE);
            }
            button.setText(color + " " + number);
            p1HandGrid.add(button, i, 1);   //hand should just be 1 ROW
        }
        p1.setHandGrid(p1HandGrid);
        bottomVBox.getChildren().addAll(p1Label, p1HandGrid.getGridPane());

        //PLAYER 2
        HBox leftHBox = new HBox();
        Label p2Label = new Label();
        p2Label.setText("PLAYER 2");
        p2Label.setAlignment(Pos.CENTER);
        HandGrid p2HandGrid = new HandGrid(2);
        for(int i = 0; i < 7; ++i){
            Button button = new Button();

            button.setDisable(true);

            button.setPrefSize(cardHeight, cardLength);
            int number = p2.getHand().getHand().get(i).getNumber();
            String color = p2.getHand().getHand().get(i).getColor();
            setHandCardColor(button, color);

            button.setOnMouseClicked(mouseEvent -> {
                if( discardPile.add(p2.getHand().playCard(color, number)) ) {
                    p2.getHand().remove(p2.getHand().playCard(color, number));
                    strToColor(color);

                    cardColor.setFill(topColor);
                    if(topColor.equals(YELLOW)){
                        cardText.setTextFill(BLACK);
                    }else{
                        cardText.setTextFill(WHITE);
                    }
                    cardText.setText("(discard pile)\n       " + number);
                    p2HandGrid.remove(button);        //remove card from hand.
                }
            });

            if(color.equals("YELLOW")){
                button.setTextFill(BLACK);
            }else{
                button.setTextFill(WHITE);
            }
            button.setText(color + " " + number);
            p2HandGrid.add(button, i ,2);   //hand should just be 1 COL
        }
        p2.setHandGrid(p2HandGrid);
        leftHBox.getChildren().addAll( p2HandGrid.getGridPane(), p2Label);

        //PLAYER 3
        VBox topVBox = new VBox();
        HandGrid p3HandGrid = new HandGrid(3);
        if(numOfPlayers >= 3) {
            Label p3Label = new Label();
            p3Label.setText("PLAYER 3");
            p3Label.setAlignment(Pos.CENTER);
            for (int i = 0; i < 7; ++i) {
                Button button = new Button();

                button.setDisable(true);

                button.setPrefSize(cardLength, cardHeight);
                int number = p3.getHand().getHand().get(i).getNumber();
                String color = p3.getHand().getHand().get(i).getColor();
                setHandCardColor(button, color);
                button.setOnMouseClicked(mouseEvent -> {
                    if( discardPile.add(p3.getHand().playCard(color, number)) ){
                        p3.getHand().remove(p3.getHand().playCard(color, number));
                        strToColor(color);

                        cardColor.setFill(topColor);
                        if(topColor.equals(YELLOW)){
                            cardText.setTextFill(BLACK);
                        }else{
                            cardText.setTextFill(WHITE);
                        }
                        cardText.setText("(discard pile)\n       " + number);
                        p3HandGrid.remove(button);        //remove card from hand.
                        p3.setMyTurn(false);
                        turnOrder.peek().setMyTurn(true);
                        System.out.println("Is it the next Player's turn?   " + turnOrder.peek().getTurn());
                    }
                });

                if(color.equals("YELLOW")){
                    button.setTextFill(BLACK);
                }else{
                    button.setTextFill(WHITE);
                }
                button.setText(color + " " + number);
                p3HandGrid.add(button, i, 3);   //hand should just be 1 ROW
            }
            p3.setHandGrid(p3HandGrid);
            topVBox.getChildren().addAll(p3HandGrid.getGridPane(), p3Label);
        }

        //PLAYER 4
        HBox rightHBox = new HBox();
        HandGrid p4HandGrid = new HandGrid(4);
        if(numOfPlayers == 4) {
            Label p4Label = new Label();
            p4Label.setText("PLAYER 4");
            p4Label.setAlignment(Pos.CENTER);
            for (int i = 0; i < 7; ++i) {
                Button button = new Button();

                button.setDisable(true);

                button.setPrefSize(cardHeight, cardLength);
                int number = p4.getHand().getHand().get(i).getNumber();
                String color = p4.getHand().getHand().get(i).getColor();
                setHandCardColor(button, color);
                button.setOnMouseClicked(mouseEvent -> {
                    if( discardPile.add(p4.getHand().playCard(color, number)) ){
                        p4.getHand().remove(p4.getHand().playCard(color, number));
                        strToColor(color);

                        cardColor.setFill(topColor);
                        if(topColor.equals(YELLOW)){
                            cardText.setTextFill(BLACK);
                        }else{
                            cardText.setTextFill(WHITE);
                        }
                        cardText.setText("(discard pile)\n       " + number);
                        p4HandGrid.remove(button);        //remove card from hand.
                        p4.setMyTurn(false);
                        turnOrder.peek().setMyTurn(true);
                        System.out.println("Is it the next Player's turn?   " + turnOrder.peek().getTurn());
                    }
                });

                if (color.equals("YELLOW")) {
                    button.setTextFill(BLACK);
                } else {
                    button.setTextFill(WHITE);
                }
                button.setText(color + " " + number);
                p4HandGrid.add(button, i,0);   //hand should just be 1 COL
            }
            p4.setHandGrid(p4HandGrid);
            rightHBox.getChildren().addAll(p4Label, p4HandGrid.getGridPane());
        }

        playingField = new PlayingField(numOfPlayers, p1, p2, p3, p4);
        turnOrder = new ConcurrentLinkedQueue<>();
        tempHolder = new Stack<>();
        turnOrder.add(p1);
        turnOrder.add(p2);
        p1.setNextPlayer(p2);
        if(numOfPlayers >=3){
            turnOrder.add(p3);
            p2.setNextPlayer(p3);
        }else{
            p2.setNextPlayer(p1);
        }
        if(numOfPlayers == 4){
            turnOrder.add(p4);
            p3.setNextPlayer(p4);
        }else{
            p3.setNextPlayer(p1);
        }
        //Start the PLAYER threads
        for(Player p : turnOrder){
            p.setPlayingField(playingField);
            p.start();
        }



        p1.setMyTurn(true);
        playingField.prepNextPlayer();







        /**centerPane holds discard pile and the deck
         * discardpile is a Stackpane w/ rectangle whose color and label change
         * deck is a button. When clicked, add a card to hand*/
        FlowPane centerPane = new FlowPane();
        discardPileStackPane = new StackPane();
        cardColor = new Rectangle(cardLength, cardHeight);
        cardText = new Label();
        if(discardPile.getTopColor().equals("BLACK")){
            //playingField.setBlackCardPlayed(1);
            System.out.println("The first top card is a wild card; player 1 chooses the starting color!");
            topColor = BLACK;
            cardColor.setFill(topColor);
            PopUpWindow wildWindow = new PopUpWindow(playingField);
            wildWindow.start();

        }else {
            strToColor(discardPile.getTopColor());
        }
        cardColor.setFill(topColor);

        int topNumber = discardPile.getTopNumber();
        if(discardPile.getTopColor().equals("YELLOW")){
            cardText.setTextFill(BLACK);
        }else{
            cardText.setTextFill(WHITE);
        }
        cardText.setText("(discard pile)\n       " + topNumber);
        Button unoDeck = new Button();
        unoDeck.setText("Deck");
        unoDeck.setPrefSize(cardLength, cardHeight);
        //only set disable to false when player needs a new card
        unoDeck.setDisable(true);

        //the Uno Button
        Button unoButton = new Button();
        unoButton.setText("UNO!");
        unoButton.setDisable(true);
        unoButton.setPrefSize(50, 50);

        //if the top card is a BLACK card
        /**if(discardPile.getTopColor().equals(BLACK)){
            blackCardPlayed = true;
            cardColor.setFill(BLACK);
            Stage stage2 = new Stage();
            HBox hBox = new HBox();
            BorderPane bp2 = new BorderPane();
            bp2.setTop(new Label("Click on the color you want!"));
            Button red = new Button();
            red.setStyle("-fx-background-color: #ff0000");  //red
            Button yellow = new Button();
            yellow.setStyle("-fx-background-color: #FFFF00"); //yellow
            Button blue = new Button();
            blue.setStyle("-fx-background-color: #0000FF"); //blue
            Button green = new Button();
            green.setStyle("-fx-background-color: #008000"); //green

            //event handling, and also a critical region??
           // synchronized (this) {
                red.setOnMouseClicked(mouseEvent -> {
                    topColor = RED;
                    discardPile.setTopColor("RED");
                    cardColor.setFill(RED);
                    stage2.close();
                });
                yellow.setOnMouseClicked(mouseEvent -> {
                    topColor = YELLOW;
                    discardPile.setTopColor("YELLOW");
                    cardColor.setFill(YELLOW);
                    stage2.close();
                });
                green.setOnMouseClicked(mouseEvent -> {
                    topColor = GREEN;
                    discardPile.setTopColor("GREEN");
                    cardColor.setFill(GREEN);
                    stage2.close();
                });
                blue.setOnMouseClicked(mouseEvent -> {
                    topColor = BLUE;
                    discardPile.setTopColor("BLUE");
                    cardColor.setFill(BLUE);
                    stage2.close();
                });
            //}

            hBox.getChildren().addAll(red, yellow, blue, green);
            borderPane.setCenter(hBox);
            stage2.setScene(new Scene(bp2));
            try{
                wait(9000);
            }catch(InterruptedException ie){
                ie.printStackTrace();
            }
            stage2.show();
        }*/

        //position everything I guess
        discardPileStackPane.getChildren().addAll(cardColor, cardText);
        centerPane.getChildren().addAll(discardPileStackPane, unoDeck, unoButton);
        borderPane.setCenter(centerPane);
        borderPane.setBottom(bottomVBox);
        borderPane.setLeft(leftHBox);
        borderPane.setTop(topVBox);
        borderPane.setRight(rightHBox);
        primaryStage.setScene(new Scene(borderPane));
        primaryStage.setWidth(1000);
        primaryStage.setHeight(600);
        // exit program when the red X button is pressed
        primaryStage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(1);
        });
        primaryStage.show();
    }

    public void reverseTurnOrder(){
        for(int i = 0; i < 4; ++i) {
            tempHolder.push(turnOrder.remove());
        }
        for(int i = 0; i < 4; ++i){
            turnOrder.add(tempHolder.pop());
        }
    }

    @Override
    public void update(Player p, Hand h) {

    }

    //terminate the program
    public void stop(){
        System.out.println("stopped");
    }


    public static void main(String[] args) {
        launch(args);
    }


//last curly brace is below
}

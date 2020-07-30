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
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

//This is the VIEW and the CONTROLLER (I think)
public class ModelGUI extends Application {
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

    //Uno Button related stuff
    private static Button unoButton;
    private static int playerWithUno;
    private static CriticalRegion criticalRegion;
    private UnoThread u;

    private static final int cardLength = 80;
    private static final int cardHeight = 90;

    private StackPane discardPileStackPane;

    public static ConcurrentLinkedQueue<Player> turnOrder;
    public static boolean gameOver;

    /**
     * default constructor which is called before init().
     * Gets the total number of players that the user
     * wants to play with.
     */
    public ModelGUI() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("How many TOTAL players do you want? (2, 3, or 4)");
        String temp = scanner.nextLine();
        while (!temp.equals("2") ^ !temp.equals("3") ^ !temp.equals("4")) {   //exclusive OR
            System.out.println("That was not a valid number of players! Please try again.");
            temp = scanner.nextLine();
        }
        numOfPlayers = Integer.parseInt(temp);
    }

    @Override
    public void init() {
        unoDeck = new Deck(numOfPlayers);    //1,2,3 or 4 players
        unoDeck.shuffleWell(108);

        // Initialize Players and give them *empty* hands.
        Hand[] hands = unoDeck.distributeHands(unoDeck);
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
        gameOver = false;
        draw2 = false;
        playerWithUno = 0;
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
     * @param first
     */
    public static void strToColor(String first) {
        //first should be discardPile.getTopColor();
        if (first.equals("RED")) {
            topColor = RED;
            discardPile.setTopColor("RED");
        } else if (first.equals("YELLOW")) {
            topColor = YELLOW;
            discardPile.setTopColor("YELLOW");
        } else if (first.equals("BLUE")) {
            topColor = BLUE;
            discardPile.setTopColor("BLUE");
        } else if (first.equals("GREEN")) {
            topColor = GREEN;
            discardPile.setTopColor("GREEN");
        } else {
            topColor = BLACK;
            cardColor.setFill(topColor);
        }
    }

    public static void updateDiscardPileStackPane(Color newColor, String newColorStr) {
        topColor = newColor;
        discardPile.setTopColor(newColorStr);
        if (newColor.equals(YELLOW)) {
            cardText.setTextFill(BLACK);
        } else {
            cardText.setTextFill(WHITE);
        }
        cardColor.setFill(newColor);
        playingField.updateColor(topColor);
    }

    /**
     * makes the buttons in the hands have color!
     *
     * @param b
     * @param color
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
            playerWithUno = player.getPlayerNumber();
            System.out.println("SOMEONE HAS UNO!!!");
            unoButton.setDisable(false);
            UnoThread.changeReady(true);
            criticalRegion.stopWaiting();
            playingField.setInvalidCardDrawn(true);
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("UNO");
        BorderPane borderPane = new BorderPane();

        //PLAYER 1
        VBox bottomVBox = new VBox();
        Label p1Label = new Label();
        p1Label.setText("PLAYER 1");
        p1Label.setAlignment(Pos.CENTER);
        HandGrid p1HandGrid = new HandGrid(1);
        //Putting initial cards in hand... each card is a button!
        for (int i = 0; i < 7; ++i) {
            Button button = new Button();
            button.setDisable(true);            //disable/enable PLAYER 1's cards HERE!
            button.setPrefSize(cardLength, cardHeight);
            int number = p1.getHand().getHand().get(i).getNumber();
            String color = p1.getHand().getHand().get(i).getColor();
            setHandCardColor(button, color);

            button.setOnMouseClicked(mouseEvent -> {
                if (discardPile.add(p1.getHand().playCard(color, number))) {
                    p1.getHand().remove(p1.getHand().playCard(color, number));  //remove hand from the hand (the Model)
                    p1HandGrid.remove(button);                                 //remove card from handGrid (the View)
                    checkForUno(p1);

                    //disable cards in turnplayer's hand to prevent them from playing more than 1 card in a turn
                    p1.getHandGrid().disableAll(p1);
                    strToColor(color);
                    cardColor.setFill(topColor);
                    //set the color of the text on the discard pile
                    if (topColor.equals(YELLOW)) {
                        cardText.setTextFill(BLACK);
                    } else {
                        cardText.setTextFill(WHITE);
                    }
                    //change the text on the discard pile stack pane
                    if (number == 10) {
                        cardText.setText("(discard pile)\n(" + discardPile.size() + ")\n     SKIP");
                        playingField.setSkip1Player(true);
                    } else if (number == 11) {
                        cardText.setText("(discard pile)\n(" + discardPile.size() + ")\n     REVERSE");
                    } else if (number == 12) {
                        cardText.setText("(discard pile)\n(" + discardPile.size() + ")\n     DRAW 2");
                    } else if (number == 99) {
                        cardText.setText("(discard pile)\n(" + discardPile.size() + ")\n     WILD");
                    } else if (number == 100) {
                        cardText.setText("(discard pile)\n(" + discardPile.size() + ")\n  WILD DRAW 4");
                    } else {
                        cardText.setText("(discard pile)\n(" + discardPile.size() + ")\n       " + number);
                    }
                    //update the playingField fields
                    playingField.updateColor(topColor);
                    playingField.updateNum(number);

                    //Check if the CARD played has a special effect!
                    if (topColor.equals(BLACK)) {
                        if (number == 100) {      //wild draw 4
                            unoDeckButtonAction2(p1.getNextPlayer(), 4);
                            unoDeckButton.setDisable(false);
                            unoDeckButton.fire();
                        }
                        PopUpWindow pop = new PopUpWindow(playingField);
                        pop.run();
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
                button.setText(color + " " + number);
            }
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
        for (int i = 0; i < 7; ++i) {
            Button button = new Button();
            button.setDisable(true);
            button.setPrefSize(cardHeight, cardLength);
            int number = p2.getHand().getHand().get(i).getNumber();
            String color = p2.getHand().getHand().get(i).getColor();
            setHandCardColor(button, color);

            button.setOnMouseClicked(mouseEvent -> {
                if (discardPile.add(p2.getHand().playCard(color, number))) {
                    p2.getHand().remove(p2.getHand().playCard(color, number));  //remove hand from the hand (the Model)
                    p2HandGrid.remove(button);
                    checkForUno(p2);
                    //remove card from handGrid (the View)
                    //disable cards in turnplayer's hand to prevent them from playing more than 1 card in a turn
                    p2.getHandGrid().disableAll(p2);
                    strToColor(color);
                    cardColor.setFill(topColor);
                    //set the color of the text on discard pile
                    if (topColor.equals(YELLOW)) {
                        cardText.setTextFill(BLACK);
                    } else {
                        cardText.setTextFill(WHITE);
                    }
                    //set the text of the discard pile
                    if (number == 10) {
                        cardText.setText("(discard pile)\n(" + discardPile.size() + ")\n     SKIP");
                        playingField.setSkip1Player(true);
                    } else if (number == 11) {
                        cardText.setText("(discard pile)\n(" + discardPile.size() + ")\n     REVERSE");
                    } else if (number == 12) {
                        cardText.setText("(discard pile)\n(" + discardPile.size() + ")\n     DRAW 2");
                    } else if (number == 99) {
                        cardText.setText("(discard pile)\n(" + discardPile.size() + ")\n     WILD");
                    } else if (number == 100) {
                        cardText.setText("(discard pile)\n(" + discardPile.size() + ")\n  WILD DRAW 4");
                    } else {
                        cardText.setText("(discard pile)\n(" + discardPile.size() + ")\n       " + number);
                    }
                    //update playingField fields
                    playingField.updateColor(topColor);
                    playingField.updateNum(number);

                    //Check if the CARD played has a special effect!
                    if (topColor.equals(BLACK)) {
                        if (number == 100) {      //WILD DRAW 4
                            unoDeckButtonAction2(p2.getNextPlayer(), 4);
                            unoDeckButton.setDisable(false);
                            unoDeckButton.fire();
                        }
                        PopUpWindow pop = new PopUpWindow(playingField);
                        pop.run();

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
            });

            //Set color of text on the card button (view)
            if (color.equals("YELLOW")) {
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
                button.setText(color + " " + number);
            }
            p2HandGrid.add(button, i, 2);   //hand should just be 1 COL
        }
        p2.setHandGrid(p2HandGrid);
        leftHBox.getChildren().addAll(p2HandGrid.getGridPane(), p2Label);

        //PLAYER 3
        VBox topVBox = new VBox();
        HandGrid p3HandGrid = new HandGrid(3);
        if (numOfPlayers >= 3) {
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
                    if (discardPile.add(p3.getHand().playCard(color, number))) {
                        p3.getHand().remove(p3.getHand().playCard(color, number));      //remove hand from the hand (the Model)
                        p3HandGrid.remove(button);                                      //remove card from handGrid (the View)
                        checkForUno(p3);
                        //disable cards in turnplayer's hand to prevent them from playing more than 1 card in a turn
                        p3.getHandGrid().disableAll(p3);
                        strToColor(color);
                        cardColor.setFill(topColor);
                        //Set color of text on the discard pile
                        if (topColor.equals(YELLOW)) {
                            cardText.setTextFill(BLACK);
                        } else {
                            cardText.setTextFill(WHITE);
                        }
                        //set the text on the discard pile
                        if (number == 10) {
                            cardText.setText("(discard pile)\n(" + discardPile.size() + ")\n     SKIP");
                            playingField.setSkip1Player(true);

                        } else if (number == 11) {
                            cardText.setText("(discard pile)\n(" + discardPile.size() + ")\n     REVERSE");
                        } else if (number == 12) {
                            cardText.setText("(discard pile)\n(" + discardPile.size() + ")\n     DRAW 2");
                        } else if (number == 99) {
                            cardText.setText("(discard pile)\n(" + discardPile.size() + ")\n     WILD");
                        } else if (number == 100) {
                            cardText.setText("(discard pile)\n(" + discardPile.size() + ")\n  WILD DRAW 4");
                        } else {
                            cardText.setText("(discard pile)\n(" + discardPile.size() + ")\n       " + number);
                        }
                        //update playingField fields
                        playingField.updateColor(topColor);
                        playingField.updateNum(number);

                        //Check if the CARD played has a special effect
                        if (topColor.equals(BLACK)) {
                            if (number == 100) {      //WILD DRAW 4
                                unoDeckButtonAction2(p3.getNextPlayer(), 4);
                                unoDeckButton.setDisable(false);
                                unoDeckButton.fire();
                            }
                            PopUpWindow pop = new PopUpWindow(playingField);
                            pop.run();

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
                });

                //set the color of the text on the card button
                if (color.equals("YELLOW")) {
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
                    button.setText(color + " " + number);
                }
                p3HandGrid.add(button, i, 3);   //hand should just be 1 ROW
            }
            p3.setHandGrid(p3HandGrid);
            topVBox.getChildren().addAll(p3HandGrid.getGridPane(), p3Label);
        }

        //PLAYER 4
        HBox rightHBox = new HBox();
        HandGrid p4HandGrid = new HandGrid(4);
        if (numOfPlayers == 4) {
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
                    if (discardPile.add(p4.getHand().playCard(color, number))) {
                        p4.getHand().remove(p4.getHand().playCard(color, number));      //remove hand from the hand (the Model)
                        p4HandGrid.remove(button);                                      //remove card from handGrid (the View)
                        checkForUno(p4);
                        //disable cards in turnplayer's hand to prevent them from playing more than 1 card in a turn
                        p4.getHandGrid().disableAll(p4);
                        strToColor(color);
                        cardColor.setFill(topColor);
                        //set the color of the text on the discard pile
                        if (topColor.equals(YELLOW)) {
                            cardText.setTextFill(BLACK);
                        } else {
                            cardText.setTextFill(WHITE);
                        }
                        //set the text on the discard pile
                        if (number == 10) {
                            cardText.setText("(discard pile)\n(" + discardPile.size() + ")\n     SKIP");
                            playingField.setSkip1Player(true);

                        } else if (number == 11) {
                            cardText.setText("(discard pile)\n(" + discardPile.size() + ")\n     REVERSE");
                        } else if (number == 12) {
                            cardText.setText("(discard pile)\n(" + discardPile.size() + ")\n     DRAW 2");
                        } else if (number == 99) {
                            cardText.setText("(discard pile)\n(" + discardPile.size() + ")\n     WILD");
                        } else if (number == 100) {
                            cardText.setText("(discard pile)\n(" + discardPile.size() + ")\n  WILD DRAW 4");
                        } else {
                            cardText.setText("(discard pile)\n(" + discardPile.size() + ")\n       " + number);
                        }
                        //update the playingField fields
                        playingField.updateColor(topColor);
                        playingField.updateNum(number);

                        //Check if the CARD played has a special effect
                        if (topColor.equals(BLACK)) {
                            if (number == 100) {      //WILD DRAW 4
                                unoDeckButtonAction2(p4.getNextPlayer(), 4);
                                unoDeckButton.setDisable(false);
                                unoDeckButton.fire();
                            }
                            PopUpWindow pop = new PopUpWindow(playingField);
                            pop.run();

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
                });

                //Set the color of the text on the Card Button
                if (color.equals("YELLOW")) {
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
                    button.setText(color + " " + number);
                }
                p4HandGrid.add(button, i, 4);   //hand should just be 1 COL
            }
            p4.setHandGrid(p4HandGrid);
            rightHBox.getChildren().addAll(p4Label, p4HandGrid.getGridPane());
        }

        //the other buttons/features
        int topNumber = discardPile.getTopNumber();
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

        /**centerPane holds discard pile and the deck
         * discardpile is a Stackpane w/ rectangle whose color and label change
         * deck is a button. When clicked, add a card to hand*/
        FlowPane centerPane = new FlowPane();
        discardPileStackPane = new StackPane();
        cardColor = new Rectangle(cardLength, cardHeight);
        cardText = new Label();
        //if the first card in the discardPile is a wild card (99). It'll never be a 100 b/c the rules say so & I coded it to be so.
        if (discardPile.getTopColor().equals("BLACK")) {
            System.out.println("The first top card is a wild card; player 1 chooses the starting color!");
            p1.getHandGrid().disableAll(p1);
            topColor = BLACK;
            cardColor.setFill(topColor);
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
                cardColor.setFill(RED);
                playingField.updateColor(topColor);
                playingField.updateNum(99);
                stage2.close();
                p1.getHandGrid().enableAll(p1);
            });
            yellow.setOnMouseClicked(mouseEvent -> {
                topColor = YELLOW;
                discardPile.setTopColor("YELLOW");
                cardColor.setFill(YELLOW);
                cardText.setTextFill(BLACK);
                playingField.updateColor(topColor);
                playingField.updateNum(99);
                stage2.close();
                p1.getHandGrid().enableAll(p1);
            });
            green.setOnMouseClicked(mouseEvent -> {
                topColor = GREEN;
                discardPile.setTopColor("GREEN");
                cardColor.setFill(GREEN);
                playingField.updateColor(topColor);
                playingField.updateNum(99);
                stage2.close();
                p1.getHandGrid().enableAll(p1);
            });
            blue.setOnMouseClicked(mouseEvent -> {
                topColor = BLUE;
                discardPile.setTopColor("BLUE");
                cardColor.setFill(BLUE);
                playingField.updateColor(topColor);
                playingField.updateNum(99);
                stage2.close();
                p1.getHandGrid().enableAll(p1);
            });

            hBox.getChildren().addAll(red, yellow, blue, green);
            vBox.getChildren().add(hBox);
            stage2.setScene(new Scene(vBox));
            stage2.show();

        } else {
            strToColor(discardPile.getTopColor());                  //topColor is assigned a value for the 1st time in strToColor
        }
        cardColor.setFill(topColor);
        playingField.updateColor(topColor);
        playingField.updateNum(topNumber);

        if (discardPile.getTopColor().equals("YELLOW")) {
            cardText.setTextFill(BLACK);
        } else {
            cardText.setTextFill(WHITE);
        }
        if (topNumber == 10) {
            cardText.setText("(discard pile)\n(" + discardPile.size() + ")\n     SKIP");
            playingField.setSkip1Player(true);

        } else if (topNumber == 11) {
            cardText.setText("(discard pile)\n(" + discardPile.size() + ")\n     REVERSE");
        } else if (topNumber == 12) {
            cardText.setText("(discard pile)\n(" + discardPile.size() + ")\n     DRAW 2");
        } else if (topNumber == 99) {
            cardText.setText("(discard pile)\n(" + discardPile.size() + ")\n     WILD");
        } else {
            cardText.setText("(discard pile)\n(" + discardPile.size() + ")\n       " + topNumber);
        }

        //the DECK Button
        unoDeckButton = new Button();
        unoDeckButton.setPrefSize(80, 90);
        unoDeckButton.setText("Deck\n " + unoDeck.getCardsLeft());
        unoDeckButton.setDisable(true);
        //the DECK Button has 2 action events (in 2 separate methods)


        //the UNO Button
        unoButton = new Button();
        unoButton.setText("UNO!");
        unoButton.setDisable(true);
        unoButton.setPrefSize(50, 50);
        unoButton.setOnAction(mouseEvent -> {
            System.out.println("PLAYER #" + playerWithUno + " has UNO!!!");
            UnoThread.changeReady(false);
            unoButton.setDisable(true);
        });
        criticalRegion = new CriticalRegion();
        u = new UnoThread();
        u.start();

        p1.setMyTurn(true);
        playingField.prepNextPlayer();

        //position everything I guess
        discardPileStackPane.getChildren().addAll(cardColor, cardText);
        centerPane.getChildren().addAll(discardPileStackPane, unoDeckButton, unoButton);
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

    /**
     * Reverses the turn order and sets the next player
     * of each player accordingly.
     */
    public static void reverseTurnOrder() {
        /**if numOfPlayers == 2, reverse turn is same as
         * the SKIP card. This is done in PlayingField
         */
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
            //If the deck NOW has 0 cards left...
            if (unoDeck.getCardsLeft() == 0) {
                Card temp = unoDeck.replenish(discardPile.getDiscardPile());   //keep the top card; deck is replenished
                discardPile.clear(); //clear the discardPile
                discardPile.getDiscardPile().add(temp);     //make the discardPile the (old) top card
            }
            //Add card to the hand model
            System.out.println("Player #" + player.getPlayerNumber() + " draws a card.");
            player.getHand().add(drawnCard);
            unoDeckButton.setDisable(true);

            //add card to the view!
            Button button = new Button();
            if (player.getPlayerNumber() % 2 == 0) {  //even
                button.setPrefSize(cardHeight, cardLength);
            } else {      //odd
                button.setPrefSize(cardLength, cardHeight);

            }
            int number = drawnCard.getNumber();
            String color = drawnCard.getColor();
            setHandCardColor(button, color);

            button.setOnMouseClicked(mouseEvent2 -> {
                if (discardPile.add(player.getHand().playCard(color, number))) {
                    player.getHand().remove(player.getHand().playCard(color, number));  //remove hand from the hand (the Model)
                    player.getHandGrid().remove(button);                                //remove card from handGrid (the View)
                    checkForUno(player);
                    System.out.println("A Black card was played:  " + player.getHand().getBlackCardPlayed());
                    //disable cards in turnplayer's hand to prevent them from playing more than 1 card in a turn
                    player.getHandGrid().disableAll(player);
                    strToColor(color);
                    cardColor.setFill(topColor);
                    //Set the color of the text on the discard pile
                    if (topColor.equals(YELLOW)) {
                        cardText.setTextFill(BLACK);
                    } else {
                        cardText.setTextFill(WHITE);
                    }
                    //set the text on the discard pile
                    if (number == 10) {
                        cardText.setText("(discard pile)\n(" + discardPile.size() + ")\n     SKIP");
                        playingField.setSkip1Player(true);

                    } else if (number == 11) {
                        cardText.setText("(discard pile)\n(" + discardPile.size() + ")\n     REVERSE");
                    } else if (number == 12) {
                        cardText.setText("(discard pile)\n(" + discardPile.size() + ")\n     DRAW 2");
                    } else if (number == 99) {
                        cardText.setText("(discard pile)\n(" + discardPile.size() + ")\n     WILD");
                    } else if (number == 100) {
                        cardText.setText("(discard pile)\n(" + discardPile.size() + ")\n  WILD DRAW 4");
                    } else {
                        cardText.setText("(discard pile)\n(" + discardPile.size() + ")\n       " + number);
                    }
                    //update the fields of the playingField
                    playingField.updateColor(topColor);
                    playingField.updateNum(number);

                    //Check if the CARD played has any special effects.
                    if (topColor.equals(BLACK)) {
                        if (number == 100) {      //WILD DRAW 4
                            unoDeckButtonAction2(player.getNextPlayer(), 4);
                            unoDeckButton.setDisable(false);
                            unoDeckButton.fire();
                        }
                        PopUpWindow pop = new PopUpWindow(playingField);
                        pop.run();

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
            });

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
                button.setText(color + " " + number);
            }
            int lastSlot = player.getHandGrid().getSize();
            System.out.println("Turn Player's hand size: " + lastSlot + " cards!");
            player.getHandGrid().add(button, lastSlot, player.getPlayerNumber());   //hand should just be 1 ROW

            //If the new card they just drew is ALSO invalid, then their turn is over
            //disable their cards and update the turnOrder in PlayingField
            if (!drawnCard.getColor().equals("BLACK") &&
                    !drawnCard.getColor().equals(discardPile.getTopColor()) &&
                    drawnCard.getNumber() != discardPile.getTopNumber()) {
                player.getHandGrid().disableAll(player);
                player.setMyTurn(false);
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
    }

    /**
     * This is the second action for unoDeckButton.
     * When a card that makes the next player draw cards is played,
     * the next player is forced to draw that many cards.
     *
     * @param player the player who's going to draw cards
     * @param amount the amount of times to draw a new card (2 or 4 only!!!)
     */
    public static void unoDeckButtonAction2(Player player, int amount) {
        //this one is "set on action" to allow the .fire() to work.
        unoDeckButton.setOnAction(mouseEvent -> {
            /**
             * I think because this is "set on Action", whenever
             * the unoDeckButton is clicked by a mouse, it triggers this method.
             * thus, if the unoDeckButton is being clicked to draw 1 card
             * due to having no VALID cards, then this method shouldn't be
             * triggered.
             */
            if ((draw2 && playingField.getTopNum() == 12) || (topColor.equals(BLACK) && playingField.getTopNum() == 100) || (draw2 && player.getHand().getSize() == 1)) {
                for (int i = 0; i < amount; ++i) {
                    Card drawnCard = unoDeck.drawOne();
                    unoDeckButton.setText("Deck\n" + unoDeck.getCardsLeft());
                    //If the deck NOW has 0 cards left...
                    if (unoDeck.getCardsLeft() == 0) {
                        Card temp = unoDeck.replenish(discardPile.getDiscardPile());   //keep the top card; deck is replenished
                        discardPile.clear();    //clear the discardPile
                        discardPile.getDiscardPile().add(temp);     //make the discardPile the (old) top card
                    }
                    //Add card to the hand model
                    System.out.println("Player #" + player.getPlayerNumber() + " draws a card.");
                    player.getHand().add(drawnCard);

                    //add card to the view!
                    Button button = new Button();
                    button.setDisable(true);
                    if (player.getPlayerNumber() % 2 == 0) {  //even
                        button.setPrefSize(cardHeight, cardLength);
                    } else {      //odd
                        button.setPrefSize(cardLength, cardHeight);

                    }
                    int number = drawnCard.getNumber();
                    String color = drawnCard.getColor();
                    setHandCardColor(button, color);

                    button.setOnMouseClicked(mouseEvent2 -> {
                        if (discardPile.add(player.getHand().playCard(color, number))) {
                            player.getHand().remove(player.getHand().playCard(color, number));  //remove hand from the hand (the Model)
                            player.getHandGrid().remove(button);                                //remove card from handGrid (the View)
                            checkForUno(player);
                            System.out.println("A Black card was played:  " + player.getHand().getBlackCardPlayed());
                            //disable cards in turnplayer's hand to prevent them from playing more than 1 card in a turn
                            player.getHandGrid().disableAll(player);
                            strToColor(color);
                            cardColor.setFill(topColor);
                            //set the color of the text on the discard pile
                            if (topColor.equals(YELLOW)) {
                                cardText.setTextFill(BLACK);
                            } else {
                                cardText.setTextFill(WHITE);
                            }
                            //set the text on the discard pile
                            if (number == 10) {
                                cardText.setText("(discard pile)\n(" + discardPile.size() + ")\n     SKIP");
                                playingField.setSkip1Player(true);

                            } else if (number == 11) {
                                cardText.setText("(discard pile)\n(" + discardPile.size() + ")\n     REVERSE");
                            } else if (number == 12) {
                                cardText.setText("(discard pile)\n(" + discardPile.size() + ")\n     DRAW 2");
                            } else if (number == 99) {
                                cardText.setText("(discard pile)\n(" + discardPile.size() + ")\n     WILD");
                            } else if (number == 100) {
                                cardText.setText("(discard pile)\n(" + discardPile.size() + ")\n  WILD DRAW 4");
                            } else {
                                cardText.setText("(discard pile)\n(" + discardPile.size() + ")\n       " + number);
                            }
                            //update the playingField's fields
                            playingField.updateColor(topColor);
                            playingField.updateNum(number);

                            //Check if the CARD played has any special effects!
                            if (topColor.equals(BLACK)) {
                                if (number == 100) {        //WILD DRAW 4
                                    unoDeckButtonAction2(player.getNextPlayer(), 4);
                                    unoDeckButton.setDisable(false);
                                    unoDeckButton.fire();
                                }
                                PopUpWindow pop = new PopUpWindow(playingField);
                                pop.run();

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
                    });

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
                        button.setText(color + " " + number);
                    }
                    int lastSlot = player.getHandGrid().getSize();
                    System.out.println("Next Player's hand size: " + lastSlot + " cards!");
                    player.getHandGrid().add(button, lastSlot, player.getPlayerNumber());   //hand should just be 1 ROW
                }   //curly brace of the for loop
            }       //curly brace of the if statement.
            unoDeckButton.setDisable(true);
            //unoDeckButtonAction(player);        //shift the action back to action1 I guess? I dunno...
        });
    }

    //terminate the program
    public void stop() {
        System.out.println("stopped");
    }


    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Subclass CriticalRegion starts here.
     * @return
     */
    public class CriticalRegion {
        public CriticalRegion(){ }

        public synchronized void action(){
            while(UnoThread.getReady() == false) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            /*once they exit the while loop, they wait for 2.5 seconds.
            if c isn't pressed in 2.5 sec, it's disabled.*/
            try {
                u.sleep(2500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(UnoThread.getReady() == true){
                unoButton.setDisable(true);
                System.out.println("UNO! was not pressed in time! It's now disabled.");
                for(Player p : turnOrder){
                    if(p.getPlayerNumber() == playerWithUno){
                        Platform.runLater(()->{
                            draw2 = true;
                            unoDeckButtonAction2(p, 2);
                            unoDeckButton.setDisable(false);
                            unoDeckButton.fire();
                            draw2 = false;
                            UnoThread.changeReady(false);

                            //theDeckButton is disabled, so re-enable it IF necessary!
                            if(!topColor.equals(BLACK) &&
                                    ModelGUI.turnOrder.peek().getHand().needToDraw(playingField.getTopColor(), playingField.getTopNum())){
                                ModelGUI.setUnoDeckButton(false);
                                ModelGUI.unoDeckButtonAction(ModelGUI.turnOrder.peek());
                            }
                        });
                        break;
                    }
                }
            }

        }

        public synchronized void stopWaiting(){
            notifyAll();
            //System.out.println("Stop waiting!");
        }

    }


    /**
     * Subclass UnoThread Starts Here!
     */
    public static class UnoThread extends Thread{
        private static boolean ready;

        public UnoThread(){
            ready = false;
        }

        public static boolean getReady(){
            return ready;
        }

        public static void changeReady(boolean b){
            ready = b;
        }

        public void run() {
            while(true) {
                criticalRegion.action();
            }
        }
    }

}//last curly brace is below


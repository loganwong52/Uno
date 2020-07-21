package sample;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ModelPTUI {

    private static ServerSocket serverSocket;
    private static Player client1;
    private static Player client2;
    private static Player client3;
    private static Player client4;

    public static void main(String args[]){
        //edit configurations
        // first argument is port #
        // second argument is numbers of players I guess?
        /**if(args.length != 2){
            System.out.println("Usage: java Uno port# #ofPlayers");
            System.exit(1);
        }

        int portNumber = Integer.parseInt(args[0]);
        int numOfPlayers = Integer.parseInt(args[1]);

        try{
            System.out.println("Waiting for Player One...");
            serverSocket = new ServerSocket(portNumber);
            //client 1 created
            client1 = new Player(serverSocket);
            connect(client1, numOfPlayers, 1);

            //# of players determines what game will do.
            if(numOfPlayers == 1){
                System.out.println("Starting game!");

                run(client1.getIn(), client1.getOut(), client2.getIn(), client2.getOut());

            }
            if(numOfPlayers == 2){
                System.out.println("Waiting for Player Two...");
                //client 2 created
                client2 = new Player(serverSocket);
                connect(client2, numOfPlayers, 2);
            }
            if(numOfPlayers == 3){
                System.out.println("Waiting for Player Three...");
                //client 3 created
                client3 = new Player(serverSocket);
                connect(client3, numOfPlayers, 3);
            }
            if(numOfPlayers == 4){
                System.out.println("Waiting for Player Four...");
                //client 3 created
                client4 = new Player(serverSocket);
                connect(client4, numOfPlayers, 4);
            }
            System.out.println("Starting game!");

            run(client1.getIn(), client1.getOut(), client2.getIn(), client2.getOut());
            close(serverSocket, client1, client2, client3, client4);

        } catch (IOException e) {
            System.out.println("IO Exception occured. Exit game.");
            close(serverSocket, client1, client2, client3, client4);
            System.exit(1);
        } catch (NullPointerException e){
            System.out.println("Null Pointer Exception occured. Exit game.");
            close(serverSocket, client1, client2, client3, client4);
            System.exit(1);
        }*/



        Deck unoDeck = new Deck(1);    //1 player
        DiscardPile discardPile = new DiscardPile();
        Hand player1 = new Hand();
        Scanner scanner = new Scanner(System.in);

        //print inital unshuffled deck
        //unoCards.printDeck();

        unoDeck.shuffleWell(108);

        unoDeck.printDeck();

        System.out.println("---------");

        //set up player1's hand
        for(int i = 0; i < 7; ++i){
            player1.add(unoDeck.drawOne());
        }
        player1.printHand();

        //pop top card from deck. GAME BEGINS!
        discardPile.add(unoDeck.beginGame(108));

        System.out.println("type in 'COLOR' 'NUMBER' ");
        String inputColor = scanner.next();
        int inputNum = scanner.nextInt();

        //test
        //System.out.println("YOUR inputed color: " + inputColor);
        //System.out.println("YOUR inputed num: " + inputNum);

        //1. check if player's input is a real card
        // 2. check if the input is a valid move (color OR number matches the discard pile's top card)
        //3. Finally, remove the card from the player's hand

        if( discardPile.add(player1.playCard(inputColor, inputNum)) ){
            player1.remove(player1.playCard(inputColor, inputNum));
        }
        player1.printHand();
        discardPile.seeTopCard();

    }

    /**
     * if the client is connected, it prints a message saying
     * that the player is connected.
     * @param client
     * @param players
     */
    /**public static void connect(Player client, int players, int number){
        if(client.isConnected()){
            System.out.println("Player " + number + " is connected!");
            client.println("There will be " + players + " players.");
        }
    }

    public static void close(ServerSocket server, Player p1, Player p2, Player p3, Player p4){
        try {
            server.close();
            p1.close();
            p2.close();
            p3.close();
            p4.close();
        } catch (IOException e) {
            System.out.println("IO Exception occurred. Game over.");
            System.exit(1);
        }
    }*/
}

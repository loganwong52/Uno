package sample;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Stack;

/**
 * this class is the Deck and its methods.
 */
public class Deck extends Stack {
    private int numOfPlayers;
    private static Card[] deck;
    private Stack<Card> deckStack;
    private int cardsLeft;
    private static final int STARTINGAMT = 108;

    public Deck(int num){
        numOfPlayers = num;
        deck = new Card[STARTINGAMT];
        deckStack = new Stack<Card>();

        //create 1 zero card for each color
        deck[0] = new Card("RED", 0);
        deck[1] = new Card("YELLOW", 0);
        deck[2] = new Card("BLUE", 0);
        deck[3] = new Card("GREEN", 0);

        // create all the red cards (2 of each number)
        int n = 1;
        for(int i = 4; i < 28; ++i){
            deck[i] = new Card("RED", n);
            ++i;
            deck[i] = new Card("RED", n);
            ++n;
        }

        // create all the yellow cards (2 of each number)
        int m = 1;
        for(int i = 28; i < 52; ++i){
            deck[i] = new Card("YELLOW", m);
            ++i;
            deck[i] = new Card("YELLOW", m);
            ++m;
        }

        // create all the blue cards (2 of each number)
        int o = 1;
        for(int i = 52; i < 76; ++i){
            deck[i] = new Card("BLUE", o);
            ++i;
            deck[i] = new Card("BLUE", o);
            ++o;
        }

        // create all the green cards (2 of each number)
        int p = 1;
        for(int i = 76; i < 100; ++i){
            deck[i] = new Card("GREEN", p);
            ++i;
            deck[i] = new Card("GREEN", p);
            ++p;
        }

        //the 8 black cards now
        //regular wild card = 99
        //plus 4 wild card = 100
        for(int i = 100; i < 104; ++i){
            deck[i] = new Card("BLACK", 99);
        }
        for(int i = 104; i < STARTINGAMT; ++i){
            deck[i] = new Card("BLACK", 100);
        }

        cardsLeft = STARTINGAMT;
    }

    /**
     * shuffles the initial unshuffled deck 10 times.
     * initial deck is sorted by color and by number.
     */
    public void shuffle(int cardsInDeck){
        Card[] shuffledDeck = new Card[cardsInDeck];
        ArrayList<Integer> numbers = new ArrayList<>();
        for(int i = 0; i < cardsInDeck; ++i){
            numbers.add(i);
        }
        Collections.shuffle(numbers);
        Collections.shuffle(numbers);
        Collections.shuffle(numbers);
        Collections.shuffle(numbers);
        Collections.shuffle(numbers);
        Collections.shuffle(numbers);
        Collections.shuffle(numbers);
        Collections.shuffle(numbers);
        Collections.shuffle(numbers);
        Collections.shuffle(numbers);
        Collections.shuffle(numbers);

        //rearrange cards in nonShuffled into shuffled deck
        for(int i = 0; i < cardsInDeck; ++i){
            //shuffledDeck[i] = nonShuffled[numbers[i]];
            shuffledDeck[i] = deck[numbers.get(i)];
        }

        //original deck becomes the shuffled Deck
        deck = shuffledDeck;
    }

    /**
     * turns an array of cards into a STACK of cards
     */
    public void stackify(int cardsInDeck){
        //turn deck, which is an array of cards, into a stack of cards
        for(int i = 0; i < cardsInDeck; ++i){
            deckStack.push(deck[i]);
        }
    }

    /**
     * shuffles the deck.
     * then turns deck, which is an array, into a stack
     */
    public void shuffleWell(int cardsInDeck){
        shuffle(cardsInDeck);
        stackify(cardsInDeck);
        //Note that the deck is "reversed", since things are PUSHED into stack
    }

    public Stack<Card> getDeckStack() {
        return deckStack;
    }

    /**
     * prints out the decks' cards-- color and then number
     */
    public void printDeck(){
        for(int i = 0; i < STARTINGAMT; ++i){
            System.out.println("color: " + deck[i].getColor() + " | number: " +
                    deck[i].getNumber());
        }
        System.out.println(" ");
    }

    /**
     * removes the top card from the deck,
     * which is a stack. Decreases cardsLeft by 1
     * @return draw  the card that was removed from the stack
     */
    public Card drawOne(){
        Card draw = deckStack.pop();
        cardsLeft = deckStack.size();
        return draw;
    }

    /**
     * puts discardpile back into the deck and shuffles it.
     * @param discardPile
     * @return top card of the discardPile, which is the new discardPile
     */
    public Card shuffleDiscardPile(Stack discardPile){
        //called when cardsleft = 0
        Card rVCard = (Card) discardPile.pop();

        int size = discardPile.size()-1;
        Card[] unshuffledPile = new Card[size];
        int[] numbers = new int[size];
        Card[] shuffled = new Card[size];
        int counter = 0;

        while(!discardPile.isEmpty()) {
            unshuffledPile[counter] = (Card) discardPile.pop();
            ++counter;
        }
        deckStack.clear();  //just in case, I guess...

        Random rand = new Random();
        int checker = 0;
        int randInt = rand.nextInt(size);
        numbers[0] = randInt;

        for(int i = 1; i < size; ++i){
            //generates int from 0-107 inclusive
            randInt = rand.nextInt(size);
            //make sure the randInt isn't a duplicate number
            while(checker < i){
                if(numbers[checker] == randInt){
                    randInt = rand.nextInt(size);
                    checker = 0;
                }
                ++checker;
            }

            //if it's not a duplicate, then add it to array
            numbers[i] = randInt;
            checker = 0;
        }

        //shuffle unshuffledPile by putting it randomly into toBeShuffled
        for(int i = 0; i < size; ++i){
            shuffled[i] = unshuffledPile[numbers[i]];
        }

        //stackify shuffled
        for(int i = 0; i < size; ++i){
            deckStack.push(shuffled[i]);
        }

        //the old discardPile is assigned to the return value.
        return rVCard;
    }

    /**
     * Makes an array of hands. 1 hand per player.
     * @return the array of hands
     */
    public Hand[] distributeHands(Deck deck) {
        Hand[] hands = new Hand[numOfPlayers];
        for(int i = 0; i < numOfPlayers; ++i){
            hands[i] = new Hand();
        }

        for(int i = 0; i < 7; ++i){
            for(int p = 0; p < numOfPlayers; ++p){
                hands[p].add(deck.getDeckStack().pop());
            }
        }
        return hands;
    }


    public Card beginGame(int cardsInDeck){
        /**
         * If the first card is a Wild Draw Four card AKA num == 100
         * Return it to the Draw Pile,
         * shuffle the deck
         * and turn over a new card.
         */
        System.out.println("---------");
        System.out.println("The First Card:");
        while(deckStack.peek().getNumber() == 100){
            shuffle(cardsInDeck);
            stackify(cardsInDeck);
        }
        deckStack.peek().printCard();

        return deckStack.pop();
    }

}

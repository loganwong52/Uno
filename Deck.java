package sample;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

/**
 * this class is the Deck and its methods.
 */
public class Deck extends Stack {
    private int numOfPlayers;
    private static Card[] deck;
    private Stack<Card> deckStack;
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
     * which is a STACK. Decreases cardsLeft by 1
     * @return draw  the card that was removed from the stack
     */
    public Card drawOne(){
        Card draw = deckStack.pop();
        return draw;
    }

    /**
     * Shuffles the discardpile and puts it back into the deck.
     * @param discardPile
     * @return top card of the discardPile, which is the new discardPile
     */
    public Card replenish(Stack<Card> discardPile){
        //called when deckStack.size() == 0
        Card rVCard = discardPile.pop();
        int size = discardPile.size();
        ArrayList<Card> shuffledDiscardPile = new ArrayList<>();
        for(int i = 0; i < size; ++i){
            shuffledDiscardPile.add( discardPile.pop() );
        }
        Collections.shuffle(shuffledDiscardPile);
        Collections.shuffle(shuffledDiscardPile);
        Collections.shuffle(shuffledDiscardPile);
        Collections.shuffle(shuffledDiscardPile);
        Collections.shuffle(shuffledDiscardPile);
        Collections.shuffle(shuffledDiscardPile);
        Collections.shuffle(shuffledDiscardPile);
        Collections.shuffle(shuffledDiscardPile);
        Collections.shuffle(shuffledDiscardPile);
        Collections.shuffle(shuffledDiscardPile);

        deckStack.clear();      //might already be empty, but just in case.

        //put the shuffled discardPile into the deck
        for(int i = 0; i < size; ++i){
            deckStack.push(shuffledDiscardPile.get(i));
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

    public int getCardsLeft(){
        return deckStack.size();
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

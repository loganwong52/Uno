package sample;


import java.util.Stack;

public class DeckReshuffleThread extends Thread  {
    private Deck deck;
    private int size;
    private DiscardPile discardPile;

    public DeckReshuffleThread(Deck d, DiscardPile dp){
        deck = d;
        size = d.size();
        discardPile = dp;
    }

    public void update(Deck s, DiscardPile dp){
        deck = s;
        size = s.size();
        discardPile = dp;
    }

    @Override
    public void run() {
        do {
            discardPile.clear();
            discardPile.getDiscardPile().add( deck.replenish(discardPile.getDiscardPile()) );

        }while(size == 0);

    }
}

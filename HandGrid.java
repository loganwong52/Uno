package sample;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import java.util.Iterator;

/**
 * Essentially a GridPane that holds buttons called CardButtons.
 *
 * @author Logan Wong
 */
public class HandGrid{
    private GridPane gridPane;
    private int playerNum;
    private boolean clickable;

    /**
     * Constructor that initializes the fields
     * @param owner  playerNumber of the player whose hand this is
     */
    public HandGrid(int owner){
        gridPane = new GridPane();
        playerNum = owner;
        clickable = false;
    }

    /**
     * adds a button to gridPane
     * @param button  the button to add
     * @param rowOrCol  the row/col to add it to
     * @param playerNumber  odd or even to determine which row/col is 0
     */
    public void add(CardButton button, int rowOrCol, int playerNumber){
        if(playerNumber%2 == 0){    //players 2 & 4 are vertical
            gridPane.add(button, 0, rowOrCol);      //rowOrCol represents a row index       (node, col#, row#)
            gridPane.setVgap(3);
        }else{      //players 1 and 3 are horizontal
            gridPane.add(button, rowOrCol, 0);      //rowOrCol represents a column index
            gridPane.setHgap(3);
        }
    }

    /**
     * Removes a button from gridPane.
     * Then it shifts all the buttons that were to
     * THAT button's right, 1 space left.
     * @param button    the button to be removed
     */
    public void remove(CardButton button){
        GridPane temp = new GridPane();
        gridPane.getChildren().remove(button);      //the space at (col,row) is now null (I think)
        Iterator<Node> it = gridPane.getChildren().iterator();
        while (it.hasNext()) {
            // get the next child node
            Node nextNode = it.next();
            int c = GridPane.getColumnIndex(nextNode);
            int r = GridPane.getRowIndex(nextNode);
            // remove method is used to safely remove element from the list
            it.remove();
            temp.add(nextNode, c, r);
        }
        //put the buttons back into gridpane!
        int index = 0;
        while(temp.getChildren().size() > 0){
            if(playerNum%2 == 0) {  //horizontal, so the columns increase
                gridPane.add(temp.getChildren().get(0), 0, index);
            }else{
                gridPane.add(temp.getChildren().get(0), index, 0);
            }
            ++index;
        }
    }

    /**
     * Gets the gridpane with all the cardButtons.
     * @return  the gridpane
     */
    public GridPane getGridPane() {
        return gridPane;
    }

    /**
     * Returns the gridPane as an ObservableList.
     * Basically gridPane.getChildren() for use in
     * for-each loops.
     * @return  gridPane.getChildren()
     */
    public ObservableList<Node> getGridKids(){
        return gridPane.getChildren();
    }

    /**
     * Loops through the gridPane and
     * enables the cardButtons.
     */
    public void enableAll(){
        for(Node b : gridPane.getChildren()){
            b.setDisable(false);
        }
        clickable = true;
    }

    /**
     * Loops through the gridPane and
     * disables the cardButtons.
     */
    public void disableAll(){
        for(Node b : gridPane.getChildren()){
            b.setDisable(true);
        }
        clickable = false;
    }

    /**
     * If all the buttons in the handGrid
     * are enabled, clickable is true; false otherwise
     * @return  true or false.
     */
    public boolean isClickable() {
        return clickable;
    }

    /**
     * changes the value of the field, "clickable"
     * @param clickable  true or false
     */
    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }

    /**
     * Gets the number of cards in the hand.
     * @return  size of player's hand
     */
    public int getSize(){
        return gridPane.getChildren().size();
    }
}

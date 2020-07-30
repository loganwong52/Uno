package sample;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.util.ConcurrentModificationException;
import java.util.Iterator;

public class HandGrid{
    //essentially a gridpane that holds buttons

    private GridPane gridPane;
    private int playerNum;
    private boolean clickable;

    public HandGrid(int owner){
        gridPane = new GridPane();
        playerNum = owner;
        clickable = false;
    }

    /**
     * adds a button to gridPane
     * @param button
     * @param rowOrCol
     * @param playerNumber
     */
    public void add(Node button, int rowOrCol, int playerNumber){
        if(playerNumber%2 == 0){    //players 2 & 4 are vertical
            gridPane.add(button, 0, rowOrCol);      //rowOrCol represents a row index       (node, col#, row#)
            gridPane.setVgap(3);
        }else{      //players 1 and 3 are horizontal
            gridPane.add(button, rowOrCol, 0);      //rowOrCol represents a column index
            gridPane.setHgap(3);
        }
    }

    /**
     * removes a button from gridPane
     * @param button
     */
    public void remove(Node button){
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

    public GridPane getGridPane() {
        return gridPane;
    }

    public ObservableList<Node> getGridKids(){
        return gridPane.getChildren();
    }


    public void enableAll(Player player){
        for(Node b : player.getHandGrid().getGridKids()){
            b.setDisable(false);
        }
        clickable = true;
    }

    public void disableAll(Player player){
        for(Node b : player.getHandGrid().getGridKids()){
            b.setDisable(true);
        }
        clickable = false;
    }

    public boolean isClickable() {
        return clickable;
    }

    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }

    public int getSize(){
        return gridPane.getChildren().size();
    }
}

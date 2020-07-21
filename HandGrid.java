package sample;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

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
        if(playerNumber%2 == 0){
            gridPane.add(button, 0, rowOrCol);
        }else{
            gridPane.add(button, rowOrCol, 0);
        }
    }

    /**
     * removes a button from gridPane
     * @param button
     */
    public void remove(Node button){
        gridPane.getChildren().remove(button);
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
}

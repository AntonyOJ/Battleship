package com.example.Battleship.models;

import com.example.Battleship.Constants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GameModel implements Serializable {

    private int humanPlayerCount;
    private int gridColumns;
    private int gridRows;
    private List<PlayerModel> players;
    private boolean setupPhase;
    private boolean trackingGridView;
    private boolean turnTransition;
    private int playerTurn;

    static String tag = "GameModel: ";

    public GameModel(int humanPlayerCount, int columns, int rows) {
        this.humanPlayerCount = humanPlayerCount;
        gridColumns = columns;
        gridRows = rows;
        setupPhase = true;
        trackingGridView = true;
        turnTransition = false;
        players = new ArrayList<PlayerModel>();
        for (int i = 0; i < 2; i++) {
            players.add(new PlayerModel(columns, rows));
        }
        playerTurn = 0;

        // Placeholder ships
//        players.get(0).player1ShipPlaceholders();
//        players.get(1).player2ShipPlaceholders();
    }

    public int getHumanPlayerCount() {
        return humanPlayerCount;
    }

    public int getGridColumns() {
        return gridColumns;
    }

    public int getGridRows() {
        return gridRows;
    }

    public int getPlayerTurn() {
        return playerTurn;
    }

    public int getPlayerTurnForDisplay() {
        return playerTurn + 1;
    }

    public PlayerModel getCurrentPlayerModel() {
        return players.get(playerTurn);
    }

    public PlayerModel getNonCurrentPlayerModel() {
        return players.get(getNonCurrentPlayerInt());
    }

    public int getNonCurrentPlayerInt() {
        return 1 - playerTurn;
    }

    public int getNonCurrentPlayerIntForDisplay() {
        return 2 - playerTurn;
    }

    public void flipPlayerTurn() {
        playerTurn = 1 - playerTurn;
    }

    public boolean isSetupPhase() {
        return setupPhase;
    }

    public void setSetupPhase(boolean setupPhase) {
        this.setupPhase = setupPhase;
    }

    public boolean isTrackingGridView() {
        return trackingGridView;
    }

    public void setTrackingGridView(boolean trackingGridView) {
        this.trackingGridView = trackingGridView;
    }

    public boolean isTurnTransition() {
        return turnTransition;
    }

    public void setTurnTransition(boolean turnTransition) {
        this.turnTransition = turnTransition;
    }

    public boolean tryShot(Position position) {
        try {
            if (getNonCurrentPlayerModel().getGridPositionState(position) == Constants.blank ||
                    getNonCurrentPlayerModel().getGridPositionState(position) == Constants.ship) {
                doShot(position);
                return true;
            }
        }
        catch (Exception e) {
            return false;
        }
        return false;
    }

    public void doShot(Position position) {
        String output = "Shot at player " + getNonCurrentPlayerIntForDisplay() +
                " on position " + position.getXForDisplaying() + position.getYForDisplaying() +
                        " (x: " + position.getX() + ", y: " + position.getY() + ")";
        if (getNonCurrentPlayerModel().getGridPositionState(position) == Constants.ship) {
            System.out.println(tag + output + " hits");
            getNonCurrentPlayerModel().setGridPositionState(position, Constants.hit);
            if (getNonCurrentPlayerModel().shipDestroyedAtPosition(position)) {
                getNonCurrentPlayerModel().updateModelWithDestroyedShipAtPosition(position);
            }
        }
        else {
            System.out.println(tag + output + " misses");
            getNonCurrentPlayerModel().setGridPositionState(position, Constants.miss);
        }
        getNonCurrentPlayerModel().setLastShotPosition(position);
    }

    public boolean finishedGame() {
        return (getNonCurrentPlayerModel().getShipsLeft() == 0);
    }
}

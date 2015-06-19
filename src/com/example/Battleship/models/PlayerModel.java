package com.example.Battleship.models;

import com.example.Battleship.Constants;

import java.io.Serializable;
import java.util.*;

public class PlayerModel implements Serializable {

    private int gridColumns;
    private int gridRows;
    private int[][] grid;
    private ArrayList<Ship> ships;
    private Position lastShotPosition;

    static String tag = "PlayerModel: ";

    public PlayerModel(int columns, int rows) {
        grid = new int[columns][rows];
        gridColumns = columns;
        gridRows = rows;
        initialize();
    }

    public int getGridPositionState(Position position) {
        return grid[position.getX()][position.getY()];
    }

    public void setGridPositionState(Position position, int state) {
        this.grid[position.getX()][position.getY()] = state;
    }

    public Position getLastShotPosition() {
        return lastShotPosition;
    }

    public void setLastShotPosition(Position lastShotPosition) {
        this.lastShotPosition = lastShotPosition;
    }

    public int getShipsLeft() {
        int shipsLeft = 0;
        for (Ship ship : ships) {
            if (!ship.isDestroyed()) {
                shipsLeft++;
            }
        }
        return shipsLeft;
    }

    public void player1ShipPlaceholders() {
        resetGrid();
        ships.clear();
        Ship carrier = new Ship("Carrier", 5, false, new Position(0, 4));
        ships.add(carrier);
        Ship battleship = new Ship("Battleship", 4, true, new Position(3, 5));
        ships.add(battleship);
        Ship submarine = new Ship("Submarine", 3, false, new Position(8, 6));
        ships.add(submarine);
        Ship destroyer = new Ship("Destroyer", 3, true, new Position(6, 3));
        ships.add(destroyer);
        Ship patrolBoat = new Ship("Patrol Boat", 2, false, new Position(7, 0));
        ships.add(patrolBoat);
        for (Ship ship : ships) {
            addShipToGrid(ship);
        }
    }

    public void player2ShipPlaceholders() {
        resetGrid();
        ships.clear();
        Ship carrier = new Ship("Carrier", 5, false, new Position(1, 4));
        ships.add(carrier);
        Ship battleship = new Ship("Battleship", 4, true, new Position(3, 5));
        ships.add(battleship);
        Ship submarine = new Ship("Submarine", 3, false, new Position(9, 7));
        ships.add(submarine);
        Ship destroyer = new Ship("Destroyer", 3, true, new Position(4, 7));
        ships.add(destroyer);
        Ship patrolBoat = new Ship("Patrol Boat", 2, false, new Position(3, 0));
        ships.add(patrolBoat);
        for (Ship ship : ships) {
            addShipToGrid(ship);
        }
    }

    public void initialize() {
        ships = new ArrayList<Ship>(5);
        ships.add(new Ship("Carrier", 5));
        ships.add(new Ship("Battleship", 4));
        ships.add(new Ship("Submarine", 3));
        ships.add(new Ship("Destroyer", 3));
        ships.add(new Ship("Patrol Boat", 2));
        Collections.shuffle(ships);
        randomiseShipPositions();
    }

    public void randomiseShipPositions() {
        resetGrid();
        for (Ship ship : ships) {
            ArrayList<Position> availablePositions = new ArrayList<Position>();
            Random randomOrientation = new Random();
            boolean horizontalOrientation = randomOrientation.nextBoolean();
            ship.setHorizontalOrientation(horizontalOrientation);
            if (horizontalOrientation) {
                for (int i = 0; i < gridRows - ship.getLength() + 1; i++) {
                    for (int j = 0; j < gridColumns; j++) {
                        boolean noCollidingPositions = true;
                        for (int k = i; k < i + ship.getLength(); k++) {
                            if (grid[k][j] == Constants.ship) {
                                noCollidingPositions = false;
                                break;
                            }
                        }
                        if (noCollidingPositions) {
                            availablePositions.add(new Position(i, j));
                        }
                    }
                }
            }
            else {
                for (int i = 0; i < gridColumns; i++) {
                    for (int j = 0; j < gridRows - ship.getLength() + 1; j++) {
                        boolean noCollidingPositions = true;
                        for (int k = j; k < j + ship.getLength(); k++) {
                            if (grid[i][k] == Constants.ship) {
                                noCollidingPositions = false;
                                break;
                            }
                        }
                        if (noCollidingPositions) {
                            availablePositions.add(new Position(i, j));
                        }
                    }
                }
            }
            Random random = new Random();
            int randomIndex = random.nextInt(availablePositions.size());
            ship.setStartPosition(availablePositions.get(randomIndex));
            addShipToGrid(ship);
        }
    }

    public void addShipToGrid(Ship ship) {
        ArrayList<Position> occupiedPositions = ship.getOccupiedPositions();
        for (Position position : occupiedPositions) {
            grid[position.getX()][position.getY()] = Constants.ship;
        }
    }

    public void resetGrid() {
        for (int i = 0; i < gridColumns; i++) {
            for (int j = 0; j < gridRows; j++) {
                grid[i][j] = Constants.blank;
            }
        }
    }

    public boolean aShipOccupiesPosition(Position position) {
        for (Ship ship : ships) {
            if (ship.occupiesPosition(position)) {
                return true;
            }
        }
        return false;
    }

    public void updateModelWithDestroyedShipAtPosition(Position position) {
        if (shipDestroyedAtPosition(position)) {
            Ship destroyedShip = getShipAtPosition(position);
            destroyedShip.setDestroyed(true);
            ArrayList<Position> occupiedPositions = destroyedShip.getOccupiedPositions();
            for (Position occupiedPosition : occupiedPositions) {
                grid[occupiedPosition.getX()][occupiedPosition.getY()] = Constants.shipDestroyed;
            }
            System.out.println(tag + destroyedShip.getType() + " is destroyed");
            System.out.println(tag + "There are " + getShipsLeft() + " ship(s) left");
        }
    }

    public Position getRandomPositionAI() {
        ArrayList<Position> availablePositions = getAvailablePositions();
        Random random = new Random();
        int randomIndex = random.nextInt(availablePositions.size());
        return availablePositions.get(randomIndex);
    }

    public boolean shipDestroyedAtPosition(Position position) {
        for (Ship ship : ships) {
            if (ship.occupiesPosition(position)) {
                ArrayList<Position> occupiedPositions = ship.getOccupiedPositions();
                boolean destroyed = true;
                for (Position occupiedPosition : occupiedPositions) {
                    if (grid[occupiedPosition.getX()][occupiedPosition.getY()] == Constants.blank ||
                            grid[occupiedPosition.getX()][occupiedPosition.getY()] == Constants.ship) {
                        destroyed = false;
                        break;
                    }
                }
                if (destroyed) {
                    return true;
                }
            }
        }
        return false;
    }

    public Ship getShipAtPosition(Position position) {
        for (Ship ship : ships) {
            if (ship.occupiesPosition(position)) {
                return ship;
            }
        }
        return null;
    }

    public ArrayList<Position> getAvailablePositions() {
        ArrayList<Position> availablePositions = new ArrayList<Position>();
        for (int i = 0; i < gridColumns; i++) {
            for (int j = 0; j < gridRows; j++) {
                if (grid[i][j] == Constants.blank || grid[i][j] == Constants.ship) {
                    availablePositions.add(new Position(i, j));
                }
            }
        }
        return availablePositions;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < gridColumns; i++) {
            sb.append(Arrays.toString(grid[i]) + "\n");
        }
        return sb.toString();
    }
}

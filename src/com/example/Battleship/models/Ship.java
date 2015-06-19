package com.example.Battleship.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Ship implements Serializable {

    private String type;
    private int length;
    private boolean horizontalOrientation;
    private Position startPosition;
    private boolean destroyed;

    public Ship(String type, int length, boolean horizontalOrientation, Position startPosition) {
        this.type = type;
        this.length = length;
        this.horizontalOrientation = horizontalOrientation;
        this.startPosition = startPosition;
        this.destroyed = false;
    }

    public Ship(String type, int length) {
        this.type = type;
        this.length = length;
        this.destroyed = false;
    }

    public String getType() {
        return type;
    }

    public int getLength() {
        return length;
    }

    public void setHorizontalOrientation(boolean horizontalOrientation) {
        this.horizontalOrientation = horizontalOrientation;
    }

    public void setStartPosition(Position startPosition) {
        this.startPosition = startPosition;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
    }

    public boolean occupiesPosition(Position position) {
        if (horizontalOrientation) {
            if (position.getY() != startPosition.getY())
                return false;
            if ((position.getX() >= startPosition.getX()) && (position.getX() < (startPosition.getX() + length))) {
                return true;
            }
        }
        // Vertical orientation
        else {
            if (position.getX() != startPosition.getX())
                return false;
            if ((position.getY() >= startPosition.getY()) && (position.getY() < (startPosition.getY() + length))) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<Position> getOccupiedPositions() {
        ArrayList<Position> occupiedPositions = new ArrayList<Position>();
        if (horizontalOrientation) {
            for (int x = startPosition.getX(); x < (startPosition.getX() + length); x++) {
                occupiedPositions.add(new Position(x, startPosition.getY()));
            }
        }
        // Vertical orientation
        else {
            for (int y = startPosition.getY(); y < (startPosition.getY() + length); y++) {
                occupiedPositions.add(new Position(startPosition.getX(), y));
            }
        }
        return occupiedPositions;
    }
}

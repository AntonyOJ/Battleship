package com.example.Battleship.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import com.example.Battleship.*;
import com.example.Battleship.models.GameModel;
import com.example.Battleship.models.Position;
import com.example.Battleship.models.Ship;

import java.util.concurrent.TimeUnit;

public class GameActivity extends Activity {

    // declare size of nxn puzzle grid
    static int gridColumns = 10;
    static int gridRows = 10;
    GameModel model;
    static String modelKey = "Model";
    static String tag = "Game: ";

    SharedPreferences sharedPreferences;
    int humanPlayerCount;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        humanPlayerCount = getIntent().getIntExtra(Constants.humanPlayerCount, 1);
        updateSetupGridView();
        sharedPreferences = getSharedPreferences(getString(R.string.stats_file_key), Context.MODE_PRIVATE);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putSerializable(modelKey, getModel());
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        model = (GameModel) savedInstanceState.getSerializable(modelKey);
        if (!model.isSetupPhase()) {
            if (model.isTurnTransition()) {
                setContentView(R.layout.turn_transition);
            }
            else if (model.isTrackingGridView()) {
                updateTrackingGridViews();
            }
            else {
                setContentView(R.layout.game_ship_grid);
            }
            if (model.finishedGame()) {
                gameEndedDialog(getWindow().getDecorView().findViewById(android.R.id.content));
            }
        }
        else if (model.isTurnTransition()) {
            setContentView(R.layout.turn_transition);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Quit current game.");
            alertDialog.setMessage("Are you sure?");

            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,
                    "Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            finish();
                        }
                    });

            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                    "No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            // Do nothing - dialog will dismiss
                        }
                    });

            alertDialog.show();
            return true; // Dealt with the keyevent
        }
        return super.onKeyDown(keyCode, event);
    }

    public GameModel getModel() {
        if (model == null) {
            model = new GameModel(humanPlayerCount, gridColumns, gridRows);
            System.out.println(tag + "Created GameModel");
        }
        return model;
    }

    public void resetModel() {
        model = new GameModel(humanPlayerCount, gridColumns, gridRows);
        System.out.println(tag + "New game started with " + model.getHumanPlayerCount() + " human player(s)");
        setContentView(R.layout.game_setup);
    }

    public void updateGameState(Position position) {
        if (model.getPlayerTurn() == 0 && model.getHumanPlayerCount() == 1) {
            updateInGameStatistics(position);
        }
        if (model.finishedGame()) {
            if (model.getHumanPlayerCount() == 1) {
                updateEndGameStatistics();
            }
            gameEndedDialog(getWindow().getDecorView().findViewById(android.R.id.content));
        }
        else {
            if (getModel().getNonCurrentPlayerModel().getGridPositionState(position) != Constants.hit &&
                    getModel().getNonCurrentPlayerModel().getGridPositionState(position) != Constants.shipDestroyed) {
                model.flipPlayerTurn();
                updateTrackingGridViews();    // Doesn't seem to display before the delay
                //doDelay();
                if (model.getPlayerTurn() == 1 && model.getHumanPlayerCount() == 1) {
                    doAI();
                }
                model.setTrackingGridView(false);
                setContentView(R.layout.game_ship_grid);
                if (model.getHumanPlayerCount() == 2) {
                    model.setTurnTransition(true);
                    setContentView(R.layout.turn_transition);
                }
            }
            if (model.getPlayerTurn() == 1 && model.getHumanPlayerCount() == 1) {
                doAI();
            }
        }
    }

    public void doDelay() {
        try {
            TimeUnit.SECONDS.sleep(1);
        }
        catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    public void doAI() {
        Position position = model.getNonCurrentPlayerModel().getRandomPositionAI();
        model.doShot(position);
        if (getModel().getNonCurrentPlayerModel().getGridPositionState(position) == Constants.hit ||
                getModel().getNonCurrentPlayerModel().getGridPositionState(position) == Constants.shipDestroyed) {
            updateGameState(position);
        }
        else {
            model.flipPlayerTurn();
        }
    }

    public void updateInGameStatistics(Position position) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        updateShotsFiredStatistic(editor);
        if (getModel().getNonCurrentPlayerModel().getGridPositionState(position) == Constants.hit) {
            updateShipsHitStatistic(editor);
        }
        if (getModel().getNonCurrentPlayerModel().getGridPositionState(position) == Constants.shipDestroyed) {
            updateShipsHitStatistic(editor);
            updateShipsDestroyedStatistic(editor);
        }
        editor.apply();
    }

    public void updateShotsFiredStatistic(SharedPreferences.Editor editor) {
        int shotsFired = sharedPreferences.getInt(getString(R.string.shots_fired_key), Constants.defStatVal);
        shotsFired++;
        editor.putInt(getString(R.string.shots_fired_key), shotsFired);
        System.out.println(tag + "Shots fired statistic updated to " + shotsFired);
    }

    public void updateShipsHitStatistic(SharedPreferences.Editor editor) {
        int shipsHit = sharedPreferences.getInt(getString(R.string.ships_hit_key), Constants.defStatVal);
        shipsHit++;
        editor.putInt(getString(R.string.ships_hit_key), shipsHit);
        System.out.println(tag + "Ships hit statistic updated to " + shipsHit);
    }

    public void updateShipsDestroyedStatistic(SharedPreferences.Editor editor) {
        int shipsDestroyed = sharedPreferences.getInt(getString(R.string.ships_destroyed_key), Constants.defStatVal);
        shipsDestroyed++;
        editor.putInt(getString(R.string.ships_destroyed_key), shipsDestroyed);
        System.out.println(tag + "Ships destroyed statistic updated to " + shipsDestroyed);
    }

    public void updateTrackingGridViews() {
        setContentView(R.layout.game_tracking_grid);    // Used to realign text views in case they change size based on the text
        updateGridView();
        TextView numberOfShipsView = (TextView) findViewById(R.id.shipsLeft);
        numberOfShipsView.setText(String.valueOf(getModel().getNonCurrentPlayerModel().getShipsLeft()));
        TextView lastShotPositionView = (TextView) findViewById(R.id.last_shot_position);
        Position lastShotPosition = getModel().getNonCurrentPlayerModel().getLastShotPosition();
        if (lastShotPosition != null) {
            String shotResult;
            if (getModel().getNonCurrentPlayerModel().getGridPositionState(lastShotPosition) == Constants.hit ||
                    getModel().getNonCurrentPlayerModel().getGridPositionState(lastShotPosition) == Constants.shipDestroyed) {
                shotResult = "hit";
                if (getModel().getNonCurrentPlayerModel().getGridPositionState(lastShotPosition) == Constants.shipDestroyed) {
                    Ship destroyedShip = getModel().getNonCurrentPlayerModel().getShipAtPosition(lastShotPosition);
                    TextView destroyedShipView = (TextView) findViewById(R.id.destroyed_ship);
                    destroyedShipView.setText("and destroyed the " + destroyedShip.getType().toLowerCase());
                }
            }
            else {
                shotResult = "missed";
            }
            lastShotPositionView.setText("Your last shot at " + String.valueOf(lastShotPosition.getXForDisplaying()) +
                    lastShotPosition.getYForDisplaying() + " " + shotResult);
        }
    }

    public void updateShipGridViews() {
        updateGridView();
        TextView numberOfShipsView = (TextView) findViewById(R.id.shipsLeft);
        numberOfShipsView.setText(String.valueOf(getModel().getCurrentPlayerModel().getShipsLeft()));
        TextView lastShotPositionView = (TextView) findViewById(R.id.last_shot_position);
        Position lastShotPosition = getModel().getCurrentPlayerModel().getLastShotPosition();
        if (lastShotPosition != null) {
            String shotResult;
            if (getModel().getCurrentPlayerModel().getGridPositionState(lastShotPosition) == Constants.hit ||
                    getModel().getCurrentPlayerModel().getGridPositionState(lastShotPosition) == Constants.shipDestroyed) {
                shotResult = "hit";
                if (getModel().getCurrentPlayerModel().getGridPositionState(lastShotPosition) == Constants.shipDestroyed) {
                    Ship destroyedShip = getModel().getCurrentPlayerModel().getShipAtPosition(lastShotPosition);
                    TextView destroyedShipView = (TextView) findViewById(R.id.destroyed_ship);
                    destroyedShipView.setText("and destroyed the " + destroyedShip.getType().toLowerCase());
                }
            }
            else {
                shotResult = "missed";
            }
            lastShotPositionView.setText("Opponent's last shot at " + String.valueOf(lastShotPosition.getXForDisplaying()) +
                    lastShotPosition.getYForDisplaying() + " " + shotResult);
        }
    }

    public void displayShipGridView() {
        setContentView(R.layout.game_ship_grid);
    }

    public void updateSetupGridView() {
        setContentView(R.layout.game_setup);
        updateGridView();
    }

    public void updateGridView() {
        TextView playerTurnView = (TextView) findViewById(R.id.playerTurn);
        playerTurnView.setText(String.valueOf(getModel().getPlayerTurnForDisplay()));
    }

    public String getTurnTransitionText() {
        return "Player " + model.getPlayerTurnForDisplay() + " is up next, tap to continue";
    }

    public void handleButtonRandomiseShipPositions(View view) {
        model.getCurrentPlayerModel().randomiseShipPositions();
        System.out.println(tag + "Player " + getModel().getPlayerTurnForDisplay() + "'s ships have been randomised");
        updateSetupGridView();
    }

    public void handleButtonConfirmShipPositions(View view) {
        if (model.getPlayerTurn() == 0 && model.getHumanPlayerCount() == 2) {
            model.flipPlayerTurn();
            model.setTurnTransition(true);
            setContentView(R.layout.turn_transition);
        }
        else {
            if (model.getPlayerTurn() == 1) {
                model.flipPlayerTurn();
            }
            model.setSetupPhase(false);
            if (model.getHumanPlayerCount() == 2) {
                model.setTurnTransition(true);
                setContentView(R.layout.turn_transition);
            }
            else {
                updateTrackingGridViews();
            }
            System.out.println(tag + "Entering main game phase with " + model.getHumanPlayerCount() + " human player(s)");
        }
    }

    public void handleButtonSwitchToShipGrid(View view) {
        model.setTrackingGridView(false);
        setContentView(R.layout.game_ship_grid);
    }

    public void handleButtonSwitchToTrackingGrid(View view) {
        model.setTrackingGridView(true);
        updateTrackingGridViews();
    }

    public void updateEndGameStatistics() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int gamesPlayed = sharedPreferences.getInt(getString(R.string.games_played_key), Constants.defStatVal);
        gamesPlayed++;
        editor.putInt(getString(R.string.games_played_key), gamesPlayed);
        System.out.println(tag + "Games played statistic updated to " + gamesPlayed);
        if (getModel().getPlayerTurn() == 0) {
            int wins = sharedPreferences.getInt(getString(R.string.wins_key), Constants.defStatVal);
            wins++;
            editor.putInt(getString(R.string.wins_key), wins);
            System.out.println(tag + "Wins statistic updated to " + wins);
        }
        else {
            int losses = sharedPreferences.getInt(getString(R.string.losses_key), Constants.defStatVal);
            losses++;
            editor.putInt(getString(R.string.losses_key), losses);
            System.out.println(tag + "Losses statistic updated to " + losses);
        }
        editor.apply();
    }

    public void gameEndedDialog(View view) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("The game has ended with player " + (model.getPlayerTurn() + 1) +
                " as the winner with " + model.getCurrentPlayerModel().getShipsLeft() + " ship(s) left");
        alertDialog.setMessage("Start a new game?");

        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,
                "Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        resetModel();
                    }
                });

        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                "Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        finish();
                    }
                });

        alertDialog.show();
    }
}

package com.example.Battleship.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.example.Battleship.Constants;
import com.example.Battleship.R;

public class StatisticsActivity extends Activity {

    TextView gamesPlayedView;
    TextView winsView;
    TextView lossesView;
    TextView shotsFiredView;
    TextView shipHitsView;
    TextView shipsDestroyedView;
    static String tag = "Statistics: ";
    SharedPreferences sharedPreferences;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics);
        getTextViews();
        sharedPreferences = getSharedPreferences(getString(R.string.stats_file_key), Context.MODE_PRIVATE);
        getStatistics();
    }

    public void getTextViews() {
        gamesPlayedView = (TextView) findViewById(R.id.gamesPlayed);
        winsView = (TextView) findViewById(R.id.wins);
        lossesView = (TextView) findViewById(R.id.losses);
        shotsFiredView = (TextView) findViewById(R.id.shotsFired);
        shipHitsView = (TextView) findViewById(R.id.shipHits);
        shipsDestroyedView = (TextView) findViewById(R.id.shipsDestroyed);
    }

    public void getStatistics() {
        gamesPlayedView.setText(String.valueOf(sharedPreferences.getInt(getString(R.string.games_played_key), Constants.defStatVal)));
        winsView.setText(String.valueOf(sharedPreferences.getInt(getString(R.string.wins_key), Constants.defStatVal)));
        lossesView.setText(String.valueOf(sharedPreferences.getInt(getString(R.string.losses_key), Constants.defStatVal)));
        shotsFiredView.setText(String.valueOf(sharedPreferences.getInt(getString(R.string.shots_fired_key), Constants.defStatVal)));
        shipHitsView.setText(String.valueOf(sharedPreferences.getInt(getString(R.string.ships_hit_key), Constants.defStatVal)));
        shipsDestroyedView.setText(String.valueOf(sharedPreferences.getInt(getString(R.string.ships_destroyed_key), Constants.defStatVal)));
    }

    public void handleButtonReset(View view) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Reset statistics");
        alertDialog.setMessage("Are you sure?");

        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,
                "Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        resetStatistics();
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
    }

    public void resetStatistics() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(getString(R.string.games_played_key), Constants.defStatVal);
        editor.putInt(getString(R.string.wins_key), Constants.defStatVal);
        editor.putInt(getString(R.string.losses_key), Constants.defStatVal);
        editor.putInt(getString(R.string.shots_fired_key), Constants.defStatVal);
        editor.putInt(getString(R.string.ships_hit_key), Constants.defStatVal);
        editor.putInt(getString(R.string.ships_destroyed_key), Constants.defStatVal);
        editor.apply();
        getStatistics();
        System.out.println(tag + "statistics reset");
    }
}

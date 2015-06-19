package com.example.Battleship.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.example.Battleship.Constants;
import com.example.Battleship.R;

public class GameModeActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_mode);
    }

    public void handleButtonOnePlayer(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(Constants.humanPlayerCount, 1);
        startActivity(intent);
    }

    public void handleButtonTwoPlayer(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(Constants.humanPlayerCount, 2);
        startActivity(intent);
    }
}

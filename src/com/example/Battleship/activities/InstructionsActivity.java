package com.example.Battleship.activities;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import com.example.Battleship.R;

public class InstructionsActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instructions);
        WebView webView = (WebView) findViewById(R.id.instructions);
        webView.loadUrl("file:///android_asset/html/Instructions.html");
    }
}

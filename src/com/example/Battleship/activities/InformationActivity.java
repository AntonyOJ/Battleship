package com.example.Battleship.activities;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import com.example.Battleship.R;

public class InformationActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information);
        WebView webView = (WebView) findViewById(R.id.information);
        webView.loadUrl("file:///android_asset/html/Information.html");
    }
}

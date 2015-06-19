package com.example.Battleship.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import com.example.Battleship.activities.GameActivity;

public class TurnTransitionGameView extends View implements
        View.OnClickListener {

    static String tag = "TurnTransitionGameView: ";
    static int bgc = Color.BLACK, fgc = Color.WHITE;
    float left = 0, top = 0;

    GameActivity controller;

    public TurnTransitionGameView(Context context) {
        super(context);
        setup(context, "Constructor 1");
    }

    public TurnTransitionGameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context, "Constructor 2");
    }

    public TurnTransitionGameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setup(context, "Constructor 3");
    }

    private void setup(Context context, String cons) {
        System.out.println(tag + cons);
        controller = (GameActivity) context;
        setOnClickListener(this);
    }

    public void onDraw(Canvas g) {
        // fill the view with a background colour
        Paint bg = new Paint();
        bg.setColor(bgc);
        bg.setStyle(Paint.Style.FILL);
        g.drawRect(left, top, getWidth(), getHeight(), bg);

        // draw the text
        Paint textPaint = new Paint();
        textPaint.setColor(fgc);
        // text size is specified in Pixels:
        // width of widest character in font?
        textPaint.setTextSize(getWidth() / 16);
        textPaint.setAntiAlias(true);
        Rect bounds = new Rect();
        String text = controller.getTurnTransitionText();
        textPaint.getTextBounds(text, 0, text.length(), bounds);
        g.drawText(text, (g.getWidth() / 2) - (bounds.width() / 2),
                g.getHeight() / 2, textPaint);
    }

    @Override
    public void onClick(View view) {
        System.out.println(tag + "Player " + controller.getModel().getPlayerTurnForDisplay() + " has tapped to play");
        controller.getModel().setTurnTransition(false);
        if (controller.getModel().isSetupPhase()) {
            controller.updateSetupGridView();
        }
        else {
            controller.displayShipGridView();
        }
    }
}

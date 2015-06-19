package com.example.Battleship.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import com.example.Battleship.models.GameModel;
import com.example.Battleship.models.Position;
import com.example.Battleship.activities.GameActivity;

public class SetupGridGameView extends View {

    // Color constants
    static int bg = Color.DKGRAY;
    static int bgGrid = Color.GRAY;
    static int fgBlank = Color.BLUE;
    static int fgMiss = Color.WHITE;
    static int fgHit = Color.YELLOW;
    static int fgShipDestroyed = Color.RED;
    static int fgShip = Color.DKGRAY;

    // an array of color to show the state
    // of each switch
    static int[] cols = {fgBlank, fgMiss, fgHit, fgShipDestroyed, fgShip};

    static String tag = "SetupGridGameView: ";

    GameActivity controller;
    int size;

    public SetupGridGameView(Context context) {
        super(context);
        setup(context, "Constructor 1");
    }

    public SetupGridGameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context, "Constructor 2");
    }

    public SetupGridGameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setup(context, "Constructor 3");
    }

    private void setup(Context context, String cons) {
        System.out.println(tag + cons);
        controller = (GameActivity) context;
    }

    // these offsets are to cope with the fact that we
    // might be drawing a square within a rectangle
    int xOff, yOff, minLen, gridSquareLen;

    private void setGeometry() {
        int n = controller.getModel().getGridColumns();
        int midX = getWidth() / 2;
        int midY = getHeight() / 2;
        minLen = Math.min(getWidth(), getHeight());
        // gridSquareLen will be the size of
        // the lights grid in pixels
        gridSquareLen = (minLen / n) * n;
        // size of individual squares
        size = gridSquareLen / n;
        xOff = midX - gridSquareLen / 2;
        yOff = midY - gridSquareLen / 2;
    }

    public void draw(Canvas g) {
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setStyle(Paint.Style.FILL);
        p.setColor(bg);
        setGeometry();
        minLen = Math.min(getWidth(), getHeight());
        //draw view background
        g.drawRect(0, 0, minLen, minLen, p);
        // draw the grid background
        p.setColor(bgGrid);
        g.drawRoundRect(new RectF(xOff, yOff,
                xOff + gridSquareLen, yOff + gridSquareLen), 5, 5, p);
        // draw the switches
        GameModel model = controller.getModel();
        int n = model.getGridColumns();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int cx = xOff + size * i + size / 2;
                int cy = yOff + size * j + size / 2;
                p.setColor(cols[model.getCurrentPlayerModel().getGridPositionState(new Position(i, j))]);
                drawTile(g, cx, cy, p);
            }
        }
    }

    private void drawTile(Canvas g, int cx, int cy, Paint p) {
        // System.out.println(tag + " cx, cy : " + cx + " : " + cy);
        int length = (size * 7) / 8;
        int rad = size / 6;

        int x = cx - length / 2;
        int y = cy - length / 2;
        RectF rect = new RectF(x, y, x + length, y + length);
        g.drawRoundRect(rect, rad, rad, p);
    }
}

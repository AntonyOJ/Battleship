package com.example.Battleship.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.example.Battleship.Constants;
import com.example.Battleship.models.GameModel;
import com.example.Battleship.models.Position;
import com.example.Battleship.activities.GameActivity;

public class TrackingGridGameView extends View implements
        View.OnClickListener,
        View.OnTouchListener {

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

    static String tag = "TrackingGridGameView: ";

    GameActivity controller;
    int size;

    public TrackingGridGameView(Context context) {
        super(context);
        setup(context, "Constructor 1");
    }

    public TrackingGridGameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context, "Constructor 2");
    }

    public TrackingGridGameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setup(context, "Constructor 3");
    }

    private void setup(Context context, String cons) {
        System.out.println(tag + cons);
        controller = (GameActivity) context;
        // checkModel();
        setOnTouchListener(this);
        setOnClickListener(this);
    }

    // these offsets are to cope with the fact that we
    // might be drawing a square within a rectangle
    int xOff, yOff, minLen, gridSquareLen;

    private void setGeometry() {
        int n = controller.getModel().getGridColumns();
        int midX = getWidth() / 2;
        int midY = getHeight() / 2;
        minLen = Math.min(getWidth(), getHeight());
        //System.out.println("getWidth(): " + getWidth());
        //System.out.println("getHeight(): " + getHeight());
        //System.out.println("minLen: " + minLen);
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
                int positionState = model.getNonCurrentPlayerModel().getGridPositionState(new Position(i, j));
                if (positionState == Constants.ship) {
                    // Prevent player from viewing ship positions that have not been targeted
                    positionState = Constants.blank;
                }
                p.setColor(cols[positionState]);
                drawTile(g, cx, cy, p);
            }
        }
        //controller.updateTrackingGridViews();
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

    public void onClick(View view) {
        // use the x, y coords from the onTouch event, then
        // assume that the geometry params have already been set
        int x = (int) ((curX - xOff) / size);
        int y = (int) ((curY - yOff) / size);
        Position position = new Position(x, y);
        if (controller.getModel().tryShot(position)) {
            postInvalidate();
            controller.updateTrackingGridViews();
            controller.updateGameState(position);
        }
    }

    float curX, curY;

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        curX = motionEvent.getX();
        curY = motionEvent.getY();
        // return false to ensure that the event can help make
        // an onClick event
        return false;
    }
}

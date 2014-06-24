package com.castlefrog.games.asg;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public final class HexView extends View {
    private static final int SCALE = 100;
    private static final int DEFAULT_BOARD_SIZE = 5;
    public static final int BACKGROUND_COLOR_VALUE = 0;
    public static final int AGENT1_COLOR_VALUE = 1;
    public static final int AGENT2_COLOR_VALUE = 2;
    public static final int CONNECTION_COLOR_VALUE = 3;

    private PointF[][] locations;
    private ShapeDrawable hexagon;
    /** Distance from center to corner */
    private float hexagonRadius;
    private float hexagonCRadius;

    private byte[][] locationColors;
    private List<Integer> paletteColors = new ArrayList<>();
    private int boardOutlineColor = Color.WHITE;
    private int lineColor = Color.BLACK;

    private int paddingLeft;
    private int paddingTop;
    private int paddingRight;
    private int paddingBottom;
    private int contentWidth;
    private int contentHeight;
    private Paint paint = new Paint();
    private Path path = new Path();

    private int boardSize;
    private HexBoardStyle boardStyle = HexBoardStyle.HEXAGONS;
    private SelectActionListener selectActionListener = new DummySelectActionListener();
    private Point selectedHex;

    public interface SelectActionListener {
        void onActionSelected(int x, int y);
    }

    private class DummySelectActionListener implements SelectActionListener {
        public void onActionSelected(int x, int y) {}
    }

    public HexView(Context context) {
        super(context);
        init(null, 0);
    }

    public HexView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public HexView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.HexView, defStyle, 0);

        boardSize = a.getInt(R.styleable.HexView_boardSize, DEFAULT_BOARD_SIZE);
        int boardBackgroundColor = a.getColor(R.styleable.HexView_boardBackgroundColor, Color.GRAY);
        int agent1Color = a.getColor(R.styleable.HexView_agent1Color, Color.RED);
        int agent2Color = a.getColor(R.styleable.HexView_agent2Color, Color.BLUE);
        int connectionColor = a.getColor(R.styleable.HexView_connectionColor, Color.WHITE);
        boardOutlineColor = a.getColor(R.styleable.HexView_boardOutlineColor, boardOutlineColor);
        lineColor = a.getColor(R.styleable.HexView_lineColor, lineColor);

        a.recycle();

        paletteColors.add(boardBackgroundColor);
        paletteColors.add(agent1Color);
        paletteColors.add(agent2Color);
        paletteColors.add(connectionColor);

        hexagon = new ShapeDrawable(new PathShape(Utils.getHexagon(SCALE), 2 * SCALE, 2 * SCALE * Utils.HEXAGON_SHORT_RADIUS));
        hexagon.getPaint().setAntiAlias(true);
        hexagon.getPaint().setStrokeWidth(0.1f * SCALE);

        locations = new PointF[boardSize][boardSize];
        locationColors = new byte[boardSize][boardSize];
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isEnabled()) {
            float x = ev.getX();
            float y = ev.getY();
            switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                Point newSelected = null;
                for (int i = 0; i < boardSize; i += 1) {
                    for (int j = 0; j < boardSize; j += 1) {
                        PointF point = locations[i][j];
                        if (Math.hypot(point.x - x, point.y - y) < hexagonCRadius) {
                            newSelected = new Point(i, j);
                        }
                    }
                }
                if (selectedHex == null || !selectedHex.equals(newSelected)) {
                    selectedHex = newSelected;
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (selectedHex != null) {
                    selectActionListener.onActionSelected(selectedHex.x, selectedHex.y);
                    selectedHex = null;
                    invalidate();
                }
                break;
            }
        }
        return true;
    }

    @Override
    public void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        paddingLeft = getPaddingLeft();
        paddingTop = getPaddingTop();
        paddingRight = getPaddingRight();
        paddingBottom = getPaddingBottom();
        contentWidth = width - paddingLeft - paddingRight;
        contentHeight = height - paddingTop - paddingBottom;

        float maxHexWidth = contentWidth / (0.75f * boardSize + 0.25f);
        float maxHexHeight = (2 * contentHeight) / (3 * boardSize - 1);
        hexagonRadius = Math.min(maxHexWidth, maxHexHeight / Utils.HEXAGON_SHORT_RADIUS) / 2;
        hexagonCRadius = Utils.HEXAGON_SHORT_RADIUS * hexagonRadius;
        float boardWidth = hexagonRadius * (1.5f * boardSize + 0.5f);
        float boardHeight = hexagonCRadius * (3 * boardSize - 1);
        float xPadding = (width - boardWidth) / 2;
        float yPadding = (height - boardHeight) / 2;
        float yAdjust = hexagonCRadius * (boardSize - 1);

        float x0 = xPadding + hexagonRadius;
        float y0 = yAdjust + yPadding + (Utils.HEXAGON_SHORT_RADIUS * hexagonRadius);
        for (int i = 0; i < boardSize; i += 1) {
            for (int j = 0; j < boardSize; j += 1) {
                locations[i][j] = new PointF(x0 + i * hexagonRadius * 1.5f,
                                         y0 - i * hexagonCRadius + j * 2 * hexagonCRadius);
            }
        }
    }

    private PointF getCorner(PointF location,
                             int xChange,
                             int yChange) {
        return new PointF((location.x + xChange * Utils.HEXAGON_HALF_RADIUS * hexagonRadius),
                          (location.y + yChange * Utils.HEXAGON_SHORT_RADIUS * hexagonRadius));
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //draw board outer edges
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(paletteColors.get(0));
        PointF p1 = getCorner(locations[0][0], -2, 0);
        PointF p2 = locations[0][boardSize - 1];
        canvas.drawRect(p1.x, p1.y, p2.x, p2.y, paint);
        p1 = getCorner(locations[boardSize - 1][0], 2, 0);
        p2 = locations[boardSize - 1][boardSize - 1];
        canvas.drawRect(p1.x, p1.y, p2.x, p2.y, paint);

        paint.setColor(paletteColors.get(1));
        p1 = getCorner(locations[0][0], -1, -1);
        p2 = getCorner(locations[boardSize - 1][0], -1, -1);
        PointF p3 = locations[boardSize - 1][0];
        PointF p4 = locations[0][0];
        path.reset();
        path.moveTo(p1.x, p1.y);
        path.lineTo(p2.x, p2.y);
        path.lineTo(p3.x, p3.y);
        path.lineTo(p4.x, p4.y);
        path.close();
        canvas.drawPath(path, paint);
        p1 = getCorner(locations[0][boardSize - 1], 1, 1);
        p2 = getCorner(locations[boardSize - 1][boardSize - 1], 1, 1);
        p3 = locations[boardSize - 1][boardSize - 1];
        p4 = locations[0][boardSize - 1];
        path.reset();
        path.moveTo(p1.x, p1.y);
        path.lineTo(p2.x, p2.y);
        path.lineTo(p3.x, p3.y);
        path.lineTo(p4.x, p4.y);
        path.close();
        canvas.drawPath(path, paint);

        // draw board background
        for (int i = 0; i < boardSize; i += 1) {
            for (int j = 0; j < boardSize; j += 1) {
                hexagon.getPaint().setStyle(Paint.Style.FILL);
                PointF point = locations[i][j];
                hexagon.setBounds((int) (point.x - hexagonRadius),
                        (int) (point.y - hexagonCRadius),
                        (int) (point.x + hexagonRadius),
                        (int) (point.y + hexagonCRadius));
                hexagon.getPaint().setColor(paletteColors.get(locationColors[i][j]));
                hexagon.draw(canvas);
            }
        }
        // draw board inner edges
        for (PointF[] row: locations) {
            for (PointF point: row) {
                hexagon.getPaint().setStyle(Paint.Style.STROKE);
                hexagon.getPaint().setColor(boardOutlineColor);
                hexagon.setBounds((int) (point.x - hexagonRadius),
                        (int) (point.y - hexagonCRadius),
                        (int) (point.x + hexagonRadius),
                        (int) (point.y + hexagonCRadius));
                hexagon.draw(canvas);
            }
        }
        // draw selection lines
        if (selectedHex != null) {
            int y = selectedHex.y;
            int x = selectedHex.x;
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(lineColor);
            paint.setStrokeWidth(4f);
            PointF p1i = locations[x][0];
            PointF p1f = locations[x][locations.length - 1];
            PointF p2i = locations[0][y];
            PointF p2f = locations[locations.length - 1][y];
            canvas.drawLine(p1i.x, p1i.y, p1f.x, p1f.y, paint);
            canvas.drawLine(p2i.x, p2i.y, p2f.x, p2f.y, paint);
        }
    }

    public int getBoardSize() {
        return boardSize;
    }

    public void setBoardSize(int boardSize) {
        this.boardSize = boardSize;
        invalidate();
    }

    public int getBoardBackgroundColor() {
        return paletteColors.get(BACKGROUND_COLOR_VALUE);
    }

    public void setBoardBackgroundColor(int boardBackgroundColor) {
        paletteColors.set(BACKGROUND_COLOR_VALUE, boardBackgroundColor);
        invalidate();
    }

    public int getAgent1Color() {
        return paletteColors.get(AGENT1_COLOR_VALUE);
    }

    public void setAgent1Color(int agent1Color) {
        paletteColors.set(AGENT1_COLOR_VALUE, agent1Color);
        invalidate();
    }

    public int getAgent2Color() {
        return paletteColors.get(AGENT2_COLOR_VALUE);
    }

    public void setAgent2Color(int agent2Color) {
        paletteColors.set(AGENT2_COLOR_VALUE, agent2Color);
        invalidate();
    }

    public int getConnectionColor() {
        return paletteColors.get(CONNECTION_COLOR_VALUE);
    }

    public void setConnectionColor(int connectionColor) {
        paletteColors.set(CONNECTION_COLOR_VALUE, connectionColor);
        invalidate();
    }

    public int getBoardOutlineColor() {
        return boardOutlineColor;
    }

    public void setBoardOutlineColor(int boardOutlineColor) {
        this.boardOutlineColor = boardOutlineColor;
        invalidate();
    }

    public void setSelectActionListener(SelectActionListener listener) {
        selectActionListener = (listener == null) ? new DummySelectActionListener() : listener;
    }
}

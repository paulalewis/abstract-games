package com.castlefrog.games.asg;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public final class HavannahView extends View {
    private static final int DEFAULT_BOARD_SIZE = 5;
    private static final float LINE_WIDTH_RATIO = 0.1f;

    private Vector<Vector<PointF>> locations;
    private Path hexagon;
    /** distance from center to corner */
    private float hexagonRadius;
    private float hexagonCRadius;
    private float boardWidth;
    private float boardHeight;
    private Point selectedHex;

    private int boardSize;
    //private Vector<Vector<PointF>> locationsColors;
    private byte[][] locationColors;
    private List<Integer> paletteColors = new ArrayList<>();
    private int boardOutlineColor = Color.WHITE;
    private int lineColor = Color.BLACK;

    private Paint paint = new Paint();
    private Matrix matrix = new Matrix();
    private int paddingLeft;
    private int paddingTop;
    private int paddingRight;
    private int paddingBottom;
    private int contentWidth;
    private int contentHeight;
    private float lineWidth;

    private HexBoardStyle boardStyle = HexBoardStyle.HEXAGONS;
    private SelectActionListener selectActionListener;

    public interface SelectActionListener {
        void onActionSelected(int x, int y);
    }

    private class DummySelectActionListener implements SelectActionListener {
        public void onActionSelected(int x, int y) {}
    }

    public HavannahView(Context context) {
        super(context);
        init(null, 0);
    }

    public HavannahView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public HavannahView(Context context, AttributeSet attrs, int defStyle) {
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

        locations = new Vector<>();
        locations.setSize(boardSize);
        int sideLength = (boardSize + 1) / 2;
        for (int i = 0; i < sideLength; i += 1) {
            locations.set(i, new Vector<PointF>());
            locations.get(i).setSize(sideLength + i);
        }
        for (int i = sideLength; i < boardSize; i += 1) {
            locations.set(i, new Vector<PointF>());
            locations.get(i).setSize(boardSize + sideLength - i - 1);
        }

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
                for (int i = 0; i < locations.size(); i += 1) {
                    for (int j = 0; j < locations.get(i).size(); j += 1) {
                        PointF point = locations.get(i).get(j);
                        float dx = point.x - x;
                        float dy = point.y - y;
                        if (Math.hypot(dx, dy) < hexagonCRadius) {
                            int dj = j;
                            if (i > locations.size() / 2) {
                                dj += i - locations.size() / 2;
                            }
                            newSelected = new Point(i, dj);
                        }
                    }
                }
                if (selectedHex == null || !selectedHex.equals(newSelected)) {
                    selectedHex = null;
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

        boardWidth = Math.min(contentWidth, contentHeight);
        boardHeight = boardWidth * (2 * PathUtils.HEXAGON_SHORT_RADIUS * boardSize) / (PathUtils.HEXAGON_RADIUS * (1.5f * boardSize + 0.5f));
        hexagonCRadius = boardHeight / (2 * boardSize);
        hexagonRadius = hexagonCRadius / PathUtils.HEXAGON_SHORT_RADIUS;
        hexagon = PathUtils.getHexagon(hexagonRadius);
        lineWidth = hexagonRadius * LINE_WIDTH_RATIO;
        float xPadding = (width - boardWidth) / 2;
        float yPadding = (height - boardHeight) / 2;

        float x0 = xPadding + boardWidth / 2;
        float y0 = yPadding + hexagonCRadius;

        for (int i = 0; i < boardSize / 2 + 1; i += 1) {
            for (int j = 0; j < locations.get(i).size(); j += 1) {
                locations.get(i).set(j, new PointF(x0 + (j - i) * 1.5f * hexagonRadius, y0 + (i + j) * hexagonCRadius));
            }
        }
        float x02 = x0 - (boardSize / 2) * hexagonRadius * 3 / 2;
        float y02 = y0 + (boardSize / 2) * hexagonCRadius;
        for (int i = boardSize / 2 + 1; i < boardSize; i += 1) {
            for (int j = 0; j < locations.get(i).size(); j += 1) {
                locations.get(i).set(j, new PointF(x02 + j * 1.5f * hexagonRadius, y02 + (2 * (i - boardSize / 2) + j) * hexagonCRadius));
            }
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //draw board
        for (int i = 0; i < locations.size(); i += 1) {
            for (int j = 0; j < locations.get(i).size(); j += 1) {
                paint.setStyle(Paint.Style.FILL);
                PointF point = locations.get(i).get(j);
                Path temp = new Path();
                matrix.reset();
                matrix.postTranslate(point.x - hexagonRadius, point.y - hexagonCRadius);
                hexagon.transform(matrix, temp);
                if (i > locations.size() / 2) {
                    int dj = j + i - locations.size() / 2;
                    paint.setColor(paletteColors.get(locationColors[i][dj]));
                } else {
                    paint.setColor(paletteColors.get(locationColors[i][j]));
                }
                canvas.drawPath(temp, paint);
                //draw board outline
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(boardOutlineColor);
                paint.setStrokeWidth(lineWidth);
                canvas.drawPath(temp, paint);
            }
        }
        //draw selectedHex lines
        if (selectedHex != null) {
            int y = selectedHex.x;
            int x = selectedHex.y;
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(4f);
            PointF p1i = locations.get(x).get(0);
            PointF p1f = locations.get(x).get(locations.get(x).size() - 1);
            PointF p2i = locations.get(Math.max(0, y - locations.size() / 2)).get(y);
            int nx = Math.min(locations.size() - 1, y + locations.size() / 2);
            int ny = Math.max(0, y - locations.size() / 2);
            PointF p2f = locations.get(nx).get(ny);
            canvas.drawLine(p1i.x, p1i.y, p1f.x, p1f.y, paint);
            canvas.drawLine(p2i.x, p2i.y, p2f.x, p2f.y, paint);
        }
    }
}
